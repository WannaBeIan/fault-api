package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the result of an operation, including success status and optional message.
 * 
 * <p>This type provides a standardized way to return operation results across the API,
 * allowing callers to check for success, get error messages, and handle failures gracefully.
 * 
 * @param success whether the operation completed successfully
 * @param message optional message providing additional context
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record OperationResult(boolean success, @Nullable String message) {
    
    /**
     * Compact constructor with validation.
     * 
     * @param success whether the operation succeeded
     * @param message optional message (trimmed if provided)
     * @throws IllegalArgumentException if message is empty (use null instead)
     */
    public OperationResult {
        if (message != null && message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty (use null instead)");
        }
        if (message != null) {
            message = message.trim();
        }
    }
    
    /**
     * Creates a successful operation result with no message.
     * 
     * @return successful operation result
     */
    public static @NotNull OperationResult ok() {
        return new OperationResult(true, null);
    }
    
    /**
     * Creates a successful operation result with a message.
     * 
     * @param message success message
     * @return successful operation result
     */
    public static @NotNull OperationResult ok(@NotNull String message) {
        return new OperationResult(true, message);
    }
    
    /**
     * Creates a failed operation result with an error message.
     * 
     * @param errorMessage error message describing why the operation failed
     * @return failed operation result
     * @throws IllegalArgumentException if errorMessage is null or empty
     */
    public static @NotNull OperationResult failure(@NotNull String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Failure message cannot be null or empty");
        }
        return new OperationResult(false, errorMessage);
    }
    
    /**
     * Creates a failed operation result from an exception.
     * 
     * @param exception the exception that caused the failure
     * @return failed operation result
     * @throws IllegalArgumentException if exception is null
     */
    public static @NotNull OperationResult failure(@NotNull Exception exception) {
        if (exception == null) {
            throw new IllegalArgumentException("Exception cannot be null");
        }
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = exception.getClass().getSimpleName();
        }
        return new OperationResult(false, message);
    }
    
    /**
     * Checks if the operation failed.
     * 
     * @return true if the operation was not successful
     */
    public boolean isFailure() {
        return !success;
    }
    
    /**
     * Gets the message, or a default message if none is provided.
     * 
     * @param defaultMessage the default message to use if none is set
     * @return the message or default message
     */
    public @NotNull String getMessageOrDefault(@NotNull String defaultMessage) {
        return message != null ? message : defaultMessage;
    }
    
    /**
     * Throws an exception if the operation failed.
     * 
     * @throws RuntimeException if the operation was not successful
     */
    public void throwIfFailure() {
        if (!success) {
            String errorMsg = message != null ? message : "Operation failed";
            throw new RuntimeException(errorMsg);
        }
    }
    
    /**
     * Combines this result with another result using logical AND.
     * Both operations must succeed for the combined result to succeed.
     * 
     * @param other the other operation result
     * @return combined operation result
     * @throws IllegalArgumentException if other is null
     */
    public @NotNull OperationResult and(@NotNull OperationResult other) {
        if (other == null) {
            throw new IllegalArgumentException("Other result cannot be null");
        }
        
        if (success && other.success) {
            // Both successful - combine messages if any
            if (message != null && other.message != null) {
                return new OperationResult(true, message + "; " + other.message);
            } else if (message != null) {
                return this;
            } else if (other.message != null) {
                return other;
            } else {
                return OperationResult.ok();
            }
        } else {
            // At least one failed - combine error messages
            String errorMsg;
            if (!success && !other.success) {
                errorMsg = (message != null ? message : "Operation failed") + 
                          "; " + (other.message != null ? other.message : "Operation failed");
            } else if (!success) {
                errorMsg = message != null ? message : "Operation failed";
            } else {
                errorMsg = other.message != null ? other.message : "Operation failed";
            }
            return new OperationResult(false, errorMsg);
        }
    }
    
    @Override
    public @NotNull String toString() {
        if (success) {
            return message != null ? "Success: " + message : "Success";
        } else {
            return "Failure: " + (message != null ? message : "Unknown error");
        }
    }
}