package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Immutable state representing a cooldown or warmup period.
 * 
 * <p>CooldownState tracks the timing information for actions that have
 * cooldown periods, warmup delays, or other time-based restrictions.
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Create an active cooldown
 * CooldownState cooldown = CooldownState.active(
 *     Duration.ofMinutes(5), // total duration
 *     Duration.ofSeconds(30) // remaining time
 * );
 * 
 * if (cooldown.isActive()) {
 *     long secondsLeft = cooldown.getRemainingTime().toSeconds();
 *     player.sendMessage("Cooldown active: " + secondsLeft + " seconds remaining");
 * }
 * 
 * // Check if cooldown expired
 * if (cooldown.hasExpired()) {
 *     // Allow action to proceed
 * }
 * 
 * // Create a warmup period
 * CooldownState warmup = CooldownState.warmup(Duration.ofSeconds(3));
 * if (warmup.isWarmingUp()) {
 *     player.sendMessage("Preparing action...");
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> CooldownState is immutable and thread-safe.
 * 
 * @since 0.0.1
 * @apiNote All time calculations are based on the state creation time
 */
public final class CooldownState {
    
    private final Type type;
    private final Duration totalDuration;
    private final Instant startTime;
    private final Instant endTime;
    private final String reason;
    
    private CooldownState(@NotNull Type type, @NotNull Duration totalDuration, @NotNull Instant startTime, @Nullable String reason) {
        this.type = Objects.requireNonNull(type, "Cooldown type cannot be null");
        this.totalDuration = Objects.requireNonNull(totalDuration, "Total duration cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time cannot be null");
        this.endTime = startTime.plus(totalDuration);
        this.reason = reason;
        
        if (totalDuration.isNegative() || totalDuration.isZero()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
    }
    
    /**
     * Creates an inactive cooldown state.
     * 
     * @return inactive cooldown state
     */
    public static @NotNull CooldownState inactive() {
        return new CooldownState(Type.INACTIVE, Duration.ZERO, Instant.EPOCH, null);
    }
    
    /**
     * Creates an active cooldown with the specified total and remaining duration.
     * 
     * @param totalDuration the total cooldown duration
     * @param remainingDuration the remaining cooldown duration
     * @return active cooldown state
     * @throws IllegalArgumentException if durations are invalid
     */
    public static @NotNull CooldownState active(@NotNull Duration totalDuration, @NotNull Duration remainingDuration) {
        if (remainingDuration.compareTo(totalDuration) > 0) {
            throw new IllegalArgumentException("Remaining duration cannot exceed total duration");
        }
        Instant now = Instant.now();
        Instant startTime = now.minus(totalDuration).plus(remainingDuration);
        return new CooldownState(Type.COOLDOWN, totalDuration, startTime, null);
    }
    
    /**
     * Creates an active cooldown starting now.
     * 
     * @param duration the cooldown duration
     * @return active cooldown state
     * @throws IllegalArgumentException if duration is not positive
     */
    public static @NotNull CooldownState cooldown(@NotNull Duration duration) {
        return new CooldownState(Type.COOLDOWN, duration, Instant.now(), null);
    }
    
    /**
     * Creates an active cooldown with a reason.
     * 
     * @param duration the cooldown duration
     * @param reason the reason for the cooldown
     * @return active cooldown state
     * @throws IllegalArgumentException if duration is not positive
     */
    public static @NotNull CooldownState cooldown(@NotNull Duration duration, @NotNull String reason) {
        return new CooldownState(Type.COOLDOWN, duration, Instant.now(), reason);
    }
    
    /**
     * Creates an active warmup period starting now.
     * 
     * @param duration the warmup duration
     * @return active warmup state
     * @throws IllegalArgumentException if duration is not positive
     */
    public static @NotNull CooldownState warmup(@NotNull Duration duration) {
        return new CooldownState(Type.WARMUP, duration, Instant.now(), null);
    }
    
    /**
     * Creates an active warmup period with a reason.
     * 
     * @param duration the warmup duration
     * @param reason the reason for the warmup
     * @return active warmup state
     * @throws IllegalArgumentException if duration is not positive
     */
    public static @NotNull CooldownState warmup(@NotNull Duration duration, @NotNull String reason) {
        return new CooldownState(Type.WARMUP, duration, Instant.now(), reason);
    }
    
    /**
     * Creates a cooldown from start and end times.
     * 
     * @param startTime when the cooldown started
     * @param endTime when the cooldown ends
     * @return cooldown state
     * @throws IllegalArgumentException if end time is before start time
     */
    public static @NotNull CooldownState fromTimes(@NotNull Instant startTime, @NotNull Instant endTime) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        Duration duration = Duration.between(startTime, endTime);
        return new CooldownState(Type.COOLDOWN, duration, startTime, null);
    }
    
    /**
     * Gets the cooldown type.
     * 
     * @return the cooldown type
     */
    public @NotNull Type getType() {
        return type;
    }
    
    /**
     * Gets the total duration of this cooldown.
     * 
     * @return the total duration
     */
    public @NotNull Duration getTotalDuration() {
        return totalDuration;
    }
    
    /**
     * Gets when this cooldown started.
     * 
     * @return the start time
     */
    public @NotNull Instant getStartTime() {
        return startTime;
    }
    
    /**
     * Gets when this cooldown ends.
     * 
     * @return the end time
     */
    public @NotNull Instant getEndTime() {
        return endTime;
    }
    
    /**
     * Gets the reason for this cooldown.
     * 
     * @return the reason, or null if not specified
     */
    public @Nullable String getReason() {
        return reason;
    }
    
    /**
     * Gets the remaining duration based on current time.
     * 
     * @return remaining duration, or zero if expired
     */
    public @NotNull Duration getRemainingTime() {
        if (type == Type.INACTIVE) {
            return Duration.ZERO;
        }
        
        Instant now = Instant.now();
        if (now.isAfter(endTime)) {
            return Duration.ZERO;
        }
        
        return Duration.between(now, endTime);
    }
    
    /**
     * Gets the elapsed duration based on current time.
     * 
     * @return elapsed duration, capped at total duration
     */
    public @NotNull Duration getElapsedTime() {
        if (type == Type.INACTIVE) {
            return Duration.ZERO;
        }
        
        Instant now = Instant.now();
        if (now.isBefore(startTime)) {
            return Duration.ZERO;
        }
        
        Duration elapsed = Duration.between(startTime, now);
        return elapsed.compareTo(totalDuration) > 0 ? totalDuration : elapsed;
    }
    
    /**
     * Gets the completion percentage (0.0 to 1.0).
     * 
     * @return completion percentage
     */
    public double getCompletionPercentage() {
        if (type == Type.INACTIVE || totalDuration.isZero()) {
            return 1.0;
        }
        
        Duration elapsed = getElapsedTime();
        return Math.min(1.0, (double) elapsed.toMillis() / totalDuration.toMillis());
    }
    
    /**
     * Checks if this cooldown is currently active.
     * 
     * @return true if active and not expired
     */
    public boolean isActive() {
        return type != Type.INACTIVE && !hasExpired();
    }
    
    /**
     * Checks if this cooldown is inactive.
     * 
     * @return true if no cooldown is in effect
     */
    public boolean isInactive() {
        return type == Type.INACTIVE || hasExpired();
    }
    
    /**
     * Checks if this is a cooldown period.
     * 
     * @return true if type is COOLDOWN
     */
    public boolean isCooldown() {
        return type == Type.COOLDOWN;
    }
    
    /**
     * Checks if this is a warmup period.
     * 
     * @return true if type is WARMUP
     */
    public boolean isWarmup() {
        return type == Type.WARMUP;
    }
    
    /**
     * Checks if this is currently in a warmup state.
     * 
     * @return true if warmup and not expired
     */
    public boolean isWarmingUp() {
        return type == Type.WARMUP && !hasExpired();
    }
    
    /**
     * Checks if this cooldown has expired.
     * 
     * @return true if current time is after end time
     */
    public boolean hasExpired() {
        if (type == Type.INACTIVE) {
            return true;
        }
        return Instant.now().isAfter(endTime);
    }
    
    /**
     * Checks if this cooldown has a reason.
     * 
     * @return true if reason is not null
     */
    public boolean hasReason() {
        return reason != null;
    }
    
    /**
     * Creates a new cooldown state with a different reason.
     * 
     * @param newReason the new reason
     * @return cooldown state with updated reason
     */
    public @NotNull CooldownState withReason(@Nullable String newReason) {
        return new CooldownState(type, totalDuration, startTime, newReason);
    }
    
    /**
     * Creates a new cooldown state extending the duration.
     * 
     * @param additionalTime additional time to add
     * @return extended cooldown state
     * @throws IllegalArgumentException if additional time is negative
     */
    public @NotNull CooldownState extend(@NotNull Duration additionalTime) {
        if (additionalTime.isNegative()) {
            throw new IllegalArgumentException("Additional time cannot be negative");
        }
        
        if (type == Type.INACTIVE) {
            return this;
        }
        
        Duration newTotalDuration = totalDuration.plus(additionalTime);
        return new CooldownState(type, newTotalDuration, startTime, reason);
    }
    
    /**
     * Creates a new cooldown state reducing the duration.
     * 
     * @param reductionTime time to subtract from remaining duration
     * @return reduced cooldown state
     * @throws IllegalArgumentException if reduction time is negative
     */
    public @NotNull CooldownState reduce(@NotNull Duration reductionTime) {
        if (reductionTime.isNegative()) {
            throw new IllegalArgumentException("Reduction time cannot be negative");
        }
        
        if (type == Type.INACTIVE) {
            return this;
        }
        
        Duration remaining = getRemainingTime();
        if (reductionTime.compareTo(remaining) >= 0) {
            return inactive();
        }
        
        // Adjust start time to effectively reduce remaining duration
        Instant now = Instant.now();
        Duration newRemaining = remaining.minus(reductionTime);
        Instant newStartTime = now.minus(totalDuration).plus(newRemaining);
        
        return new CooldownState(type, totalDuration, newStartTime, reason);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CooldownState other)) return false;
        return type == other.type &&
               Objects.equals(totalDuration, other.totalDuration) &&
               Objects.equals(startTime, other.startTime) &&
               Objects.equals(reason, other.reason);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, totalDuration, startTime, reason);
    }
    
    @Override
    public @NotNull String toString() {
        if (type == Type.INACTIVE) {
            return "CooldownState[INACTIVE]";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("CooldownState[").append(type.name());
        sb.append(", total=").append(formatDuration(totalDuration));
        sb.append(", remaining=").append(formatDuration(getRemainingTime()));
        
        if (hasReason()) {
            sb.append(", reason='").append(reason).append("'");
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    private static String formatDuration(@NotNull Duration duration) {
        if (duration.isZero()) {
            return "0s";
        }
        
        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long remainingSeconds = seconds % 60;
            return hours + "h " + minutes + "m " + remainingSeconds + "s";
        }
    }
    
    /**
     * Cooldown state types.
     * 
     * @since 0.0.1
     */
    public enum Type {
        /**
         * No cooldown is active.
         */
        INACTIVE,
        
        /**
         * Cooldown period preventing action.
         */
        COOLDOWN,
        
        /**
         * Warmup period before action executes.
         */
        WARMUP
    }
}