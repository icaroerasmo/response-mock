package com.icaroerasmo.responsemock.repositories;

import com.icaroerasmo.responsemock.models.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RedisRepository {
    @Autowired
    private RedisTemplate<UUID, Endpoint> template;
    public Endpoint get(UUID uuid) {
        try {
            return template.opsForValue().get(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void put(Endpoint endpoint) {
        template.opsForValue().set(endpoint.getUuid(), endpoint);
    }
}
