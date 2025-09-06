package com.upfault.fault.api;

import com.upfault.fault.api.types.DurationTicks;
import com.upfault.fault.api.types.TickTime;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Provides server tick time utilities and conversions.
 * 
 * <p>This service abstracts the server's tick-based time system and provides
 * utilities for converting between real-world durations and server ticks.
 * 
 * <p>Example usage:
 * <pre>{@code
 * TimeService timeService = Fault.service(TimeService.class);
 * 
 * // Convert 5 seconds to ticks
 * DurationTicks fiveSecondTicks = timeService.toTicks(Duration.ofSeconds(5));
 * 
 * // Get current server tick
 * TickTime currentTick = timeService.getCurrentTick();
 * 
 * // Schedule something for 100 ticks from now
 * TickTime future = timeService.ticksFromNow(100);
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All methods are safe to call from any thread.
 * Current tick values are monotonic and increase by approximately 20 per second
 * under normal server performance.
 * 
 * @since 0.0.1
 * @apiNote This service uses the server's internal tick counter as the source of truth
 */
public interface TimeService {

    /**
     * Gets the current server tick.
     * 
     * <p>This is a monotonic counter that increases by approximately 20 per second.
     * The exact rate depends on server performance and TPS.
     * 
     * @return the current server tick
     * @since 0.0.1
     */
    @NotNull
    TickTime getCurrentTick();

    /**
     * Converts a real-world duration to server ticks.
     * 
     * <p>Uses the standard 20 TPS rate for conversion. This may not reflect
     * actual server performance but provides a consistent baseline.
     * 
     * @param duration the duration to convert
     * @return equivalent duration in ticks
     * @since 0.0.1
     */
    @NotNull
    DurationTicks toTicks(@NotNull Duration duration);

    /**
     * Converts server ticks to a real-world duration.
     * 
     * <p>Uses the standard 20 TPS rate for conversion.
     * 
     * @param ticks the tick count to convert
     * @return equivalent real-world duration
     * @since 0.0.1
     */
    @NotNull
    Duration toDuration(@NotNull DurationTicks ticks);

    /**
     * Calculates a tick time in the future.
     * 
     * @param ticks number of ticks to add to current time
     * @return future tick time
     * @since 0.0.1
     */
    @NotNull
    TickTime ticksFromNow(long ticks);

    /**
     * Calculates a tick time in the past.
     * 
     * @param ticks number of ticks to subtract from current time
     * @return past tick time
     * @since 0.0.1
     */
    @NotNull
    TickTime ticksAgo(long ticks);

    /**
     * Calculates the difference between two tick times.
     * 
     * @param from the starting tick time
     * @param to the ending tick time
     * @return the difference in ticks (positive if 'to' is after 'from')
     * @since 0.0.1
     */
    long ticksBetween(@NotNull TickTime from, @NotNull TickTime to);

    /**
     * Checks if a tick time has passed.
     * 
     * @param tickTime the tick time to check
     * @return true if the current tick is at or after the specified time
     * @since 0.0.1
     */
    boolean hasTickPassed(@NotNull TickTime tickTime);
}