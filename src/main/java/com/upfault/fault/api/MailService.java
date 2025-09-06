package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing offline player mail and message delivery.
 * 
 * <p>This service provides an inbox system where players can receive messages
 * and item attachments even when offline. Mail persists until explicitly
 * deleted and supports both player-to-player and system-to-player messaging.
 * 
 * <p>The service handles mail storage, delivery notifications, and attachment
 * management automatically. Players can read mail, claim attachments, and
 * manage their inbox through appropriate interfaces.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and return CompletableFutures for async operations
 */
public interface MailService {
    
    /**
     * Retrieves all mail in a player's inbox.
     * 
     * <p>Returns mail in reverse chronological order (newest first).
     * This includes both read and unread mail, but may exclude
     * mail that has been explicitly deleted.
     * 
     * @param player the player whose inbox to retrieve
     * @return future containing the list of mail messages
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<List<Mail>> inbox(@NotNull UUID player);
    
    /**
     * Retrieves only unread mail from a player's inbox.
     * 
     * @param player the player whose unread mail to retrieve
     * @return future containing the list of unread mail messages
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<List<Mail>> unreadMail(@NotNull UUID player);
    
    /**
     * Sends a mail message.
     * 
     * <p>The mail is delivered to the recipient's inbox immediately and
     * persists until they delete it. If the recipient is online, they
     * may receive a notification about the new mail.
     * 
     * @param mail the mail message to send
     * @return future containing the operation result
     * @throws IllegalArgumentException if mail is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> send(@NotNull Mail mail);
    
    /**
     * Marks a mail message as read.
     * 
     * @param player the player whose mail to mark as read
     * @param mailId the mail message to mark as read
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> markRead(@NotNull UUID player, @NotNull NamespacedId mailId);
    
    /**
     * Claims attachments from a mail message.
     * 
     * <p>This attempts to add the attached items to the player's inventory.
     * If successful, the mail is marked as claimed. If the player's inventory
     * is full, the operation may fail or items may be dropped.
     * 
     * @param player the player claiming attachments
     * @param mailId the mail message to claim attachments from
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Inventory modifications happen atomically.
     */
    @NotNull CompletableFuture<OperationResult> claimAttachments(@NotNull UUID player, @NotNull NamespacedId mailId);
    
    /**
     * Deletes a mail message from a player's inbox.
     * 
     * <p>Once deleted, the mail cannot be recovered. If the mail has
     * unclaimed attachments, they are lost unless the implementation
     * provides a safety mechanism.
     * 
     * @param player the player whose mail to delete
     * @param mailId the mail message to delete
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Deletion is permanent.
     */
    @NotNull CompletableFuture<OperationResult> deleteMail(@NotNull UUID player, @NotNull NamespacedId mailId);
    
    /**
     * Gets a specific mail message by ID.
     * 
     * @param player the player who should own this mail
     * @param mailId the mail message ID
     * @return future containing the mail message, or empty if not found or not owned by player
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<java.util.Optional<Mail>> getMail(@NotNull UUID player, @NotNull NamespacedId mailId);
    
    /**
     * Counts unread mail for a player.
     * 
     * <p>This is useful for displaying notification badges or counts
     * in user interfaces without loading all mail content.
     * 
     * @param player the player to count unread mail for
     * @return future containing the number of unread messages
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Integer> getUnreadCount(@NotNull UUID player);
    
    /**
     * Clears all read mail from a player's inbox.
     * 
     * <p>This removes all mail that has been marked as read and has no
     * unclaimed attachments. Unread mail and mail with attachments are
     * preserved.
     * 
     * @param player the player whose inbox to clean up
     * @return future containing the operation result with count of deleted messages
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> clearReadMail(@NotNull UUID player);
    
    /**
     * Sends a broadcast mail to all players.
     * 
     * <p>This is useful for server announcements, event notifications,
     * or system messages that all players should receive.
     * 
     * @param subject the mail subject
     * @param body the mail content
     * @return future containing the operation result with count of recipients
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          This may create a large number of mail records.
     */
    @NotNull CompletableFuture<OperationResult> broadcast(
        @NotNull net.kyori.adventure.text.Component subject,
        @NotNull net.kyori.adventure.text.Component body
    );
    
    /**
     * Gets mail statistics for monitoring and administration.
     * 
     * @return future containing system-wide mail statistics
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<MailStats> getSystemStats();
    
    /**
     * Statistics about the mail system.
     * 
     * @param totalMail total number of mail messages in the system
     * @param unreadMail total number of unread messages
     * @param mailWithAttachments number of messages with unclaimed attachments
     * @param averageMailPerPlayer average number of mail messages per player
     */
    record MailStats(
        long totalMail,
        long unreadMail,
        long mailWithAttachments,
        double averageMailPerPlayer
    ) {
        public MailStats {
            if (totalMail < 0) throw new IllegalArgumentException("Total mail cannot be negative");
            if (unreadMail < 0) throw new IllegalArgumentException("Unread mail cannot be negative");
            if (mailWithAttachments < 0) throw new IllegalArgumentException("Mail with attachments cannot be negative");
            if (averageMailPerPlayer < 0) throw new IllegalArgumentException("Average mail per player cannot be negative");
        }
    }
}