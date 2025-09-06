package com.upfault.fault.api.types;

/**
 * Represents a subscription that can be cancelled.
 * 
 * <p>This interface provides a common pattern for managing
 * subscriptions to various services and events.
 * 
 * @since 0.0.1
 * @apiNote Implementations should be idempotent - calling unsubscribe
 *          multiple times should have no additional effect.
 */
public interface Subscription {
    
    /**
     * Checks if this subscription is still active.
     * 
     * @return true if the subscription is active
     */
    boolean isActive();
    
    /**
     * Cancels this subscription.
     * 
     * <p>After calling this method, the associated listener will no longer
     * receive events or notifications. This method should be idempotent.
     */
    void unsubscribe();
}