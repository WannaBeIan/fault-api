package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a spherical region with a center point and radius.
 * 
 * <p>This region type defines a sphere in 3D space, useful for
 * circular protection areas or detection zones.
 * 
 * @param center the center point of the sphere
 * @param radius the radius of the sphere in blocks
 * 
 * @since 0.0.1
 */
public record RegionSphere(@NotNull Coordinates center, double radius) implements Region {
    
    /**
     * Creates a new RegionSphere with validation.
     * 
     * @param center the center point
     * @param radius the radius (must be positive)
     * @throws IllegalArgumentException if center is null or radius is not positive
     */
    public RegionSphere {
        if (center == null) {
            throw new IllegalArgumentException("Center cannot be null");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive, got: " + radius);
        }
    }
    
    @Override
    public boolean contains(@NotNull Coordinates coordinates) {
        if (!coordinates.worldId().equals(center.worldId())) {
            return false;
        }
        
        double distance = center.distanceTo(coordinates);
        return distance >= 0 && distance <= radius;
    }
    
    @Override
    public @NotNull Coordinates getCenter() {
        return center;
    }
    
    @Override
    public @NotNull NamespacedId getWorld() {
        return center.worldId();
    }
    
    /**
     * Gets the diameter of this sphere.
     * 
     * @return the diameter in blocks
     */
    public double getDiameter() {
        return radius * 2;
    }
    
    /**
     * Gets the volume of this sphere.
     * 
     * @return the volume in cubic blocks
     */
    public double getVolume() {
        return (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
    }
    
    /**
     * Gets the surface area of this sphere.
     * 
     * @return the surface area in square blocks
     */
    public double getSurfaceArea() {
        return 4 * Math.PI * Math.pow(radius, 2);
    }
    
    /**
     * Checks if this sphere intersects with another sphere.
     * 
     * @param other the other sphere to check
     * @return true if the spheres intersect
     */
    public boolean intersects(@NotNull RegionSphere other) {
        if (!this.getWorld().equals(other.getWorld())) {
            return false;
        }
        
        double distance = this.center.distanceTo(other.center);
        return distance >= 0 && distance <= (this.radius + other.radius);
    }
    
    /**
     * Checks if this sphere is completely contained within another sphere.
     * 
     * @param other the other sphere to check
     * @return true if this sphere is completely within the other
     */
    public boolean isWithin(@NotNull RegionSphere other) {
        if (!this.getWorld().equals(other.getWorld())) {
            return false;
        }
        
        double distance = this.center.distanceTo(other.center);
        return distance >= 0 && (distance + this.radius) <= other.radius;
    }
    
    /**
     * Creates a new sphere with a different radius but same center.
     * 
     * @param newRadius the new radius
     * @return new sphere with updated radius
     * @throws IllegalArgumentException if radius is not positive
     */
    public @NotNull RegionSphere withRadius(double newRadius) {
        return new RegionSphere(center, newRadius);
    }
    
    /**
     * Creates a new sphere with a different center but same radius.
     * 
     * @param newCenter the new center
     * @return new sphere with updated center
     */
    public @NotNull RegionSphere withCenter(@NotNull Coordinates newCenter) {
        return new RegionSphere(newCenter, radius);
    }
    
    /**
     * Creates a new sphere expanded by the given amount.
     * 
     * @param amount the amount to expand the radius by
     * @return new expanded sphere
     * @throws IllegalArgumentException if expansion would result in non-positive radius
     */
    public @NotNull RegionSphere expand(double amount) {
        double newRadius = radius + amount;
        if (newRadius <= 0) {
            throw new IllegalArgumentException("Expansion would result in non-positive radius: " + newRadius);
        }
        return new RegionSphere(center, newRadius);
    }
    
    /**
     * Creates a new sphere contracted by the given amount.
     * 
     * @param amount the amount to contract the radius by
     * @return new contracted sphere
     * @throws IllegalArgumentException if contraction would result in non-positive radius
     */
    public @NotNull RegionSphere contract(double amount) {
        double newRadius = radius - amount;
        if (newRadius <= 0) {
            throw new IllegalArgumentException("Contraction would result in non-positive radius: " + newRadius);
        }
        return new RegionSphere(center, newRadius);
    }
    
    /**
     * Gets the bounding box that completely contains this sphere.
     * 
     * @return the bounding box
     */
    public @NotNull RegionBox getBoundingBox() {
        int radiusCeil = (int) Math.ceil(radius);
        Coordinates min = center.add(-radiusCeil, -radiusCeil, -radiusCeil);
        Coordinates max = center.add(radiusCeil, radiusCeil, radiusCeil);
        return new RegionBox(min, max);
    }
}