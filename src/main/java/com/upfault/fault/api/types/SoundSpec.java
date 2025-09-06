package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Specification for sound effects with volume and pitch parameters.
 * 
 * <p>This record defines how sounds should be played, including the sound
 * type, volume level, and pitch adjustment. The implementation maps these
 * specifications to the appropriate Minecraft sound system calls.
 * 
 * @param key the namespaced identifier for the sound
 * @param volume the volume level (1.0 = normal, higher = louder)
 * @param pitch the pitch adjustment (1.0 = normal, higher = higher pitch)
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record SoundSpec(
    @NotNull NamespacedId key,
    float volume,
    float pitch
) {
    
    /**
     * Compact constructor with validation.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public SoundSpec {
        if (key == null) {
            throw new IllegalArgumentException("Sound key cannot be null");
        }
        if (Float.isNaN(volume) || Float.isInfinite(volume) || volume < 0) {
            throw new IllegalArgumentException("Volume must be a non-negative finite number, got: " + volume);
        }
        if (Float.isNaN(pitch) || Float.isInfinite(pitch) || pitch <= 0) {
            throw new IllegalArgumentException("Pitch must be a positive finite number, got: " + pitch);
        }
    }
    
    /**
     * Creates a sound spec with normal volume and pitch.
     * 
     * @param key the sound identifier
     * @return new sound spec with volume=1.0 and pitch=1.0
     */
    public static @NotNull SoundSpec normal(@NotNull NamespacedId key) {
        return new SoundSpec(key, 1.0f, 1.0f);
    }
    
    /**
     * Creates a sound spec with custom volume but normal pitch.
     * 
     * @param key the sound identifier
     * @param volume the volume level
     * @return new sound spec with specified volume and pitch=1.0
     */
    public static @NotNull SoundSpec withVolume(@NotNull NamespacedId key, float volume) {
        return new SoundSpec(key, volume, 1.0f);
    }
    
    /**
     * Creates a sound spec with custom pitch but normal volume.
     * 
     * @param key the sound identifier
     * @param pitch the pitch level
     * @return new sound spec with volume=1.0 and specified pitch
     */
    public static @NotNull SoundSpec withPitch(@NotNull NamespacedId key, float pitch) {
        return new SoundSpec(key, 1.0f, pitch);
    }
    
    /**
     * Creates a copy with different volume.
     * 
     * @param newVolume the new volume level
     * @return new sound spec with updated volume
     * @throws IllegalArgumentException if newVolume is negative or not finite
     */
    public @NotNull SoundSpec withVolume(float newVolume) {
        return new SoundSpec(key, newVolume, pitch);
    }
    
    /**
     * Creates a copy with different pitch.
     * 
     * @param newPitch the new pitch level
     * @return new sound spec with updated pitch
     * @throws IllegalArgumentException if newPitch is not positive or not finite
     */
    public @NotNull SoundSpec withPitch(float newPitch) {
        return new SoundSpec(key, volume, newPitch);
    }
    
    /**
     * Checks if this sound is at normal volume (1.0).
     * 
     * @return true if volume is exactly 1.0
     */
    public boolean isNormalVolume() {
        return volume == 1.0f;
    }
    
    /**
     * Checks if this sound is at normal pitch (1.0).
     * 
     * @return true if pitch is exactly 1.0
     */
    public boolean isNormalPitch() {
        return pitch == 1.0f;
    }
}