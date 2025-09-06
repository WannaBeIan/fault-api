package com.upfault.fault.api;

import com.upfault.fault.api.types.Subscription;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Service for tracking player session lifecycle.
 * 
 * <p>This service tracks when players join and leave the server,
 * providing access to session information and events.
 * 
 * @since 0.0.1
 * @apiNote Thread safety: All methods are safe to call from any thread.
 *          Event listeners are called on the main server thread.
 */
public interface SessionService {
    
    /**
     * Represents a player session with login/logout timestamps.
     * 
     * @param player the player UUID
     * @param loginAt when the player logged in
     * @param logoutAt when the player logged out (empty if still online)
     */
    record Session(
        @NotNull UUID player, 
        @NotNull Instant loginAt, 
        @NotNull Optional<Instant> logoutAt
    ) {}
    
    /**
     * Gets the current session for a player.
     * 
     * @param player the player UUID
     * @return the current session, or empty if the player is not online
     * 
     * @apiNote Safe to call from any thread
     */
    @NotNull Optional<Session> current(@NotNull UUID player);
    
    /**
     * Subscribes to session start events.
     * 
     * <p>The listener will be called whenever a player starts a new session.
     * 
     * @param listener the listener to call on session start
     * @return a subscription that can be used to unsubscribe
     * 
     * @apiNote Safe to call from any thread. The listener will be called
     *          on the main server thread.
     */
    @NotNull Subscription onStart(@NotNull Consumer<Session> listener);
    
    /**
     * Subscribes to session end events.
     * 
     * <p>The listener will be called whenever a player's session ends.
     * The session object will have its logoutAt field populated.
     * 
     * @param listener the listener to call on session end
     * @return a subscription that can be used to unsubscribe
     * 
     * @apiNote Safe to call from any thread. The listener will be called
     *          on the main server thread.
     */
    @NotNull Subscription onEnd(@NotNull Consumer<Session> listener);
}