package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a rectangular (AABB - Axis-Aligned Bounding Box) region.
 * 
 * <p>This is the most common region type, defining a rectangular area
 * with minimum and maximum corners.
 * 
 * @param min the minimum corner (lower bounds)
 * @param max the maximum corner (upper bounds)
 * 
 * @since 0.0.1
 */
public record RegionBox(@NotNull Coordinates min, @NotNull Coordinates max) implements Region {
    
    /**
     * Creates a new RegionBox with validation.
     * 
     * @param min the minimum corner
     * @param max the maximum corner
     * @throws IllegalArgumentException if corners are in different worlds or invalid
     */
    public RegionBox {
        if (min == null || max == null) {
            throw new IllegalArgumentException("Region corners cannot be null");
        }
        if (!min.worldId().equals(max.worldId())) {
            throw new IllegalArgumentException("Region corners must be in the same world");
        }
        
        // Ensure min is actually the minimum
        if (min.x() > max.x() || min.y() > max.y() || min.z() > max.z()) {
            throw new IllegalArgumentException("Min corner must have coordinates <= max corner");
        }
    }
    
    /**
     * Creates a RegionBox from two corners, automatically determining min/max.
     * 
     * @param corner1 first corner
     * @param corner2 second corner
     * @return new RegionBox with properly ordered corners
     * @throws IllegalArgumentException if corners are in different worlds
     */
    public static @NotNull RegionBox of(@NotNull Coordinates corner1, @NotNull Coordinates corner2) {
        if (!corner1.worldId().equals(corner2.worldId())) {
            throw new IllegalArgumentException("Corners must be in the same world");
        }
        
        int minX = Math.min(corner1.x(), corner2.x());
        int minY = Math.min(corner1.y(), corner2.y());
        int minZ = Math.min(corner1.z(), corner2.z());
        int maxX = Math.max(corner1.x(), corner2.x());
        int maxY = Math.max(corner1.y(), corner2.y());
        int maxZ = Math.max(corner1.z(), corner2.z());
        
        Coordinates minCorner = new Coordinates(corner1.worldId(), minX, minY, minZ);
        Coordinates maxCorner = new Coordinates(corner1.worldId(), maxX, maxY, maxZ);
        
        return new RegionBox(minCorner, maxCorner);
    }
    
    @Override
    public boolean contains(@NotNull Coordinates coordinates) {
        if (!coordinates.worldId().equals(min.worldId())) {
            return false;
        }
        
        return coordinates.x() >= min.x() && coordinates.x() <= max.x() &&
               coordinates.y() >= min.y() && coordinates.y() <= max.y() &&
               coordinates.z() >= min.z() && coordinates.z() <= max.z();
    }
    
    @Override
    public @NotNull Coordinates getCenter() {
        int centerX = (min.x() + max.x()) / 2;
        int centerY = (min.y() + max.y()) / 2;
        int centerZ = (min.z() + max.z()) / 2;
        
        return new Coordinates(min.worldId(), centerX, centerY, centerZ);
    }
    
    @Override
    public @NotNull NamespacedId getWorld() {
        return min.worldId();
    }
    
    /**
     * Gets the width (X-axis size) of this box.
     * 
     * @return the width in blocks
     */
    public int getWidth() {
        return max.x() - min.x() + 1;
    }
    
    /**
     * Gets the height (Y-axis size) of this box.
     * 
     * @return the height in blocks
     */
    public int getHeight() {
        return max.y() - min.y() + 1;
    }
    
    /**
     * Gets the length (Z-axis size) of this box.
     * 
     * @return the length in blocks
     */
    public int getLength() {
        return max.z() - min.z() + 1;
    }
    
    /**
     * Gets the total volume of this box.
     * 
     * @return the volume in blocks
     */
    public long getVolume() {
        return (long) getWidth() * getHeight() * getLength();
    }
    
    /**
     * Checks if this box intersects with another box.
     * 
     * @param other the other box to check
     * @return true if the boxes intersect
     */
    public boolean intersects(@NotNull RegionBox other) {
        if (!this.getWorld().equals(other.getWorld())) {
            return false;
        }
        
        return this.min.x() <= other.max.x() && this.max.x() >= other.min.x() &&
               this.min.y() <= other.max.y() && this.max.y() >= other.min.y() &&
               this.min.z() <= other.max.z() && this.max.z() >= other.min.z();
    }
    
    /**
     * Creates a new box that is expanded by the given amount in all directions.
     * 
     * @param amount the amount to expand by
     * @return new expanded box
     */
    public @NotNull RegionBox expand(int amount) {
        Coordinates newMin = min.add(-amount, -amount, -amount);
        Coordinates newMax = max.add(amount, amount, amount);
        return new RegionBox(newMin, newMax);
    }
    
    /**
     * Creates a new box that is contracted by the given amount in all directions.
     * 
     * @param amount the amount to contract by
     * @return new contracted box
     * @throws IllegalArgumentException if contraction would result in invalid box
     */
    public @NotNull RegionBox contract(int amount) {
        Coordinates newMin = min.add(amount, amount, amount);
        Coordinates newMax = max.add(-amount, -amount, -amount);
        
        if (newMin.x() > newMax.x() || newMin.y() > newMax.y() || newMin.z() > newMax.z()) {
            throw new IllegalArgumentException("Contraction amount too large for box size");
        }
        
        return new RegionBox(newMin, newMax);
    }
}