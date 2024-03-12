package com.icaroerasmo.responsemock.repositories;

import com.icaroerasmo.responsemock.models.Endpoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Log4j2
@Repository
public class RedisRepository {
    @Autowired
    private RedisTemplate<UUID, Endpoint> template;
    public Optional<Endpoint> get(UUID uuid) {
        Endpoint endpoint = null;
        try {
            endpoint = template.opsForValue().get(uuid);
        } catch (IllegalArgumentException e) {
            log.info("Endpoint {} not found", uuid);
        }
        return Optional.ofNullable(endpoint);
    }

    public void put(Endpoint endpoint) {
        template.opsForValue().set(endpoint.getUuid(), endpoint, 60, TimeUnit.MINUTES);
    }
}
