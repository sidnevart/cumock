package com.example.cumock.repository;

import com.example.cumock.model.PvPContest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PvPContestRepository extends JpaRepository<PvPContest, Long> {

    Optional<PvPContest> findByIdAndStatus(Long id, String status);

    List<PvPContest> findAllByStatus(String status);
    List<PvPContest> findByUser1IdAndStatus(Long userId, String status);
    List<PvPContest> findByUser2IdAndStatus(Long userId, String status);

    Optional<PvPContest> findFirstByUser1IdAndStatus(Long user1Id, String status);
    Optional<PvPContest> findFirstByUser2IdAndStatus(Long user2Id, String status);
}
