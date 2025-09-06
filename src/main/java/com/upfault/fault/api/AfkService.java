package com.upfault.fault.api;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;

/**
 * Service for detecting idle/AFK players.
 * 
 * <p>This service tracks player activity and determines when players
 * are considered away from keyboard (AFK) based on their inactivity.
 * 
 * @since 0.0.1
 * @apiNote Thread safety: All methods are safe to call from any thread
 */
public interface AfkService {
    
    /**
     * Checks if a player is currently considered AFK.
     * 
     * <p>Players are typically considered AFK after a period of inactivity
     * such as not moving, not interacting with blocks, or not sending messages.
     * 
     * @param player the player to check
     * @return true if the player is currently AFK
     * 
     * @apiNote Safe to call from any thread. Returns false if the player
     *          is not online.
     */
    boolean isAfk(@NotNull UUID player);
    
    /**
     * Gets how long a player has been idle.
     * 
     * <p>Returns the duration since the player last performed an activity
     * that would reset their idle timer.
     * 
     * @param player the player to check
     * @return the duration the player has been idle
     * 
     * @apiNote Safe to call from any thread. Returns Duration.ZERO if the
     *          player is not online or has recent activity.
     */
    @NotNull Duration idleFor(@NotNull UUID player);
}