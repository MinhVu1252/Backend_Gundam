package com.joyboy.userservice.applications.usecase.redis;

import java.util.Set;

/**
 * Interface for Redis service operations.
 * <p>
 * This interface defines methods for interacting with Redis, specifically for
 * saving, retrieving, deleting tokens, and managing Redis keys. Implementations
 * of this interface are responsible for handling Redis operations, including
 * token storage and retrieval, as well as key management.
 * </p>
 *
 * <p><strong>Methods:</strong></p>
 * <ul>
 *     <li>{@link #saveTokenToRedis(String key, String tokenJson, int expiration)}: Saves a token to Redis
 *         with the specified key and expiration time. The token is stored as a JSON string.</li>
 *     <li>{@link #getToken(String key)}: Retrieves the token associated with the specified key from Redis.
 *         Returns the token as a JSON string.</li>
 *     <li>{@link #deleteToken(String key)}: Deletes the token associated with the specified key from Redis.</li>
 *     <li>{@link #getKeys(String pattern)}: Retrieves a set of keys from Redis that match the specified pattern.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link Exception}: Thrown for general exceptions that occur during Redis operations.</li>
 * </ul>
 *
 * @see String
 * @see Set
 * @see Exception
 *
 * @author ThaoDien
 * @version 1.0
 */
public interface IRedisService {
    /**
     * Saves a token to Redis with the specified key and expiration time.
     *
     * @param key the key under which the token will be stored.
     * @param tokenJson the token represented as a JSON string.
     * @param expiration the expiration time of the token in seconds.
     */
    void saveTokenToRedis(String key, String tokenJson, int expiration);

    /**
     * Retrieves the token associated with the specified key from Redis.
     *
     * @param key the key of the token to retrieve.
     * @return the token represented as a JSON string.
     */
    String getToken(String key);

    /**
     * Deletes the token associated with the specified key from Redis.
     *
     * @param key the key of the token to delete.
     */
    void deleteToken(String key);

    /**
     * Retrieves a set of keys from Redis that match the specified pattern.
     *
     * @param pattern the pattern to match keys against.
     * @return a set of keys that match the pattern.
     */
    Set<String> getKeys(String pattern);
}

