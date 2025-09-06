package com.upfault.fault.api;

import com.upfault.fault.api.types.NamespacedId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for recording and querying audit logs of player and system actions.
 * 
 * <p>This service provides comprehensive audit logging capabilities for tracking
 * player actions, administrative commands, system events, and security-related
 * activities with structured metadata and efficient querying.
 * 
 * <p>Example usage:
 * <pre>{@code
 * AuditService audit = Fault.service(AuditService.class);
 * if (audit != null) {
 *     // Record a player action
 *     audit.recordAction(
 *         new NamespacedId("myplugin", "block_break"),
 *         playerId,
 *         "Player broke diamond ore",
 *         Map.of(
 *             "block_type", "DIAMOND_ORE",
 *             "location", "world,123,64,456",
 *             "tool", "DIAMOND_PICKAXE"
 *         )
 *     );
 *     
 *     // Record an admin command
 *     audit.recordAdminAction(
 *         adminId,
 *         "ban_player",
 *         "Banned player for griefing",
 *         Map.of(
 *             "target_player", "Griefer123",
 *             "reason", "Destroying other players builds",
 *             "duration", "7d"
 *         )
 *     );
 *     
 *     // Query audit logs
 *     audit.getPlayerActions(playerId, 50).thenAccept(actions -> {
 *         logger.info("Found " + actions.size() + " recent actions for player");
 *     });
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations are thread-safe. Log recording
 * is asynchronous and non-blocking for performance.
 * 
 * @since 0.0.1
 * @apiNote Audit logs are immutable once recorded and support retention policies
 */
public interface AuditService {

    /**
     * Records a player action in the audit log.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param action the action identifier
     * @param playerId the player who performed the action
     * @param description human-readable action description
     * @param metadata additional action metadata
     * @return future that completes when action is recorded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> recordAction(@NotNull NamespacedId action, @NotNull UUID playerId, @NotNull String description, @NotNull Map<String, Object> metadata);

    /**
     * Records a player action without metadata.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param action the action identifier
     * @param playerId the player who performed the action
     * @param description human-readable action description
     * @return future that completes when action is recorded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> recordAction(@NotNull NamespacedId action, @NotNull UUID playerId, @NotNull String description);

    /**
     * Records an administrative action in the audit log.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param adminId the administrator who performed the action
     * @param action the action type
     * @param description human-readable action description
     * @param metadata additional action metadata
     * @return future that completes when action is recorded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> recordAdminAction(@NotNull UUID adminId, @NotNull String action, @NotNull String description, @NotNull Map<String, Object> metadata);

    /**
     * Records a system event in the audit log.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param event the event identifier
     * @param description human-readable event description
     * @param metadata additional event metadata
     * @return future that completes when event is recorded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> recordSystemEvent(@NotNull NamespacedId event, @NotNull String description, @NotNull Map<String, Object> metadata);

    /**
     * Records a security event in the audit log.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param event the security event type
     * @param playerId the player involved (may be null for system events)
     * @param severity the event severity level
     * @param description human-readable event description
     * @param metadata additional event metadata
     * @return future that completes when event is recorded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> recordSecurityEvent(@NotNull String event, @Nullable UUID playerId, @NotNull SecurityLevel severity, @NotNull String description, @NotNull Map<String, Object> metadata);

    /**
     * Gets recent actions for a specific player.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @param limit maximum number of actions to return
     * @return future containing list of audit entries
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEntry>> getPlayerActions(@NotNull UUID playerId, int limit);

    /**
     * Gets recent actions for a specific action type.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param action the action identifier
     * @param limit maximum number of actions to return
     * @return future containing list of audit entries
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEntry>> getActionHistory(@NotNull NamespacedId action, int limit);

    /**
     * Gets recent administrative actions.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param limit maximum number of actions to return
     * @return future containing list of audit entries
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEntry>> getAdminActions(int limit);

    /**
     * Gets recent security events.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param severity minimum severity level
     * @param limit maximum number of events to return
     * @return future containing list of audit entries
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEntry>> getSecurityEvents(@NotNull SecurityLevel severity, int limit);

    /**
     * Gets audit entries within a time range.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during query.
     * 
     * @param start start time (inclusive)
     * @param end end time (exclusive)
     * @param limit maximum number of entries to return
     * @return future containing list of audit entries
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEntry>> getEntriesByTimeRange(@NotNull Instant start, @NotNull Instant end, int limit);

    /**
     * Searches audit entries by description text.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during search.
     * 
     * @param query search query text
     * @param limit maximum number of entries to return
     * @return future containing list of matching audit entries
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEntry>> searchEntries(@NotNull String query, int limit);

    /**
     * Gets audit entries with specific metadata criteria.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during query.
     * 
     * @param metadataFilters metadata key-value filters
     * @param limit maximum number of entries to return
     * @return future containing list of matching audit entries
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEntry>> getEntriesByMetadata(@NotNull Map<String, Object> metadataFilters, int limit);

    /**
     * Gets all unique action types recorded.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing set of action identifiers
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<NamespacedId>> getRecordedActions();

    /**
     * Gets all players who have recorded audit entries.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing set of player UUIDs
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<UUID>> getPlayersWithEntries();

    /**
     * Gets audit statistics for analysis.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during calculation.
     * 
     * @return future containing audit statistics
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<AuditStatistics> getStatistics();

    /**
     * Purges old audit entries based on retention policy.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes purge on background thread.
     * 
     * @param olderThan entries older than this instant will be purged
     * @return future containing number of entries purged
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> purgeOldEntries(@NotNull Instant olderThan);

    /**
     * Exports audit entries to a file.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes export on background thread.
     * 
     * @param start start time for export range
     * @param end end time for export range
     * @param format export format
     * @param outputPath path for the output file
     * @return future containing number of entries exported
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> exportEntries(@NotNull Instant start, @NotNull Instant end, @NotNull ExportFormat format, @NotNull String outputPath);

    /**
     * Security event severity levels.
     * 
     * @since 0.0.1
     */
    enum SecurityLevel {
        /**
         * Informational security event.
         */
        INFO,

        /**
         * Low-severity security event.
         */
        LOW,

        /**
         * Medium-severity security event.
         */
        MEDIUM,

        /**
         * High-severity security event.
         */
        HIGH,

        /**
         * Critical security event requiring immediate attention.
         */
        CRITICAL
    }

    /**
     * Audit entry export formats.
     * 
     * @since 0.0.1
     */
    enum ExportFormat {
        /**
         * JSON format with full metadata.
         */
        JSON,

        /**
         * CSV format for spreadsheet analysis.
         */
        CSV,

        /**
         * Plain text format for reading.
         */
        TEXT
    }

    /**
     * Immutable audit log entry.
     * 
     * @param id unique identifier for this entry
     * @param timestamp when the action occurred
     * @param type the entry type
     * @param action the action identifier (may be null for system events)
     * @param playerId the player involved (may be null)
     * @param description human-readable description
     * @param metadata additional structured data
     * 
     * @since 0.0.1
     */
    record AuditEntry(
        @NotNull String id,
        @NotNull Instant timestamp,
        @NotNull EntryType type,
        @Nullable NamespacedId action,
        @Nullable UUID playerId,
        @NotNull String description,
        @NotNull Map<String, Object> metadata
    ) {

        /**
         * Creates a player action audit entry.
         * 
         * @param action the action identifier
         * @param playerId the player UUID
         * @param description action description
         * @param metadata action metadata
         * @return new audit entry
         */
        public static @NotNull AuditEntry playerAction(@NotNull NamespacedId action, @NotNull UUID playerId, @NotNull String description, @NotNull Map<String, Object> metadata) {
            return new AuditEntry(
                generateId(),
                Instant.now(),
                EntryType.PLAYER_ACTION,
                action,
                playerId,
                description,
                metadata
            );
        }

        /**
         * Creates an admin action audit entry.
         * 
         * @param adminId the administrator UUID
         * @param action action type
         * @param description action description
         * @param metadata action metadata
         * @return new audit entry
         */
        public static @NotNull AuditEntry adminAction(@NotNull UUID adminId, @NotNull String action, @NotNull String description, @NotNull Map<String, Object> metadata) {
            Map<String, Object> fullMetadata = new java.util.HashMap<>(metadata);
            fullMetadata.put("action_type", action);
            return new AuditEntry(
                generateId(),
                Instant.now(),
                EntryType.ADMIN_ACTION,
                null,
                adminId,
                description,
                Map.copyOf(fullMetadata)
            );
        }

        /**
         * Creates a system event audit entry.
         * 
         * @param event the event identifier
         * @param description event description
         * @param metadata event metadata
         * @return new audit entry
         */
        public static @NotNull AuditEntry systemEvent(@NotNull NamespacedId event, @NotNull String description, @NotNull Map<String, Object> metadata) {
            return new AuditEntry(
                generateId(),
                Instant.now(),
                EntryType.SYSTEM_EVENT,
                event,
                null,
                description,
                metadata
            );
        }

        /**
         * Creates a security event audit entry.
         * 
         * @param event event type
         * @param playerId player involved (may be null)
         * @param severity security level
         * @param description event description
         * @param metadata event metadata
         * @return new audit entry
         */
        public static @NotNull AuditEntry securityEvent(@NotNull String event, @Nullable UUID playerId, @NotNull SecurityLevel severity, @NotNull String description, @NotNull Map<String, Object> metadata) {
            Map<String, Object> fullMetadata = new java.util.HashMap<>(metadata);
            fullMetadata.put("event_type", event);
            fullMetadata.put("severity", severity.name());
            return new AuditEntry(
                generateId(),
                Instant.now(),
                EntryType.SECURITY_EVENT,
                null,
                playerId,
                description,
                Map.copyOf(fullMetadata)
            );
        }

        /**
         * Checks if this entry involves a player.
         * 
         * @return true if playerId is not null
         */
        public boolean hasPlayer() {
            return playerId != null;
        }

        /**
         * Checks if this entry has an action identifier.
         * 
         * @return true if action is not null
         */
        public boolean hasAction() {
            return action != null;
        }

        /**
         * Gets a metadata value by key.
         * 
         * @param key the metadata key
         * @return the metadata value, or null if not found
         */
        public @Nullable Object getMetadata(@NotNull String key) {
            return metadata.get(key);
        }

        /**
         * Gets a typed metadata value by key.
         * 
         * @param key the metadata key
         * @param type the expected value type
         * @param <T> the value type
         * @return the typed metadata value, or null if not found or wrong type
         */
        @SuppressWarnings("unchecked")
        public <T> @Nullable T getMetadata(@NotNull String key, @NotNull Class<T> type) {
            Object value = metadata.get(key);
            return type.isInstance(value) ? (T) value : null;
        }

        private static String generateId() {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    /**
     * Types of audit entries.
     * 
     * @since 0.0.1
     */
    enum EntryType {
        /**
         * Action performed by a player.
         */
        PLAYER_ACTION,

        /**
         * Administrative action by staff.
         */
        ADMIN_ACTION,

        /**
         * System or plugin event.
         */
        SYSTEM_EVENT,

        /**
         * Security-related event.
         */
        SECURITY_EVENT
    }

    /**
     * Statistics about audit log contents.
     * 
     * @param totalEntries total number of audit entries
     * @param playerActions number of player action entries
     * @param adminActions number of admin action entries
     * @param systemEvents number of system event entries
     * @param securityEvents number of security event entries
     * @param uniquePlayers number of unique players in logs
     * @param uniqueActions number of unique action types
     * @param oldestEntry timestamp of oldest entry
     * @param newestEntry timestamp of newest entry
     * 
     * @since 0.0.1
     */
    record AuditStatistics(
        long totalEntries,
        long playerActions,
        long adminActions,
        long systemEvents,
        long securityEvents,
        long uniquePlayers,
        long uniqueActions,
        @Nullable Instant oldestEntry,
        @Nullable Instant newestEntry
    ) {

        /**
         * Gets the percentage of entries that are player actions.
         * 
         * @return percentage (0.0 to 1.0)
         */
        public double getPlayerActionPercentage() {
            return totalEntries > 0 ? (double) playerActions / totalEntries : 0.0;
        }

        /**
         * Gets the percentage of entries that are security events.
         * 
         * @return percentage (0.0 to 1.0)
         */
        public double getSecurityEventPercentage() {
            return totalEntries > 0 ? (double) securityEvents / totalEntries : 0.0;
        }

        /**
         * Gets the average entries per player.
         * 
         * @return average entries per player
         */
        public double getAverageEntriesPerPlayer() {
            return uniquePlayers > 0 ? (double) totalEntries / uniquePlayers : 0.0;
        }
    }
}