package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Sealed interface representing the result of an operation.
 * 
 * <p>This provides a type-safe way to handle operation results without
 * relying on exceptions for flow control.
 * 
 * @since 0.0.1
 * @apiNote Uses sealed interface pattern for exhaustive matching
 */
public sealed interface OperationResult 
    permits OperationResult.Success, OperationResult.Failure {
    
    /**
     * Creates a successful operation result.
     * 
     * @return success result
     */
    static @NotNull OperationResult success() {
        return Success.INSTANCE;
    }
    
    /**
     * Creates a successful operation result with a message.
     * 
     * @param message the success message
     * @return success result with message
     */
    static @NotNull OperationResult success(@NotNull String message) {
        return new Success(message);
    }
    
    /**
     * Creates a failed operation result.
     * 
     * @param code the fault code
     * @param message the failure message
     * @return failure result
     */
    static @NotNull OperationResult failure(@NotNull FaultCode code, @NotNull String message) {
        return new Failure(code, message, null);
    }
    
    /**
     * Creates a failed operation result with a cause.
     * 
     * @param code the fault code
     * @param message the failure message
     * @param cause the underlying cause
     * @return failure result with cause
     */
    static @NotNull OperationResult failure(@NotNull FaultCode code, @NotNull String message, @Nullable Throwable cause) {
        return new Failure(code, message, cause);
    }
    
    /**
     * Checks if this result represents success.
     * 
     * @return true if the operation was successful
     */
    boolean isSuccess();
    
    /**
     * Checks if this result represents failure.
     * 
     * @return true if the operation failed
     */
    boolean isFailure();
    
    /**
     * Represents a successful operation result.
     * 
     * @param message optional success message
     */
    record Success(@Nullable String message) implements OperationResult {
        
        static final Success INSTANCE = new Success(null);
        
        @Override
        public boolean isSuccess() {
            return true;
        }
        
        @Override
        public boolean isFailure() {
            return false;
        }
        
        @Override
        public @NotNull String toString() {
            return message != null ? "Success[" + message + "]" : "Success";
        }
    }
    
    /**
     * Represents a failed operation result.
     * 
     * @param code the fault code
     * @param message the failure message
     * @param cause the optional underlying cause
     */
    record Failure(@NotNull FaultCode code, @NotNull String message, @Nullable Throwable cause) implements OperationResult {
        
        public Failure {
            if (code == null) {
                throw new IllegalArgumentException("Fault code cannot be null");
            }
            if (message == null || message.trim().isEmpty()) {
                throw new IllegalArgumentException("Failure message cannot be null or empty");
            }
        }
        
        @Override
        public boolean isSuccess() {
            return false;
        }
        
        @Override
        public boolean isFailure() {
            return true;
        }
        
        /**
         * Checks if this failure has an underlying cause.
         * 
         * @return true if there is a cause
         */
        public boolean hasCause() {
            return cause != null;
        }
        
        @Override
        public @NotNull String toString() {
            return cause != null ? 
                "Failure[" + code + ": " + message + ", caused by " + cause + "]" :
                "Failure[" + code + ": " + message + "]";
        }
    }
    
    /**
     * Pattern matching helper for operation results.
     * 
     * @param <T> the return type
     */
    interface Matcher<T> {
        
        /**
         * Handles successful result.
         * 
         * @param success the success result
         * @return the result
         */
        T onSuccess(@NotNull Success success);
        
        /**
         * Handles failed result.
         * 
         * @param failure the failure result
         * @return the result
         */
        T onFailure(@NotNull Failure failure);
    }
    
    /**
     * Applies pattern matching to this operation result.
     * 
     * @param matcher the matcher to apply
     * @param <T> the return type
     * @return the result of the matching
     */
    default <T> T match(@NotNull Matcher<T> matcher) {
        return switch (this) {
            case Success success -> matcher.onSuccess(success);
            case Failure failure -> matcher.onFailure(failure);
        };
    }
    
    /**
     * Executes code if the result is successful.
     * 
     * @param action the action to execute on success
     */
    default void ifSuccess(@NotNull Runnable action) {
        if (isSuccess()) {
            action.run();
        }
    }
    
    /**
     * Executes code if the result is a failure.
     * 
     * @param action the action to execute on failure
     */
    default void ifFailure(@NotNull java.util.function.Consumer<Failure> action) {
        if (this instanceof Failure failure) {
            action.accept(failure);
        }
    }
}
