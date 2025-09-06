package com.upfault.fault.api.types;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Represents a mail message in the offline inbox system.
 * 
 * <p>Mail messages can contain text content and optional item attachments
 * that are delivered to players even when they are offline. This enables
 * asynchronous communication and item delivery between players and systems.
 * 
 * @param id unique identifier for this mail message
 * @param to the UUID of the recipient player
 * @param from the UUID of the sender, or null for system messages
 * @param at the timestamp when this mail was sent
 * @param subject the mail subject line
 * @param body the mail content
 * @param attachments optional item attachments
 * @param read whether this mail has been read by the recipient
 * @param claimed whether attachments have been claimed
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record Mail(
    @NotNull NamespacedId id,
    @NotNull UUID to,
    @Nullable UUID from,
    @NotNull Instant at,
    @NotNull Component subject,
    @NotNull Component body,
    @NotNull List<ItemModel> attachments,
    boolean read,
    boolean claimed
) {
    
    /**
     * Compact constructor with validation.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public Mail {
        if (id == null) {
            throw new IllegalArgumentException("Mail ID cannot be null");
        }
        if (to == null) {
            throw new IllegalArgumentException("Recipient cannot be null");
        }
        if (at == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }
        if (body == null) {
            throw new IllegalArgumentException("Body cannot be null");
        }
        if (attachments == null) {
            throw new IllegalArgumentException("Attachments list cannot be null");
        }
        
        // Defensive copying
        attachments = List.copyOf(attachments);
    }
    
    /**
     * Creates a simple text-only mail message.
     * 
     * @param id the mail identifier
     * @param to the recipient
     * @param from the sender (null for system messages)
     * @param subject the subject line
     * @param body the message content
     * @return new mail with no attachments
     */
    public static @NotNull Mail textOnly(
        @NotNull NamespacedId id,
        @NotNull UUID to,
        @Nullable UUID from,
        @NotNull Component subject,
        @NotNull Component body
    ) {
        return new Mail(id, to, from, Instant.now(), subject, body, List.of(), false, false);
    }
    
    /**
     * Creates a system mail message (no sender).
     * 
     * @param id the mail identifier
     * @param to the recipient
     * @param subject the subject line
     * @param body the message content
     * @return new system mail with no attachments
     */
    public static @NotNull Mail system(
        @NotNull NamespacedId id,
        @NotNull UUID to,
        @NotNull Component subject,
        @NotNull Component body
    ) {
        return new Mail(id, to, null, Instant.now(), subject, body, List.of(), false, false);
    }
    
    /**
     * Creates a mail message with item attachments.
     * 
     * @param id the mail identifier
     * @param to the recipient
     * @param from the sender
     * @param subject the subject line
     * @param body the message content
     * @param attachments the attached items
     * @return new mail with attachments
     */
    public static @NotNull Mail withAttachments(
        @NotNull NamespacedId id,
        @NotNull UUID to,
        @Nullable UUID from,
        @NotNull Component subject,
        @NotNull Component body,
        @NotNull List<ItemModel> attachments
    ) {
        return new Mail(id, to, from, Instant.now(), subject, body, attachments, false, false);
    }
    
    /**
     * Checks if this mail is from the system (no sender).
     * 
     * @return true if this is a system-generated message
     */
    public boolean isSystemMail() {
        return from == null;
    }
    
    /**
     * Checks if this mail has item attachments.
     * 
     * @return true if there are attached items
     */
    public boolean hasAttachments() {
        return !attachments.isEmpty();
    }
    
    /**
     * Checks if this mail is unread.
     * 
     * @return true if the mail hasn't been read yet
     */
    public boolean isUnread() {
        return !read;
    }
    
    /**
     * Checks if attachments can be claimed.
     * 
     * @return true if there are unclaimed attachments
     */
    public boolean hasUnclaimedAttachments() {
        return hasAttachments() && !claimed;
    }
    
    /**
     * Creates a copy marked as read.
     * 
     * @return new mail instance marked as read
     */
    public @NotNull Mail markRead() {
        return new Mail(id, to, from, at, subject, body, attachments, true, claimed);
    }
    
    /**
     * Creates a copy marked as claimed (attachments collected).
     * 
     * @return new mail instance marked as claimed
     */
    public @NotNull Mail markClaimed() {
        return new Mail(id, to, from, at, subject, body, attachments, read, true);
    }
    
    /**
     * Gets the age of this mail message.
     * 
     * @return duration since the mail was sent
     */
    public @NotNull java.time.Duration getAge() {
        return java.time.Duration.between(at, Instant.now());
    }
    
    /**
     * Creates a simple text component representation of this mail.
     * 
     * @return text summary of the mail
     */
    public @NotNull Component toSummary() {
        var sender = isSystemMail() ? "System" : from.toString().substring(0, 8);
        var status = isUnread() ? "[NEW]" : "[READ]";
        return Component.text(status + " From: " + sender + " - ").append(subject);
    }
}