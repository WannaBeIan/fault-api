package com.upfault.fault.api;

import com.upfault.fault.api.types.CacheStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Builder-style cache contracts with TTL, size limits, and statistics.
 * 
 * <p>This facade provides a unified interface for creating and managing caches
 * with various configuration options and monitoring capabilities.
 * 
 * <p>Example usage:
 * <pre>{@code
 * CacheFacade cacheService = Fault.service(CacheFacade.class);
 * 
 * // Create a cache for player data
 * Cache<UUID, PlayerData> playerCache = cacheService.newCache()
 *     .maximumSize(1000)
 *     .expireAfterWrite(Duration.ofMinutes(30))
 *     .enableStats()
 *     .build();
 * 
 * // Use the cache
 * playerCache.put(playerId, playerData);
 * PlayerData data = playerCache.get(playerId);
 * 
 * // Get statistics
 * CacheStats stats = playerCache.stats();
 * System.out.println("Hit rate: " + stats.hitRate());
 * }</pre>
 * 
 * <p><strong>Threading:</strong> Cache instances are thread-safe and can be
 * used concurrently from multiple threads.
 * 
 * @since 0.0.1
 * @apiNote Implementations may use different underlying cache libraries
 */
public interface CacheFacade {

    /**
     * Creates a new cache builder.
     * 
     * @param <K> the cache key type
     * @param <V> the cache value type
     * @return a new cache builder
     * @since 0.0.1
     */
    @NotNull
    <K, V> CacheBuilder<K, V> newCache();

    /**
     * Gets an existing cache by name, if it exists.
     * 
     * @param name the cache name
     * @param <K> the key type
     * @param <V> the value type
     * @return the existing cache, or null if not found
     * @since 0.0.1
     */
    @Nullable
    <K, V> Cache<K, V> getCache(@NotNull String name);

    /**
     * Removes and invalidates a named cache.
     * 
     * @param name the cache name
     * @return true if a cache was removed
     * @since 0.0.1
     */
    boolean removeCache(@NotNull String name);

    /**
     * Gets the names of all registered caches.
     * 
     * @return array of cache names
     * @since 0.0.1
     */
    @NotNull
    String[] getCacheNames();

    /**
     * Builder for configuring cache instances.
     * 
     * @param <K> the cache key type
     * @param <V> the cache value type
     * @since 0.0.1
     */
    interface CacheBuilder<K, V> {

        /**
         * Sets the maximum number of entries the cache can hold.
         * 
         * @param size the maximum cache size
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CacheBuilder<K, V> maximumSize(long size);

        /**
         * Sets the TTL (time-to-live) for cache entries after they are written.
         * 
         * @param duration the TTL duration
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CacheBuilder<K, V> expireAfterWrite(@NotNull Duration duration);

        /**
         * Sets the TTL for cache entries after they are last accessed.
         * 
         * @param duration the TTL duration
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CacheBuilder<K, V> expireAfterAccess(@NotNull Duration duration);

        /**
         * Enables statistics collection for this cache.
         * 
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CacheBuilder<K, V> enableStats();

        /**
         * Sets a name for this cache for management purposes.
         * 
         * @param name the cache name
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CacheBuilder<K, V> name(@NotNull String name);

        /**
         * Sets the initial capacity of the cache.
         * 
         * @param capacity the initial capacity
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CacheBuilder<K, V> initialCapacity(int capacity);

        /**
         * Builds the configured cache.
         * 
         * @return the new cache instance
         * @since 0.0.1
         */
        @NotNull
        Cache<K, V> build();

        /**
         * Builds a loading cache with the specified loader function.
         * 
         * @param loader the function to load missing values
         * @return the new loading cache instance
         * @since 0.0.1
         */
        @NotNull
        LoadingCache<K, V> build(@NotNull Function<K, V> loader);

        /**
         * Builds an async loading cache with the specified async loader function.
         * 
         * @param loader the function to load missing values asynchronously
         * @return the new async loading cache instance
         * @since 0.0.1
         */
        @NotNull
        AsyncLoadingCache<K, V> buildAsync(@NotNull Function<K, CompletableFuture<V>> loader);
    }

    /**
     * Basic cache interface for storing key-value pairs.
     * 
     * @param <K> the key type
     * @param <V> the value type
     * @since 0.0.1
     */
    interface Cache<K, V> {

        /**
         * Gets a value from the cache.
         * 
         * @param key the key to look up
         * @return the cached value, or null if not present
         * @since 0.0.1
         */
        @Nullable
        V get(@NotNull K key);

        /**
         * Puts a value in the cache.
         * 
         * @param key the key to store under
         * @param value the value to store
         * @since 0.0.1
         */
        void put(@NotNull K key, @NotNull V value);

        /**
         * Removes a key from the cache.
         * 
         * @param key the key to remove
         * @since 0.0.1
         */
        void invalidate(@NotNull K key);

        /**
         * Removes all entries from the cache.
         * 
         * @since 0.0.1
         */
        void invalidateAll();

        /**
         * Gets the current size of the cache.
         * 
         * @return the number of cached entries
         * @since 0.0.1
         */
        long size();

        /**
         * Gets cache statistics if statistics were enabled.
         * 
         * @return cache statistics, or null if stats not enabled
         * @since 0.0.1
         */
        @Nullable
        CacheStats stats();

        /**
         * Performs maintenance operations on the cache.
         * 
         * <p>This may include evicting expired entries or other cleanup.
         * 
         * @since 0.0.1
         */
        void cleanUp();
    }

    /**
     * Cache that automatically loads missing values.
     * 
     * @param <K> the key type
     * @param <V> the value type
     * @since 0.0.1
     */
    interface LoadingCache<K, V> extends Cache<K, V> {

        /**
         * Gets a value from the cache, loading it if necessary.
         * 
         * @param key the key to look up
         * @return the cached or loaded value
         * @since 0.0.1
         */
        @NotNull
        V getUnchecked(@NotNull K key);

        /**
         * Refreshes the cached value for a key.
         * 
         * @param key the key to refresh
         * @since 0.0.1
         */
        void refresh(@NotNull K key);
    }

    /**
     * Cache that automatically loads missing values asynchronously.
     * 
     * @param <K> the key type
     * @param <V> the value type
     * @since 0.0.1
     */
    interface AsyncLoadingCache<K, V> extends Cache<K, V> {

        /**
         * Gets a value from the cache asynchronously, loading it if necessary.
         * 
         * @param key the key to look up
         * @return future containing the cached or loaded value
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<V> getAsync(@NotNull K key);

        /**
         * Refreshes the cached value for a key asynchronously.
         * 
         * @param key the key to refresh
         * @return future that completes when refresh is done
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> refreshAsync(@NotNull K key);
    }
}