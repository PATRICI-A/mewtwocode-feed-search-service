package edu.eci.patricia.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Redis cache configuration active only under the {@code docker} Spring profile and only when a
 * {@link RedisConnectionFactory} bean is present in the application context.
 *
 * <p>Enables Spring's annotation-driven caching ({@code @Cacheable}, {@code @CacheEvict}, etc.)
 * and configures a {@link RedisCacheManager} with JSON serialisation and a fixed TTL of 5 minutes
 * for all cache entries (primarily recommendation results).</p>
 */
@Configuration
@EnableCaching
@Profile("docker")
@ConditionalOnBean(RedisConnectionFactory.class)
public class RedisConfig {

    private static final Duration RECOMMENDATIONS_TTL = Duration.ofMinutes(5);

    /**
     * Creates and configures the primary {@link RedisCacheManager} bean.
     *
     * <p>All cache entries are serialised as JSON using {@link GenericJackson2JsonRedisSerializer}
     * and expire after {@value #RECOMMENDATIONS_TTL} minutes.</p>
     *
     * @param connectionFactory the {@link RedisConnectionFactory} provided by Spring Boot auto-configuration
     * @return a fully configured {@link RedisCacheManager} instance
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(RECOMMENDATIONS_TTL)
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
