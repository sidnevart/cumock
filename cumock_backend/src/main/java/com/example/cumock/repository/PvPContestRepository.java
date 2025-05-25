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


    Optional<PvPContest> findFirstByUser1IdOrUser2IdAndStatus(Long user1Id, Long user2Id, String status);
}
