package com.upfault.fault.api;

import com.upfault.fault.api.types.Subscription;
import org.jetbrains.annotations.NotNull;

/**
 * Service for scheduling tasks using cron-like expressions.
 * 
 * <p>This service provides cron-based task scheduling without providing
 * implementation details. It accepts {@link CronSpec} specifications and
 * executes the provided tasks according to the cron schedule.
 * 
 * <p>The scheduler handles timezone conversions, daylight saving time
 * transitions, and provides cancellation through the returned subscription.
 * Tasks are executed on a background thread pool to avoid blocking the
 * main server thread.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe. Scheduled tasks run on background threads.
 */
public interface CronScheduler {
    
    /**
     * Schedules a task to run according to a cron specification.
     * 
     * <p>The task will be executed every time the cron expression matches
     * the current time in the specified timezone. The task is executed
     * asynchronously on a background thread pool.
     * 
     * <p>The returned subscription can be used to cancel the scheduled task.
     * Once cancelled, the task will not be executed again, even if the cron
     * expression continues to match.
     * 
     * @param spec the cron specification defining when to run
     * @param task the task to execute (must be thread-safe)
     * @return subscription that can be used to cancel the scheduled task
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          The task must be thread-safe as it runs on background threads.
     */
    @NotNull Subscription schedule(@NotNull CronSpec spec, @NotNull Runnable task);
    
    /**
     * Schedules a task with error handling and retry logic.
     * 
     * <p>If the task throws an exception, it will be logged and the scheduler
     * will continue to execute the task on future scheduled times. The
     * retry policy determines how failed executions are handled.
     * 
     * @param spec the cron specification defining when to run
     * @param task the task to execute (must be thread-safe)
     * @param errorHandler handler for task execution errors
     * @return subscription that can be used to cancel the scheduled task
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Both task and errorHandler must be thread-safe.
     */
    @NotNull Subscription scheduleWithErrorHandling(
        @NotNull CronSpec spec,
        @NotNull Runnable task,
        @NotNull java.util.function.Consumer<Exception> errorHandler
    );
    
    /**
     * Gets information about all currently scheduled tasks.
     * 
     * @return list of information about active scheduled tasks
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull java.util.List<ScheduledTaskInfo> getActiveTasks();
    
    /**
     * Gets the next execution time for a cron expression.
     * 
     * <p>This is useful for testing cron expressions or displaying when
     * a task will next run. The calculation is performed in the timezone
     * specified in the cron spec.
     * 
     * @param spec the cron specification to evaluate
     * @return the next execution time, or empty if the expression will never match again
     * @throws IllegalArgumentException if spec is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull java.util.Optional<java.time.Instant> getNextExecution(@NotNull CronSpec spec);
    
    /**
     * Information about a scheduled task.
     * 
     * @param spec the cron specification for this task
     * @param createdAt when this task was scheduled
     * @param lastExecution the last time this task was executed, if any
     * @param nextExecution when this task will next execute, if calculable
     * @param executionCount how many times this task has been executed
     * @param errorCount how many times this task has thrown exceptions
     */
    record ScheduledTaskInfo(
        @NotNull CronSpec spec,
        @NotNull java.time.Instant createdAt,
        @NotNull java.util.Optional<java.time.Instant> lastExecution,
        @NotNull java.util.Optional<java.time.Instant> nextExecution,
        long executionCount,
        long errorCount
    ) {
        public ScheduledTaskInfo {
            if (spec == null) throw new IllegalArgumentException("Cron spec cannot be null");
            if (createdAt == null) throw new IllegalArgumentException("Created timestamp cannot be null");
            if (lastExecution == null) throw new IllegalArgumentException("Last execution optional cannot be null");
            if (nextExecution == null) throw new IllegalArgumentException("Next execution optional cannot be null");
            if (executionCount < 0) throw new IllegalArgumentException("Execution count cannot be negative");
            if (errorCount < 0) throw new IllegalArgumentException("Error count cannot be negative");
        }
        
        /**
         * Checks if this task has ever been executed.
         * 
         * @return true if the task has run at least once
         */
        public boolean hasExecuted() {
            return lastExecution.isPresent();
        }
        
        /**
         * Gets the success rate of this task (executions without errors).
         * 
         * @return success rate from 0.0 to 1.0
         */
        public double getSuccessRate() {
            if (executionCount == 0) return 1.0;
            return (double) (executionCount - errorCount) / executionCount;
        }
    }
}