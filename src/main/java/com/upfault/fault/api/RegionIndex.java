package com.upfault.fault.api;

import com.upfault.fault.api.types.Coordinates;
import com.upfault.fault.api.types.NamespacedId;
import com.upfault.fault.api.types.Region;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Service for efficiently indexing and querying regions.
 * 
 * <p>This service provides spatial indexing capabilities for regions,
 * allowing fast lookup of which regions contain specific coordinates.
 * 
 * @since 0.0.1
 * @apiNote Thread safety: All methods are safe to call from any thread
 */
public interface RegionIndex {
    
    /**
     * Adds a region to the index.
     * 
     * <p>The region will be indexed for fast spatial queries. If a region
     * with the same ID already exists, it will be replaced.
     * 
     * @param id the unique identifier for this region
     * @param region the region to add
     * 
     * @apiNote Safe to call from any thread. Changes may not be immediately
     *          visible to concurrent queries due to indexing overhead.
     */
    void add(@NotNull NamespacedId id, @NotNull Region region);
    
    /**
     * Removes a region from the index.
     * 
     * @param id the identifier of the region to remove
     * @return true if a region was removed, false if not found
     * 
     * @apiNote Safe to call from any thread
     */
    boolean remove(@NotNull NamespacedId id);
    
    /**
     * Queries for all regions that contain the given coordinates.
     * 
     * <p>This is the primary query method, returning all indexed regions
     * that contain the specified point.
     * 
     * @param coordinates the coordinates to query
     * @return set of region IDs that contain the coordinates
     * 
     * @apiNote Safe to call from any thread. Returns empty set if no
     *          regions contain the coordinates.
     */
    @NotNull Set<NamespacedId> query(@NotNull Coordinates coordinates);
    
    /**
     * Queries for all regions in a specific world.
     * 
     * @param worldId the world to query
     * @return set of region IDs in the specified world
     * 
     * @apiNote Safe to call from any thread
     */
    @NotNull Set<NamespacedId> queryWorld(@NotNull NamespacedId worldId);
    
    /**
     * Gets a region by its ID.
     * 
     * @param id the region identifier
     * @return the region, or null if not found
     * 
     * @apiNote Safe to call from any thread
     */
    Region get(@NotNull NamespacedId id);
    
    /**
     * Checks if a region with the given ID exists in the index.
     * 
     * @param id the region identifier
     * @return true if the region exists
     * 
     * @apiNote Safe to call from any thread
     */
    boolean contains(@NotNull NamespacedId id);
    
    /**
     * Gets the total number of regions in the index.
     * 
     * @return the region count
     * 
     * @apiNote Safe to call from any thread
     */
    int size();
    
    /**
     * Clears all regions from the index.
     * 
     * @apiNote Safe to call from any thread
     */
    void clear();
}