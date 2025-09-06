package com.upfault.fault.api;

import com.upfault.fault.api.types.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for creating interactive prompts and forms for players.
 * 
 * <p>This service allows plugins to display text input prompts and 
 * selection menus to players with configurable timeouts.
 * 
 * @since 0.0.1
 * @apiNote All methods are async and safe to call from any thread.
 *          Results may be empty if the player disconnects or times out.
 */
public interface FormsService {
    
    /**
     * Prompts a player to enter text.
     * 
     * <p>Displays a text input form to the player with the given title.
     * The operation will complete when the player submits text or the timeout expires.
     * 
     * @param player the player to prompt
     * @param title the title to display
     * @param timeout the maximum time to wait for input
     * @return a future completing with the entered text, or empty if cancelled/timeout
     * 
     * @apiNote Safe to call from any thread. The returned future completes
     *          on an unspecified thread.
     */
    @NotNull CompletableFuture<Optional<String>> promptText(
        @NotNull UUID player, 
        @NotNull Component title, 
        @NotNull Duration timeout
    );
    
    /**
     * Prompts a player to select from a list of options.
     * 
     * <p>Displays a selection menu to the player with the given options.
     * The operation will complete when the player makes a selection or the timeout expires.
     * 
     * @param player the player to prompt
     * @param title the title to display
     * @param options the list of options to choose from
     * @param timeout the maximum time to wait for selection
     * @return a future completing with the selected option index, or empty if cancelled/timeout
     * 
     * @apiNote Safe to call from any thread. The returned future completes
     *          on an unspecified thread. The returned index corresponds to the
     *          position in the options list (0-based).
     */
    @NotNull CompletableFuture<Optional<Integer>> promptSelect(
        @NotNull UUID player,
        @NotNull Component title,
        @NotNull List<Component> options,
        @NotNull Duration timeout
    );
}