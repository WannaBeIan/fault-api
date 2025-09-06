package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents server uptime information.
 * 
 * @param seconds the number of seconds the server has been running
 * 
 * @since 0.0.1
 * @apiNote Uptime is calculated from server start time
 */
public record Uptime(long seconds) {
    
    /**
     * Creates a new uptime with validation.
     * 
     * @param seconds the number of seconds (must be non-negative)
     * @throws IllegalArgumentException if seconds is negative
     */
    public Uptime {
        if (seconds < 0) {
            throw new IllegalArgumentException("Uptime cannot be negative: " + seconds);
        }
    }
    
    /**
     * Converts this uptime to a Duration.
     * 
     * @return Duration representing the uptime
     */
    public @NotNull Duration toDuration() {
        return Duration.ofSeconds(seconds);
    }
    
    /**
     * Creates an Uptime from a Duration.
     * 
     * @param duration the duration
     * @return Uptime representing the duration
     */
    public static @NotNull Uptime fromDuration(@NotNull Duration duration) {
        return new Uptime(duration.toSeconds());
    }
    
    /**
     * Creates an Uptime from a start time.
     * 
     * @param startTime when the server started
     * @return Uptime calculated from the start time to now
     */
    public static @NotNull Uptime since(@NotNull Instant startTime) {
        Duration uptime = Duration.between(startTime, Instant.now());
        return new Uptime(uptime.toSeconds());
    }
    
    /**
     * Gets the uptime in minutes.
     * 
     * @return uptime in minutes
     */
    public long getMinutes() {
        return seconds / 60;
    }
    
    /**
     * Gets the uptime in hours.
     * 
     * @return uptime in hours
     */
    public long getHours() {
        return seconds / 3600;
    }
    
    /**
     * Gets the uptime in days.
     * 
     * @return uptime in days
     */
    public long getDays() {
        return seconds / 86400;
    }
    
    /**
     * Formats the uptime as a human-readable string.
     * 
     * @return formatted uptime string (e.g., "2 days, 3 hours, 45 minutes")
     */
    public @NotNull String toHumanReadable() {
        if (seconds < 60) {
            return seconds + " second" + (seconds == 1 ? "" : "s");
        }
        
        long days = getDays();
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        
        StringBuilder sb = new StringBuilder();
        
        if (days > 0) {
            sb.append(days).append(" day").append(days == 1 ? "" : "s");
        }
        
        if (hours > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(hours).append(" hour").append(hours == 1 ? "" : "s");
        }
        
        if (minutes > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(minutes).append(" minute").append(minutes == 1 ? "" : "s");
        }
        
        if (remainingSeconds > 0 && days == 0 && hours == 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(remainingSeconds).append(" second").append(remainingSeconds == 1 ? "" : "s");
        }
        
        return sb.toString();
    }
    
    /**
     * Formats the uptime in compact form (e.g., "2d 3h 45m").
     * 
     * @return compact uptime string
     */
    public @NotNull String toCompactString() {
        if (seconds < 60) {
            return seconds + "s";
        }
        
        long days = getDays();
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        
        StringBuilder sb = new StringBuilder();
        
        if (days > 0) {
            sb.append(days).append("d");
        }
        
        if (hours > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(hours).append("h");
        }
        
        if (minutes > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(minutes).append("m");
        }
        
        if (sb.length() == 0) {
            sb.append("0m");
        }
        
        return sb.toString();
    }
    
    @Override
    public @NotNull String toString() {
        return toHumanReadable();
    }
}
