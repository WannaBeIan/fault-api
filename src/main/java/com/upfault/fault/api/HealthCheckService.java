package com.upfault.fault.api;

import com.upfault.fault.api.types.NamespacedId;
import com.upfault.fault.api.types.OperationResult;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for registering and querying plugin health checks.
 * 
 * <p>This service allows plugins to register health check endpoints that can be
 * monitored for operational status, resource usage, and error conditions.
 * 
 * <p>Example usage:
 * <pre>{@code
 * HealthCheckService health = Fault.service(HealthCheckService.class);
 * if (health != null) {
 *     // Register a health check
 *     HealthCheck check = new HealthCheck() {
 *         public CompletableFuture<CheckResult> check() {
 *             return CompletableFuture.supplyAsync(() -> {
 *                 if (isDbConnected()) {
 *                     return CheckResult.healthy("Database connection active");
 *                 } else {
 *                     return CheckResult.unhealthy("Database connection lost");
 *                 }
 *             });
 *         }
 *     };
 *     
 *     health.registerCheck(new NamespacedId("myplugin", "database"), check);
 *     
 *     // Query health status
 *     health.getOverallHealth().thenAccept(result -> {
 *         if (result.isHealthy()) {
 *             logger.info("System healthy");
 *         } else {
 *             logger.warning("System unhealthy: " + result.getMessage());
 *         }
 *     });
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations are thread-safe and non-blocking.
 * Health checks may be executed on background threads with configurable timeouts.
 * 
 * @since 0.0.1
 * @apiNote Health checks are executed periodically and cached for performance
 */
public interface HealthCheckService {

    /**
     * Registers a health check for monitoring.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during registration.
     * 
     * @param id unique identifier for this health check
     * @param check the health check implementation
     * @return future that completes when registration is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> registerCheck(@NotNull NamespacedId id, @NotNull HealthCheck check);

    /**
     * Unregisters a health check.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param id the health check identifier
     * @return future containing true if the check was removed
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> unregisterCheck(@NotNull NamespacedId id);

    /**
     * Gets the result of a specific health check.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking (uses cached result).
     * 
     * @param id the health check identifier
     * @return future containing the check result, or null if not found
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<CheckResult> getCheckResult(@NotNull NamespacedId id);

    /**
     * Gets results from all registered health checks.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking (uses cached results).
     * 
     * @return future containing list of all check results
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<CheckResult>> getAllCheckResults();

    /**
     * Gets the overall system health status.
     * 
     * <p>System is considered healthy if all registered checks are healthy.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing overall health status
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<CheckResult> getOverallHealth();

    /**
     * Forces immediate execution of a specific health check.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes check on background thread.
     * 
     * @param id the health check identifier
     * @return future containing the fresh check result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<CheckResult> executeCheck(@NotNull NamespacedId id);

    /**
     * Forces immediate execution of all health checks.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes checks on background threads.
     * 
     * @return future containing list of fresh check results
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<CheckResult>> executeAllChecks();

    /**
     * Gets all registered health check identifiers.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing list of registered check IDs
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<NamespacedId>> getRegisteredChecks();

    /**
     * Configures the execution interval for health checks.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during configuration.
     * 
     * @param interval the execution interval
     * @return future that completes when configuration is applied
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setCheckInterval(@NotNull Duration interval);

    /**
     * Configures the timeout for health check execution.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during configuration.
     * 
     * @param timeout the execution timeout
     * @return future that completes when configuration is applied
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setCheckTimeout(@NotNull Duration timeout);

    /**
     * Interface for plugin health check implementations.
     * 
     * @since 0.0.1
     */
    interface HealthCheck {

        /**
         * Executes this health check.
         * 
         * <p><strong>Threading:</strong> May be called on any thread, should be non-blocking.
         * 
         * @return future containing the check result
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<CheckResult> check();

        /**
         * Gets a human-readable name for this health check.
         * 
         * <p><strong>Threading:</strong> Thread-safe, non-blocking.
         * 
         * @return the check name (defaults to "Health Check")
         * @since 0.0.1
         */
        default @NotNull String getName() {
            return "Health Check";
        }

        /**
         * Gets the expected execution duration for this check.
         * 
         * <p><strong>Threading:</strong> Thread-safe, non-blocking.
         * 
         * @return expected duration (defaults to 5 seconds)
         * @since 0.0.1
         */
        default @NotNull Duration getExpectedDuration() {
            return Duration.ofSeconds(5);
        }
    }

    /**
     * Result of a health check execution.
     * 
     * @param id the health check identifier
     * @param healthy whether the check passed
     * @param message descriptive message about the check result
     * @param lastChecked when this check was last executed
     * @param duration how long the check took to execute
     * 
     * @since 0.0.1
     */
    record CheckResult(
        @NotNull NamespacedId id,
        boolean healthy,
        @NotNull String message,
        @NotNull Instant lastChecked,
        @NotNull Duration duration
    ) {

        /**
         * Creates a healthy check result.
         * 
         * @param id the check identifier
         * @param message success message
         * @return healthy check result
         */
        public static @NotNull CheckResult healthy(@NotNull NamespacedId id, @NotNull String message) {
            return new CheckResult(id, true, message, Instant.now(), Duration.ZERO);
        }

        /**
         * Creates an unhealthy check result.
         * 
         * @param id the check identifier
         * @param message failure message
         * @return unhealthy check result
         */
        public static @NotNull CheckResult unhealthy(@NotNull NamespacedId id, @NotNull String message) {
            return new CheckResult(id, false, message, Instant.now(), Duration.ZERO);
        }

        /**
         * Creates a check result with timing information.
         * 
         * @param id the check identifier
         * @param healthy whether the check passed
         * @param message descriptive message
         * @param duration execution duration
         * @return timed check result
         */
        public static @NotNull CheckResult timed(@NotNull NamespacedId id, boolean healthy, @NotNull String message, @NotNull Duration duration) {
            return new CheckResult(id, healthy, message, Instant.now(), duration);
        }

        /**
         * Checks if this result indicates the system is healthy.
         * 
         * @return true if healthy
         */
        public boolean isHealthy() {
            return healthy;
        }

        /**
         * Checks if this result indicates the system is unhealthy.
         * 
         * @return true if unhealthy
         */
        public boolean isUnhealthy() {
            return !healthy;
        }
    }
}