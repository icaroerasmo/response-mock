package com.icaroerasmo.responsemock.config;

import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import com.icaroerasmo.responsemock.models.Endpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import java.time.Duration;
import java.util.UUID;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@Configuration
public class Beans {

    @Bean
    public RedisTemplate<UUID, Endpoint> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<UUID, Endpoint> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
