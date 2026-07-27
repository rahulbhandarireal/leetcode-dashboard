package com.example.LeetDeCode_Battle_Module.service;



import com.example.LeetDeCode_Battle_Module.DTO.ProblemStatement;
import com.example.LeetDeCode_Battle_Module.client.AiProblemClient;
import com.example.LeetDeCode_Battle_Module.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AiProblemClient aiProblemClient;

    private static final Duration PROBLEM_CACHE_TTL = Duration.ofHours(6);

    /** Cache-aside: check Redis first, fall back to the AI service on miss. */
    public ProblemStatement getProblem(int problemId) {
        String key = RedisKeyUtil.problemKey(problemId);

        ProblemStatement cached = (ProblemStatement) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached; // cache hit
        }

        ProblemStatement fetched = aiProblemClient.fetchById(problemId); // cache miss
        redisTemplate.opsForValue().set(key, fetched, PROBLEM_CACHE_TTL);
        return fetched;
    }

    /** Used when a new room is created and needs a fresh problem assigned. */
    public ProblemStatement getRandomProblem(String topic, String level) {
        ProblemStatement fetched = aiProblemClient.fetchRandom(topic, level);
        String key = RedisKeyUtil.problemKey(fetched.getId());
        redisTemplate.opsForValue().set(key, fetched, PROBLEM_CACHE_TTL);
        return fetched;
    }
}