package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for executing scripted sequences of steps for players.
 * 
 * <p>This service enables the creation and execution of timeline sequences
 * that can combine delays, sound effects, titles, and custom actions into
 * coordinated experiences. Timelines are useful for cutscenes, tutorials,
 * automated events, and complex player interactions.
 * 
 * <p>Timeline execution is asynchronous and can be cancelled if needed.
 * The service handles step scheduling, error recovery, and resource cleanup.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and return CompletableFutures for async operations
 */
public interface TimelineService {
    
    /**
     * Executes a timeline sequence for a specific player.
     * 
     * <p>The steps are executed in order, with each step completing before
     * the next one begins. The returned future completes when all steps
     * have finished or if execution is cancelled or fails.
     * 
     * @param player the player to execute the timeline for
     * @param steps the sequence of steps to execute
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null or steps is empty
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Timeline execution happens asynchronously.
     */
    @NotNull CompletableFuture<OperationResult> play(@NotNull UUID player, @NotNull List<Step> steps);
    
    /**
     * Executes a named timeline sequence for a player.
     * 
     * <p>This variant allows for timeline reuse and easier management of
     * complex sequences. Named timelines can be pre-registered and then
     * executed by name.
     * 
     * @param player the player to execute the timeline for
     * @param timelineId the identifier of the timeline to execute
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> play(@NotNull UUID player, @NotNull NamespacedId timelineId);
    
    /**
     * Cancels an actively running timeline for a player.
     * 
     * <p>If the player has a timeline currently executing, it will be
     * stopped immediately. Any in-progress steps will be allowed to
     * complete, but no further steps will be executed.
     * 
     * @param player the player whose timeline should be cancelled
     * @return future containing the operation result
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> cancel(@NotNull UUID player);
    
    /**
     * Checks if a player currently has a timeline executing.
     * 
     * @param player the player to check
     * @return future containing true if a timeline is currently running
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Boolean> isPlaying(@NotNull UUID player);
    
    /**
     * Registers a named timeline sequence for later execution.
     * 
     * <p>Named timelines can be reused across multiple executions and
     * make it easier to manage complex sequences. Once registered, the
     * timeline can be executed using {@link #play(UUID, NamespacedId)}.
     * 
     * @param timelineId the unique identifier for this timeline
     * @param steps the sequence of steps that make up this timeline
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null or steps is empty
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> registerTimeline(@NotNull NamespacedId timelineId, @NotNull List<Step> steps);
    
    /**
     * Unregisters a named timeline sequence.
     * 
     * @param timelineId the timeline to unregister
     * @return future containing the operation result
     * @throws IllegalArgumentException if timelineId is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> unregisterTimeline(@NotNull NamespacedId timelineId);
    
    /**
     * Gets information about a registered timeline.
     * 
     * @param timelineId the timeline to get information about
     * @return future containing timeline information, or empty if not found
     * @throws IllegalArgumentException if timelineId is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<java.util.Optional<TimelineInfo>> getTimeline(@NotNull NamespacedId timelineId);
    
    /**
     * Lists all registered timeline sequences.
     * 
     * @param page the page number (0-based)
     * @param size the page size (must be positive)
     * @return future containing the page of timeline information
     * @throws IllegalArgumentException if page is negative or size is non-positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Page<TimelineInfo>> getAllTimelines(int page, int size);
    
    /**
     * Gets the current execution status for a player's timeline.
     * 
     * @param player the player to get status for
     * @return future containing execution status, or empty if no timeline is running
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<java.util.Optional<ExecutionStatus>> getExecutionStatus(@NotNull UUID player);
    
    /**
     * Information about a registered timeline.
     * 
     * @param id the timeline identifier
     * @param stepCount number of steps in this timeline
     * @param registeredAt when this timeline was registered
     * @param executionCount how many times this timeline has been executed
     */
    record TimelineInfo(
        @NotNull NamespacedId id,
        int stepCount,
        @NotNull java.time.Instant registeredAt,
        long executionCount
    ) {
        public TimelineInfo {
            if (id == null) throw new IllegalArgumentException("Timeline ID cannot be null");
            if (stepCount < 0) throw new IllegalArgumentException("Step count cannot be negative");
            if (registeredAt == null) throw new IllegalArgumentException("Registered timestamp cannot be null");
            if (executionCount < 0) throw new IllegalArgumentException("Execution count cannot be negative");
        }
    }
    
    /**
     * Status of a timeline execution in progress.
     * 
     * @param player the player this execution is for
     * @param timelineId the timeline being executed, if named
     * @param currentStep the index of the currently executing step
     * @param totalSteps the total number of steps in this timeline
     * @param startedAt when this execution began
     */
    record ExecutionStatus(
        @NotNull UUID player,
        @NotNull java.util.Optional<NamespacedId> timelineId,
        int currentStep,
        int totalSteps,
        @NotNull java.time.Instant startedAt
    ) {
        public ExecutionStatus {
            if (player == null) throw new IllegalArgumentException("Player cannot be null");
            if (timelineId == null) throw new IllegalArgumentException("Timeline ID optional cannot be null");
            if (currentStep < 0) throw new IllegalArgumentException("Current step cannot be negative");
            if (totalSteps < 1) throw new IllegalArgumentException("Total steps must be positive");
            if (currentStep >= totalSteps) throw new IllegalArgumentException("Current step cannot exceed total steps");
            if (startedAt == null) throw new IllegalArgumentException("Started timestamp cannot be null");
        }
        
        /**
         * Gets the progress as a percentage.
         * 
         * @return progress from 0.0 to 1.0
         */
        public double getProgress() {
            return (double) currentStep / totalSteps;
        }
        
        /**
         * Gets how long this timeline has been executing.
         * 
         * @return duration since execution started
         */
        public @NotNull java.time.Duration getRuntime() {
            return java.time.Duration.between(startedAt, java.time.Instant.now());
        }
    }
}