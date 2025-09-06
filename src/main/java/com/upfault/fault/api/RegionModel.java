package com.upfault.fault.api;

import com.upfault.fault.api.types.Coordinates;
import com.upfault.fault.api.types.NamespacedId;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * World/region abstractions for location-based operations.
 * 
 * <p>This interface provides abstractions for running code in specific
 * world regions without exposing Bukkit World or chunk classes directly.
 * 
 * <p>Example usage:
 * <pre>{@code
 * RegionModel regions = Fault.service(RegionModel.class);
 * 
 * // Create a region identifier
 * NamespacedId worldId = new NamespacedId("minecraft", "overworld");
 * Coordinates center = new Coordinates(worldId, 100, 64, 200);
 * 
 * // Run something in a specific region
 * regions.runInRegion(center, 50, () -> {
 *     // This code runs when the region is loaded
 *     System.out.println("Executing in region around " + center);
 *     return "Task completed";
 * }).thenAccept(result -> {
 *     System.out.println("Region task result: " + result);
 * });
 * 
 * // Check if a region is loaded
 * regions.isRegionLoaded(center, 16).thenAccept(loaded -> {
 *     if (loaded) {
 *         System.out.println("Region is currently loaded");
 *     }
 * });
 * }</pre>
 * 
 * <p><strong>Threading:</strong> Region operations may be asynchronous,
 * especially when loading chunks or crossing world boundaries.
 * 
 * @since 0.0.1
 * @apiNote This abstraction supports both traditional chunk-based and Folia region-based threading
 */
public interface RegionModel {

    /**
     * Executes a task in a specific world region.
     * 
     * <p>The task will be executed when the region is loaded and available.
     * If the region is not loaded, it may be loaded asynchronously.
     * 
     * @param center the center coordinates of the region
     * @param radius the radius around the center (in blocks)
     * @param task the task to execute
     * @param <T> the return type of the task
     * @return future containing the task result
     * @since 0.0.1
     */
    @NotNull
    <T> CompletableFuture<T> runInRegion(@NotNull Coordinates center, int radius, @NotNull Supplier<T> task);

    /**
     * Executes a runnable task in a specific world region.
     * 
     * @param center the center coordinates of the region
     * @param radius the radius around the center (in blocks)
     * @param task the task to execute
     * @return future that completes when the task is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> runInRegion(@NotNull Coordinates center, int radius, @NotNull Runnable task);

    /**
     * Executes a task at specific coordinates.
     * 
     * @param coordinates the exact coordinates
     * @param task the task to execute
     * @param <T> the return type of the task
     * @return future containing the task result
     * @since 0.0.1
     */
    @NotNull
    <T> CompletableFuture<T> runAt(@NotNull Coordinates coordinates, @NotNull Supplier<T> task);

    /**
     * Executes a runnable task at specific coordinates.
     * 
     * @param coordinates the exact coordinates
     * @param task the task to execute
     * @return future that completes when the task is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> runAt(@NotNull Coordinates coordinates, @NotNull Runnable task);

    /**
     * Checks if a region is currently loaded.
     * 
     * @param center the center coordinates of the region
     * @param radius the radius to check (in blocks)
     * @return future containing true if the region is loaded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> isRegionLoaded(@NotNull Coordinates center, int radius);

    /**
     * Checks if specific coordinates are in a loaded region.
     * 
     * @param coordinates the coordinates to check
     * @return future containing true if the coordinates are loaded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> isLoaded(@NotNull Coordinates coordinates);

    /**
     * Ensures a region is loaded before executing a task.
     * 
     * @param center the center coordinates of the region
     * @param radius the radius to load (in blocks)
     * @return future that completes when the region is loaded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> ensureLoaded(@NotNull Coordinates center, int radius);

    /**
     * Ensures specific coordinates are loaded.
     * 
     * @param coordinates the coordinates to ensure are loaded
     * @return future that completes when the coordinates are loaded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> ensureLoaded(@NotNull Coordinates coordinates);

    /**
     * Gets the distance between two coordinate points.
     * 
     * @param first the first coordinates
     * @param second the second coordinates
     * @return the distance in blocks, or -1 if different worlds
     * @since 0.0.1
     */
    double getDistance(@NotNull Coordinates first, @NotNull Coordinates second);

    /**
     * Checks if two coordinates are in the same world.
     * 
     * @param first the first coordinates
     * @param second the second coordinates
     * @return true if both coordinates are in the same world
     * @since 0.0.1
     */
    boolean isSameWorld(@NotNull Coordinates first, @NotNull Coordinates second);

    /**
     * Gets all available world identifiers.
     * 
     * @return future containing array of world identifiers
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<NamespacedId[]> getAvailableWorlds();

    /**
     * Checks if a world exists and is available.
     * 
     * @param worldId the world identifier
     * @return future containing true if the world exists
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> isWorldAvailable(@NotNull NamespacedId worldId);

    /**
     * Creates a new coordinates object.
     * 
     * @param worldId the world identifier
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return new coordinates object
     * @since 0.0.1
     */
    @NotNull
    Coordinates createCoordinates(@NotNull NamespacedId worldId, int x, int y, int z);

    /**
     * Creates a region handle for easier repeated operations.
     * 
     * @param center the center coordinates of the region
     * @param radius the radius of the region (in blocks)
     * @return region handle for the specified area
     * @since 0.0.1
     */
    @NotNull
    RegionHandle createRegionHandle(@NotNull Coordinates center, int radius);

    /**
     * Handle for performing operations within a specific region.
     * 
     * @since 0.0.1
     */
    interface RegionHandle {

        /**
         * Gets the center coordinates of this region.
         * 
         * @return the center coordinates
         * @since 0.0.1
         */
        @NotNull
        Coordinates getCenter();

        /**
         * Gets the radius of this region.
         * 
         * @return the radius in blocks
         * @since 0.0.1
         */
        int getRadius();

        /**
         * Executes a task within this region.
         * 
         * @param task the task to execute
         * @param <T> the return type
         * @return future containing the task result
         * @since 0.0.1
         */
        @NotNull
        <T> CompletableFuture<T> execute(@NotNull Supplier<T> task);

        /**
         * Executes a runnable task within this region.
         * 
         * @param task the task to execute
         * @return future that completes when the task is done
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> execute(@NotNull Runnable task);

        /**
         * Checks if this region is currently loaded.
         * 
         * @return future containing true if the region is loaded
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Boolean> isLoaded();

        /**
         * Ensures this region is loaded.
         * 
         * @return future that completes when the region is loaded
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> ensureLoaded();

        /**
         * Checks if the given coordinates are within this region.
         * 
         * @param coordinates the coordinates to check
         * @return true if the coordinates are within this region
         * @since 0.0.1
         */
        boolean contains(@NotNull Coordinates coordinates);
    }
}
