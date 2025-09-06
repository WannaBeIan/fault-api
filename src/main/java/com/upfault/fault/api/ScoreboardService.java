package com.upfault.fault.api;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing player scoreboards without Bukkit leakage.
 * 
 * <p>This service provides a clean abstraction for managing player-specific
 * scoreboards that display information on the right side of the screen.
 * Unlike Bukkit scoreboards, this service focuses on simple line-based
 * display without complex team or objective management.
 * 
 * <p>Each player has their own independent scoreboard that can display up
 * to 15 lines of text. Lines are indexed from 0 (top) to 14 (bottom).
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and may be called from any thread
 */
public interface ScoreboardService {
    
    /**
     * Sets the title of a player's scoreboard.
     * 
     * <p>The title appears at the top of the scoreboard and is typically
     * used to identify what information the scoreboard is displaying.
     * 
     * @param player the player whose scoreboard to modify
     * @param title the title component
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void setTitle(@NotNull UUID player, @NotNull Component title);
    
    /**
     * Sets a specific line of a player's scoreboard.
     * 
     * <p>Lines are indexed from 0 (top) to 14 (bottom). Setting a line
     * to null or empty will clear that line. Lines between set lines
     * may appear as blank space.
     * 
     * @param player the player whose scoreboard to modify
     * @param index the line index (0-14)
     * @param line the line content
     * @throws IllegalArgumentException if player is null or index is out of range
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void setLine(@NotNull UUID player, int index, @Nullable Component line);
    
    /**
     * Sets multiple lines of a player's scoreboard at once.
     * 
     * <p>This is more efficient than calling setLine() multiple times.
     * The list should contain up to 15 elements corresponding to lines 0-14.
     * Null elements or elements beyond the list size will clear those lines.
     * 
     * @param player the player whose scoreboard to modify
     * @param lines the list of line contents
     * @throws IllegalArgumentException if player is null or lines is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void setLines(@NotNull UUID player, @NotNull java.util.List<Component> lines);
    
    /**
     * Clears all lines from a player's scoreboard.
     * 
     * <p>This removes all content from the scoreboard but keeps it visible.
     * The title remains unchanged unless explicitly modified.
     * 
     * @param player the player whose scoreboard to clear
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void clear(@NotNull UUID player);
    
    /**
     * Hides a player's scoreboard completely.
     * 
     * <p>The scoreboard content is preserved but becomes invisible to the
     * player. It can be shown again with {@link #show(UUID)}.
     * 
     * @param player the player whose scoreboard to hide
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void hide(@NotNull UUID player);
    
    /**
     * Shows a previously hidden scoreboard.
     * 
     * <p>If the player has no scoreboard content, this will show an empty
     * scoreboard. If they have content, it will be displayed immediately.
     * 
     * @param player the player whose scoreboard to show
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void show(@NotNull UUID player);
    
    /**
     * Checks if a player's scoreboard is currently visible.
     * 
     * @param player the player to check
     * @return true if the scoreboard is visible
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    boolean isVisible(@NotNull UUID player);
    
    /**
     * Gets the current content of a player's scoreboard.
     * 
     * @param player the player whose scoreboard to retrieve
     * @return future containing the current scoreboard state, or empty if no scoreboard exists
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<java.util.Optional<ScoreboardContent>> getScoreboard(@NotNull UUID player);
    
    /**
     * Updates a player's scoreboard using a builder pattern.
     * 
     * <p>This provides a fluent interface for making multiple scoreboard
     * changes in a single operation.
     * 
     * @param player the player whose scoreboard to update
     * @return a builder for making scoreboard changes
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull ScoreboardBuilder builder(@NotNull UUID player);
    
    /**
     * Represents the content of a player's scoreboard.
     * 
     * @param title the scoreboard title
     * @param lines the scoreboard lines (up to 15 elements)
     * @param visible whether the scoreboard is currently visible
     */
    record ScoreboardContent(
        @Nullable Component title,
        @NotNull java.util.List<Component> lines,
        boolean visible
    ) {
        public ScoreboardContent {
            if (lines == null) {
                throw new IllegalArgumentException("Lines list cannot be null");
            }
            if (lines.size() > 15) {
                throw new IllegalArgumentException("Scoreboard cannot have more than 15 lines");
            }
            // Defensive copy
            lines = java.util.List.copyOf(lines);
        }
        
        /**
         * Gets a specific line from the scoreboard.
         * 
         * @param index the line index (0-14)
         * @return the line content, or null if the line is not set
         * @throws IndexOutOfBoundsException if index is out of range
         */
        public @Nullable Component getLine(int index) {
            if (index < 0 || index >= 15) {
                throw new IndexOutOfBoundsException("Line index must be 0-14, got: " + index);
            }
            return index < lines.size() ? lines.get(index) : null;
        }
        
        /**
         * Checks if the scoreboard has any content.
         * 
         * @return true if there are any non-null lines
         */
        public boolean hasContent() {
            return lines.stream().anyMatch(line -> line != null);
        }
    }
    
    /**
     * Builder for constructing scoreboards fluently.
     */
    interface ScoreboardBuilder {
        
        /**
         * Sets the scoreboard title.
         * 
         * @param title the title component
         * @return this builder
         */
        @NotNull ScoreboardBuilder title(@Nullable Component title);
        
        /**
         * Sets a specific line.
         * 
         * @param index the line index (0-14)
         * @param line the line content
         * @return this builder
         */
        @NotNull ScoreboardBuilder line(int index, @Nullable Component line);
        
        /**
         * Sets all lines at once.
         * 
         * @param lines the line contents
         * @return this builder
         */
        @NotNull ScoreboardBuilder lines(@NotNull java.util.List<Component> lines);
        
        /**
         * Clears all lines.
         * 
         * @return this builder
         */
        @NotNull ScoreboardBuilder clear();
        
        /**
         * Sets the visibility of the scoreboard.
         * 
         * @param visible whether to show or hide the scoreboard
         * @return this builder
         */
        @NotNull ScoreboardBuilder visible(boolean visible);
        
        /**
         * Applies all changes to the player's scoreboard.
         */
        void apply();
    }
}