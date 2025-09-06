package com.upfault.fault.api.types;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Sealed interface representing individual steps in a timeline sequence.
 * 
 * <p>Timeline steps are the building blocks of scripted sequences that can
 * be executed for players. Each step type provides different functionality,
 * from simple delays to complex interactions.
 * 
 * <p>Steps are designed to be composable and serializable, allowing for
 * complex timeline sequences to be built, stored, and executed reliably.
 * 
 * @since 0.0.1
 * @apiNote This sealed interface permits only the defined step implementations
 */
public sealed interface Step 
    permits Step.Wait, Step.PlaySound, Step.ShowTitle, Step.RunAction {
    
    /**
     * A step that introduces a delay in timeline execution.
     * 
     * @param duration how long to wait before continuing to the next step
     */
    record Wait(@NotNull Duration duration) implements Step {
        public Wait {
            if (duration == null) {
                throw new IllegalArgumentException("Duration cannot be null");
            }
            if (duration.isNegative()) {
                throw new IllegalArgumentException("Duration cannot be negative");
            }
        }
        
        /**
         * Creates a wait step with the specified number of ticks.
         * 
         * @param ticks the number of ticks to wait (20 ticks = 1 second)
         * @return new wait step
         * @throws IllegalArgumentException if ticks is negative
         */
        public static @NotNull Wait ticks(long ticks) {
            if (ticks < 0) {
                throw new IllegalArgumentException("Ticks cannot be negative");
            }
            return new Wait(Duration.ofMillis(ticks * 50)); // 50ms per tick
        }
        
        /**
         * Creates a wait step with the specified number of seconds.
         * 
         * @param seconds the number of seconds to wait
         * @return new wait step
         * @throws IllegalArgumentException if seconds is negative
         */
        public static @NotNull Wait seconds(long seconds) {
            if (seconds < 0) {
                throw new IllegalArgumentException("Seconds cannot be negative");
            }
            return new Wait(Duration.ofSeconds(seconds));
        }
    }
    
    /**
     * A step that plays a sound effect.
     * 
     * @param sound the sound specification to play
     */
    record PlaySound(@NotNull SoundSpec sound) implements Step {
        public PlaySound {
            if (sound == null) {
                throw new IllegalArgumentException("Sound spec cannot be null");
            }
        }
        
        /**
         * Creates a play sound step with normal volume and pitch.
         * 
         * @param soundKey the sound identifier
         * @return new play sound step
         */
        public static @NotNull PlaySound of(@NotNull NamespacedId soundKey) {
            return new PlaySound(SoundSpec.normal(soundKey));
        }
    }
    
    /**
     * A step that displays a title and subtitle to the player.
     * 
     * @param title the main title text
     * @param subtitle the subtitle text (can be empty)
     * @param fadeIn duration in ticks for fade-in animation
     * @param stay duration in ticks to display the title
     * @param fadeOut duration in ticks for fade-out animation
     */
    record ShowTitle(
        @NotNull Component title,
        @NotNull Component subtitle,
        int fadeIn,
        int stay,
        int fadeOut
    ) implements Step {
        public ShowTitle {
            if (title == null) {
                throw new IllegalArgumentException("Title cannot be null");
            }
            if (subtitle == null) {
                throw new IllegalArgumentException("Subtitle cannot be null");
            }
            if (fadeIn < 0) {
                throw new IllegalArgumentException("Fade in cannot be negative");
            }
            if (stay < 0) {
                throw new IllegalArgumentException("Stay duration cannot be negative");
            }
            if (fadeOut < 0) {
                throw new IllegalArgumentException("Fade out cannot be negative");
            }
        }
        
        /**
         * Creates a show title step with default timing.
         * 
         * @param title the title text
         * @param subtitle the subtitle text
         * @return new show title step with default timing (10, 70, 20)
         */
        public static @NotNull ShowTitle of(@NotNull Component title, @NotNull Component subtitle) {
            return new ShowTitle(title, subtitle, 10, 70, 20);
        }
        
        /**
         * Creates a show title step with only a title and default timing.
         * 
         * @param title the title text
         * @return new show title step with empty subtitle and default timing
         */
        public static @NotNull ShowTitle of(@NotNull Component title) {
            return new ShowTitle(title, Component.empty(), 10, 70, 20);
        }
    }
    
    /**
     * A step that executes an arbitrary action.
     * 
     * <p>This provides an extension point for custom timeline behaviors
     * that don't fit into the predefined step types. The action is
     * identified by a namespaced ID that implementations can use to
     * dispatch to appropriate handlers.
     * 
     * @param actionId the identifier for the action to execute
     * @param parameters optional parameters for the action
     */
    record RunAction(
        @NotNull NamespacedId actionId,
        @NotNull java.util.Map<String, Object> parameters
    ) implements Step {
        public RunAction {
            if (actionId == null) {
                throw new IllegalArgumentException("Action ID cannot be null");
            }
            if (parameters == null) {
                throw new IllegalArgumentException("Parameters map cannot be null");
            }
            // Defensive copy
            parameters = java.util.Map.copyOf(parameters);
        }
        
        /**
         * Creates a run action step with no parameters.
         * 
         * @param actionId the action identifier
         * @return new run action step with empty parameters
         */
        public static @NotNull RunAction of(@NotNull NamespacedId actionId) {
            return new RunAction(actionId, java.util.Map.of());
        }
        
        /**
         * Creates a run action step with parameters.
         * 
         * @param actionId the action identifier
         * @param parameters the action parameters
         * @return new run action step
         */
        public static @NotNull RunAction of(@NotNull NamespacedId actionId, @NotNull java.util.Map<String, Object> parameters) {
            return new RunAction(actionId, parameters);
        }
    }
}