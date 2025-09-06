package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable audit event record.
 * 
 * @param actor the actor (e.g., player) who performed the action
 * @param action the action identifier
 * @param target the target of the action (optional)
 * @param context additional context information
 * @param timestamp when the event occurred
 * @param severity the event severity level
 * 
 * @since 0.0.1
 * @apiNote Immutable record for audit logging
 */
public record AuditEvent(
    @NotNull UUID actor,
    @NotNull String action,
    @Nullable String target,
    @NotNull Map<String, String> context,
    @NotNull Instant timestamp,
    @NotNull com.upfault.fault.api.AuditFacade.AuditSeverity severity
) {
    
    public AuditEvent {
        if (actor == null) {
            throw new IllegalArgumentException("Actor cannot be null");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action cannot be null or empty");
        }
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        if (severity == null) {
            throw new IllegalArgumentException("Severity cannot be null");
        }
        
        // Make context immutable
        context = Map.copyOf(context);
    }
    
    /**
     * Creates a basic audit event with INFO severity.
     * 
     * @param actor the actor UUID
     * @param action the action
     * @return basic audit event
     */
    public static @NotNull AuditEvent of(@NotNull UUID actor, @NotNull String action) {
        return new AuditEvent(
            actor, action, null, Map.of(), Instant.now(),
            com.upfault.fault.api.AuditFacade.AuditSeverity.INFO
        );
    }
    
    /**
     * Creates an audit event with a target.
     * 
     * @param actor the actor UUID
     * @param action the action
     * @param target the target
     * @return audit event with target
     */
    public static @NotNull AuditEvent of(@NotNull UUID actor, @NotNull String action, @NotNull String target) {
        return new AuditEvent(
            actor, action, target, Map.of(), Instant.now(),
            com.upfault.fault.api.AuditFacade.AuditSeverity.INFO
        );
    }
    
    /**
     * Gets a context value by key.
     * 
     * @param key the context key
     * @return the context value, or null if not present
     */
    public @Nullable String getContext(@NotNull String key) {
        return context.get(key);
    }
    
    /**
     * Gets a context value with a default.
     * 
     * @param key the context key
     * @param defaultValue the default value
     * @return the context value or default
     */
    public @NotNull String getContextOrDefault(@NotNull String key, @NotNull String defaultValue) {
        return context.getOrDefault(key, defaultValue);
    }
    
    /**
     * Checks if a context key exists.
     * 
     * @param key the context key
     * @return true if the key exists
     */
    public boolean hasContext(@NotNull String key) {
        return context.containsKey(key);
    }
    
    /**
     * Checks if this event has any context.
     * 
     * @return true if context is not empty
     */
    public boolean hasContext() {
        return !context.isEmpty();
    }
    
    /**
     * Checks if this event has a target.
     * 
     * @return true if target is not null
     */
    public boolean hasTarget() {
        return target != null;
    }
    
    /**
     * Creates a new event with additional context.
     * 
     * @param key the context key
     * @param value the context value
     * @return new event with additional context
     */
    public @NotNull AuditEvent withContext(@NotNull String key, @NotNull String value) {
        Map<String, String> newContext = new java.util.HashMap<>(context);
        newContext.put(key, value);
        return new AuditEvent(actor, action, target, newContext, timestamp, severity);
    }
    
    /**
     * Creates a new event with a different severity.
     * 
     * @param newSeverity the new severity
     * @return new event with different severity
     */
    public @NotNull AuditEvent withSeverity(@NotNull com.upfault.fault.api.AuditFacade.AuditSeverity newSeverity) {
        return new AuditEvent(actor, action, target, context, timestamp, newSeverity);
    }
    
    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AuditEvent[");
        sb.append("actor=").append(actor);
        sb.append(", action=").append(action);
        if (hasTarget()) {
            sb.append(", target=").append(target);
        }
        sb.append(", severity=").append(severity);
        sb.append(", time=").append(timestamp);
        if (hasContext()) {
            sb.append(", context=").append(context);
        }
        sb.append("]");
        return sb.toString();
    }
}
