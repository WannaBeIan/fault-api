package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Sealed interface representing different region shapes.
 * 
 * <p>Regions define spatial areas in the world that can be used for
 * various purposes like protection, detection, or gameplay mechanics.
 * 
 * @since 0.0.1
 * @apiNote This is a sealed interface with specific implementations for
 *          different geometric shapes
 */
public sealed interface Region permits RegionBox, RegionSphere, RegionPolygon {
    
    /**
     * Checks if the given coordinates are within this region.
     * 
     * @param coordinates the coordinates to check
     * @return true if the coordinates are within this region
     */
    boolean contains(@NotNull Coordinates coordinates);
    
    /**
     * Gets the approximate center point of this region.
     * 
     * @return the center coordinates
     */
    @NotNull Coordinates getCenter();
    
    /**
     * Gets the world this region exists in.
     * 
     * @return the world identifier
     */
    @NotNull NamespacedId getWorld();
}