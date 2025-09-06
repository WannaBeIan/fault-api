package com.upfault.fault.api.types;

/**
 * Standard fault codes for operation failures.
 *
 * @since 0.0.1
 * @apiNote These codes provide consistent error classification across all Fault services
 */
public enum FaultCode {
    /**
     * Operation not permitted due to insufficient permissions.
     */
    PERMISSION_DENIED,
    
    /**
     * Requested resource was not found.
     */
    NOT_FOUND,
    
    /**
     * Request was invalid or malformed.
     */
    INVALID_REQUEST,
    
    /**
     * Operation failed due to concurrent modification.
     */
    CONFLICT,
    
    /**
     * Rate limit or quota exceeded.
     */
    RATE_LIMITED,
    
    /**
     * Service is temporarily unavailable.
     */
    UNAVAILABLE,
    
    /**
     * Internal system error.
     */
    INTERNAL_ERROR,
    
    /**
     * External dependency failed.
     */
    EXTERNAL_ERROR,
    
    /**
     * Timeout occurred during operation.
     */
    TIMEOUT,
    
    /**
     * Resource limit exceeded.
     */
    RESOURCE_EXHAUSTED
}