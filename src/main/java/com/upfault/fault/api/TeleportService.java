package com.upfault.fault.api;

import com.upfault.fault.api.types.Coordinates;
import com.upfault.fault.api.types.OperationResult;
import com.upfault.fault.api.types.TeleportCause;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for safe player teleportation with cause tracking.
 * 
 * <p>This service provides safe teleportation that includes validation,
 * cause tracking, and proper async handling to prevent blocking the server.
 * 
 * @since 0.0.1
 * @apiNote Thread safety: All methods are safe to call from any thread
 */
public interface TeleportService {
    
    /**
     * Safely teleports a player to the specified coordinates.
     * 
     * <p>This method performs safety checks such as:
     * <ul>
     *   <li>Verifying the destination is safe (not in lava, void, etc.)</li>
     *   <li>Ensuring the world exists and is loaded</li>
     *   <li>Checking player permissions if applicable</li>
     *   <li>Loading chunks if necessary</li>
     * </ul>
     * 
     * @param player the player to teleport
     * @param to the destination coordinates
     * @param cause the reason for the teleportation
     * @return a future completing with the operation result
     * 
     * @apiNote Safe to call from any thread. The returned future completes
     *          on an unspecified thread. The actual teleportation happens
     *          on the main server thread.
     */
    @NotNull CompletableFuture<OperationResult> safeTeleport(
        @NotNull UUID player, 
        @NotNull Coordinates to, 
        @NotNull TeleportCause cause
    );
}