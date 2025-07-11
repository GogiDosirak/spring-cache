package com.sprint.springcache.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
    BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
        .allowIfSubType("com.sprint.springcache") // 해당 패키지 하위만 허용
        .build();

    //ObjectMapper에 JavaTimeModule 등록
    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .activateDefaultTyping(
            ptv,
            ObjectMapper.DefaultTyping.NON_FINAL // 또는 OBJECT_AND_NON_CONCRETE 등 필요에 따라 선택
        )
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 직렬화기 생성
    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(objectMapper);

    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(serializer)
        )
        .entryTtl(Duration.ofSeconds(120))
        .disableCachingNullValues();


    return RedisCacheManager.builder(redisConnectionFactory)
        .cacheDefaults(config)
        .build();
  }
}
