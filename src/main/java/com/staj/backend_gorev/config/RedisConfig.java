package com.staj.backend_gorev.config; // Paket isminin doğruluğunu kontrol et

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Anahtarlar (Key) String olarak tutulsun (Okunabilirlik için)
        template.setKeySerializer(new StringRedisSerializer());

        // Değerler (Value) JSON formatında tutulsun
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Hash operasyonları için de aynısı
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}