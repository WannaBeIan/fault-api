package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing player achievements and badges.
 * 
 * <p>This service provides functionality for tracking player achievements,
 * granting badges, and querying achievement status. Achievements can represent
 * various milestones, accomplishments, or recognition within the game.
 * 
 * <p>The service supports both manual achievement granting (by administrators
 * or other systems) and automatic achievement detection based on player
 * actions and statistics.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and return CompletableFutures for async operations
 */
public interface AchievementService {
    
    /**
     * Gets all achievements that a player has earned.
     * 
     * @param player the player to query
     * @return future containing the set of earned achievement IDs
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Set<NamespacedId>> earned(@NotNull UUID player);
    
    /**
     * Grants an achievement to a player.
     * 
     * <p>If the player already has this achievement, the operation succeeds
     * silently. The grant operation may trigger notifications or other
     * side effects depending on the implementation.
     * 
     * @param player the player to grant the achievement to
     * @param achievementId the achievement to grant
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> grant(@NotNull UUID player, @NotNull NamespacedId achievementId);
    
    /**
     * Revokes an achievement from a player.
     * 
     * <p>If the player doesn't have this achievement, the operation succeeds
     * silently.
     * 
     * @param player the player to revoke the achievement from
     * @param achievementId the achievement to revoke
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> revoke(@NotNull UUID player, @NotNull NamespacedId achievementId);
    
    /**
     * Checks if a player has earned a specific achievement.
     * 
     * @param player the player to check
     * @param achievementId the achievement to check for
     * @return future containing true if the player has the achievement
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Boolean> hasAchievement(@NotNull UUID player, @NotNull NamespacedId achievementId);
    
    /**
     * Gets information about a specific achievement.
     * 
     * @param achievementId the achievement to get information about
     * @return future containing the achievement information, or empty if not found
     * @throws IllegalArgumentException if achievementId is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<java.util.Optional<Achievement>> getAchievement(@NotNull NamespacedId achievementId);
    
    /**
     * Lists all available achievements in the system.
     * 
     * @param page the page number (0-based)
     * @param size the page size (must be positive)
     * @return future containing the page of achievements
     * @throws IllegalArgumentException if page is negative or size is non-positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Page<Achievement>> getAllAchievements(int page, int size);
    
    /**
     * Gets achievement statistics for a player.
     * 
     * @param player the player to get statistics for
     * @return future containing the player's achievement statistics
     * @throws IllegalArgumentException if player is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<AchievementStats> getPlayerStats(@NotNull UUID player);
    
    /**
     * Registers a new achievement in the system.
     * 
     * <p>This is typically used by plugins to add their custom achievements.
     * The achievement becomes available for granting and tracking.
     * 
     * @param achievement the achievement to register
     * @return future containing the operation result
     * @throws IllegalArgumentException if achievement is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> registerAchievement(@NotNull Achievement achievement);
    
    /**
     * Statistics about a player's achievements.
     * 
     * @param totalEarned number of achievements the player has earned
     * @param totalAvailable total number of achievements available
     * @param completionPercentage completion percentage (0.0 to 1.0)
     * @param recentlyEarned recently earned achievement IDs
     */
    record AchievementStats(
        int totalEarned,
        int totalAvailable,
        double completionPercentage,
        @NotNull java.util.List<NamespacedId> recentlyEarned
    ) {
        public AchievementStats {
            if (totalEarned < 0) throw new IllegalArgumentException("Total earned cannot be negative");
            if (totalAvailable < 0) throw new IllegalArgumentException("Total available cannot be negative");
            if (completionPercentage < 0 || completionPercentage > 1) throw new IllegalArgumentException("Completion percentage must be between 0 and 1");
            if (recentlyEarned == null) throw new IllegalArgumentException("Recently earned list cannot be null");
            recentlyEarned = java.util.List.copyOf(recentlyEarned);
        }
    }
}