package com.upfault.fault.api;

import com.upfault.fault.api.types.AuditEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Append-only audit event system with configurable sinks.
 * 
 * <p>This facade provides a structured way to record and query audit events
 * for security, debugging, and compliance purposes.
 * 
 * <p>Example usage:
 * <pre>{@code
 * AuditFacade audit = Fault.service(AuditFacade.class);
 * 
 * // Record a simple audit event
 * AuditEvent event = audit.createEvent()
 *     .actor(playerId)
 *     .action("item.purchase")
 *     .target("diamond_sword")
 *     .context(Map.of(
 *         "price", "100",
 *         "currency", "coins",
 *         "shop", "weapons"
 *     ))
 *     .build();
 * 
 * audit.recordEvent(event).thenRun(() -> 
 *     System.out.println("Purchase event recorded")
 * );
 * 
 * // Query recent events for a player
 * audit.getEventsForActor(playerId, 10).thenAccept(events -> {
 *     System.out.println("Player has " + events.size() + " recent events");
 *     events.forEach(e -> System.out.println(e.action() + " at " + e.timestamp()));
 * });
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All audit operations are asynchronous and
 * thread-safe. Events are immutable once created.
 * 
 * @since 0.0.1
 * @apiNote Events are append-only and should never be modified after creation
 */
public interface AuditFacade {

    /**
     * Creates a new audit event builder.
     * 
     * @return a new event builder
     * @since 0.0.1
     */
    @NotNull
    AuditEventBuilder createEvent();

    /**
     * Records an audit event to all configured sinks.
     * 
     * @param event the event to record
     * @return future that completes when the event is recorded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> recordEvent(@NotNull AuditEvent event);

    /**
     * Records multiple audit events as a batch.
     * 
     * @param events the events to record
     * @return future that completes when all events are recorded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> recordEvents(@NotNull List<AuditEvent> events);

    /**
     * Gets recent audit events for a specific actor (e.g., player).
     * 
     * @param actorId the actor's UUID
     * @param limit the maximum number of events to return
     * @return future containing list of events (most recent first)
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEvent>> getEventsForActor(@NotNull UUID actorId, int limit);

    /**
     * Gets audit events matching a specific action type.
     * 
     * @param action the action to search for
     * @param limit the maximum number of events to return
     * @return future containing list of matching events (most recent first)
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEvent>> getEventsByAction(@NotNull String action, int limit);

    /**
     * Gets audit events within a time range.
     * 
     * @param startTime the start of the time range (inclusive)
     * @param endTime the end of the time range (inclusive)
     * @param limit the maximum number of events to return
     * @return future containing list of events in the time range
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEvent>> getEventsInTimeRange(@NotNull Instant startTime, @NotNull Instant endTime, int limit);

    /**
     * Searches audit events using multiple criteria.
     * 
     * @param query the search criteria
     * @return future containing list of matching events
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<AuditEvent>> searchEvents(@NotNull AuditQuery query);

    /**
     * Gets the total count of audit events for an actor.
     * 
     * @param actorId the actor's UUID
     * @return future containing the event count
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Long> getEventCountForActor(@NotNull UUID actorId);

    /**
     * Gets the total count of audit events matching an action.
     * 
     * @param action the action to count
     * @return future containing the event count
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Long> getEventCountByAction(@NotNull String action);

    /**
     * Registers a new audit sink for receiving events.
     * 
     * @param sink the audit sink to register
     * @return future that completes when the sink is registered
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> registerSink(@NotNull AuditSink sink);

    /**
     * Unregisters an audit sink.
     * 
     * @param sink the audit sink to unregister
     * @return future that completes when the sink is unregistered
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> unregisterSink(@NotNull AuditSink sink);

    /**
     * Gets all currently registered audit sinks.
     * 
     * @return list of registered sinks
     * @since 0.0.1
     */
    @NotNull
    List<AuditSink> getRegisteredSinks();

    /**
     * Builder for creating audit events.
     * 
     * @since 0.0.1
     */
    interface AuditEventBuilder {

        /**
         * Sets the actor (e.g., player) who performed the action.
         * 
         * @param actorId the actor's UUID
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditEventBuilder actor(@NotNull UUID actorId);

        /**
         * Sets the action that was performed.
         * 
         * @param action the action identifier (e.g., "item.purchase", "block.break")
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditEventBuilder action(@NotNull String action);

        /**
         * Sets the target of the action.
         * 
         * @param target the target identifier
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditEventBuilder target(@Nullable String target);

        /**
         * Sets additional context information.
         * 
         * @param context map of context key-value pairs
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditEventBuilder context(@NotNull Map<String, String> context);

        /**
         * Adds a single context entry.
         * 
         * @param key the context key
         * @param value the context value
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditEventBuilder addContext(@NotNull String key, @NotNull String value);

        /**
         * Sets the timestamp for this event.
         * 
         * <p>If not set, the current time will be used.
         * 
         * @param timestamp the event timestamp
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditEventBuilder timestamp(@NotNull Instant timestamp);

        /**
         * Sets a severity level for this event.
         * 
         * @param severity the severity level
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditEventBuilder severity(@NotNull AuditSeverity severity);

        /**
         * Builds the audit event.
         * 
         * @return the built audit event
         * @since 0.0.1
         */
        @NotNull
        AuditEvent build();
    }

    /**
     * Query builder for searching audit events.
     * 
     * @since 0.0.1
     */
    interface AuditQuery {

        /**
         * Creates a new audit query.
         * 
         * @return new query builder
         * @since 0.0.1
         */
        @NotNull
        static AuditQueryBuilder builder() {
            throw new UnsupportedOperationException("Implementation required");
        }
    }

    /**
     * Builder for audit queries.
     * 
     * @since 0.0.1
     */
    interface AuditQueryBuilder {

        /**
         * Filters by actor UUID.
         * 
         * @param actorId the actor UUID
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditQueryBuilder actor(@NotNull UUID actorId);

        /**
         * Filters by action.
         * 
         * @param action the action
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditQueryBuilder action(@NotNull String action);

        /**
         * Filters by target.
         * 
         * @param target the target
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditQueryBuilder target(@NotNull String target);

        /**
         * Filters by time range.
         * 
         * @param startTime start time (inclusive)
         * @param endTime end time (inclusive)
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditQueryBuilder timeRange(@NotNull Instant startTime, @NotNull Instant endTime);

        /**
         * Filters by severity.
         * 
         * @param severity the minimum severity
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditQueryBuilder severity(@NotNull AuditSeverity severity);

        /**
         * Limits the number of results.
         * 
         * @param limit the maximum results
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        AuditQueryBuilder limit(int limit);

        /**
         * Builds the query.
         * 
         * @return the built query
         * @since 0.0.1
         */
        @NotNull
        AuditQuery build();
    }

    /**
     * Severity levels for audit events.
     * 
     * @since 0.0.1
     */
    enum AuditSeverity {
        TRACE, DEBUG, INFO, WARN, ERROR, CRITICAL
    }

    /**
     * Interface for audit event sinks.
     * 
     * @since 0.0.1
     */
    interface AuditSink {

        /**
         * Gets the name of this sink.
         * 
         * @return the sink name
         * @since 0.0.1
         */
        @NotNull
        String getName();

        /**
         * Records an audit event.
         * 
         * @param event the event to record
         * @return future that completes when the event is recorded
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> recordEvent(@NotNull AuditEvent event);

        /**
         * Records multiple events as a batch.
         * 
         * @param events the events to record
         * @return future that completes when all events are recorded
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> recordEvents(@NotNull List<AuditEvent> events);

        /**
         * Checks if this sink is currently available.
         * 
         * @return true if the sink is available
         * @since 0.0.1
         */
        boolean isAvailable();

        /**
         * Closes this sink and releases resources.
         * 
         * @return future that completes when the sink is closed
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> close();
    }
}
