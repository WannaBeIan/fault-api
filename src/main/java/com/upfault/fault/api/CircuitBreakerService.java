package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Service for circuit breaker pattern implementation to protect against cascading failures.
 * 
 * <p>This service provides circuit breaker functionality to protect systems from
 * repeated calls to failing external services or operations. When failures exceed
 * a threshold, the circuit breaker "opens" and fails fast, preventing resource
 * exhaustion and allowing time for recovery.
 * 
 * <p>Circuit breakers have three states:
 * <ul>
 *   <li>CLOSED: Normal operation, requests pass through
 *   <li>OPEN: Fast-fail mode, requests are immediately rejected
 *   <li>HALF_OPEN: Testing recovery, limited requests allowed through
 * </ul>
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and return CompletableFutures for async operations
 */
public interface CircuitBreakerService {
    
    /**
     * Gets the current state of a circuit breaker.
     * 
     * @param key the unique identifier for the circuit breaker
     * @return the current state of the circuit breaker
     * @throws IllegalArgumentException if key is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull BreakerState state(@NotNull NamespacedId key);
    
    /**
     * Executes an operation through a circuit breaker.
     * 
     * <p>If the circuit breaker is closed or half-open, the operation is
     * executed. If it's open, the operation is not executed and a failure
     * result is returned immediately.
     * 
     * <p>The success or failure of the operation affects the circuit breaker
     * state and failure counters.
     * 
     * @param key the unique identifier for the circuit breaker
     * @param operation the operation to execute
     * @param <T> the return type of the operation
     * @return future containing the result of the operation or circuit breaker decision
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    <T> @NotNull CompletableFuture<T> run(@NotNull NamespacedId key, @NotNull Supplier<CompletableFuture<T>> operation);
    
    /**
     * Configures a circuit breaker with custom parameters.
     * 
     * <p>If a circuit breaker with this key already exists, its configuration
     * is updated. Otherwise, a new circuit breaker is created.
     * 
     * @param key the unique identifier for the circuit breaker
     * @param config the configuration parameters
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> configure(@NotNull NamespacedId key, @NotNull BreakerConfig config);
    
    /**
     * Manually opens a circuit breaker (puts it in fail-fast mode).
     * 
     * <p>This can be used for administrative purposes or when external
     * monitoring detects issues that should trigger circuit breaker opening.
     * 
     * @param key the unique identifier for the circuit breaker
     * @return future containing the operation result
     * @throws IllegalArgumentException if key is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> open(@NotNull NamespacedId key);
    
    /**
     * Manually closes a circuit breaker (allows requests through).
     * 
     * <p>This resets the circuit breaker to normal operation. Use with
     * caution as it bypasses the automatic failure detection.
     * 
     * @param key the unique identifier for the circuit breaker
     * @return future containing the operation result
     * @throws IllegalArgumentException if key is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> close(@NotNull NamespacedId key);
    
    /**
     * Resets the failure counters for a circuit breaker.
     * 
     * <p>This clears the failure history but does not change the current
     * state of the circuit breaker.
     * 
     * @param key the unique identifier for the circuit breaker
     * @return future containing the operation result
     * @throws IllegalArgumentException if key is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> reset(@NotNull NamespacedId key);
    
    /**
     * Gets statistics and information about a circuit breaker.
     * 
     * @param key the unique identifier for the circuit breaker
     * @return future containing circuit breaker information, or empty if not found
     * @throws IllegalArgumentException if key is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<java.util.Optional<BreakerInfo>> getInfo(@NotNull NamespacedId key);
    
    /**
     * Lists all active circuit breakers.
     * 
     * @param page the page number (0-based)
     * @param size the page size (must be positive)
     * @return future containing the page of circuit breaker information
     * @throws IllegalArgumentException if page is negative or size is non-positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Page<BreakerInfo>> getAllBreakers(int page, int size);
    
    /**
     * Configuration for a circuit breaker.
     * 
     * @param failureThreshold number of failures before opening the breaker
     * @param successThreshold number of successes needed to close from half-open
     * @param timeout how long to wait before transitioning from open to half-open
     * @param maxHalfOpenRequests maximum concurrent requests in half-open state
     */
    record BreakerConfig(
        int failureThreshold,
        int successThreshold,
        @NotNull java.time.Duration timeout,
        int maxHalfOpenRequests
    ) {
        public BreakerConfig {
            if (failureThreshold < 1) throw new IllegalArgumentException("Failure threshold must be positive");
            if (successThreshold < 1) throw new IllegalArgumentException("Success threshold must be positive");
            if (timeout == null) throw new IllegalArgumentException("Timeout cannot be null");
            if (timeout.isNegative()) throw new IllegalArgumentException("Timeout cannot be negative");
            if (maxHalfOpenRequests < 1) throw new IllegalArgumentException("Max half-open requests must be positive");
        }
        
        /**
         * Creates a default configuration suitable for most use cases.
         * 
         * @return default breaker configuration
         */
        public static @NotNull BreakerConfig defaults() {
            return new BreakerConfig(
                5,  // 5 failures to open
                3,  // 3 successes to close
                java.time.Duration.ofSeconds(30),  // 30 second timeout
                3   // max 3 half-open requests
            );
        }
    }
    
    /**
     * Information about a circuit breaker.
     * 
     * @param key the circuit breaker identifier
     * @param state the current state
     * @param config the configuration
     * @param failureCount current failure count
     * @param successCount current success count  
     * @param lastFailure timestamp of last failure, if any
     * @param createdAt when this breaker was created
     */
    record BreakerInfo(
        @NotNull NamespacedId key,
        @NotNull BreakerState state,
        @NotNull BreakerConfig config,
        int failureCount,
        int successCount,
        @NotNull java.util.Optional<java.time.Instant> lastFailure,
        @NotNull java.time.Instant createdAt
    ) {
        public BreakerInfo {
            if (key == null) throw new IllegalArgumentException("Key cannot be null");
            if (state == null) throw new IllegalArgumentException("State cannot be null");
            if (config == null) throw new IllegalArgumentException("Config cannot be null");
            if (failureCount < 0) throw new IllegalArgumentException("Failure count cannot be negative");
            if (successCount < 0) throw new IllegalArgumentException("Success count cannot be negative");
            if (lastFailure == null) throw new IllegalArgumentException("Last failure optional cannot be null");
            if (createdAt == null) throw new IllegalArgumentException("Created timestamp cannot be null");
        }
        
        /**
         * Gets the failure rate as a percentage.
         * 
         * @return failure rate from 0.0 to 1.0
         */
        public double getFailureRate() {
            int total = failureCount + successCount;
            if (total == 0) return 0.0;
            return (double) failureCount / total;
        }
    }
}