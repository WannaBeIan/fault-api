package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Result of a rate limit check operation.
 * 
 * @since 0.0.1
 * @apiNote Uses sealed interface for exhaustive pattern matching
 */
public sealed interface RateLimitResult 
    permits RateLimitResult.Allowed, RateLimitResult.Denied {
    
    /**
     * Creates an allowed result.
     * 
     * @param remainingTokens the number of tokens remaining
     * @param nextRefillAt when the next token refill occurs
     * @return allowed result
     */
    static @NotNull RateLimitResult allowed(int remainingTokens, @NotNull Instant nextRefillAt) {
        return new Allowed(remainingTokens, nextRefillAt);
    }
    
    /**
     * Creates a denied result.
     * 
     * @param nextAvailableAt when the operation can next be attempted
     * @return denied result
     */
    static @NotNull RateLimitResult denied(@NotNull Instant nextAvailableAt) {
        return new Denied(nextAvailableAt);
    }
    
    /**
     * Checks if the rate limit check was successful (allowed).
     * 
     * @return true if the operation is allowed
     */
    boolean isAllowed();
    
    /**
     * Represents a successful rate limit check.
     * 
     * @param remainingTokens number of tokens remaining after this operation
     * @param nextRefillAt when the next token refill will occur
     */
    record Allowed(int remainingTokens, @NotNull Instant nextRefillAt) implements RateLimitResult {
        
        public Allowed {
            if (remainingTokens < 0) {
                throw new IllegalArgumentException("Remaining tokens cannot be negative: " + remainingTokens);
            }
            if (nextRefillAt == null) {
                throw new IllegalArgumentException("Next refill time cannot be null");
            }
        }
        
        @Override
        public boolean isAllowed() {
            return true;
        }
        
        /**
         * Checks if there are more tokens available.
         * 
         * @return true if remaining tokens > 0
         */
        public boolean hasRemainingTokens() {
            return remainingTokens > 0;
        }
        
        @Override
        public @NotNull String toString() {
            return String.format("Allowed[%d remaining, next refill at %s]", 
                               remainingTokens, nextRefillAt);
        }
    }
    
    /**
     * Represents a denied rate limit check.
     * 
     * @param nextAvailableAt when the operation can next be attempted
     */
    record Denied(@NotNull Instant nextAvailableAt) implements RateLimitResult {
        
        public Denied {
            if (nextAvailableAt == null) {
                throw new IllegalArgumentException("Next available time cannot be null");
            }
        }
        
        @Override
        public boolean isAllowed() {
            return false;
        }
        
        /**
         * Gets the time until the operation can be attempted again.
         * 
         * @return duration until next attempt is allowed
         */
        public @NotNull java.time.Duration getTimeUntilAvailable() {
            return java.time.Duration.between(Instant.now(), nextAvailableAt);
        }
        
        /**
         * Gets the seconds until the operation can be attempted again.
         * 
         * @return seconds until next attempt (may be negative if time has passed)
         */
        public long getSecondsUntilAvailable() {
            return getTimeUntilAvailable().toSeconds();
        }
        
        @Override
        public @NotNull String toString() {
            return String.format("Denied[available at %s]", nextAvailableAt);
        }
    }
    
    /**
     * Pattern matching helper for rate limit results.
     * 
     * @param <T> the return type
     */
    interface Matcher<T> {
        
        /**
         * Handles allowed result.
         * 
         * @param allowed the allowed result
         * @return the result
         */
        T onAllowed(@NotNull Allowed allowed);
        
        /**
         * Handles denied result.
         * 
         * @param denied the denied result
         * @return the result
         */
        T onDenied(@NotNull Denied denied);
    }
    
    /**
     * Applies pattern matching to this rate limit result.
     * 
     * @param matcher the matcher to apply
     * @param <T> the return type
     * @return the result of the matching
     */
    default <T> T match(@NotNull Matcher<T> matcher) {
        return switch (this) {
            case Allowed allowed -> matcher.onAllowed(allowed);
            case Denied denied -> matcher.onDenied(denied);
        };
    }
}
