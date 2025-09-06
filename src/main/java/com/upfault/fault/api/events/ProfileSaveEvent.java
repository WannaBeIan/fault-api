package com.upfault.fault.api.events;

import com.upfault.fault.api.types.ProfileSnapshot;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Called when a player profile is about to be saved.
 * 
 * <p>This event can be cancelled to prevent the save operation.
 * Listeners can also modify the profile data before it's saved.
 * 
 * @since 0.0.1
 * @apiNote This event is cancellable and allows profile modification
 */
public class ProfileSaveEvent extends Event implements Cancellable {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final UUID playerId;
    private ProfileSnapshot profile;
    private boolean cancelled = false;
    
    /**
     * Creates a new ProfileSaveEvent.
     * 
     * @param playerId the player's UUID
     * @param profile the profile to be saved
     */
    public ProfileSaveEvent(@NotNull UUID playerId, @NotNull ProfileSnapshot profile) {
        this.playerId = playerId;
        this.profile = profile;
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
     * Gets the profile to be saved.
     * 
     * @return the profile snapshot
     */
    public @NotNull ProfileSnapshot getProfile() {
        return profile;
    }
    
    /**
     * Sets the profile to be saved.
     * 
     * <p>Listeners can use this to modify the profile before it's saved.
     * 
     * @param profile the new profile to save
     */
    public void setProfile(@NotNull ProfileSnapshot profile) {
        this.profile = profile;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
