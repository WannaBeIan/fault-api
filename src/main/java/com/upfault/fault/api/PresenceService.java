package com.upfault.fault.api;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service for tracking player presence across multiple servers.
 * 
 * <p>This service provides cross-server player presence information,
 * allowing plugins to determine which server/proxy a player is connected to
 * and which players are online on specific nodes.
 * 
 * @since 0.0.1
 * @apiNote Thread safety: All methods are safe to call from any thread
 */
public interface PresenceService {
    
    /**
     * Gets the server/proxy node ID where a player is currently online.
     * 
     * <p>Node IDs are typically server names or proxy identifiers in a network.
     * 
     * @param player the player to check
     * @return the node ID where the player is online, or empty if offline
     * 
     * @apiNote Safe to call from any thread. Returns empty if the player
     *          is not online anywhere in the network.
     */
    @NotNull Optional<String> nodeOf(@NotNull UUID player);
    
    /**
     * Gets all players currently online on a specific node.
     * 
     * @param nodeId the node ID to query
     * @return set of player UUIDs online on the specified node
     * 
     * @apiNote Safe to call from any thread. Returns empty set if the
     *          node is not known or has no players.
     */
    @NotNull Set<UUID> onlineOn(@NotNull String nodeId);
}