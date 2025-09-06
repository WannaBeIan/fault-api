package com.upfault.fault.api.types;

/**
 * Represents the current state of a circuit breaker.
 * 
 * <p>Circuit breakers protect systems from repeated calls to failing services
 * by transitioning through different states based on success/failure patterns.
 * 
 * @since 0.0.1
 */
public enum BreakerState {
    
    /**
     * Normal operation state - requests pass through to the protected service.
     * 
     * <p>In this state, the circuit breaker allows all requests to pass through
     * to the underlying service. Success and failure counts are tracked to
     * determine when to transition to the OPEN state.
     * 
     * <p>Transitions to OPEN when the failure threshold is exceeded.
     */
    CLOSED,
    
    /**
     * Fail-fast state - requests are immediately rejected without calling the service.
     * 
     * <p>In this state, the circuit breaker immediately fails all requests
     * without attempting to call the underlying service. This prevents
     * resource exhaustion and cascading failures while giving the service
     * time to recover.
     * 
     * <p>After a timeout period, transitions to HALF_OPEN for testing recovery.
     */
    OPEN,
    
    /**
     * Testing recovery state - limited requests are allowed through.
     * 
     * <p>In this state, the circuit breaker allows a limited number of
     * requests through to test if the underlying service has recovered.
     * Additional requests beyond the limit are rejected.
     * 
     * <p>Transitions to CLOSED if enough consecutive requests succeed,
     * or back to OPEN if failures are detected.
     */
    HALF_OPEN;
    
    /**
     * Checks if requests are allowed to pass through in this state.
     * 
     * @return true if requests can be attempted
     */
    public boolean allowsRequests() {
        return this != OPEN;
    }
    
    /**
     * Checks if this state represents a failure mode.
     * 
     * @return true if the breaker is preventing requests
     */
    public boolean isFailureMode() {
        return this == OPEN;
    }
    
    /**
     * Checks if this state is testing recovery.
     * 
     * @return true if the breaker is in recovery testing mode
     */
    public boolean isTesting() {
        return this == HALF_OPEN;
    }
    
    /**
     * Checks if this state represents normal operation.
     * 
     * @return true if the breaker is in normal operation
     */
    public boolean isHealthy() {
        return this == CLOSED;
    }
    
    /**
     * Gets a human-readable description of this state.
     * 
     * @return description of the state
     */
    public String getDescription() {
        return switch (this) {
            case CLOSED -> "Normal operation - requests pass through";
            case OPEN -> "Fail-fast mode - requests are rejected";
            case HALF_OPEN -> "Recovery testing - limited requests allowed";
        };
    }
}