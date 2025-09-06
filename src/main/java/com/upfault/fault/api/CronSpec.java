package com.upfault.fault.api;

import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;

/**
 * Specification for cron-like scheduled tasks.
 * 
 * <p>This interface defines a contract for cron expression scheduling without
 * providing any implementation. The expression format and evaluation is left
 * to the implementing service, allowing for different cron dialects or
 * scheduling backends.
 * 
 * <p>Common cron expression formats include:
 * <ul>
 *   <li>Standard 5-field format: {@code minute hour day month weekday}
 *   <li>Extended 6-field format: {@code second minute hour day month weekday}
 *   <li>Spring/Quartz format with additional year field
 * </ul>
 * 
 * @since 0.0.1
 * @apiNote This interface is immutable and thread-safe
 */
public interface CronSpec {

    /**
     * Returns the cron expression string.
     *
     * <p>5-field: minute hour day-of-month month day-of-week.</p>
     * <ul>
     *   <li>{@code "0 0 * * *"} daily at midnight</li>
     *   <li>{@code "0/5 * * * *"} every 5 minutes</li>
     *   <li>{@code "0 9 * * 1-5"} weekdays at 09:00</li>
     *   <li>{@code "0 0 1 * *"} first day of each month</li>
     * </ul>
     *
     * @return non-empty cron expression
     */
    @NotNull String expression();

    /**
     * Gets the timezone for evaluating this cron expression.
     * 
     * <p>Cron expressions are timezone-sensitive, especially for schedules
     * that run at specific times of day. This timezone is used to determine
     * when the expression should trigger relative to wall-clock time.
     * 
     * <p>Common timezone considerations:
     * <ul>
     *   <li>Server timezone vs user timezone
     *   <li>Daylight saving time transitions
     *   <li>Cross-timezone coordination
     * </ul>
     * 
     * @return the timezone for this schedule
     * @apiNote The returned ZoneId must not be null
     */
    @NotNull ZoneId zone();
    
    /**
     * Creates a simple cron specification with server timezone.
     * 
     * @param expression the cron expression
     * @return new cron specification
     * @throws IllegalArgumentException if expression is null or empty
     */
    static @NotNull CronSpec of(@NotNull String expression) {
        return of(expression, ZoneId.systemDefault());
    }
    
    /**
     * Creates a cron specification with explicit timezone.
     * 
     * @param expression the cron expression
     * @param zone the timezone
     * @return new cron specification
     * @throws IllegalArgumentException if any parameter is null or expression is empty
     */
    static @NotNull CronSpec of(@NotNull String expression, @NotNull ZoneId zone) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Cron expression cannot be null or empty");
        }
        if (zone == null) {
            throw new IllegalArgumentException("Zone cannot be null");
        }
        
        return new CronSpec() {
            @Override
            public @NotNull String expression() {
                return expression;
            }
            
            @Override
            public @NotNull ZoneId zone() {
                return zone;
            }
            
            @Override
            public @NotNull String toString() {
                return "CronSpec[" + expression + " @ " + zone + "]";
            }
            
            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (!(obj instanceof CronSpec other)) return false;
                return expression.equals(other.expression()) && zone.equals(other.zone());
            }
            
            @Override
            public int hashCode() {
                return expression.hashCode() * 31 + zone.hashCode();
            }
        };
    }
}