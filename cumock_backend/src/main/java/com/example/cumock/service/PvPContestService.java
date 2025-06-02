package com.example.cumock.service;

import com.example.cumock.dto.pvp.PvPProgressResponse;
import com.example.cumock.model.Problem;
import com.example.cumock.model.ProblemTestCase;
import com.example.cumock.model.PvPContest;
import com.example.cumock.model.Submission;
import com.example.cumock.repository.ProblemRepository;
import com.example.cumock.repository.ProblemTestCaseRepository;
import com.example.cumock.repository.PvPContestRepository;
import com.example.cumock.repository.SubmissionRepository;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PvPContestService {

    private final PvPContestRepository repository;
    private final SubmissionRepository submissionRepository;
    private final ProblemTestCaseRepository testCaseRepository;
    private final ProblemRepository problemRepository;
    private final RunResultCacheService runResultCache;
    private final SimpMessagingTemplate messagingTemplate; // Добавляем поле

    private static final int CHALLENGE_EXPIRY_MINUTES = 5;

    public PvPContestService(
            PvPContestRepository repository,
            SubmissionRepository submissionRepository,
            ProblemTestCaseRepository testCaseRepository,
            RunResultCacheService runResultCache,
            ProblemRepository problemRepository,
            SimpMessagingTemplate messagingTemplate // Добавляем в конструктор
    ) {
        this.repository = repository;
        this.submissionRepository = submissionRepository;
        this.testCaseRepository = testCaseRepository;
        this.runResultCache = runResultCache;
        this.problemRepository = problemRepository;
        this.messagingTemplate = messagingTemplate; // Инициализируем поле
    }

    public PvPContest startMatch(Long user1Id, Long user2Id, Long task1Id, Long task2Id) {
        PvPContest contest = new PvPContest();
        contest.setUser1Id(user1Id);
        contest.setUser2Id(user2Id);
        contest.setProblem1Id(task1Id);
        contest.setProblem2Id(task2Id);
        contest.setStartTime(LocalDateTime.now());
        contest.setStatus("ONGOING");
        return repository.save(contest);
    }

    public PvPContest createChallenge(Long challengerId, Long challengedId) {
        // Check if challenger is in an active contest
        Optional<PvPContest> existingContest = repository.findFirstByUser1IdAndStatus(challengerId, "ONGOING");
        if (existingContest.isEmpty()) {
            existingContest = repository.findFirstByUser2IdAndStatus(challengerId, "ONGOING");
        }
        
        if (existingContest.isPresent()) {
            throw new IllegalStateException("Challenger is already in an active contest");
        }

        // Check if challenged user is in an active contest
        existingContest = repository.findFirstByUser1IdAndStatus(challengedId, "ONGOING");
        if (existingContest.isEmpty()) {
            existingContest = repository.findFirstByUser2IdAndStatus(challengedId, "ONGOING");
        }
        
        if (existingContest.isPresent()) {
            throw new IllegalStateException("Challenged user is already in an active contest");
        }

        // Get random problems for each user
        try {
            Long problem1Id = getRandomProblemId();
            Long problem2Id = getRandomProblemId();

            PvPContest contest = new PvPContest();
            contest.setUser1Id(challengerId);
            contest.setUser2Id(challengedId);
            contest.setProblem1Id(problem1Id);
            contest.setProblem2Id(problem2Id);
            contest.setStartTime(LocalDateTime.now());
            contest.setStatus("CHALLENGE");
            contest.setChallengeExpiresAt(LocalDateTime.now().plusMinutes(CHALLENGE_EXPIRY_MINUTES));
            return repository.save(contest);
        } catch (Exception e) {
            System.err.println("Error creating challenge: " + e.getMessage());
            throw new IllegalStateException("Failed to create challenge: " + e.getMessage());
        }
    }

    private Long getRandomProblemId() {
        List<Problem> problems = problemRepository.findAll();
        if (problems.isEmpty()) {
            throw new IllegalStateException("No problems available");
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(problems.size());
        return problems.get(randomIndex).getId();
    }

    public PvPContest acceptChallenge(Long contestId, Long userId) {
        Optional<PvPContest> optional = repository.findById(contestId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Challenge not found");
        }

        PvPContest contest = optional.get();
        if (!"CHALLENGE".equals(contest.getStatus())) {
            throw new IllegalStateException("Contest is not in CHALLENGE status");
        }

        if (!contest.getUser2Id().equals(userId)) {
            throw new IllegalStateException("User is not the challenged player");
        }

        if (LocalDateTime.now().isAfter(contest.getChallengeExpiresAt())) {
            contest.setStatus("REJECTED");
            repository.save(contest);
            throw new IllegalStateException("Challenge has expired");
        }

        contest.setStatus("ONGOING");
        return repository.save(contest);
    }

    public PvPContest rejectChallenge(Long contestId, Long userId) {
        Optional<PvPContest> optional = repository.findById(contestId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Challenge not found");
        }

        PvPContest contest = optional.get();
        if (!"CHALLENGE".equals(contest.getStatus())) {
            throw new IllegalStateException("Contest is not in CHALLENGE status");
        }

        if (!contest.getUser2Id().equals(userId)) {
            throw new IllegalStateException("User is not the challenged player");
        }

        contest.setStatus("REJECTED");
        return repository.save(contest);
    }

    @Scheduled(fixedRate = 60_000)
    public void checkExpiredChallenges() {
        List<PvPContest> expiredChallenges = repository.findAllByStatus("CHALLENGE").stream()
                .filter(contest -> LocalDateTime.now().isAfter(contest.getChallengeExpiresAt()))
                .toList();

        for (PvPContest contest : expiredChallenges) {
            contest.setStatus("REJECTED");
            repository.save(contest);
        }
    }


    @Scheduled(fixedRate = 10_000) // Проверяем каждые 10 секунд
    public void checkForEarlyFinish() {
        List<PvPContest> ongoing = repository.findAllByStatus("ONGOING");
        
        for (PvPContest contest : ongoing) {
            boolean user1Solved = submissionRepository
                .findByUserIdAndProblemIdAndContestIdAndVerdict(
                    contest.getUser1Id(), contest.getProblem1Id(), contest.getId(), "OK")
                .size() > 0;
                
            boolean user2Solved = submissionRepository
                .findByUserIdAndProblemIdAndContestIdAndVerdict(
                    contest.getUser2Id(), contest.getProblem2Id(), contest.getId(), "OK")
                .size() > 0;
            
            // Если оба участника решили свои задачи, завершаем соревнование
            if (user1Solved && user2Solved) {
                System.out.println("🏁 Досрочно завершаем PvP #" + contest.getId() + " - оба участника решили задачи");
                endMatchWithEvaluation(contest.getId());
            }
        }
    }

    public Optional<PvPContest> findOngoingById(Long contestId) {
        return repository.findByIdAndStatus(contestId, "ONGOING");
    }

    public Optional<PvPContest> getContestById(Long contestId) {
        return repository.findById(contestId);
    }

    public PvPContest endMatchWithEvaluation(Long contestId) {
        Optional<PvPContest> optional = repository.findById(contestId);
        if (optional.isEmpty()) return null;
        
        PvPContest contest = optional.get();
        
        if ("FINISHED".equals(contest.getStatus())) {
            return contest;
        }
        
        boolean user1Solved = submissionRepository
            .findByUserIdAndProblemIdAndContestIdAndVerdict(
                contest.getUser1Id(), contest.getProblem1Id(), contest.getId(), "OK")
            .size() > 0;
            
        boolean user2Solved = submissionRepository
            .findByUserIdAndProblemIdAndContestIdAndVerdict(
                contest.getUser2Id(), contest.getProblem2Id(), contest.getId(), "OK")
            .size() > 0;
        
        Long winnerId = null;
        String resultDetails = "";
        
        if (user1Solved && !user2Solved) {
            winnerId = contest.getUser1Id();
            resultDetails = "Пользователь 1 решил задачу, Пользователь 2 - нет.";
        } else if (!user1Solved && user2Solved) {
            winnerId = contest.getUser2Id();
            resultDetails = "Пользователь 2 решил задачу, Пользователь 1 - нет.";
        } else if (user1Solved && user2Solved) {
            int user1Attempts = submissionRepository
                .countByUserIdAndProblemIdAndContestId(
                    contest.getUser1Id(), contest.getProblem1Id(), contest.getId());
                    
            int user2Attempts = submissionRepository
                .countByUserIdAndProblemIdAndContestId(
                    contest.getUser2Id(), contest.getProblem2Id(), contest.getId());
            
            if (user1Attempts < user2Attempts) {
                winnerId = contest.getUser1Id();
                resultDetails = "Оба решили задачи, но Пользователь 1 сделал меньше попыток: " + 
                    user1Attempts + " против " + user2Attempts;
            } else if (user2Attempts < user1Attempts) {
                winnerId = contest.getUser2Id();
                resultDetails = "Оба решили задачи, но Пользователь 2 сделал меньше попыток: " + 
                    user2Attempts + " против " + user1Attempts;
            } else {
                LocalDateTime user1FirstSuccess = submissionRepository
                    .findFirstByUserIdAndProblemIdAndContestIdAndVerdictOrderByCreatedAtAsc(
                        contest.getUser1Id(), contest.getProblem1Id(), contest.getId(), "OK")
                    .map(Submission::getCreatedAt)
                    .orElse(null);
                    
                LocalDateTime user2FirstSuccess = submissionRepository
                    .findFirstByUserIdAndProblemIdAndContestIdAndVerdictOrderByCreatedAtAsc(
                        contest.getUser2Id(), contest.getProblem2Id(), contest.getId(), "OK")
                    .map(Submission::getCreatedAt)
                    .orElse(null);
                
                if (user1FirstSuccess != null && (user2FirstSuccess == null || user1FirstSuccess.isBefore(user2FirstSuccess))) {
                    winnerId = contest.getUser1Id();
                    resultDetails = "Оба решили задачи с одинаковым числом попыток, но Пользователь 1 был быстрее";
                } else {
                    winnerId = contest.getUser2Id();
                    resultDetails = "Оба решили задачи с одинаковым числом попыток, но Пользователь 2 был быстрее";
                }
            }
        } else {
            resultDetails = "Ни один участник не решил свою задачу в отведенное время.";
        }
        
        contest.setEndTime(LocalDateTime.now());
        contest.setStatus("FINISHED");
        contest.setWinnerId(winnerId);
        
        PvPContest saved = repository.save(contest);
        
        notifyContestFinished(saved, resultDetails);
        
        return saved;
    }

    private void notifyContestFinished(PvPContest contest, String resultDetails) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "CONTEST_FINISHED");
            payload.put("contestId", contest.getId());
            payload.put("status", contest.getStatus());
            payload.put("winnerId", contest.getWinnerId());
            payload.put("endTime", contest.getEndTime());
            payload.put("resultDetails", resultDetails);
            
            messagingTemplate.convertAndSend("/topic/pvp-progress/" + contest.getId(), payload);
            
            System.out.println("🏆 PvP соревнование #" + contest.getId() + " завершено. " + 
                (contest.getWinnerId() != null ? "Победитель: Пользователь #" + contest.getWinnerId() : "Ничья"));
        } catch (Exception e) {
            System.err.println("Ошибка при отправке уведомления о завершении: " + e.getMessage());
        }
    }

    public PvPProgressResponse getProgress(Long userId, Long problemId, Long contestId, boolean isSubmit) {
        List<ProblemTestCase> tests = testCaseRepository.findByProblemId(problemId);
        int total = tests.size();

        int passed = 0;
        int attempts = isSubmit
                ? submissionRepository.countByUserIdAndProblemIdAndContestId(userId, problemId, contestId)
                : 0;

        if (isSubmit) {
            List<Submission> submissions = submissionRepository.findByUserIdAndProblemIdAndContestId(userId, problemId, contestId);
            boolean solved = submissions.stream().anyMatch(s -> "OK".equals(s.getVerdict()));
            return new PvPProgressResponse(userId, contestId, solved ? total : 0, total, solved, attempts);
        } else {
            passed = runResultCache.getPassed(userId, problemId, contestId);
            return new PvPProgressResponse(userId, contestId, passed, total, false, attempts);
        }
    }
}
