package com.upfault.fault.api;

import com.upfault.fault.api.types.NamespacedId;
import com.upfault.fault.api.types.OperationResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing temporary and permanent feature flags for players and regions.
 * 
 * <p>This service provides a flexible flag system that can be used for permissions,
 * temporary states, region protection, cooldowns, and other boolean conditions.
 * 
 * <p>Example usage:
 * <pre>{@code
 * FlagService flags = Fault.service(FlagService.class);
 * if (flags != null) {
 *     NamespacedId flyFlag = new NamespacedId("myplugin", "can_fly");
 *     
 *     // Set a temporary flag
 *     flags.setPlayerFlag(playerId, flyFlag, Duration.ofMinutes(5));
 *     
 *     // Check flag status
 *     flags.hasPlayerFlag(playerId, flyFlag).thenAccept(hasFlag -> {
 *         if (hasFlag) {
 *             // Grant flight ability
 *             player.setAllowFlight(true);
 *         }
 *     });
 *     
 *     // Set a permanent flag
 *     flags.setPlayerFlag(playerId, new NamespacedId("myplugin", "vip"), null);
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations are thread-safe and non-blocking.
 * Flag expiration is handled automatically in the background.
 * 
 * @since 0.0.1
 * @apiNote Flags support both temporary (with expiration) and permanent storage
 */
public interface FlagService {

    /**
     * Sets a flag for a player with optional expiration.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @param flag the flag identifier
     * @param duration flag duration (null for permanent)
     * @return future that completes when the flag is set
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setPlayerFlag(@NotNull UUID playerId, @NotNull NamespacedId flag, @Nullable Duration duration);

    /**
     * Removes a flag from a player.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @param flag the flag identifier
     * @return future containing true if the flag was removed
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> removePlayerFlag(@NotNull UUID playerId, @NotNull NamespacedId flag);

    /**
     * Checks if a player has a specific flag.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @param flag the flag identifier
     * @return future containing true if the player has the flag
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> hasPlayerFlag(@NotNull UUID playerId, @NotNull NamespacedId flag);

    /**
     * Gets all flags for a player.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @return future containing set of flag identifiers
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<NamespacedId>> getPlayerFlags(@NotNull UUID playerId);

    /**
     * Gets the expiration time for a player flag.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @param flag the flag identifier
     * @return future containing expiration time, or null if permanent/not found
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Instant> getFlagExpiration(@NotNull UUID playerId, @NotNull NamespacedId flag);

    /**
     * Extends the duration of an existing flag.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @param flag the flag identifier
     * @param additionalDuration duration to add
     * @return future containing the operation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<OperationResult> extendPlayerFlag(@NotNull UUID playerId, @NotNull NamespacedId flag, @NotNull Duration additionalDuration);

    /**
     * Sets a global server flag with optional expiration.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param flag the flag identifier
     * @param duration flag duration (null for permanent)
     * @return future that completes when the flag is set
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setGlobalFlag(@NotNull NamespacedId flag, @Nullable Duration duration);

    /**
     * Removes a global server flag.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param flag the flag identifier
     * @return future containing true if the flag was removed
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> removeGlobalFlag(@NotNull NamespacedId flag);

    /**
     * Checks if a global server flag is set.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param flag the flag identifier
     * @return future containing true if the flag is set
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> hasGlobalFlag(@NotNull NamespacedId flag);

    /**
     * Gets all global server flags.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing set of global flag identifiers
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<NamespacedId>> getGlobalFlags();

    /**
     * Sets a flag for a world with optional expiration.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param worldName the world name
     * @param flag the flag identifier
     * @param duration flag duration (null for permanent)
     * @return future that completes when the flag is set
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setWorldFlag(@NotNull String worldName, @NotNull NamespacedId flag, @Nullable Duration duration);

    /**
     * Removes a flag from a world.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param worldName the world name
     * @param flag the flag identifier
     * @return future containing true if the flag was removed
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> removeWorldFlag(@NotNull String worldName, @NotNull NamespacedId flag);

    /**
     * Checks if a world has a specific flag.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param worldName the world name
     * @param flag the flag identifier
     * @return future containing true if the world has the flag
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> hasWorldFlag(@NotNull String worldName, @NotNull NamespacedId flag);

    /**
     * Gets all flags for a world.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param worldName the world name
     * @return future containing set of flag identifiers
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<NamespacedId>> getWorldFlags(@NotNull String worldName);

    /**
     * Clears all expired flags across all scopes.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during cleanup.
     * 
     * @return future containing the number of flags cleared
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> clearExpiredFlags();

    /**
     * Clears all flags for a specific player.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @return future containing the number of flags cleared
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> clearPlayerFlags(@NotNull UUID playerId);

    /**
     * Finds all players with a specific flag.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during search.
     * 
     * @param flag the flag identifier
     * @return future containing set of player UUIDs with this flag
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<UUID>> findPlayersWithFlag(@NotNull NamespacedId flag);

    /**
     * Finds all worlds with a specific flag.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during search.
     * 
     * @param flag the flag identifier
     * @return future containing set of world names with this flag
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<String>> findWorldsWithFlag(@NotNull NamespacedId flag);

    /**
     * Gets statistics about flag usage.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during calculation.
     * 
     * @return future containing flag statistics
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<FlagStatistics> getFlagStatistics();

    /**
     * Bulk operation to set multiple flags for a player.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @param flags set of flag identifiers to set
     * @param duration flag duration (null for permanent)
     * @return future that completes when all flags are set
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setPlayerFlags(@NotNull UUID playerId, @NotNull Set<NamespacedId> flags, @Nullable Duration duration);

    /**
     * Flag usage statistics.
     * 
     * @param totalFlags total number of active flags
     * @param playerFlags number of player-specific flags
     * @param worldFlags number of world-specific flags
     * @param globalFlags number of global flags
     * @param temporaryFlags number of temporary flags with expiration
     * @param permanentFlags number of permanent flags
     * 
     * @since 0.0.1
     */
    record FlagStatistics(
        int totalFlags,
        int playerFlags,
        int worldFlags,
        int globalFlags,
        int temporaryFlags,
        int permanentFlags
    ) {

        /**
         * Gets the percentage of flags that are temporary.
         * 
         * @return percentage of temporary flags (0.0 to 1.0)
         */
        public double getTemporaryPercentage() {
            return totalFlags > 0 ? (double) temporaryFlags / totalFlags : 0.0;
        }

        /**
         * Gets the percentage of flags that are player-specific.
         * 
         * @return percentage of player flags (0.0 to 1.0)
         */
        public double getPlayerPercentage() {
            return totalFlags > 0 ? (double) playerFlags / totalFlags : 0.0;
        }
    }
}