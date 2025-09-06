package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Identifier for player profiles.
 * 
 * @param playerId the player's UUID
 * 
 * @since 0.0.1
 * @apiNote Wrapper for type safety in profile operations
 */
public record ProfileId(@NotNull UUID playerId) {
    
    public ProfileId {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }
    }
    
    public static @NotNull ProfileId of(@NotNull UUID playerId) {
        return new ProfileId(playerId);
    }
    
    @Override
    public @NotNull String toString() {
        return "ProfileId[" + playerId + "]";
    }
}
