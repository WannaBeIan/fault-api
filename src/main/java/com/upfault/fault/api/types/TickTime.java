package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a specific point in server tick time.
 * 
 * <p>Server ticks are monotonic and increase by approximately 20 per second.
 * This provides a stable time reference that is independent of system clock changes.
 * 
 * @param tick the server tick number
 * 
 * @since 0.0.1
 * @apiNote Tick values are monotonic and always increasing
 */
public record TickTime(long tick) {
    
    /**
     * Creates a new tick time with validation.
     * 
     * @param tick the tick number (must be non-negative)
     * @throws IllegalArgumentException if tick is negative
     */
    public TickTime {
        if (tick < 0) {
            throw new IllegalArgumentException("Tick cannot be negative: " + tick);
        }
    }
    
    /**
     * Adds a number of ticks to this tick time.
     * 
     * @param ticks the number of ticks to add
     * @return new TickTime with the added ticks
     * @throws IllegalArgumentException if ticks is negative
     */
    public @NotNull TickTime plus(long ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Cannot add negative ticks: " + ticks);
        }
        return new TickTime(this.tick + ticks);
    }
    
    /**
     * Adds a duration in ticks to this tick time.
     * 
     * @param duration the duration to add
     * @return new TickTime with the added duration
     */
    public @NotNull TickTime plus(@NotNull DurationTicks duration) {
        return new TickTime(this.tick + duration.ticks());
    }
    
    /**
     * Subtracts a number of ticks from this tick time.
     * 
     * @param ticks the number of ticks to subtract
     * @return new TickTime with the subtracted ticks
     * @throws IllegalArgumentException if ticks is negative or result would be negative
     */
    public @NotNull TickTime minus(long ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Cannot subtract negative ticks: " + ticks);
        }
        long result = this.tick - ticks;
        if (result < 0) {
            throw new IllegalArgumentException("Subtraction would result in negative tick time");
        }
        return new TickTime(result);
    }
    
    /**
     * Subtracts a duration in ticks from this tick time.
     * 
     * @param duration the duration to subtract
     * @return new TickTime with the subtracted duration
     * @throws IllegalArgumentException if result would be negative
     */
    public @NotNull TickTime minus(@NotNull DurationTicks duration) {
        long result = this.tick - duration.ticks();
        if (result < 0) {
            throw new IllegalArgumentException("Subtraction would result in negative tick time");
        }
        return new TickTime(result);
    }
    
    /**
     * Calculates the difference between this and another tick time.
     * 
     * @param other the other tick time
     * @return the difference in ticks (positive if this is later)
     */
    public long ticksSince(@NotNull TickTime other) {
        return this.tick - other.tick;
    }
    
    /**
     * Calculates the duration between this and another tick time.
     * 
     * @param other the other tick time
     * @return the duration between the two tick times
     * @throws IllegalArgumentException if other is after this tick time
     */
    public @NotNull DurationTicks durationSince(@NotNull TickTime other) {
        long difference = this.tick - other.tick;
        if (difference < 0) {
            throw new IllegalArgumentException("Other tick time is after this one");
        }
        return new DurationTicks(difference);
    }
    
    /**
     * Checks if this tick time is before another.
     * 
     * @param other the other tick time
     * @return true if this tick time is before the other
     */
    public boolean isBefore(@NotNull TickTime other) {
        return this.tick < other.tick;
    }
    
    /**
     * Checks if this tick time is after another.
     * 
     * @param other the other tick time
     * @return true if this tick time is after the other
     */
    public boolean isAfter(@NotNull TickTime other) {
        return this.tick > other.tick;
    }
    
    /**
     * Checks if this tick time is at or before another.
     * 
     * @param other the other tick time
     * @return true if this tick time is at or before the other
     */
    public boolean isAtOrBefore(@NotNull TickTime other) {
        return this.tick <= other.tick;
    }
    
    /**
     * Checks if this tick time is at or after another.
     * 
     * @param other the other tick time
     * @return true if this tick time is at or after the other
     */
    public boolean isAtOrAfter(@NotNull TickTime other) {
        return this.tick >= other.tick;
    }
    
    /**
     * Compares this tick time to another.
     * 
     * @param other the other tick time
     * @return negative if this is earlier, 0 if equal, positive if later
     */
    public int compareTo(@NotNull TickTime other) {
        return Long.compare(this.tick, other.tick);
    }
    
    @Override
    public @NotNull String toString() {
        return "tick " + tick;
    }
}
