package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Represents a duration measured in server ticks.
 * 
 * <p>Server ticks are the fundamental time unit in Minecraft, with servers
 * targeting 20 ticks per second under optimal conditions.
 * 
 * @param ticks the number of ticks
 * 
 * @since 0.0.1
 * @apiNote 1 second = 20 ticks under optimal server performance
 */
public record DurationTicks(long ticks) {
    
    /**
     * Creates a new duration in ticks with validation.
     * 
     * @param ticks the number of ticks (must be non-negative)
     * @throws IllegalArgumentException if ticks is negative
     */
    public DurationTicks {
        if (ticks < 0) {
            throw new IllegalArgumentException("Ticks cannot be negative: " + ticks);
        }
    }
    
    /**
     * Converts this tick duration to a real-world Duration.
     * 
     * <p>Uses the standard 20 TPS rate for conversion.
     * 
     * @return equivalent Duration (50ms per tick)
     */
    public @NotNull Duration toDuration() {
        return Duration.ofMillis(ticks * 50); // 50ms per tick at 20 TPS
    }
    
    /**
     * Creates a DurationTicks from a real-world Duration.
     * 
     * <p>Uses the standard 20 TPS rate for conversion.
     * 
     * @param duration the duration to convert
     * @return equivalent DurationTicks
     */
    public static @NotNull DurationTicks fromDuration(@NotNull Duration duration) {
        return new DurationTicks(duration.toMillis() / 50); // 50ms per tick at 20 TPS
    }
    
    /**
     * Creates a DurationTicks from seconds.
     * 
     * @param seconds the number of seconds
     * @return equivalent DurationTicks (seconds * 20)
     */
    public static @NotNull DurationTicks ofSeconds(long seconds) {
        return new DurationTicks(seconds * 20);
    }
    
    /**
     * Creates a DurationTicks from minutes.
     * 
     * @param minutes the number of minutes
     * @return equivalent DurationTicks (minutes * 1200)
     */
    public static @NotNull DurationTicks ofMinutes(long minutes) {
        return new DurationTicks(minutes * 1200); // 60 seconds * 20 ticks
    }
    
    /**
     * Adds another DurationTicks to this one.
     * 
     * @param other the duration to add
     * @return new DurationTicks with the sum
     */
    public @NotNull DurationTicks plus(@NotNull DurationTicks other) {
        return new DurationTicks(this.ticks + other.ticks);
    }
    
    /**
     * Subtracts another DurationTicks from this one.
     * 
     * @param other the duration to subtract
     * @return new DurationTicks with the difference
     * @throws IllegalArgumentException if result would be negative
     */
    public @NotNull DurationTicks minus(@NotNull DurationTicks other) {
        long result = this.ticks - other.ticks;
        if (result < 0) {
            throw new IllegalArgumentException("Subtraction would result in negative ticks");
        }
        return new DurationTicks(result);
    }
    
    /**
     * Multiplies this duration by a scalar.
     * 
     * @param multiplier the multiplier
     * @return new DurationTicks multiplied by the scalar
     * @throws IllegalArgumentException if multiplier is negative
     */
    public @NotNull DurationTicks multiply(long multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative: " + multiplier);
        }
        return new DurationTicks(this.ticks * multiplier);
    }
    
    /**
     * Checks if this duration is zero.
     * 
     * @return true if ticks is 0
     */
    public boolean isZero() {
        return ticks == 0;
    }
    
    /**
     * Compares this duration to another.
     * 
     * @param other the other duration
     * @return negative if this is less, 0 if equal, positive if greater
     */
    public int compareTo(@NotNull DurationTicks other) {
        return Long.compare(this.ticks, other.ticks);
    }
    
    @Override
    public @NotNull String toString() {
        if (ticks == 0) {
            return "0 ticks";
        } else if (ticks == 1) {
            return "1 tick";
        } else if (ticks % 20 == 0) {
            long seconds = ticks / 20;
            return seconds + " second" + (seconds == 1 ? "" : "s") + " (" + ticks + " ticks)";
        } else {
            return ticks + " ticks";
        }
    }
}
