package com.upfault.fault.api;

import com.upfault.fault.api.types.DurationTicks;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Provides scheduling contracts for main-thread and async execution.
 * 
 * <p>This facade abstracts the underlying scheduler implementation and provides
 * Folia-friendly scheduling patterns without implementing them directly.
 * 
 * <p>Example usage:
 * <pre>{@code
 * SchedulerFacade scheduler = Fault.service(SchedulerFacade.class);
 * 
 * // Run something on the main thread next tick
 * scheduler.runNextTick(() -> {
 *     // Main thread code here
 * });
 * 
 * // Run something async
 * CompletableFuture<String> result = scheduler.runAsync(() -> {
 *     // Background thread work
 *     return "completed";
 * });
 * 
 * // Schedule repeating task
 * TaskHandle handle = scheduler.runRepeating(
 *     Duration.ofSeconds(5), 
 *     () -> System.out.println("Every 5 seconds")
 * );
 * }</pre>
 * 
 * <p><strong>Threading:</strong> This service itself is thread-safe, but scheduled
 * tasks will run on the appropriate threads (main thread for sync tasks, 
 * background threads for async tasks).
 * 
 * @since 0.0.1
 * @apiNote Implementations should be prepared for Folia's regional threading model
 */
public interface SchedulerFacade {

    /**
     * Executes a task on the main thread during the next tick.
     * 
     * @param task the task to execute
     * @return handle to the scheduled task
     * @since 0.0.1
     */
    @NotNull
    TaskHandle runNextTick(@NotNull Runnable task);

    /**
     * Executes a task on the main thread after a delay.
     * 
     * @param delay the delay before execution
     * @param task the task to execute
     * @return handle to the scheduled task
     * @since 0.0.1
     */
    @NotNull
    TaskHandle runLater(@NotNull Duration delay, @NotNull Runnable task);

    /**
     * Executes a task on the main thread after a delay in ticks.
     * 
     * @param delay the delay in ticks before execution
     * @param task the task to execute
     * @return handle to the scheduled task
     * @since 0.0.1
     */
    @NotNull
    TaskHandle runLater(@NotNull DurationTicks delay, @NotNull Runnable task);

    /**
     * Executes a repeating task on the main thread.
     * 
     * @param period the time between executions
     * @param task the task to execute repeatedly
     * @return handle to the scheduled task
     * @since 0.0.1
     */
    @NotNull
    TaskHandle runRepeating(@NotNull Duration period, @NotNull Runnable task);

    /**
     * Executes a repeating task on the main thread with tick-based timing.
     * 
     * @param period the ticks between executions
     * @param task the task to execute repeatedly
     * @return handle to the scheduled task
     * @since 0.0.1
     */
    @NotNull
    TaskHandle runRepeating(@NotNull DurationTicks period, @NotNull Runnable task);

    /**
     * Executes a task asynchronously on a background thread.
     * 
     * @param task the task to execute
     * @param <T> the return type of the task
     * @return future representing the task result
     * @since 0.0.1
     */
    @NotNull
    <T> CompletableFuture<T> runAsync(@NotNull java.util.concurrent.Callable<T> task);

    /**
     * Executes a runnable task asynchronously on a background thread.
     * 
     * @param task the task to execute
     * @return future that completes when the task is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> runAsync(@NotNull Runnable task);

    /**
     * Executes an async task after a delay.
     * 
     * @param delay the delay before execution
     * @param task the task to execute
     * @param <T> the return type of the task
     * @return future representing the task result
     * @since 0.0.1
     */
    @NotNull
    <T> CompletableFuture<T> runAsyncLater(@NotNull Duration delay, @NotNull java.util.concurrent.Callable<T> task);

    /**
     * Handle to a scheduled task that allows cancellation and status checking.
     * 
     * @since 0.0.1
     */
    interface TaskHandle {
        
        /**
         * Cancels the scheduled task.
         * 
         * @return true if the task was successfully cancelled
         * @since 0.0.1
         */
        boolean cancel();
        
        /**
         * Checks if the task has been cancelled.
         * 
         * @return true if the task is cancelled
         * @since 0.0.1
         */
        boolean isCancelled();
        
        /**
         * Checks if the task has completed execution.
         * 
         * <p>For repeating tasks, this returns true only if the task
         * has been cancelled or failed permanently.
         * 
         * @return true if the task has finished
         * @since 0.0.1
         */
        boolean isDone();
    }
}