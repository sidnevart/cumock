package com.example.cumock.service;

import com.example.cumock.dto.pvp.PvPProgressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PvPProgressPublisherService {

    private final SimpMessagingTemplate messagingTemplate;
    private final PvPTrackerService tracker;

    public PvPProgressPublisherService(SimpMessagingTemplate messagingTemplate, PvPTrackerService tracker) {
        this.messagingTemplate = messagingTemplate;
        this.tracker = tracker;
    }

    public void publish(Long contestId, Long problemId, Long userId, boolean isSubmit) {
        PvPProgressResponse progress = tracker.calculateProgress(userId, problemId, contestId, isSubmit);
        messagingTemplate.convertAndSend("/topic/pvp-progress/" + contestId, progress);
    }
}
