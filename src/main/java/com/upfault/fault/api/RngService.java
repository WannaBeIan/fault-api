package com.upfault.fault.api;

import com.upfault.fault.api.types.NamespacedId;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for deterministic random number generation with session management.
 * 
 * <p>This service provides reproducible randomness for game mechanics by maintaining
 * separate random sessions keyed by purpose and player. This ensures fairness and
 * allows for deterministic replay of random events.
 * 
 * <p>Example usage:
 * <pre>{@code
 * RngService rng = Fault.service(RngService.class);
 * if (rng != null) {
 *     // Get a session for loot generation for a specific player
 *     RandomSession session = rng.getSession(
 *         new NamespacedId("myplugin", "loot"), 
 *         playerId
 *     );
 *     
 *     // Generate deterministic random numbers
 *     int lootRoll = session.nextInt(100); // 0-99
 *     double dropChance = session.nextDouble(); // 0.0-1.0
 *     
 *     // Sessions are reusable and maintain state
 *     boolean criticalHit = session.nextBoolean();
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All methods are thread-safe. RandomSession instances
 * are thread-safe and can be used concurrently, though determinism may be affected.
 * 
 * @since 0.0.1
 * @apiNote Sessions provide deterministic randomness that can be replayed with the same seed
 */
public interface RngService {

    /**
     * Gets or creates a random session for the specified purpose and player.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during session creation.
     * 
     * @param purpose the purpose identifier (e.g., "combat", "loot", "spawning")
     * @param playerId the player UUID for player-specific randomness
     * @return a deterministic random session
     * @since 0.0.1
     */
    @NotNull
    RandomSession getSession(@NotNull NamespacedId purpose, @NotNull UUID playerId);

    /**
     * Gets or creates a global random session for the specified purpose.
     * 
     * <p>Global sessions are not player-specific and provide server-wide randomness.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during session creation.
     * 
     * @param purpose the purpose identifier
     * @return a deterministic random session
     * @since 0.0.1
     */
    @NotNull
    RandomSession getGlobalSession(@NotNull NamespacedId purpose);

    /**
     * Resets a player's random session for a specific purpose.
     * 
     * <p>This will cause the session to be reseeded on next use.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param purpose the purpose identifier
     * @param playerId the player UUID
     * @return future that completes when the session is reset
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> resetSession(@NotNull NamespacedId purpose, @NotNull UUID playerId);

    /**
     * Resets a global random session for a specific purpose.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param purpose the purpose identifier
     * @return future that completes when the session is reset
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> resetGlobalSession(@NotNull NamespacedId purpose);

    /**
     * Gets the current seed for a player's session.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param purpose the purpose identifier
     * @param playerId the player UUID
     * @return future containing the current seed, or null if no session exists
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Long> getSeed(@NotNull NamespacedId purpose, @NotNull UUID playerId);

    /**
     * Thread-safe deterministic random number generator session.
     * 
     * @since 0.0.1
     */
    interface RandomSession {

        /**
         * Generates the next pseudorandom integer.
         * 
         * <p><strong>Threading:</strong> Thread-safe, deterministic when called sequentially.
         * 
         * @return a pseudorandom int value
         * @since 0.0.1
         */
        int nextInt();

        /**
         * Generates a pseudorandom integer between 0 (inclusive) and bound (exclusive).
         * 
         * <p><strong>Threading:</strong> Thread-safe, deterministic when called sequentially.
         * 
         * @param bound the upper bound (exclusive), must be positive
         * @return a pseudorandom int between 0 and bound-1
         * @throws IllegalArgumentException if bound is not positive
         * @since 0.0.1
         */
        int nextInt(int bound);

        /**
         * Generates the next pseudorandom long.
         * 
         * <p><strong>Threading:</strong> Thread-safe, deterministic when called sequentially.
         * 
         * @return a pseudorandom long value
         * @since 0.0.1
         */
        long nextLong();

        /**
         * Generates the next pseudorandom double between 0.0 and 1.0.
         * 
         * <p><strong>Threading:</strong> Thread-safe, deterministic when called sequentially.
         * 
         * @return a pseudorandom double between 0.0 (inclusive) and 1.0 (exclusive)
         * @since 0.0.1
         */
        double nextDouble();

        /**
         * Generates the next pseudorandom float between 0.0 and 1.0.
         * 
         * <p><strong>Threading:</strong> Thread-safe, deterministic when called sequentially.
         * 
         * @return a pseudorandom float between 0.0 (inclusive) and 1.0 (exclusive)
         * @since 0.0.1
         */
        float nextFloat();

        /**
         * Generates the next pseudorandom boolean.
         * 
         * <p><strong>Threading:</strong> Thread-safe, deterministic when called sequentially.
         * 
         * @return a pseudorandom boolean value
         * @since 0.0.1
         */
        boolean nextBoolean();

        /**
         * Generates a pseudorandom double with normal (Gaussian) distribution.
         * 
         * <p><strong>Threading:</strong> Thread-safe, deterministic when called sequentially.
         * 
         * @return a pseudorandom double from normal distribution (mean=0.0, stddev=1.0)
         * @since 0.0.1
         */
        double nextGaussian();

        /**
         * Gets the current seed of this session.
         * 
         * <p><strong>Threading:</strong> Thread-safe, non-blocking.
         * 
         * @return the current seed value
         * @since 0.0.1
         */
        long getSeed();

        /**
         * Gets the number of random values generated by this session.
         * 
         * <p><strong>Threading:</strong> Thread-safe, non-blocking.
         * 
         * @return the generation count
         * @since 0.0.1
         */
        long getGenerationCount();
    }
}