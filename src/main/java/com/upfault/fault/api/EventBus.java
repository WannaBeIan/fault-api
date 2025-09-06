package com.upfault.fault.api;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Lightweight pub/sub event system with sync/async channels.
 * 
 * <p>This event bus provides an additional layer of event handling beyond
 * Bukkit's event system, allowing for more flexible inter-plugin communication
 * and internal event handling.
 * 
 * <p>Example usage:
 * <pre>{@code
 * EventBus eventBus = Fault.service(EventBus.class);
 * 
 * // Subscribe to an event type
 * EventSubscription<PlayerJoinEvent> subscription = eventBus.subscribe(
 *     PlayerJoinEvent.class,
 *     event -> System.out.println("Player joined: " + event.playerName())
 * );
 * 
 * // Publish an event synchronously
 * eventBus.publishSync(new PlayerJoinEvent("Steve"));
 * 
 * // Publish an event asynchronously
 * eventBus.publishAsync(new PlayerQuitEvent("Alex"));
 * 
 * // Unsubscribe when done
 * subscription.unsubscribe();
 * }</pre>
 * 
 * <p><strong>Threading:</strong> Synchronous events are published on the calling
 * thread. Asynchronous events are published on background threads. Subscribers
 * should handle thread safety appropriately.
 * 
 * @since 0.0.1
 * @apiNote This complements but does not replace Bukkit's event system
 */
public interface EventBus {

    /**
     * Subscribes to events of a specific type.
     * 
     * @param eventType the class of events to subscribe to
     * @param handler the event handler
     * @param <T> the event type
     * @return subscription handle for managing the subscription
     * @since 0.0.1
     */
    @NotNull
    <T> EventSubscription<T> subscribe(@NotNull Class<T> eventType, @NotNull Consumer<T> handler);

    /**
     * Subscribes to events with a priority.
     * 
     * <p>Higher priority subscribers receive events before lower priority ones.
     * Default priority is 0.
     * 
     * @param eventType the class of events to subscribe to
     * @param handler the event handler
     * @param priority the subscription priority
     * @param <T> the event type
     * @return subscription handle for managing the subscription
     * @since 0.0.1
     */
    @NotNull
    <T> EventSubscription<T> subscribe(@NotNull Class<T> eventType, @NotNull Consumer<T> handler, int priority);

    /**
     * Publishes an event synchronously.
     * 
     * <p>All subscribers will be notified on the calling thread before this
     * method returns.
     * 
     * @param event the event to publish
     * @param <T> the event type
     * @since 0.0.1
     */
    <T> void publishSync(@NotNull T event);

    /**
     * Publishes an event asynchronously.
     * 
     * <p>Subscribers will be notified on background threads. The future
     * completes when all subscribers have been notified.
     * 
     * @param event the event to publish
     * @param <T> the event type
     * @return future that completes when all subscribers are notified
     * @since 0.0.1
     */
    @NotNull
    <T> CompletableFuture<Void> publishAsync(@NotNull T event);

    /**
     * Gets the number of active subscribers for an event type.
     * 
     * @param eventType the event type to check
     * @return the number of active subscribers
     * @since 0.0.1
     */
    int getSubscriberCount(@NotNull Class<?> eventType);

    /**
     * Clears all subscribers for a specific event type.
     * 
     * @param eventType the event type to clear
     * @since 0.0.1
     */
    void clearSubscribers(@NotNull Class<?> eventType);

    /**
     * Represents a subscription to events.
     * 
     * @param <T> the event type
     * @since 0.0.1
     */
    interface EventSubscription<T> {

        /**
         * Gets the event type this subscription is for.
         * 
         * @return the event type
         * @since 0.0.1
         */
        @NotNull
        Class<T> getEventType();

        /**
         * Gets the priority of this subscription.
         * 
         * @return the priority
         * @since 0.0.1
         */
        int getPriority();

        /**
         * Checks if this subscription is still active.
         * 
         * @return true if the subscription is active
         * @since 0.0.1
         */
        boolean isActive();

        /**
         * Unsubscribes from events.
         * 
         * <p>After calling this method, the handler will no longer receive events.
         * 
         * @since 0.0.1
         */
        void unsubscribe();
    }

    /**
     * Marker interface for bridging to Bukkit events.
     * 
     * <p>Implementations can use this to identify events that should
     * also be published to the Bukkit event system.
     * 
     * @since 0.0.1
     */
    interface BukkitEventBridge {
        
        /**
         * Gets the corresponding Bukkit event, if any.
         * 
         * @return the Bukkit event, or null if no bridging is needed
         * @since 0.0.1
         */
        @NotNull
        org.bukkit.event.Event toBukkitEvent();
    }
}