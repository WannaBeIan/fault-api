package com.upfault.fault.api;

import com.upfault.fault.api.types.OperationResult;
import com.upfault.fault.api.types.Region;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing player region selections.
 * 
 * <p>This service allows players to select regions in the world,
 * typically using tools or commands, for use with other systems.
 * 
 * @since 0.0.1
 * @apiNote Thread safety: All methods are safe to call from any thread.
 *          Selection requests use the main thread for player interaction.
 */
public interface SelectionService {
    
    /**
     * Gets the current region selection for a player.
     * 
     * @param player the player to check
     * @return the player's current selection, or empty if none
     * 
     * @apiNote Safe to call from any thread
     */
    @NotNull Optional<Region> selectionOf(@NotNull UUID player);
    
    /**
     * Requests a player to select a region with a prompt.
     * 
     * <p>This will display a prompt to the player and wait for them to
     * select a region using the configured selection tool or method.
     * 
     * @param player the player to request selection from
     * @param prompt the prompt to display to the player
     * @return a future completing when the selection is made or cancelled
     * 
     * @apiNote Safe to call from any thread. The returned future completes
     *          on an unspecified thread. The prompt is shown on the main thread.
     */
    @NotNull CompletableFuture<OperationResult> requestSelection(
        @NotNull UUID player, 
        @NotNull Component prompt
    );
    
    /**
     * Clears a player's current selection.
     * 
     * @param player the player whose selection to clear
     * 
     * @apiNote Safe to call from any thread
     */
    void clearSelection(@NotNull UUID player);
    
    /**
     * Sets a player's selection to a specific region.
     * 
     * @param player the player
     * @param region the region to select
     * 
     * @apiNote Safe to call from any thread
     */
    void setSelection(@NotNull UUID player, @NotNull Region region);
}