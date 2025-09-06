package com.upfault.fault.api;

import com.upfault.fault.api.types.ItemDescription;
import com.upfault.fault.api.types.NamespacedId;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * API-only inventory GUI model with views, pages, and interaction contracts.
 * 
 * <p>This interface provides a declarative way to create inventory GUIs
 * without directly handling Bukkit inventory management or click events.
 * 
 * <p>Example usage:
 * <pre>{@code
 * GuiModel gui = Fault.service(GuiModel.class);
 * 
 * // Create a simple shop GUI
 * GuiView shopView = gui.createView()
 *     .title(Component.text("Shop"))
 *     .size(27) // 3 rows
 *     .button(10, shopItem, click -> {
 *         // Handle shop item purchase
 *         Player player = click.player();
 *         if (economy.canAfford(player, shopItem.price())) {
 *             economy.withdraw(player, shopItem.price());
 *             player.getInventory().addItem(shopItem.item());
 *             click.closeGui();
 *         }
 *     })
 *     .closeButton(26, Component.text("Close"))
 *     .build();
 * 
 * // Open for a player
 * gui.openGui(playerId, shopView);
 * }</pre>
 * 
 * <p><strong>Threading:</strong> GUI operations should be performed on the main
 * thread. Click handlers may be called asynchronously depending on implementation.
 * 
 * @since 0.0.1
 * @apiNote All identifiers use NamespacedId to prevent conflicts
 */
public interface GuiModel {

    /**
     * Creates a new GUI view builder.
     * 
     * @return a new view builder
     * @since 0.0.1
     */
    @NotNull
    GuiViewBuilder createView();

    /**
     * Creates a paginated GUI view builder.
     * 
     * @return a new paginated view builder
     * @since 0.0.1
     */
    @NotNull
    PaginatedViewBuilder createPaginatedView();

    /**
     * Opens a GUI for a player.
     * 
     * @param playerId the player's UUID
     * @param view the GUI view to open
     * @return future that completes when the GUI is opened
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> openGui(@NotNull UUID playerId, @NotNull GuiView view);

    /**
     * Closes any open GUI for a player.
     * 
     * @param playerId the player's UUID
     * @return future that completes when the GUI is closed
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> closeGui(@NotNull UUID playerId);

    /**
     * Updates an item in an open GUI.
     * 
     * @param playerId the player's UUID
     * @param slot the inventory slot to update
     * @param item the new item description
     * @return future that completes when the update is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> updateItem(@NotNull UUID playerId, int slot, @NotNull ItemDescription item);

    /**
     * Refreshes an entire GUI view for a player.
     * 
     * @param playerId the player's UUID
     * @param view the updated view
     * @return future that completes when the refresh is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> refreshGui(@NotNull UUID playerId, @NotNull GuiView view);

    /**
     * Checks if a player currently has a GUI open.
     * 
     * @param playerId the player's UUID
     * @return true if the player has a GUI open
     * @since 0.0.1
     */
    boolean hasGuiOpen(@NotNull UUID playerId);

    /**
     * Gets the currently open GUI view for a player.
     * 
     * @param playerId the player's UUID
     * @return the current GUI view, or null if no GUI is open
     * @since 0.0.1
     */
    @Nullable
    GuiView getCurrentGui(@NotNull UUID playerId);

    /**
     * Builder for creating GUI views.
     * 
     * @since 0.0.1
     */
    interface GuiViewBuilder {

        /**
         * Sets the GUI title.
         * 
         * @param title the title component
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        GuiViewBuilder title(@NotNull Component title);

        /**
         * Sets the GUI size (number of slots).
         * 
         * @param size the inventory size (must be multiple of 9, max 54)
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        GuiViewBuilder size(int size);

        /**
         * Sets a unique identifier for this GUI.
         * 
         * @param id the GUI identifier
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        GuiViewBuilder id(@NotNull NamespacedId id);

        /**
         * Adds a clickable button at the specified slot.
         * 
         * @param slot the inventory slot (0-based)
         * @param item the item to display
         * @param clickHandler the click handler
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        GuiViewBuilder button(int slot, @NotNull ItemDescription item, @NotNull ClickHandler clickHandler);

        /**
         * Adds a decorative item (non-clickable) at the specified slot.
         * 
         * @param slot the inventory slot (0-based)
         * @param item the item to display
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        GuiViewBuilder decoration(int slot, @NotNull ItemDescription item);

        /**
         * Adds a close button that closes the GUI when clicked.
         * 
         * @param slot the inventory slot (0-based)
         * @param item the close button item
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        GuiViewBuilder closeButton(int slot, @NotNull ItemDescription item);

        /**
         * Fills empty slots with a decorative item.
         * 
         * @param item the fill item
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        GuiViewBuilder fillEmpty(@NotNull ItemDescription item);

        /**
         * Sets whether players can take items from the GUI.
         * 
         * @param allowTake true to allow taking items
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        GuiViewBuilder allowTake(boolean allowTake);

        /**
         * Sets whether players can place items in the GUI.
         * 
         * @param allowPlace true to allow placing items
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        GuiViewBuilder allowPlace(boolean allowPlace);

        /**
         * Sets a handler for when the GUI is closed.
         * 
         * @param closeHandler the close handler
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        GuiViewBuilder onClose(@NotNull Consumer<Player> closeHandler);

        /**
         * Builds the GUI view.
         * 
         * @return the built GUI view
         * @since 0.0.1
         */
        @NotNull
        GuiView build();
    }

