package com.upfault.fault.api.types;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a player achievement with identification and display information.
 * 
 * <p>Achievements are badges or rewards that players can earn by completing
 * specific criteria or reaching milestones. Each achievement has a unique
 * identifier for tracking purposes and display information for showing to
 * players.
 * 
 * @param id the unique identifier for this achievement
 * @param name the display name shown to players
 * @param description a longer description of what the achievement represents
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record Achievement(
    @NotNull NamespacedId id,
    @NotNull Component name,
    @NotNull Component description
) {
    
    /**
     * Compact constructor with validation.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public Achievement {
        if (id == null) {
            throw new IllegalArgumentException("Achievement ID cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Achievement name cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Achievement description cannot be null");
        }
    }
    
    /**
     * Creates an achievement with text components.
     * 
     * @param id the achievement identifier
     * @param name the display name as plain text
     * @param description the description as plain text
     * @return new achievement with text components
     */
    public static @NotNull Achievement of(@NotNull NamespacedId id, @NotNull String name, @NotNull String description) {
        return new Achievement(
            id,
            Component.text(name),
            Component.text(description)
        );
    }
}