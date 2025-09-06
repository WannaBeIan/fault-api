package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Service for playing sound effects to players.
 * 
 * <p>This service provides centralized sound playback functionality with
 * audience targeting and volume/pitch control. The implementation maps
 * sound specifications to the appropriate Minecraft sound system calls.
 * 
 * <p>Sounds are played based on audience selectors, allowing for targeted
 * audio effects that are only heard by specific players or groups.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and may be called from any thread
 */
public interface SoundService {
    
    /**
     * Plays a sound effect for the selected audience.
     * 
     * <p>The sound is played at the location specified in the coordinates
     * with the volume and pitch defined in the sound specification.
     * 
     * @param spec the sound specification
     * @param location the world coordinates where the sound should originate
     * @param to the audience selector for targeting players
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void play(@NotNull SoundSpec spec, @NotNull Coordinates location, @NotNull AudienceSelector to);
    
    /**
     * Plays a sound effect directly to players without a world location.
     * 
     * <p>This plays the sound directly to each selected player without
     * positional audio or distance attenuation effects.
     * 
     * @param spec the sound specification
     * @param to the audience selector for targeting players
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void play(@NotNull SoundSpec spec, @NotNull AudienceSelector to);
    
    /**
     * Plays a sound for all players in the world.
     * 
     * <p>This is a convenience method for broadcasting sounds to all online
     * players at the specified location.
     * 
     * @param spec the sound specification
     * @param location the world coordinates where the sound should originate
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void playForAll(@NotNull SoundSpec spec, @NotNull Coordinates location);
    
    /**
     * Plays a sound sequence with timing control.
     * 
     * <p>Plays multiple sounds in sequence with specified delays between them.
     * This is useful for creating complex audio effects or musical sequences.
     * 
     * @param sounds list of sound specifications and their delays
     * @param location the world coordinates where sounds should originate
     * @param to the audience selector for targeting players
     * @return future that completes when all sounds have been played
     * @throws IllegalArgumentException if any parameter is null or sounds list is empty
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Sound playback happens asynchronously.
     */
    @NotNull CompletableFuture<Void> playSequence(
        @NotNull java.util.List<TimedSound> sounds,
        @NotNull Coordinates location,
        @NotNull AudienceSelector to
    );
    
    /**
     * Stops all sounds of a specific type for the selected audience.
     * 
     * <p>This can be used to interrupt looping sounds or cancel ongoing
     * audio effects. Note that this only works for sounds that support
     * being stopped (implementation-dependent).
     * 
     * @param soundKey the identifier of the sound type to stop
     * @param to the audience selector for targeting players
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Not all sound types support being stopped.
     */
    void stop(@NotNull NamespacedId soundKey, @NotNull AudienceSelector to);
    
    /**
     * Checks if a sound type is supported by the server.
     * 
     * @param soundKey the sound identifier to check
     * @return true if the sound is available
     * @throws IllegalArgumentException if soundKey is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    boolean isSoundSupported(@NotNull NamespacedId soundKey);
    
    /**
     * Gets a list of all supported sound types.
     * 
     * @return list of supported sound identifiers
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull java.util.List<NamespacedId> getSupportedSounds();
    
    /**
     * Represents a sound with a delay before playing.
     * 
     * @param spec the sound specification
     * @param delay how long to wait before playing this sound
     */
    record TimedSound(
        @NotNull SoundSpec spec,
        @NotNull java.time.Duration delay
    ) {
        public TimedSound {
            if (spec == null) {
                throw new IllegalArgumentException("Sound spec cannot be null");
            }
            if (delay == null) {
                throw new IllegalArgumentException("Delay cannot be null");
            }
            if (delay.isNegative()) {
                throw new IllegalArgumentException("Delay cannot be negative");
            }
        }
        
        /**
         * Creates a timed sound with no delay.
         * 
         * @param spec the sound specification
         * @return timed sound with zero delay
         */
        public static @NotNull TimedSound immediate(@NotNull SoundSpec spec) {
            return new TimedSound(spec, java.time.Duration.ZERO);
        }
        
        /**
         * Creates a timed sound with a delay in milliseconds.
         * 
         * @param spec the sound specification
         * @param delayMs the delay in milliseconds
         * @return timed sound with specified delay
         */
        public static @NotNull TimedSound delayed(@NotNull SoundSpec spec, long delayMs) {
            return new TimedSound(spec, java.time.Duration.ofMillis(delayMs));
        }
    }
}