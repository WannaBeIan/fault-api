package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Service for sending notifications through multiple channels.
 * 
 * <p>This service provides a unified interface for sending messages to players
 * and external systems through various notification channels. Each channel has
 * different characteristics and is appropriate for different types of content.
 * 
 * <p>The service supports both immediate delivery and scheduled notifications,
 * with automatic fallback handling for external services that may be unavailable.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe. Synchronous methods deliver immediately,
 *          asynchronous methods return CompletableFutures for delivery confirmation.
 */
public interface NotificationService {
    
    /**
     * Sends a notification immediately through the specified channel.
     * 
     * <p>This is a fire-and-forget operation that attempts immediate delivery.
     * For in-game channels (CHAT, ACTIONBAR, TITLE), the message is sent
     * directly to matching players. For external channels (WEBHOOK), the
     * message is queued for delivery.
     * 
     * @param channel the notification channel to use
     * @param to the audience selector for targeting recipients
     * @param message the message content
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Delivery is not guaranteed for external channels.
     */
    void send(@NotNull Channel channel, @NotNull AudienceSelector to, @NotNull Component message);
    
    /**
     * Sends a notification asynchronously with delivery confirmation.
     * 
     * <p>This method provides confirmation of successful delivery for all
     * channel types. For in-game channels, success means the message was
     * sent to all matching online players. For external channels, success
     * means the webhook was successfully delivered.
     * 
     * @param channel the notification channel to use
     * @param to the audience selector for targeting recipients
     * @param message the message content
     * @return future containing the operation result with delivery details
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          External delivery may have significant latency.
     */
    @NotNull CompletableFuture<OperationResult> sendAsync(
        @NotNull Channel channel, 
        @NotNull AudienceSelector to, 
        @NotNull Component message
    );
    
    /**
     * Sends a title notification with subtitle and timing control.
     * 
     * <p>This is a specialized method for title notifications that provides
     * full control over the title display timing and subtitle content.
     * Only works with the TITLE channel.
     * 
     * @param to the audience selector for targeting recipients
     * @param title the main title text
     * @param subtitle the subtitle text (can be empty)
     * @param fadeIn duration in ticks for fade-in animation
     * @param stay duration in ticks to display the title
     * @param fadeOut duration in ticks for fade-out animation
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null or timing values are negative
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Timing values are in Minecraft ticks (20 ticks = 1 second).
     */
    @NotNull CompletableFuture<OperationResult> sendTitle(
        @NotNull AudienceSelector to,
        @NotNull Component title,
        @NotNull Component subtitle,
        int fadeIn,
        int stay,
        int fadeOut
    );
    
    /**
     * Broadcasts a message to all online players through the specified channel.
     * 
     * <p>This is a convenience method equivalent to calling send() with
     * AudienceSelector.all().
     * 
     * @param channel the notification channel to use
     * @param message the message content
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void broadcast(@NotNull Channel channel, @NotNull Component message);
    
    /**
     * Sends a notification with priority and metadata.
     * 
     * <p>Priority affects delivery timing and retry behavior for external
     * channels. Higher priority messages are delivered first and retried
     * more aggressively if delivery fails.
     * 
     * @param channel the notification channel to use
     * @param to the audience selector for targeting recipients
     * @param message the message content
     * @param priority the message priority
     * @param metadata additional metadata for the notification
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Metadata format is channel-specific.
     */
    @NotNull CompletableFuture<OperationResult> sendWithPriority(
        @NotNull Channel channel,
        @NotNull AudienceSelector to,
        @NotNull Component message,
        @NotNull Priority priority,
        @NotNull java.util.Map<String, String> metadata
    );
    
    /**
     * Checks if a notification channel is currently available.
     * 
     * <p>In-game channels are always available. External channels may be
     * unavailable due to network issues or service outages.
     * 
     * @param channel the channel to check
     * @return future containing true if the channel is available
     * @throws IllegalArgumentException if channel is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Availability may change rapidly for external services.
     */
    @NotNull CompletableFuture<Boolean> isChannelAvailable(@NotNull Channel channel);
    
    /**
     * Gets statistics about notification delivery.
     * 
     * @param channel the channel to get statistics for, or null for all channels
     * @return future containing delivery statistics
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<NotificationStats> getStats(@NotNull Channel channel);
    
    /**
     * Priority levels for notifications.
     */
    enum Priority {
        /** Low priority - best effort delivery */
        LOW,
        /** Normal priority - standard delivery */
        NORMAL,
        /** High priority - prioritized delivery with retries */
        HIGH,
        /** Critical priority - guaranteed delivery with aggressive retries */
        CRITICAL
    }
    
    /**
     * Statistics about notification delivery.
     * 
     * @param channel the channel these stats apply to
     * @param totalSent total number of notifications sent
     * @param successful number of successful deliveries
     * @param failed number of failed deliveries
     * @param pending number of notifications currently pending delivery
     */
    record NotificationStats(
        @NotNull Channel channel,
        long totalSent,
        long successful,
        long failed,
        long pending
    ) {
        public NotificationStats {
            if (channel == null) throw new IllegalArgumentException("Channel cannot be null");
            if (totalSent < 0) throw new IllegalArgumentException("Total sent cannot be negative");
            if (successful < 0) throw new IllegalArgumentException("Successful count cannot be negative");
            if (failed < 0) throw new IllegalArgumentException("Failed count cannot be negative");
            if (pending < 0) throw new IllegalArgumentException("Pending count cannot be negative");
        }
        
        /**
         * Calculates the success rate as a percentage.
         * 
         * @return success rate from 0.0 to 1.0
         */
        public double getSuccessRate() {
            if (totalSent == 0) return 1.0;
            return (double) successful / totalSent;
        }
    }
}