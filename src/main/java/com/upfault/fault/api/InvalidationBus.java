package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Service for cache invalidation coordination across distributed systems.
 * 
 * <p>This service provides a publish-subscribe mechanism for coordinating
 * cache invalidation events across multiple server instances, plugin
 * components, and cache layers. When data changes in one location, the
 * invalidation bus ensures that all relevant caches are notified to
 * update or clear their cached data.
 * 
 * <p>The service supports hierarchical invalidation using namespaced IDs,
 * allowing for both specific cache key invalidation and broader pattern-based
 * invalidation of related cache entries.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe. Listeners are called synchronously
 *          on the publishing thread unless specified otherwise.
 */
public interface InvalidationBus {
    
    /**
     * Publishes a cache invalidation event for a specific key.
     * 
     * <p>This notifies all subscribers that are listening for this specific
     * key or any parent prefix of this key. For example, invalidating
     * "player:data:uuid123" would notify listeners for "player:data:uuid123",
     * "player:data", and "player".
     * 
     * @param key the cache key that should be invalidated
     * @throws IllegalArgumentException if key is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Listeners are notified synchronously on the calling thread.
     */
    void publish(@NotNull NamespacedId key);
    
    /**
     * Publishes multiple invalidation events efficiently.
     * 
     * <p>This is more efficient than calling publish() multiple times
     * individually, as it can batch notifications and reduce the number
     * of times each listener is called.
     * 
     * @param keys the cache keys that should be invalidated
     * @throws IllegalArgumentException if keys is null or contains null elements
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Duplicate keys are automatically deduplicated.
     */
    void publishBatch(@NotNull java.util.Collection<NamespacedId> keys);
    
    /**
     * Subscribes to invalidation events for keys matching a prefix.
     * 
     * <p>The listener will be called whenever any key that starts with the
     * specified prefix is invalidated. For example, subscribing to "player"
     * will receive notifications for "player:data:uuid123", "player:inventory",
     * etc.
     * 
     * <p>The returned subscription can be used to unsubscribe from these events.
     * 
     * @param prefix the key prefix to listen for
     * @param listener the callback to invoke when matching keys are invalidated
     * @return subscription that can be used to unsubscribe
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Listeners must be thread-safe as they may be called concurrently.
     */
    @NotNull Subscription subscribe(@NotNull NamespacedId prefix, @NotNull Consumer<NamespacedId> listener);
    
    /**
     * Subscribes to all invalidation events.
     * 
     * <p>The listener will be called for every invalidation event published
     * to the bus. This is useful for debugging, monitoring, or implementing
     * global cache statistics.
     * 
     * @param listener the callback to invoke for all invalidation events
     * @return subscription that can be used to unsubscribe
     * @throws IllegalArgumentException if listener is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Global listeners may have performance impact under high load.
     */
    @NotNull Subscription subscribeToAll(@NotNull Consumer<NamespacedId> listener);
    
    /**
     * Subscribes with filtering based on key patterns.
     * 
     * <p>The filter is applied before calling the listener, allowing for
     * complex matching logic beyond simple prefix matching.
     * 
     * @param filter predicate that determines which keys to process
     * @param listener the callback to invoke when filter matches
     * @return subscription that can be used to unsubscribe
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Both filter and listener must be thread-safe.
     */
    @NotNull Subscription subscribeFiltered(
        @NotNull java.util.function.Predicate<NamespacedId> filter,
        @NotNull Consumer<NamespacedId> listener
    );
    
    /**
     * Publishes an invalidation event asynchronously.
     * 
     * <p>This method queues the invalidation event for asynchronous processing,
     * allowing the caller to continue without waiting for all listeners to
     * be notified. This is useful when publishing from performance-critical
     * code paths.
     * 
     * @param key the cache key that should be invalidated
     * @throws IllegalArgumentException if key is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Listeners are notified on background threads.
     */
    void publishAsync(@NotNull NamespacedId key);
    
    /**
     * Gets statistics about invalidation bus usage.
     * 
     * @return current statistics about published events and active subscriptions
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull InvalidationStats getStats();
    
    /**
     * Clears all active subscriptions (for testing/cleanup).
     * 
     * <p>This is primarily intended for testing scenarios or plugin
     * disable/reload situations where all listeners need to be cleaned up.
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Use with caution in production environments.
     */
    void clearAllSubscriptions();
    
    /**
     * Statistics about invalidation bus usage.
     * 
     * @param totalPublished total number of invalidation events published
     * @param totalSubscriptions current number of active subscriptions
     * @param averageListenersPerEvent average number of listeners notified per event
     * @param queuedEvents number of events currently queued for async processing
     */
    record InvalidationStats(
        long totalPublished,
        int totalSubscriptions,
        double averageListenersPerEvent,
        int queuedEvents
    ) {
        public InvalidationStats {
            if (totalPublished < 0) throw new IllegalArgumentException("Total published cannot be negative");
            if (totalSubscriptions < 0) throw new IllegalArgumentException("Total subscriptions cannot be negative");
            if (averageListenersPerEvent < 0) throw new IllegalArgumentException("Average listeners cannot be negative");
            if (queuedEvents < 0) throw new IllegalArgumentException("Queued events cannot be negative");
        }
    }
}