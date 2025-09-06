package com.upfault.fault.api.events;

import com.upfault.fault.api.types.ProfileSnapshot;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Called when a player profile is loaded.
 * 
 * <p>This event is fired after a profile has been successfully loaded
 * from storage and is ready to be used.
 * 
 * @since 0.0.1
 * @apiNote This event is not cancellable as the load has already occurred
 */
public class ProfileLoadEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final UUID playerId;
    private final ProfileSnapshot profile;
    private final boolean wasCreated;
    
    /**
     * Creates a new ProfileLoadEvent.
     * 
     * @param playerId the player's UUID
     * @param profile the loaded profile
     * @param wasCreated whether the profile was newly created
     */
    public ProfileLoadEvent(@NotNull UUID playerId, @NotNull ProfileSnapshot profile, boolean wasCreated) {
        this.playerId = playerId;
        this.profile = profile;
        this.wasCreated = wasCreated;
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
     * Gets the loaded profile snapshot.
     * 
     * @return the profile snapshot
     */
    public @NotNull ProfileSnapshot getProfile() {
        return profile;
    }
    
    /**
     * Checks if this profile was newly created.
     * 
     * @return true if the profile was created during this load operation
     */
    public boolean wasCreated() {
        return wasCreated;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
