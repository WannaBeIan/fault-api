package com.upfault.fault.api;

import com.upfault.fault.api.types.NamespacedId;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for collecting and reporting system diagnostics and metrics.
 * 
 * <p>This service provides a centralized way to gather diagnostic information
 * about plugin performance, resource usage, and system state for debugging
 * and monitoring purposes.
 * 
 * <p>Example usage:
 * <pre>{@code
 * DiagnosticsService diagnostics = Fault.service(DiagnosticsService.class);
 * if (diagnostics != null) {
 *     // Record a metric
 *     diagnostics.recordMetric("database_queries", 145);
 *     
 *     // Record an event
 *     diagnostics.recordEvent("player_join", Map.of(
 *         "player", "Steve",
 *         "world", "world_nether"
 *     ));
 *     
 *     // Get system information
 *     diagnostics.getSystemInfo().thenAccept(info -> {
 *         logger.info("JVM Memory: " + info.get("jvm.memory.used"));
 *         logger.info("Plugin Count: " + info.get("bukkit.plugins.enabled"));
 *     });
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations are thread-safe and non-blocking.
 * Metrics collection uses lock-free data structures for high performance.
 * 
 * @since 0.0.1
 * @apiNote Diagnostics data may be aggregated and sampled for performance
 */
public interface DiagnosticsService {

    /**
     * Records a numeric metric value.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the metric identifier
     * @param value the numeric value
     * @since 0.0.1
     */
    void recordMetric(@NotNull String key, double value);

    /**
     * Records a namespaced metric value.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param id the namespaced metric identifier
     * @param value the numeric value
     * @since 0.0.1
     */
    void recordMetric(@NotNull NamespacedId id, double value);

    /**
     * Increments a counter metric by 1.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the counter identifier
     * @since 0.0.1
     */
    void incrementCounter(@NotNull String key);

    /**
     * Increments a counter metric by a specific amount.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the counter identifier
     * @param amount the increment amount
     * @since 0.0.1
     */
    void incrementCounter(@NotNull String key, long amount);

    /**
     * Records a timing measurement in milliseconds.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the timing identifier
     * @param milliseconds the duration in milliseconds
     * @since 0.0.1
     */
    void recordTiming(@NotNull String key, long milliseconds);

    /**
     * Records a diagnostic event with metadata.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param event the event identifier
     * @param metadata additional event data
     * @since 0.0.1
     */
    void recordEvent(@NotNull String event, @NotNull Map<String, Object> metadata);

    /**
     * Records a diagnostic event without metadata.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param event the event identifier
     * @since 0.0.1
     */
    void recordEvent(@NotNull String event);

    /**
     * Gets the current value of a metric.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the metric identifier
     * @return future containing the metric value, or null if not found
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Double> getMetric(@NotNull String key);

    /**
     * Gets all recorded metrics.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing map of metric keys to values
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Map<String, Double>> getAllMetrics();

    /**
     * Gets metrics for a specific namespace.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param namespace the namespace to filter by
     * @return future containing map of metric keys to values
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Map<String, Double>> getMetrics(@NotNull String namespace);

    /**
     * Gets recent diagnostic events.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param limit maximum number of events to return
     * @return future containing list of recent events
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<DiagnosticEvent>> getRecentEvents(int limit);

    /**
     * Gets diagnostic events for a specific event type.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param event the event identifier
     * @param limit maximum number of events to return
     * @return future containing list of matching events
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<DiagnosticEvent>> getEvents(@NotNull String event, int limit);

    /**
     * Gets comprehensive system information and metrics.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly gathering system info.
     * 
     * @return future containing map of system properties and metrics
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Map<String, Object>> getSystemInfo();

    /**
     * Gets performance statistics for plugins.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing map of plugin names to performance data
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Map<String, PluginPerformance>> getPluginPerformance();

    /**
     * Clears all recorded metrics and events.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during cleanup.
     * 
     * @return future that completes when clearing is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> clearDiagnostics();

    /**
     * Clears metrics for a specific namespace.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param namespace the namespace to clear
     * @return future that completes when clearing is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> clearMetrics(@NotNull String namespace);

    /**
     * Creates a timing scope for automatic duration recording.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the timing identifier
     * @return a timing scope that records duration when closed
     * @since 0.0.1
     */
    @NotNull
    TimingScope startTiming(@NotNull String key);

    /**
     * Represents a diagnostic event with metadata.
     * 
     * @param event the event identifier
     * @param timestamp when the event occurred
     * @param metadata additional event data
     * 
     * @since 0.0.1
     */
    record DiagnosticEvent(
        @NotNull String event,
        @NotNull Instant timestamp,
        @NotNull Map<String, Object> metadata
    ) {

        /**
         * Creates a diagnostic event with the current timestamp.
         * 
         * @param event the event identifier
         * @param metadata event metadata
         * @return new diagnostic event
         */
        public static @NotNull DiagnosticEvent now(@NotNull String event, @NotNull Map<String, Object> metadata) {
            return new DiagnosticEvent(event, Instant.now(), metadata);
        }

        /**
         * Creates a diagnostic event without metadata.
         * 
         * @param event the event identifier
         * @return new diagnostic event
         */
        public static @NotNull DiagnosticEvent simple(@NotNull String event) {
            return new DiagnosticEvent(event, Instant.now(), Map.of());
        }
    }

    /**
     * Performance statistics for a plugin.
     * 
     * @param pluginName the plugin name
     * @param cpuTime total CPU time used in milliseconds
     * @param memoryUsage estimated memory usage in bytes
     * @param eventCount number of events fired
     * @param commandCount number of commands executed
     * 
     * @since 0.0.1
     */
    record PluginPerformance(
        @NotNull String pluginName,
        long cpuTime,
        long memoryUsage,
        long eventCount,
        long commandCount
    ) {

        /**
         * Gets CPU time in seconds.
         * 
         * @return CPU time in seconds
         */
        public double getCpuSeconds() {
            return cpuTime / 1000.0;
        }

        /**
         * Gets memory usage in megabytes.
         * 
         * @return memory usage in MB
         */
        public double getMemoryMB() {
            return memoryUsage / (1024.0 * 1024.0);
        }
    }

    /**
     * Automatic timing scope that records duration when closed.
     * 
     * @since 0.0.1
     */
    interface TimingScope extends AutoCloseable {

        /**
         * Gets the elapsed time since this scope was created.
         * 
         * <p><strong>Threading:</strong> Thread-safe, non-blocking.
         * 
         * @return elapsed milliseconds
         * @since 0.0.1
         */
        long getElapsedMillis();

        /**
         * Closes this timing scope and records the duration.
         * 
         * <p><strong>Threading:</strong> Thread-safe, non-blocking.
         * 
         * @since 0.0.1
         */
        @Override
        void close();
    }
}