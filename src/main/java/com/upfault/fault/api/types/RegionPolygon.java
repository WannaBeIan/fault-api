package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a polygonal region defined by a series of vertices.
 * 
 * <p>This region type defines a polygon in 2D space (X-Z plane) that extends
 * infinitely in the Y direction, or can be bounded by min/max Y values.
 * 
 * @param worldId the world this polygon exists in
 * @param vertices the vertices of the polygon (at least 3 required)
 * @param minY the minimum Y coordinate (inclusive), or null for no lower bound
 * @param maxY the maximum Y coordinate (inclusive), or null for no upper bound
 * 
 * @since 0.0.1
 */
public record RegionPolygon(
    @NotNull NamespacedId worldId,
    @NotNull List<Coordinates> vertices,
    Integer minY,
    Integer maxY
) implements Region {
    
    /**
     * Creates a new RegionPolygon with validation.
     * 
     * @param worldId the world identifier
     * @param vertices the vertices of the polygon
     * @param minY the minimum Y coordinate
     * @param maxY the maximum Y coordinate
     * @throws IllegalArgumentException if validation fails
     */
    public RegionPolygon {
        if (worldId == null) {
            throw new IllegalArgumentException("World ID cannot be null");
        }
        if (vertices == null || vertices.size() < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 vertices");
        }
        
        // Validate all vertices are in the same world
        for (Coordinates vertex : vertices) {
            if (!worldId.equals(vertex.worldId())) {
                throw new IllegalArgumentException("All vertices must be in the same world: " + worldId);
            }
        }
        
        // Validate Y bounds
        if (minY != null && maxY != null && minY > maxY) {
            throw new IllegalArgumentException("minY cannot be greater than maxY");
        }
        
        // Make vertices immutable
        vertices = List.copyOf(vertices);
    }
    
    /**
     * Creates a polygon without Y bounds (extends infinitely in Y direction).
     * 
     * @param worldId the world identifier
     * @param vertices the vertices of the polygon
     * @return new unbounded polygon
     */
    public static @NotNull RegionPolygon unbounded(@NotNull NamespacedId worldId, @NotNull List<Coordinates> vertices) {
        return new RegionPolygon(worldId, vertices, null, null);
    }
    
    @Override
    public boolean contains(@NotNull Coordinates coordinates) {
        if (!coordinates.worldId().equals(worldId)) {
            return false;
        }
        
        // Check Y bounds if specified
        if (minY != null && coordinates.y() < minY) {
            return false;
        }
        if (maxY != null && coordinates.y() > maxY) {
            return false;
        }
        
        // Use ray casting algorithm to check if point is inside polygon
        return isPointInPolygon(coordinates.x(), coordinates.z());
    }
    
    @Override
    public @NotNull Coordinates getCenter() {
        // Calculate centroid of vertices
        double sumX = 0, sumZ = 0;
        for (Coordinates vertex : vertices) {
            sumX += vertex.x();
            sumZ += vertex.z();
        }
        
        int centerX = (int) (sumX / vertices.size());
        int centerZ = (int) (sumZ / vertices.size());
        
        // Use middle of Y bounds or first vertex's Y if unbounded
        int centerY;
        if (minY != null && maxY != null) {
            centerY = (minY + maxY) / 2;
        } else {
            centerY = vertices.get(0).y();
        }
        
        return new Coordinates(worldId, centerX, centerY, centerZ);
    }
    
    @Override
    public @NotNull NamespacedId getWorld() {
        return worldId;
    }
    
    /**
     * Ray casting algorithm to determine if a point is inside the polygon.
     * 
     * @param x the X coordinate to test
     * @param z the Z coordinate to test
     * @return true if the point is inside the polygon
     */
    private boolean isPointInPolygon(int x, int z) {
        boolean inside = false;
        int j = vertices.size() - 1;
        
        for (int i = 0; i < vertices.size(); i++) {
            Coordinates vi = vertices.get(i);
            Coordinates vj = vertices.get(j);
            
            if (((vi.z() > z) != (vj.z() > z)) &&
                (x < (vj.x() - vi.x()) * (z - vi.z()) / (vj.z() - vi.z()) + vi.x())) {
                inside = !inside;
            }
            j = i;
        }
        
        return inside;
    }
    
    /**
     * Gets the number of vertices in this polygon.
     * 
     * @return the vertex count
     */
    public int getVertexCount() {
        return vertices.size();
    }
    
    /**
     * Checks if this polygon has Y bounds.
     * 
     * @return true if both minY and maxY are specified
     */
    public boolean isBounded() {
        return minY != null && maxY != null;
    }
    
    /**
     * Gets the height of this polygon if it's bounded.
     * 
     * @return the height in blocks, or -1 if unbounded
     */
    public int getHeight() {
        return isBounded() ? maxY - minY + 1 : -1;
    }
    
    /**
     * Creates a new polygon with different Y bounds.
     * 
     * @param newMinY the new minimum Y
     * @param newMaxY the new maximum Y
     * @return new polygon with updated Y bounds
     */
    public @NotNull RegionPolygon withYBounds(Integer newMinY, Integer newMaxY) {
        return new RegionPolygon(worldId, vertices, newMinY, newMaxY);
    }
    
    /**
     * Creates a new unbounded polygon (infinite Y).
     * 
     * @return new unbounded polygon
     */
    public @NotNull RegionPolygon withoutYBounds() {
        return new RegionPolygon(worldId, vertices, null, null);
    }
    
    /**
     * Gets the bounding box that completely contains this polygon.
     * 
     * @return the bounding box
     */
    public @NotNull RegionBox getBoundingBox() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        
        for (Coordinates vertex : vertices) {
            minX = Math.min(minX, vertex.x());
            maxX = Math.max(maxX, vertex.x());
            minZ = Math.min(minZ, vertex.z());
            maxZ = Math.max(maxZ, vertex.z());
        }
        
        int boundMinY = minY != null ? minY : Integer.MIN_VALUE;
        int boundMaxY = maxY != null ? maxY : Integer.MAX_VALUE;
        
        // If unbounded, use reasonable defaults based on vertices
        if (minY == null || maxY == null) {
            int vertexY = vertices.get(0).y();
            boundMinY = minY != null ? minY : vertexY - 128;
            boundMaxY = maxY != null ? maxY : vertexY + 128;
        }
        
        Coordinates min = new Coordinates(worldId, minX, boundMinY, minZ);
        Coordinates max = new Coordinates(worldId, maxX, boundMaxY, maxZ);
        
        return new RegionBox(min, max);
    }
}