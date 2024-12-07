package com.joyboy.userservice.applications.usecase.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService implements  IRedisService{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveTokenToRedis(String key, String tokenJson, int expiration) {
        redisTemplate.opsForValue().set(key, tokenJson, expiration, TimeUnit.SECONDS);
    }

    @Override
    public String getToken(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteToken(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }
}