    /**
     * Builder for creating paginated GUI views.
     * 
     * @since 0.0.1
     */
    interface PaginatedViewBuilder extends GuiViewBuilder {

        /**
         * Sets the content area where paginated items will be displayed.
         * 
         * @param startSlot the first slot of the content area
         * @param endSlot the last slot of the content area
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        PaginatedViewBuilder contentArea(int startSlot, int endSlot);

        /**
         * Adds a previous page button.
         * 
         * @param slot the slot for the button
         * @param item the button item
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        PaginatedViewBuilder previousButton(int slot, @NotNull ItemDescription item);

        /**
         * Adds a next page button.
         * 
         * @param slot the slot for the button
         * @param item the button item
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        PaginatedViewBuilder nextButton(int slot, @NotNull ItemDescription item);

        /**
         * Builds the paginated GUI view.
         * 
         * @return the built paginated GUI view
         * @since 0.0.1
         */
        @NotNull
        @Override
        PaginatedGuiView build();
    }

    /**
     * Represents a GUI view configuration.
     * 
     * @since 0.0.1
     */
    interface GuiView {

        /**
         * Gets the GUI identifier.
         * 
         * @return the GUI identifier
         * @since 0.0.1
         */
        @NotNull
        NamespacedId getId();

        /**
         * Gets the GUI title.
         * 
         * @return the title component
         * @since 0.0.1
         */
        @NotNull
        Component getTitle();

        /**
         * Gets the GUI size.
         * 
         * @return the inventory size
         * @since 0.0.1
         */
        int getSize();

        /**
         * Gets the item at a specific slot.
         * 
         * @param slot the slot to check
         * @return the item at that slot, or null if empty
         * @since 0.0.1
         */
        @Nullable
        ItemDescription getItem(int slot);

        /**
         * Gets the click handler for a specific slot.
         * 
         * @param slot the slot to check
         * @return the click handler, or null if no handler
         * @since 0.0.1
         */
        @Nullable
        ClickHandler getClickHandler(int slot);
    }

    /**
     * Paginated GUI view with content management.
     * 
     * @since 0.0.1
     */
    interface PaginatedGuiView extends GuiView {

        /**
         * Sets the content items for pagination.
         * 
         * @param items the items to paginate
         * @param clickHandler click handler for content items
         * @return future that completes when content is set
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> setContent(@NotNull java.util.List<ItemDescription> items, @Nullable ClickHandler clickHandler);

        /**
         * Gets the current page number.
         * 
         * @return the current page (0-based)
         * @since 0.0.1
         */
        int getCurrentPage();

        /**
         * Gets the total number of pages.
         * 
         * @return the total pages
         * @since 0.0.1
         */
        int getTotalPages();

        /**
         * Navigates to a specific page.
         * 
         * @param page the page number (0-based)
         * @return future that completes when navigation is done
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> goToPage(int page);
    }

    /**
     * Context information for GUI click events.
     * 
     * @since 0.0.1
     */
    interface ClickContext {

        /**
         * Gets the player who clicked.
         * 
         * @return the clicking player
         * @since 0.0.1
         */
        @NotNull
        Player player();

        /**
         * Gets the slot that was clicked.
         * 
         * @return the clicked slot
         * @since 0.0.1
         */
        int slot();

        /**
         * Gets the item that was clicked.
         * 
         * @return the clicked item
         * @since 0.0.1
         */
        @NotNull
        ItemDescription item();

        /**
         * Gets the click type (left, right, shift, etc.).
         * 
         * @return the click type
         * @since 0.0.1
         */
        @NotNull
        ClickType clickType();

        /**
         * Closes the GUI for this player.
         * 
         * @since 0.0.1
         */
        void closeGui();

        /**
         * Updates the clicked item.
         * 
         * @param newItem the new item
         * @since 0.0.1
         */
        void updateItem(@NotNull ItemDescription newItem);

        /**
         * Refreshes the entire GUI.
         * 
         * @since 0.0.1
         */
        void refreshGui();
    }

    /**
     * Types of clicks that can occur in a GUI.
     * 
     * @since 0.0.1
     */
    enum ClickType {
        LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT, MIDDLE, DROP, CTRL_DROP
    }

    /**
     * Functional interface for handling GUI clicks.
     * 
     * @since 0.0.1
     */
    @FunctionalInterface
    interface ClickHandler {

        /**
         * Handles a GUI click event.
         * 
         * @param context the click context
         * @since 0.0.1
         */
        void handle(@NotNull ClickContext context);
    }
}
