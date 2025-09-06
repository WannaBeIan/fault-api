package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Specification for particle effects with position, spread, and count parameters.
 * 
 * <p>This record defines how particle effects should be displayed, including
 * the particle type, spawn location, spread pattern, and quantity. The
 * implementation maps these specifications to the appropriate Minecraft
 * particle system calls.
 * 
 * @param type the namespaced identifier for the particle type
 * @param at the world coordinates where particles should spawn
 * @param spread the maximum distance particles can deviate from the center point
 * @param count the number of individual particles to spawn
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record ParticleSpec(
    @NotNull NamespacedId type,
    @NotNull Coordinates at,
    double spread,
    int count
) {
    
    /**
     * Compact constructor with validation.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public ParticleSpec {
        if (type == null) {
            throw new IllegalArgumentException("Particle type cannot be null");
        }
        if (at == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if (Double.isNaN(spread) || Double.isInfinite(spread) || spread < 0) {
            throw new IllegalArgumentException("Spread must be a non-negative finite number, got: " + spread);
        }
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative, got: " + count);
        }
    }
    
    /**
     * Creates a single particle with no spread.
     * 
     * @param type the particle type
     * @param at the spawn location
     * @return new particle spec with count=1 and spread=0
     */
    public static @NotNull ParticleSpec single(@NotNull NamespacedId type, @NotNull Coordinates at) {
        return new ParticleSpec(type, at, 0.0, 1);
    }
    
    /**
     * Creates multiple particles with no spread.
     * 
     * @param type the particle type
     * @param at the spawn location
     * @param count the number of particles
     * @return new particle spec with the specified count and no spread
     */
    public static @NotNull ParticleSpec multiple(@NotNull NamespacedId type, @NotNull Coordinates at, int count) {
        return new ParticleSpec(type, at, 0.0, count);
    }
    
    /**
     * Creates particles with spread in a sphere around the location.
     * 
     * @param type the particle type
     * @param at the center spawn location
     * @param spread the radius of the sphere
     * @param count the number of particles
     * @return new particle spec with spherical distribution
     */
    public static @NotNull ParticleSpec sphere(@NotNull NamespacedId type, @NotNull Coordinates at, double spread, int count) {
        return new ParticleSpec(type, at, spread, count);
    }
    
    /**
     * Creates a copy with a different particle count.
     * 
     * @param newCount the new particle count
     * @return new particle spec with updated count
     * @throws IllegalArgumentException if newCount is negative
     */
    public @NotNull ParticleSpec withCount(int newCount) {
        return new ParticleSpec(type, at, spread, newCount);
    }
    
    /**
     * Creates a copy with a different spread radius.
     * 
     * @param newSpread the new spread radius
     * @return new particle spec with updated spread
     * @throws IllegalArgumentException if newSpread is negative or not finite
     */
    public @NotNull ParticleSpec withSpread(double newSpread) {
        return new ParticleSpec(type, at, newSpread, count);
    }
    
    /**
     * Creates a copy at a different location.
     * 
     * @param newLocation the new spawn coordinates
     * @return new particle spec with updated location
     * @throws IllegalArgumentException if newLocation is null
     */
    public @NotNull ParticleSpec at(@NotNull Coordinates newLocation) {
        return new ParticleSpec(type, newLocation, spread, count);
    }
    
    /**
     * Creates a copy with a different particle type.
     * 
     * @param newType the new particle type
     * @return new particle spec with updated type
     * @throws IllegalArgumentException if newType is null
     */
    public @NotNull ParticleSpec withType(@NotNull NamespacedId newType) {
        return new ParticleSpec(newType, at, spread, count);
    }
    
    /**
     * Checks if this particle effect has spread (particles distributed over an area).
     * 
     * @return true if spread is greater than zero
     */
    public boolean hasSpread() {
        return spread > 0.0;
    }
    
    /**
     * Checks if this is a single particle effect.
     * 
     * @return true if count is exactly 1
     */
    public boolean isSingle() {
        return count == 1;
    }
    
    /**
     * Gets the effective area covered by this particle effect.
     * 
     * <p>For particles with no spread, this returns 0. For particles with
     * spread, this calculates the volume of the sphere they can spawn in.
     * 
     * @return the volume in cubic blocks
     */
    public double getEffectiveVolume() {
        if (spread == 0.0) {
            return 0.0;
        }
        return (4.0 / 3.0) * Math.PI * Math.pow(spread, 3);
    }
    
    /**
     * Creates a copy scaled by a factor (affects both spread and count).
     * 
     * @param factor the scaling factor
     * @return new particle spec with scaled parameters
     * @throws IllegalArgumentException if factor is negative or not finite
     */
    public @NotNull ParticleSpec scaled(double factor) {
        if (Double.isNaN(factor) || Double.isInfinite(factor) || factor < 0) {
            throw new IllegalArgumentException("Scale factor must be a non-negative finite number, got: " + factor);
        }
        return new ParticleSpec(
            type,
            at,
            spread * factor,
            Math.max(1, (int) Math.round(count * factor))
        );
    }
}