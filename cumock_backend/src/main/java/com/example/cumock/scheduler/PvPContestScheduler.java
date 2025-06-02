package com.example.cumock.scheduler;

import com.example.cumock.model.PvPContest;
import com.example.cumock.repository.PvPContestRepository;
import com.example.cumock.service.PvPContestService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PvPContestScheduler {

    private final PvPContestRepository repository;
    private final PvPContestService service;

    public PvPContestScheduler(PvPContestRepository repository, PvPContestService service) {
        this.repository = repository;
        this.service = service;
    }

    @Scheduled(fixedRate = 30_000)
    public void autoFinishTimedOutContests() {
        LocalDateTime now = LocalDateTime.now();
        List<PvPContest> ongoing = repository.findAllByStatus("ONGOING");

        for (PvPContest contest : ongoing) {
            if (contest.getStartTime().plusMinutes(30).isBefore(now)) {
                System.out.println("⏰ Завершаем PvP #" + contest.getId());
                service.endMatchWithEvaluation(contest.getId());
            }
        }
    }



}
