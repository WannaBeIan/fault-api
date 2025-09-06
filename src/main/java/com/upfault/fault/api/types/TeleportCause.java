package com.upfault.fault.api.types;

/**
 * Represents the cause or reason for a teleportation.
 * 
 * <p>This enum categorizes different reasons why a player might be
 * teleported, allowing for different handling or logging based on context.
 * 
 * @since 0.0.1
 */
public enum TeleportCause {
    
    /**
     * Teleportation triggered by a command.
     * 
     * <p>This includes player commands like /tp, /home, /spawn, etc.
     */
    COMMAND,
    
    /**
     * Teleportation triggered by a matchmaker system.
     * 
     * <p>This includes automatic teleportation for game matching,
     * queue systems, or lobby assignment.
     */
    MATCHMAKER,
    
    /**
     * Teleportation triggered by an administrator.
     * 
     * <p>This includes admin actions, moderation tools, or staff commands.
     */
    ADMIN,
    
    /**
     * Teleportation triggered by a timeline or scripted event.
     * 
     * <p>This includes cutscenes, timed events, or automated sequences.
     */
    TIMELINE
}