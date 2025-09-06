package com.upfault.fault.api;

import com.upfault.fault.api.types.AudienceSelector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Provides Component-based messaging services with MiniMessage support.
 * 
 * <p>This service handles player messaging, localization, placeholders,
 * and various message delivery methods including chat, actionbar, and titles.
 * 
 * <p>Example usage:
 * <pre>{@code
 * MessageService messages = Fault.service(MessageService.class);
 * 
 * // Send a simple message
 * Component message = messages.parse("&lt;gold&gt;Hello, &lt;player&gt;!");
 * messages.sendMessage(playerId, message, Map.of("player", playerName));
 * 
 * // Send to all online players
 * messages.broadcast(Component.text("Server announcement!"));
 * 
 * // Show title with fade effects
 * Title title = messages.createTitle(
 *     Component.text("Welcome!"),
 *     Component.text("Enjoy your stay"),
 *     Duration.ofSeconds(1),
 *     Duration.ofSeconds(3),
 *     Duration.ofSeconds(1)
 * );
 * messages.showTitle(playerId, title);
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All methods are safe to call from any thread.
 * Message delivery is handled asynchronously when needed.
 * 
 * @since 0.0.1
 * @apiNote Uses Adventure Component API for rich text formatting
 */
public interface MessageService {

    /**
     * Parses a MiniMessage string into a Component.
     * 
     * <p>Supports standard MiniMessage tags like &lt;color&gt;, &lt;bold&gt;, etc.
     * 
     * @param miniMessage the MiniMessage string to parse
     * @return the parsed Component
     * @since 0.0.1
     */
    @NotNull
    Component parse(@NotNull String miniMessage);

    /**
     * Gets the MiniMessage parser instance.
     * 
     * @return the MiniMessage parser
     * @since 0.0.1
     */
    @NotNull
    MiniMessage getMiniMessage();

    /**
     * Sends a chat message to a specific player.
     * 
     * @param playerId the target player's UUID
     * @param message the message to send
     * @since 0.0.1
     */
    void sendMessage(@NotNull UUID playerId, @NotNull Component message);

    /**
     * Sends a chat message with placeholder replacement.
     * 
     * @param playerId the target player's UUID
     * @param message the message template
     * @param placeholders placeholder values to substitute
     * @since 0.0.1
     */
    void sendMessage(@NotNull UUID playerId, @NotNull Component message, @NotNull Map<String, String> placeholders);

    /**
     * Broadcasts a message to all online players.
     * 
     * @param message the message to broadcast
     * @since 0.0.1
     */
    void broadcast(@NotNull Component message);

    /**
     * Sends a message to players matching the audience selector.
     * 
     * @param selector defines which players receive the message
     * @param message the message to send
     * @since 0.0.1
     */
    void sendMessage(@NotNull AudienceSelector selector, @NotNull Component message);

    /**
     * Sends an actionbar message to a player.
     * 
     * @param playerId the target player's UUID
     * @param message the actionbar message
     * @since 0.0.1
     */
    void sendActionBar(@NotNull UUID playerId, @NotNull Component message);

    /**
     * Shows a title to a player.
     * 
     * @param playerId the target player's UUID
     * @param title the title to display
     * @since 0.0.1
     */
    void showTitle(@NotNull UUID playerId, @NotNull Title title);

    /**
     * Creates a title with timing configuration.
     * 
     * @param title the main title text
     * @param subtitle the subtitle text (can be null)
     * @param fadeIn fade in duration
     * @param stay stay duration
     * @param fadeOut fade out duration
     * @return the configured Title
     * @since 0.0.1
     */
    @NotNull
    Title createTitle(@NotNull Component title, @Nullable Component subtitle, 
                      @NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut);

    /**
     * Gets a player's preferred locale.
     * 
     * @param playerId the player's UUID
     * @return the player's locale, or default if unknown
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Locale> getPlayerLocale(@NotNull UUID playerId);

    /**
     * Sets a player's preferred locale.
     * 
     * @param playerId the player's UUID
     * @param locale the new locale preference
     * @return future that completes when the locale is saved
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setPlayerLocale(@NotNull UUID playerId, @NotNull Locale locale);

    /**
     * Applies placeholders to a component.
     * 
     * <p>This method searches for placeholder patterns in the component's
     * text content and replaces them with values from the provided map.
     * 
     * @param component the component to process
     * @param placeholders the placeholder values
     * @return the component with placeholders replaced
     * @since 0.0.1
     */
    @NotNull
    Component applyPlaceholders(@NotNull Component component, @NotNull Map<String, String> placeholders);

    /**
     * Clears the chat for a specific player.
     * 
     * <p>Sends empty lines to push previous messages out of view.
     * 
     * @param playerId the player's UUID
     * @since 0.0.1
     */
    void clearChat(@NotNull UUID playerId);

    /**
     * Gets the server's default locale.
     * 
     * @return the default server locale
     * @since 0.0.1
     */
    @NotNull
    Locale getDefaultLocale();
}