package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Selects audiences for message delivery.
 * 
 * <p>This sealed interface provides type-safe audience selection for
 * messaging operations, supporting individual players, all players,
 * and permission-based groups.
 * 
 * @since 0.0.1
 * @apiNote Uses sealed interface pattern for exhaustive matching
 */
public sealed interface AudienceSelector 
    permits AudienceSelector.Player, AudienceSelector.All, AudienceSelector.Permission {
    
    /**
     * Selects a specific player by UUID.
     * 
     * @param playerId the player's UUID
     * @return audience selector for the player
     */
    static @NotNull AudienceSelector player(@NotNull UUID playerId) {
        return new Player(playerId);
    }
    
    /**
     * Selects all online players.
     * 
     * @return audience selector for all players
     */
    static @NotNull AudienceSelector all() {
        return All.INSTANCE;
    }
    
    /**
     * Selects players with a specific permission.
     * 
     * @param permission the permission string
     * @return audience selector for players with the permission
     */
    static @NotNull AudienceSelector permission(@NotNull String permission) {
        return new Permission(permission);
    }
    
    /**
     * Audience selector for a specific player.
     * 
     * @param playerId the player's UUID
     */
    record Player(@NotNull UUID playerId) implements AudienceSelector {
        
        public Player {
            if (playerId == null) {
                throw new IllegalArgumentException("Player ID cannot be null");
            }
        }
        
        @Override
        public @NotNull String toString() {
            return "Player[" + playerId + "]";
        }
    }
    
    /**
     * Audience selector for all online players.
     */
    record All() implements AudienceSelector {
        
        static final All INSTANCE = new All();
        
        @Override
        public @NotNull String toString() {
            return "All[]";
        }
    }
    
    /**
     * Audience selector for players with a specific permission.
     * 
     * @param permission the permission string
     */
    record Permission(@NotNull String permission) implements AudienceSelector {
        
        public Permission {
            if (permission == null || permission.trim().isEmpty()) {
                throw new IllegalArgumentException("Permission cannot be null or empty");
            }
        }
        
        @Override
        public @NotNull String toString() {
            return "Permission[" + permission + "]";
        }
    }
    
    /**
     * Pattern matching helper for audience selectors.
     * 
     * @param <T> the return type
     */
    interface Matcher<T> {
        
        /**
         * Handles player audience selector.
         * 
         * @param player the player selector
         * @return the result
         */
        T onPlayer(@NotNull Player player);
        
        /**
         * Handles all players audience selector.
         * 
         * @param all the all selector
         * @return the result
         */
        T onAll(@NotNull All all);
        
        /**
         * Handles permission-based audience selector.
         * 
         * @param permission the permission selector
         * @return the result
         */
        T onPermission(@NotNull Permission permission);
    }
    
    /**
     * Applies pattern matching to this audience selector.
     * 
     * @param matcher the matcher to apply
     * @param <T> the return type
     * @return the result of the matching
     */
    default <T> T match(@NotNull Matcher<T> matcher) {
        return switch (this) {
            case Player player -> matcher.onPlayer(player);
            case All all -> matcher.onAll(all);
            case Permission permission -> matcher.onPermission(permission);
        };
    }
}
