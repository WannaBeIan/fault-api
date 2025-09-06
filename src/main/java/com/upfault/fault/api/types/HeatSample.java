package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Represents a single data point in a heatmap with position, weight, and timestamp.
 * 
 * <p>Heat samples are used to build heatmaps of activity or intensity across
 * spatial coordinates. Each sample represents an event or measurement at a
 * specific location and time, with a weight indicating the intensity or
 * importance of that event.
 * 
 * <p>Common use cases include:
 * <ul>
 *   <li>Player activity heatmaps (movement, deaths, interactions)
 *   <li>Resource spawn/consumption tracking
 *   <li>Performance monitoring (lag, TPS drops) by location
 *   <li>Economic activity (trade, shop usage) mapping
 * </ul>
 * 
 * @param at the world coordinates where this sample was recorded
 * @param weight the intensity or importance of this sample (typically positive)
 * @param atTime the timestamp when this sample was recorded
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record HeatSample(
    @NotNull Coordinates at,
    double weight,
    @NotNull Instant atTime
) {
    
    /**
     * Compact constructor with validation.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public HeatSample {
        if (at == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if (Double.isNaN(weight) || Double.isInfinite(weight)) {
            throw new IllegalArgumentException("Weight must be a finite number, got: " + weight);
        }
        if (atTime == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
    }
    
    /**
     * Creates a heat sample with the current timestamp.
     * 
     * @param at the coordinates
     * @param weight the sample weight
     * @return new heat sample
     * @throws IllegalArgumentException if coordinates are null or weight is not finite
     */
    public static @NotNull HeatSample now(@NotNull Coordinates at, double weight) {
        return new HeatSample(at, weight, Instant.now());
    }
    
    /**
     * Creates a unit-weight heat sample (weight = 1.0).
     * 
     * @param at the coordinates
     * @param atTime the timestamp
     * @return new heat sample with weight 1.0
     * @throws IllegalArgumentException if any parameter is null
     */
    public static @NotNull HeatSample unit(@NotNull Coordinates at, @NotNull Instant atTime) {
        return new HeatSample(at, 1.0, atTime);
    }
    
    /**
     * Creates a unit-weight heat sample with current timestamp.
     * 
     * @param at the coordinates
     * @return new heat sample with weight 1.0 and current time
     * @throws IllegalArgumentException if coordinates are null
     */
    public static @NotNull HeatSample unitNow(@NotNull Coordinates at) {
        return new HeatSample(at, 1.0, Instant.now());
    }
    
    /**
     * Checks if this sample has positive weight.
     * 
     * @return true if weight is greater than zero
     */
    public boolean isPositive() {
        return weight > 0.0;
    }
    
    /**
     * Checks if this sample has negative weight.
     * 
     * @return true if weight is less than zero
     */
    public boolean isNegative() {
        return weight < 0.0;
    }
    
    /**
     * Gets the absolute weight value.
     * 
     * @return the absolute value of the weight
     */
    public double absoluteWeight() {
        return Math.abs(weight);
    }
    
    /**
     * Creates a copy with a different weight.
     * 
     * @param newWeight the new weight value
     * @return new heat sample with updated weight
     * @throws IllegalArgumentException if newWeight is not finite
     */
    public @NotNull HeatSample withWeight(double newWeight) {
        return new HeatSample(at, newWeight, atTime);
    }
    
    /**
     * Creates a copy with a different timestamp.
     * 
     * @param newTime the new timestamp
     * @return new heat sample with updated timestamp
     * @throws IllegalArgumentException if newTime is null
     */
    public @NotNull HeatSample withTime(@NotNull Instant newTime) {
        return new HeatSample(at, weight, newTime);
    }
    
    /**
     * Creates a copy with a different location.
     * 
     * @param newLocation the new coordinates
     * @return new heat sample with updated location
     * @throws IllegalArgumentException if newLocation is null
     */
    public @NotNull HeatSample withLocation(@NotNull Coordinates newLocation) {
        return new HeatSample(newLocation, weight, atTime);
    }
    
    /**
     * Creates a copy with the weight scaled by a factor.
     * 
     * @param factor the scaling factor
     * @return new heat sample with scaled weight
     * @throws IllegalArgumentException if factor is not finite
     */
    public @NotNull HeatSample scaled(double factor) {
        if (Double.isNaN(factor) || Double.isInfinite(factor)) {
            throw new IllegalArgumentException("Scale factor must be finite, got: " + factor);
        }
        return new HeatSample(at, weight * factor, atTime);
    }
    
    /**
     * Calculates the distance between this sample and another location.
     * 
     * @param other the other coordinates
     * @return the 3D distance between locations
     * @throws IllegalArgumentException if other is null
     */
    public double distanceTo(@NotNull Coordinates other) {
        if (other == null) {
            throw new IllegalArgumentException("Other coordinates cannot be null");
        }
        return at.distanceTo(other);
    }
    
    /**
     * Checks if this sample is within a certain distance of coordinates.
     * 
     * @param target the target coordinates
     * @param maxDistance the maximum distance
     * @return true if this sample is within maxDistance of target
     * @throws IllegalArgumentException if target is null or maxDistance is negative
     */
    public boolean isWithinDistance(@NotNull Coordinates target, double maxDistance) {
        if (target == null) {
            throw new IllegalArgumentException("Target coordinates cannot be null");
        }
        if (maxDistance < 0) {
            throw new IllegalArgumentException("Max distance cannot be negative");
        }
        return distanceTo(target) <= maxDistance;
    }
}