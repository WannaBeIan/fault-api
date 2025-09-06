package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Represents world coordinates with a world identifier.
 * 
 * @param worldId the world identifier
 * @param x the X coordinate
 * @param y the Y coordinate  
 * @param z the Z coordinate
 * 
 * @since 0.0.1
 * @apiNote Uses NamespacedId for world identification to avoid direct World references
 */
public record Coordinates(@NotNull NamespacedId worldId, int x, int y, int z) {
    
    /**
     * Creates new coordinates with validation.
     * 
     * @param worldId the world identifier (cannot be null)
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @throws IllegalArgumentException if worldId is null
     */
    public Coordinates {
        if (worldId == null) {
            throw new IllegalArgumentException("World ID cannot be null");
        }
    }
    
    /**
     * Creates coordinates in the minecraft:overworld.
     * 
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return coordinates in the overworld
     */
    public static @NotNull Coordinates overworld(int x, int y, int z) {
        return new Coordinates(NamespacedId.minecraft("overworld"), x, y, z);
    }
    
    /**
     * Creates coordinates in the minecraft:the_nether.
     * 
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return coordinates in the nether
     */
    public static @NotNull Coordinates nether(int x, int y, int z) {
        return new Coordinates(NamespacedId.minecraft("the_nether"), x, y, z);
    }
    
    /**
     * Creates coordinates in the minecraft:the_end.
     * 
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return coordinates in the end
     */
    public static @NotNull Coordinates end(int x, int y, int z) {
        return new Coordinates(NamespacedId.minecraft("the_end"), x, y, z);
    }
    
    /**
     * Adds an offset to these coordinates.
     * 
     * @param dx the X offset
     * @param dy the Y offset
     * @param dz the Z offset
     * @return new coordinates with the offset applied
     */
    public @NotNull Coordinates add(int dx, int dy, int dz) {
        return new Coordinates(worldId, x + dx, y + dy, z + dz);
    }
    
    /**
     * Adds another coordinates as an offset.
     * 
     * <p>Only works if both coordinates are in the same world.
     * 
     * @param other the coordinates to add as an offset
     * @return new coordinates with the offset applied
     * @throws IllegalArgumentException if worlds don't match
     */
    public @NotNull Coordinates add(@NotNull Coordinates other) {
        if (!this.worldId.equals(other.worldId)) {
            throw new IllegalArgumentException("Cannot add coordinates from different worlds: " + 
                                             this.worldId + " and " + other.worldId);
        }
        return new Coordinates(worldId, x + other.x, y + other.y, z + other.z);
    }
    
    /**
     * Subtracts an offset from these coordinates.
     * 
     * @param dx the X offset
     * @param dy the Y offset
     * @param dz the Z offset
     * @return new coordinates with the offset subtracted
     */
    public @NotNull Coordinates subtract(int dx, int dy, int dz) {
        return new Coordinates(worldId, x - dx, y - dy, z - dz);
    }
    
    /**
     * Creates new coordinates with a different X value.
     * 
     * @param newX the new X coordinate
     * @return new coordinates with updated X
     */
    public @NotNull Coordinates withX(int newX) {
        return new Coordinates(worldId, newX, y, z);
    }
    
    /**
     * Creates new coordinates with a different Y value.
     * 
     * @param newY the new Y coordinate
     * @return new coordinates with updated Y
     */
    public @NotNull Coordinates withY(int newY) {
        return new Coordinates(worldId, x, newY, z);
    }
    
    /**
     * Creates new coordinates with a different Z value.
     * 
     * @param newZ the new Z coordinate
     * @return new coordinates with updated Z
     */
    public @NotNull Coordinates withZ(int newZ) {
        return new Coordinates(worldId, x, y, newZ);
    }
    
    /**
     * Creates new coordinates in a different world.
     * 
     * @param newWorldId the new world identifier
     * @return new coordinates with updated world
     */
    public @NotNull Coordinates withWorld(@NotNull NamespacedId newWorldId) {
        return new Coordinates(newWorldId, x, y, z);
    }
    
    /**
     * Calculates the distance to another coordinates point.
     * 
     * <p>Returns -1 if the coordinates are in different worlds.
     * 
     * @param other the other coordinates
     * @return the distance in blocks, or -1 if different worlds
     */
    public double distanceTo(@NotNull Coordinates other) {
        if (!this.worldId.equals(other.worldId)) {
            return -1;
        }
        
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Calculates the squared distance to another coordinates point.
     * 
     * <p>Useful for distance comparisons without the expensive sqrt operation.
     * Returns -1 if the coordinates are in different worlds.
     * 
     * @param other the other coordinates
     * @return the squared distance, or -1 if different worlds
     */
    public double distanceSquaredTo(@NotNull Coordinates other) {
        if (!this.worldId.equals(other.worldId)) {
            return -1;
        }
        
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        
        return dx * dx + dy * dy + dz * dz;
    }
    
    /**
     * Calculates the Manhattan distance to another coordinates point.
     * 
     * <p>Returns -1 if the coordinates are in different worlds.
     * 
     * @param other the other coordinates
     * @return the Manhattan distance, or -1 if different worlds
     */
    public int manhattanDistanceTo(@NotNull Coordinates other) {
        if (!this.worldId.equals(other.worldId)) {
            return -1;
        }
        
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y) + Math.abs(this.z - other.z);
    }
    
    /**
     * Checks if these coordinates are in the same world as another.
     * 
     * @param other the other coordinates
     * @return true if both coordinates are in the same world
     */
    public boolean isInSameWorld(@NotNull Coordinates other) {
        return this.worldId.equals(other.worldId);
    }
    
    /**
     * Gets the chunk X coordinate.
     * 
     * @return the chunk X (block X >> 4)
     */
    public int getChunkX() {
        return x >> 4;
    }
    
    /**
     * Gets the chunk Z coordinate.
     * 
     * @return the chunk Z (block Z >> 4)
     */
    public int getChunkZ() {
        return z >> 4;
    }
    
    /**
     * Checks if these coordinates are within a rectangular region.
     * 
     * @param corner1 one corner of the region
     * @param corner2 the opposite corner of the region
     * @return true if these coordinates are within the region
     * @throws IllegalArgumentException if any coordinates are in different worlds
     */
    public boolean isWithinRegion(@NotNull Coordinates corner1, @NotNull Coordinates corner2) {
        if (!this.worldId.equals(corner1.worldId) || !this.worldId.equals(corner2.worldId)) {
            throw new IllegalArgumentException("All coordinates must be in the same world");
        }
        
        int minX = Math.min(corner1.x, corner2.x);
        int maxX = Math.max(corner1.x, corner2.x);
        int minY = Math.min(corner1.y, corner2.y);
        int maxY = Math.max(corner1.y, corner2.y);
        int minZ = Math.min(corner1.z, corner2.z);
        int maxZ = Math.max(corner1.z, corner2.z);
        
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }
    
    @Override
    public @NotNull String toString() {
        return String.format("%s[%d, %d, %d]", worldId.asString(), x, y, z);
    }
}
