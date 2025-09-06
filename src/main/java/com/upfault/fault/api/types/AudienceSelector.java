package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * Selects which players should receive notifications or other targeted actions.
 * 
 * <p>This sealed interface provides type-safe audience targeting for notifications,
 * sounds, and other player-specific operations. Different selector types allow
 * for precise targeting based on various criteria.
 * 
 * @since 0.0.1
 * @apiNote This is a sealed interface with specific implementations for different targeting strategies
 */
public sealed interface AudienceSelector 
    permits AudienceSelector.All, AudienceSelector.Players, AudienceSelector.Permission, 
            AudienceSelector.World, AudienceSelector.Region, AudienceSelector.Nearby {
    
    /**
     * Selects all online players.
     * 
     * <p>This is the broadest selector, targeting every player currently connected
     * to the server.
     */
    record All() implements AudienceSelector {
        /**
         * Gets the singleton instance of the all-players selector.
         * 
         * @return selector that targets all online players
         */
        public static @NotNull All instance() {
            return INSTANCE;
        }
        
        private static final All INSTANCE = new All();
    }
    
    /**
     * Selects specific players by their UUIDs.
     * 
     * @param players the set of player UUIDs to target
     */
    record Players(@NotNull Set<UUID> players) implements AudienceSelector {
        /**
         * Compact constructor with validation and defensive copying.
         * 
         * @param players the player UUIDs (will be defensively copied)
         * @throws IllegalArgumentException if players is null or empty
         */
        public Players {
            if (players == null || players.isEmpty()) {
                throw new IllegalArgumentException("Player set cannot be null or empty");
            }
            players = Set.copyOf(players);
        }
        
        /**
         * Creates a selector for a single player.
         * 
         * @param player the player UUID
         * @return selector targeting the single player
         * @throws IllegalArgumentException if player is null
         */
        public static @NotNull Players single(@NotNull UUID player) {
            if (player == null) {
                throw new IllegalArgumentException("Player UUID cannot be null");
            }
            return new Players(Set.of(player));
        }
        
        /**
         * Gets the number of targeted players.
         * 
         * @return the count of players in this selector
         */
        public int count() {
            return players.size();
        }
    }
    
    /**
     * Selects players who have a specific permission.
     * 
     * @param permission the permission string to check
     */
    record Permission(@NotNull String permission) implements AudienceSelector {
        /**
         * Compact constructor with validation.
         * 
         * @param permission the permission string (cannot be null or empty)
         * @throws IllegalArgumentException if permission is null or empty
         */
        public Permission {
            if (permission == null || permission.trim().isEmpty()) {
                throw new IllegalArgumentException("Permission cannot be null or empty");
            }
            permission = permission.trim();
        }
    }
    
    /**
     * Selects players in a specific world.
     * 
     * @param worldId the world identifier
     */
    record World(@NotNull NamespacedId worldId) implements AudienceSelector {
        /**
         * Compact constructor with validation.
         * 
         * @param worldId the world identifier (cannot be null)
         * @throws IllegalArgumentException if worldId is null
         */
        public World {
            if (worldId == null) {
                throw new IllegalArgumentException("World ID cannot be null");
            }
        }
        
        /**
         * Creates a selector for the overworld.
         * 
         * @return selector targeting players in the overworld
         */
        public static @NotNull World overworld() {
            return new World(NamespacedId.minecraft("overworld"));
        }
        
        /**
         * Creates a selector for the nether.
         * 
         * @return selector targeting players in the nether
         */
        public static @NotNull World nether() {
            return new World(NamespacedId.minecraft("the_nether"));
        }
        
        /**
         * Creates a selector for the end.
         * 
         * @return selector targeting players in the end
         */
        public static @NotNull World end() {
            return new World(NamespacedId.minecraft("the_end"));
        }
    }
    
    /**
     * Selects players within a specific region.
     * 
     * @param region the region to check for players
     */
    record Region(@NotNull com.upfault.fault.api.types.Region region) implements AudienceSelector {
        /**
         * Compact constructor with validation.
         * 
         * @param region the region (cannot be null)
         * @throws IllegalArgumentException if region is null
         */
        public Region {
            if (region == null) {
                throw new IllegalArgumentException("Region cannot be null");
            }
        }
    }
    
    /**
     * Selects players within a certain distance of coordinates.
     * 
     * @param center the center coordinates
     * @param radius the maximum distance in blocks
     */
    record Nearby(@NotNull Coordinates center, double radius) implements AudienceSelector {
        /**
         * Compact constructor with validation.
         * 
         * @param center the center coordinates (cannot be null)
         * @param radius the radius in blocks (must be positive)
         * @throws IllegalArgumentException if validation fails
         */
        public Nearby {
            if (center == null) {
                throw new IllegalArgumentException("Center coordinates cannot be null");
            }
            if (radius <= 0 || Double.isNaN(radius) || Double.isInfinite(radius)) {
                throw new IllegalArgumentException("Radius must be positive and finite, got: " + radius);
            }
        }
        
        /**
         * Gets the world this selector applies to.
         * 
         * @return the world identifier from the center coordinates
         */
        public @NotNull NamespacedId getWorld() {
            return center.worldId();
        }
    }
    
    /**
     * Convenience method to create an all-players selector.
     * 
     * @return selector targeting all online players
     */
    static @NotNull All all() {
        return All.instance();
    }
    
    /**
     * Convenience method to create a single-player selector.
     * 
     * @param player the player UUID
     * @return selector targeting the single player
     */
    static @NotNull Players player(@NotNull UUID player) {
        return Players.single(player);
    }
    
    /**
     * Convenience method to create a permission-based selector.
     * 
     * @param permission the permission string
     * @return selector targeting players with the permission
     */
    static @NotNull Permission permission(@NotNull String permission) {
        return new Permission(permission);
    }
    
    /**
     * Convenience method to create a world-based selector.
     * 
     * @param worldId the world identifier
     * @return selector targeting players in the world
     */
    static @NotNull World world(@NotNull NamespacedId worldId) {
        return new World(worldId);
    }
    
    /**
     * Convenience method to create a region-based selector.
     * 
     * @param region the region
     * @return selector targeting players in the region
     */
    static @NotNull Region region(@NotNull com.upfault.fault.api.types.Region region) {
        return new Region(region);
    }
    
    /**
     * Convenience method to create a proximity-based selector.
     * 
     * @param center the center coordinates
     * @param radius the radius in blocks
     * @return selector targeting players within range
     */
    static @NotNull Nearby nearby(@NotNull Coordinates center, double radius) {
        return new Nearby(center, radius);
    }
}