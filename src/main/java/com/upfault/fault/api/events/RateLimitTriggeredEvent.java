package com.upfault.fault.api.events;

import com.upfault.fault.api.types.RateLimitResult;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

/**
 * Called when a rate limit is triggered (when an action is denied due to rate limiting).
 * 
 * <p>This event is fired when a player or system hits a rate limit,
 * allowing plugins to track rate limit violations and potentially
 * take corrective actions.
 * 
 * @since 0.0.1
 * @apiNote This event is not cancellable as the rate limit has already been applied
 */
public class RateLimitTriggeredEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final String rateLimitKey;
    private final UUID playerId;
    private final String action;
    private final RateLimitResult.Denied result;
    private final int attemptedTokens;
    
    /**
     * Creates a new RateLimitTriggeredEvent.
     * 
     * @param rateLimitKey the rate limit key that was triggered
     * @param playerId the player who triggered the limit (may be null for global limits)
     * @param action the action that was rate limited
     * @param result the denied rate limit result
     * @param attemptedTokens the number of tokens that were attempted to be consumed
     */
    public RateLimitTriggeredEvent(@NotNull String rateLimitKey, @Nullable UUID playerId, 
                                   @NotNull String action, @NotNull RateLimitResult.Denied result, 
                                   int attemptedTokens) {
        this.rateLimitKey = rateLimitKey;
        this.playerId = playerId;
        this.action = action;
        this.result = result;
        this.attemptedTokens = attemptedTokens;
    }
    
    /**
     * Gets the rate limit key that was triggered.
     * 
     * @return the rate limit key
     */
    public @NotNull String getRateLimitKey() {
        return rateLimitKey;
    }
    
    /**
     * Gets the player who triggered the rate limit.
     * 
     * @return the player's UUID, or null for global rate limits
     */
    public @Nullable UUID getPlayerId() {
        return playerId;
    }
    
    /**
     * Gets the action that was rate limited.
     * 
     * @return the action identifier
     */
    public @NotNull String getAction() {
        return action;
    }
    
    /**
     * Gets the denied rate limit result.
     * 
     * @return the rate limit result
     */
    public @NotNull RateLimitResult.Denied getResult() {
        return result;
    }
    
    /**
     * Gets the number of tokens that were attempted to be consumed.
     * 
     * @return the attempted token count
     */
    public int getAttemptedTokens() {
        return attemptedTokens;
    }
    
    /**
     * Gets when the action will next be available.
     * 
     * @return the next available time
     */
    public @NotNull Instant getNextAvailableAt() {
        return result.nextAvailableAt();
    }
    
    /**
     * Gets the time remaining until the action is available again.
     * 
     * @return duration until next attempt is allowed
     */
    public @NotNull java.time.Duration getTimeUntilAvailable() {
        return result.getTimeUntilAvailable();
    }
    
    /**
     * Checks if this rate limit affects a specific player.
     * 
     * @return true if this is a player-specific rate limit
     */
    public boolean isPlayerSpecific() {
        return playerId != null;
    }
    
    /**
     * Checks if this is a global rate limit.
     * 
     * @return true if this is a global rate limit
     */
    public boolean isGlobal() {
        return playerId == null;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
