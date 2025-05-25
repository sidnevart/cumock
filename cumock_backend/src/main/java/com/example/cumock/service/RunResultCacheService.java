package com.example.cumock.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RunResultCacheService {

    private final RedisTemplate<String, Integer> redisTemplate;

    public RunResultCacheService(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(Long userId, Long problemId, Long contestId) {
        return "run:" + userId + ":" + problemId + ":" + contestId;
    }

    public void savePassed(Long userId, Long problemId, Long contestId, int passed) {
        redisTemplate.opsForValue().set(key(userId, problemId, contestId), passed, Duration.ofMinutes(30));
    }

    public int getPassed(Long userId, Long problemId, Long contestId) {
        Integer val = redisTemplate.opsForValue().get(key(userId, problemId, contestId));
        return val != null ? val : 0;
    }
}

