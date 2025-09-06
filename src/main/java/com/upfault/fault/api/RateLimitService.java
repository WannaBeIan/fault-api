package com.upfault.fault.api;

import com.upfault.fault.api.types.RateLimitResult;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Token-bucket and cooldown contracts for arbitrary keys.
 * 
 * <p>This service provides rate limiting functionality for various operations
 * using configurable token buckets and simple cooldown mechanisms.
 * 
 * <p>Example usage:
 * <pre>{@code
 * RateLimitService rateLimit = Fault.service(RateLimitService.class);
 * 
 * // Create a rate limiter for chat messages (5 messages per 10 seconds)
 * String chatKey = "chat:" + playerId;
 * CompletableFuture<RateLimitResult> result = rateLimit.tryConsume(
 *     chatKey, 1, 5, Duration.ofSeconds(10)
 * );
 * 
 * result.thenAccept(res -> {
 *     switch (res) {
 *         case RateLimitResult.Allowed allowed -> {
 *             // Process the chat message
 *             System.out.println("Remaining tokens: " + allowed.remainingTokens());
 *         }
 *         case RateLimitResult.Denied denied -> {
 *             // Rate limited
 *             player.sendMessage("Too many messages! Try again in " + 
 *                 denied.nextAvailableAt().getEpochSecond() + " seconds.");
 *         }
 *     }
 * });
 * 
 * // Simple cooldown for a command
 * CompletableFuture<RateLimitResult> cooldownResult = rateLimit.applyCooldown(
 *     "command:heal:" + playerId, Duration.ofMinutes(5)
 * );
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations are thread-safe and return
 * CompletableFuture for async processing.
 * 
 * @since 0.0.1
 * @apiNote Uses token bucket algorithm for flexible rate limiting
 */
public interface RateLimitService {

    /**
     * Attempts to consume tokens from a rate limiter.
     * 
     * <p>Creates a new rate limiter if one doesn't exist for the key.
     * 
     * @param key unique identifier for the rate limiter
     * @param tokensToConsume number of tokens to consume
     * @param maxTokens maximum number of tokens in the bucket
     * @param refillPeriod time period for refilling tokens
     * @return future containing the rate limit result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<RateLimitResult> tryConsume(@NotNull String key, int tokensToConsume, 
                                                  int maxTokens, @NotNull Duration refillPeriod);

    /**
     * Attempts to consume a single token from a rate limiter.
     * 
     * @param key unique identifier for the rate limiter
     * @param maxTokens maximum number of tokens in the bucket
     * @param refillPeriod time period for refilling tokens
     * @return future containing the rate limit result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<RateLimitResult> tryConsume(@NotNull String key, int maxTokens, @NotNull Duration refillPeriod);

    /**
     * Applies a simple cooldown to a key.
     * 
     * <p>This is equivalent to a rate limiter with 1 token that refills
     * after the cooldown period.
     * 
     * @param key unique identifier for the cooldown
     * @param cooldownDuration how long until the key can be used again
     * @return future containing the rate limit result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<RateLimitResult> applyCooldown(@NotNull String key, @NotNull Duration cooldownDuration);

    /**
     * Checks the current status of a rate limiter without consuming tokens.
     * 
     * @param key unique identifier for the rate limiter
     * @return future containing the current rate limit status
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<RateLimitResult> checkStatus(@NotNull String key);

    /**
     * Resets a rate limiter, allowing immediate use.
     * 
     * @param key unique identifier for the rate limiter to reset
     * @return future that completes when the reset is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> reset(@NotNull String key);

    /**
     * Removes a rate limiter completely.
     * 
     * @param key unique identifier for the rate limiter to remove
     * @return future that completes when the removal is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> remove(@NotNull String key);

    /**
     * Creates a player-specific rate limit key.
     * 
     * @param playerId the player's UUID
     * @param action the action being rate limited
     * @return formatted rate limit key
     * @since 0.0.1
     */
    @NotNull
    String createPlayerKey(@NotNull UUID playerId, @NotNull String action);

    /**
     * Creates a global rate limit key.
     * 
     * @param action the action being rate limited
     * @return formatted rate limit key
     * @since 0.0.1
     */
    @NotNull
    String createGlobalKey(@NotNull String action);

    /**
     * Gets information about all active rate limiters.
     * 
     * @return future containing map of keys to their current status
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<java.util.Map<String, RateLimitResult>> getAllStatus();

    /**
     * Cleans up expired rate limiters to free memory.
     * 
     * @return future containing the number of cleaned up limiters
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> cleanup();

    /**
     * Refills tokens for a specific rate limiter immediately.
     * 
     * @param key unique identifier for the rate limiter
     * @param tokens number of tokens to add
     * @return future that completes when tokens are added
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> refill(@NotNull String key, int tokens);

    /**
     * Sets the token count for a rate limiter to a specific value.
     * 
     * @param key unique identifier for the rate limiter
     * @param tokens the new token count
     * @return future that completes when tokens are set
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setTokens(@NotNull String key, int tokens);
}
