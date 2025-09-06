package com.upfault.fault.api.events;

import com.upfault.fault.api.types.AuditEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an audit event is fired to the audit system.
 * 
 * <p>This event is fired after an audit event has been recorded
 * to all configured audit sinks.
 * 
 * @since 0.0.1
 * @apiNote This event is not cancellable as the audit event has already been recorded
 */
public class AuditEventFired extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final AuditEvent auditEvent;
    private final int sinkCount;
    
    /**
     * Creates a new AuditEventFired.
     * 
     * @param auditEvent the audit event that was fired
     * @param sinkCount the number of sinks the event was sent to
     */
    public AuditEventFired(@NotNull AuditEvent auditEvent, int sinkCount) {
        this.auditEvent = auditEvent;
        this.sinkCount = sinkCount;
    }
    
    /**
     * Gets the audit event that was fired.
     * 
     * @return the audit event
     */
    public @NotNull AuditEvent getAuditEvent() {
        return auditEvent;
    }
    
    /**
     * Gets the number of audit sinks the event was sent to.
     * 
     * @return the sink count
     */
    public int getSinkCount() {
        return sinkCount;
    }
    
    /**
     * Convenience method to get the actor from the audit event.
     * 
     * @return the actor UUID
     */
    public @NotNull java.util.UUID getActor() {
        return auditEvent.actor();
    }
    
    /**
     * Convenience method to get the action from the audit event.
     * 
     * @return the action string
     */
    public @NotNull String getAction() {
        return auditEvent.action();
    }
    
    /**
     * Convenience method to get the target from the audit event.
     * 
     * @return the target string, or null if no target
     */
    public @org.jetbrains.annotations.Nullable String getTarget() {
        return auditEvent.target();
    }
    
    /**
     * Convenience method to get the timestamp from the audit event.
     * 
     * @return the event timestamp
     */
    public @NotNull java.time.Instant getTimestamp() {
        return auditEvent.timestamp();
    }
    
    /**
     * Convenience method to get the severity from the audit event.
     * 
     * @return the event severity
     */
    public @NotNull com.upfault.fault.api.AuditFacade.AuditSeverity getSeverity() {
        return auditEvent.severity();
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
