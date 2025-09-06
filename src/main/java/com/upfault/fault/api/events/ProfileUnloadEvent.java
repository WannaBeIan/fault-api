package com.upfault.fault.api.events;

import com.upfault.fault.api.types.ProfileSnapshot;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Called when a player profile is unloaded from memory.
 * 
 * <p>This event is fired when a player's profile is being removed
 * from the cache or active memory, typically when a player disconnects.
 * 
 * @since 0.0.1
 * @apiNote This event is not cancellable as the unload has already been decided
 */
public class ProfileUnloadEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final UUID playerId;
    private final ProfileSnapshot profile;
    private final UnloadReason reason;
    
    /**
     * Creates a new ProfileUnloadEvent.
     * 
     * @param playerId the player's UUID
     * @param profile the profile being unloaded
     * @param reason the reason for unloading
     */
    public ProfileUnloadEvent(@NotNull UUID playerId, @NotNull ProfileSnapshot profile, @NotNull UnloadReason reason) {
        this.playerId = playerId;
        this.profile = profile;
        this.reason = reason;
    }
    
    /**
     * Gets the player's UUID.
     * 
     * @return the player's UUID
     */
    public @NotNull UUID getPlayerId() {
        return playerId;
    }
    
    /**
     * Gets the profile being unloaded.
     * 
     * @return the profile snapshot
     */
    public @NotNull ProfileSnapshot getProfile() {
        return profile;
    }
    
    /**
     * Gets the reason for unloading.
     * 
     * @return the unload reason
     */
    public @NotNull UnloadReason getReason() {
        return reason;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    /**
     * Reasons why a profile might be unloaded.
     */
    public enum UnloadReason {
        /**
         * Player disconnected from the server.
         */
        PLAYER_QUIT,
        
        /**
         * Profile was evicted from cache due to memory constraints.
         */
        CACHE_EVICTION,
        
        /**
         * Profile was manually unloaded by an admin or plugin.
         */
        MANUAL,
        
        /**
         * Server is shutting down.
         */
        SERVER_SHUTDOWN,
        
        /**
         * Unknown or other reason.
         */
        OTHER
    }
}
