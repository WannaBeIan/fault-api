package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Represents resistance to a specific damage type.
 * 
 * <p>Resistances reduce incoming damage of the specified type by a percentage.
 * Multiple resistances to the same damage type stack additively.
 * 
 * @param type the damage type this resistance applies to
 * @param percent the resistance percentage (0.0 = no resistance, 1.0 = 100% resistance)
 * 
 * @since 0.0.1
 */
public record Resistance(@NotNull DamageType type, double percent) {
    
    /**
     * Creates a new Resistance with validation.
     * 
     * @param type the damage type (cannot be null)
     * @param percent the resistance percentage (must be between 0.0 and 1.0)
     * @throws IllegalArgumentException if type is null or percent is out of range
     */
    public Resistance {
        if (type == null) {
            throw new IllegalArgumentException("Damage type cannot be null");
        }
        if (percent < 0.0 || percent > 1.0) {
            throw new IllegalArgumentException("Resistance percent must be between 0.0 and 1.0, got: " + percent);
        }
    }
    
    /**
     * Creates a resistance with a percentage value.
     * 
     * @param type the damage type
     * @param percent the resistance percentage (0-100)
     * @return new resistance with the specified percentage
     * @throws IllegalArgumentException if percent is not between 0 and 100
     */
    public static @NotNull Resistance of(@NotNull DamageType type, double percent) {
        if (percent < 0.0 || percent > 100.0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100, got: " + percent);
        }
        return new Resistance(type, percent / 100.0);
    }
    
    /**
     * Creates a complete immunity (100% resistance) to a damage type.
     * 
     * @param type the damage type to be immune to
     * @return new resistance providing complete immunity
     */
    public static @NotNull Resistance immunity(@NotNull DamageType type) {
        return new Resistance(type, 1.0);
    }
    
    /**
     * Gets the resistance percentage as a value between 0-100.
     * 
     * @return the resistance percentage
     */
    public double getPercentage() {
        return percent * 100.0;
    }
    
    /**
     * Calculates how much damage would be reduced by this resistance.
     * 
     * @param originalDamage the original damage amount
     * @return the amount of damage that would be blocked
     */
    public double calculateReduction(double originalDamage) {
        return originalDamage * percent;
    }
    
    /**
     * Calculates the final damage after applying this resistance.
     * 
     * @param originalDamage the original damage amount
     * @return the damage amount after reduction
     */
    public double applyTo(double originalDamage) {
        return originalDamage * (1.0 - percent);
    }
    
    /**
     * Checks if this resistance provides complete immunity.
     * 
     * @return true if this resistance blocks 100% of damage
     */
    public boolean isImmune() {
        return percent >= 1.0;
    }
    
    /**
     * Creates a new resistance with a different percentage but same type.
     * 
     * @param newPercent the new resistance percentage (0.0-1.0)
     * @return new resistance with updated percentage
     */
    public @NotNull Resistance withPercent(double newPercent) {
        return new Resistance(type, newPercent);
    }
    
    /**
     * Creates a new resistance with a different type but same percentage.
     * 
     * @param newType the new damage type
     * @return new resistance with updated type
     */
    public @NotNull Resistance withType(@NotNull DamageType newType) {
        return new Resistance(newType, percent);
    }
    
    @Override
    public @NotNull String toString() {
        return String.format("Resistance[%s: %.1f%%]", type, getPercentage());
    }
}