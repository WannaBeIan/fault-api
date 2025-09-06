package com.upfault.fault.api;

import com.upfault.fault.api.types.NamespacedId;
import com.upfault.fault.api.types.OperationResult;
import com.upfault.fault.api.types.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing resource pack distribution and tracking.
 * 
 * <p>This service provides contracts for pushing resource packs to players
 * and tracking their acceptance/rejection status without implementing the
 * actual resource pack mechanics.
 * 
 * <p>Example usage:
 * <pre>{@code
 * ResourcePackService packs = Fault.service(ResourcePackService.class);
 * if (packs != null) {
 *     // Create a resource pack
 *     ResourcePack pack = ResourcePack.required(
 *         new NamespacedId("myplugin", "winter_theme"),
 *         URI.create("https://example.com/winter.zip"),
 *         "abc123..." // SHA-1 hash
 *     );
 *     
 *     // Push to a player
 *     packs.pushPack(playerId, pack).thenAccept(result -> {
 *         switch (result) {
 *             case OperationResult.Success success -> 
 *                 logger.info("Pack sent to player");
 *             case OperationResult.Failure failure -> 
 *                 logger.warning("Failed to send pack: " + failure.message());
 *         }
 *     });
 *     
 *     // Check status later
 *     packs.getStatus(playerId, pack.id()).thenAccept(status -> {
 *         if (status == PackStatus.ACCEPTED) {
 *             // Player has the pack loaded
 *         }
 *     });
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All methods are thread-safe and return CompletableFuture
 * for non-blocking operation. Pack status may be cached briefly for performance.
 * 
 * @since 0.0.1
 * @apiNote This service only provides contracts - implementations handle the actual resource pack mechanics
 */
public interface ResourcePackService {

    /**
     * Pushes a resource pack to a player.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the target player's UUID
     * @param pack the resource pack to send
     * @return future containing the operation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<OperationResult> pushPack(@NotNull UUID playerId, @NotNull ResourcePack pack);

    /**
     * Pushes a resource pack to multiple players.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerIds the target players' UUIDs
     * @param pack the resource pack to send
     * @return future containing the operation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<OperationResult> pushPackToPlayers(@NotNull List<UUID> playerIds, @NotNull ResourcePack pack);

    /**
     * Removes a resource pack from a player.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the target player's UUID
     * @param packId the pack identifier to remove
     * @return future containing the operation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<OperationResult> removePack(@NotNull UUID playerId, @NotNull NamespacedId packId);

    /**
     * Gets the status of a resource pack for a player.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @param packId the pack identifier
     * @return future containing the pack status, or null if unknown
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<PackStatus> getStatus(@NotNull UUID playerId, @NotNull NamespacedId packId);

    /**
     * Gets all resource packs currently active for a player.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @return future containing list of active pack IDs
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<NamespacedId>> getActivePacks(@NotNull UUID playerId);

    /**
     * Checks if a player has a specific resource pack loaded.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param playerId the player's UUID
     * @param packId the pack identifier
     * @return future containing true if pack is loaded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> hasPackLoaded(@NotNull UUID playerId, @NotNull NamespacedId packId);

    /**
     * Registers a resource pack definition for future use.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during registration.
     * 
     * @param pack the resource pack to register
     * @return future that completes when registration is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> registerPack(@NotNull ResourcePack pack);

    /**
     * Gets a registered resource pack by ID.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param packId the pack identifier
     * @return future containing the pack, or null if not registered
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<ResourcePack> getRegisteredPack(@NotNull NamespacedId packId);

    /**
     * Gets all registered resource pack IDs.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing list of registered pack IDs
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<NamespacedId>> getRegisteredPacks();

    /**
     * Resource pack status enumeration.
     * 
     * @since 0.0.1
     */
    enum PackStatus {
        /**
         * Pack request was sent but no response received yet.
         */
        PENDING,

        /**
         * Player accepted and successfully loaded the pack.
         */
        ACCEPTED,

        /**
         * Player declined the resource pack.
         */
        DECLINED,

        /**
         * Resource pack download failed.
         */
        FAILED_DOWNLOAD,

        /**
         * Resource pack was invalid or corrupted.
         */
        INVALID,

        /**
         * Unknown status or tracking unavailable.
         */
        UNKNOWN
    }
}