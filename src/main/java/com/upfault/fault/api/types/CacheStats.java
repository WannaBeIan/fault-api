package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Cache statistics snapshot.
 * 
 * @param requestCount total number of cache requests
 * @param hitCount number of cache hits
 * @param missCount number of cache misses
 * @param loadCount number of cache loads
 * @param evictionCount number of evicted entries
 * @param totalLoadTime total time spent loading (nanoseconds)
 * 
 * @since 0.0.1
 * @apiNote Provides read-only metrics for cache performance monitoring
 */
public record CacheStats(
    long requestCount,
    long hitCount,
    long missCount,
    long loadCount,
    long evictionCount,
    long totalLoadTime
) {
    
    public CacheStats {
        if (requestCount < 0) {
            throw new IllegalArgumentException("Request count cannot be negative");
        }
        if (hitCount < 0) {
            throw new IllegalArgumentException("Hit count cannot be negative");
        }
        if (missCount < 0) {
            throw new IllegalArgumentException("Miss count cannot be negative");
        }
        if (loadCount < 0) {
            throw new IllegalArgumentException("Load count cannot be negative");
        }
        if (evictionCount < 0) {
            throw new IllegalArgumentException("Eviction count cannot be negative");
        }
        if (totalLoadTime < 0) {
            throw new IllegalArgumentException("Total load time cannot be negative");
        }
    }
    
    /**
     * Creates empty cache stats.
     * 
     * @return stats with all counts at zero
     */
    public static @NotNull CacheStats empty() {
        return new CacheStats(0, 0, 0, 0, 0, 0);
    }
    
    /**
     * Gets the cache hit rate as a value between 0.0 and 1.0.
     * 
     * @return hit rate (hits / requests), or 1.0 if no requests
     */
    public double hitRate() {
        return requestCount == 0 ? 1.0 : (double) hitCount / requestCount;
    }
    
    /**
     * Gets the cache miss rate as a value between 0.0 and 1.0.
     * 
     * @return miss rate (misses / requests), or 0.0 if no requests
     */
    public double missRate() {
        return 1.0 - hitRate();
    }
    
    /**
     * Gets the cache hit rate as a percentage.
     * 
     * @return hit rate as percentage (0-100)
     */
    public double hitRatePercent() {
        return hitRate() * 100.0;
    }
    
    /**
     * Gets the average load time in nanoseconds.
     * 
     * @return average load time, or 0 if no loads
     */
    public double averageLoadTime() {
        return loadCount == 0 ? 0.0 : (double) totalLoadTime / loadCount;
    }
    
    /**
     * Gets the average load time in milliseconds.
     * 
     * @return average load time in milliseconds
     */
    public double averageLoadTimeMillis() {
        return averageLoadTime() / 1_000_000.0;
    }
    
    /**
     * Checks if the cache has been used.
     * 
     * @return true if there have been any requests
     */
    public boolean hasActivity() {
        return requestCount > 0;
    }
    
    @Override
    public @NotNull String toString() {
        return String.format(
            "CacheStats[requests=%d, hits=%d(%.1f%%), misses=%d, loads=%d, evictions=%d, avgLoadTime=%.2fms]",
            requestCount, hitCount, hitRatePercent(), missCount, loadCount, evictionCount, averageLoadTimeMillis()
        );
    }
}
