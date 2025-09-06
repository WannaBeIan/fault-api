package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

/**
 * Service for spawning particle effects in the world.
 * 
 * <p>This service provides a declarative way to create particle effects
 * without depending directly on Bukkit/Paper particle APIs. The implementation
 * maps particle specifications to the appropriate native calls.
 * 
 * <p>Particle effects are sent to players based on audience selectors,
 * allowing for targeted effects that are only visible to specific players
 * or groups.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and may be called from any thread
 */
public interface ParticleService {
    
    /**
     * Spawns particles according to the specification for selected players.
     * 
     * <p>The particles are sent to all players matched by the audience
     * selector who are in range to see the effect. Players who cannot
     * see the particle location are not sent the effect.
     * 
     * @param spec the particle effect specification
     * @param to the audience selector for targeting players
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Particle rendering is handled client-side after network transmission.
     */
    void spawn(@NotNull ParticleSpec spec, @NotNull AudienceSelector to);
    
    /**
     * Spawns particles for all players in range of the effect.
     * 
     * <p>This is a convenience method equivalent to calling spawn() with
     * an audience selector that includes all nearby players.
     * 
     * @param spec the particle effect specification
     * @throws IllegalArgumentException if spec is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void spawnForAll(@NotNull ParticleSpec spec);
    
    /**
     * Spawns particles in a line between two points.
     * 
     * <p>This creates a line of particle effects by interpolating between
     * the start and end coordinates. The density parameter controls how
     * many particles are created per block distance.
     * 
     * @param particleType the type of particle to spawn
     * @param start the starting coordinates
     * @param end the ending coordinates
     * @param density particles per block distance
     * @param to the audience selector for targeting players
     * @throws IllegalArgumentException if any parameter is null or density is not positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void spawnLine(
        @NotNull NamespacedId particleType,
        @NotNull Coordinates start,
        @NotNull Coordinates end,
        double density,
        @NotNull AudienceSelector to
    );
    
    /**
     * Spawns particles in a circle at the specified location.
     * 
     * <p>Creates a circular pattern of particles on the XZ plane at the
     * given Y coordinate. The radius and particle count determine the
     * appearance of the circle.
     * 
     * @param particleType the type of particle to spawn
     * @param center the center coordinates of the circle
     * @param radius the radius of the circle
     * @param particleCount how many particles to use for the circle
     * @param to the audience selector for targeting players
     * @throws IllegalArgumentException if any parameter is null, radius is not positive, or particleCount is not positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void spawnCircle(
        @NotNull NamespacedId particleType,
        @NotNull Coordinates center,
        double radius,
        int particleCount,
        @NotNull AudienceSelector to
    );
    
    /**
     * Spawns particles in a sphere around the specified location.
     * 
     * <p>Creates a spherical pattern of particles with the given radius.
     * Particles are distributed evenly across the surface of the sphere.
     * 
     * @param particleType the type of particle to spawn
     * @param center the center coordinates of the sphere
     * @param radius the radius of the sphere
     * @param particleCount how many particles to use for the sphere
     * @param to the audience selector for targeting players
     * @throws IllegalArgumentException if any parameter is null, radius is not positive, or particleCount is not positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void spawnSphere(
        @NotNull NamespacedId particleType,
        @NotNull Coordinates center,
        double radius,
        int particleCount,
        @NotNull AudienceSelector to
    );
    
    /**
     * Spawns a trail of particles following a path.
     * 
     * <p>Creates particle effects along a path defined by multiple waypoints.
     * Particles are interpolated between each waypoint pair.
     * 
     * @param particleType the type of particle to spawn
     * @param path the waypoints that define the trail path
     * @param density particles per block distance
     * @param to the audience selector for targeting players
     * @throws IllegalArgumentException if any parameter is null, path is empty, or density is not positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    void spawnTrail(
        @NotNull NamespacedId particleType,
        @NotNull java.util.List<Coordinates> path,
        double density,
        @NotNull AudienceSelector to
    );
    
    /**
     * Creates a particle effect that follows an entity or player.
     * 
     * <p>The particles will track the target's movement for the specified
     * duration. This is useful for visual effects that should stay attached
     * to moving objects.
     * 
     * @param particleType the type of particle to spawn
     * @param targetPlayer the player to follow
     * @param duration how long the effect should last
     * @param particlesPerSecond how many particles to spawn per second
     * @param to the audience selector for targeting players
     * @return a future that completes when the effect ends
     * @throws IllegalArgumentException if any parameter is null, duration is not positive, or particlesPerSecond is not positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          The effect automatically stops if the target player goes offline.
     */
    @NotNull java.util.concurrent.CompletableFuture<Void> spawnFollowing(
        @NotNull NamespacedId particleType,
        @NotNull java.util.UUID targetPlayer,
        @NotNull java.time.Duration duration,
        int particlesPerSecond,
        @NotNull AudienceSelector to
    );
    
    /**
     * Checks if a particle type is supported by the server.
     * 
     * @param particleType the particle type to check
     * @return true if the particle type is available
     * @throws IllegalArgumentException if particleType is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    boolean isParticleSupported(@NotNull NamespacedId particleType);
    
    /**
     * Gets a list of all supported particle types.
     * 
     * @return list of supported particle type identifiers
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull java.util.List<NamespacedId> getSupportedParticles();
}