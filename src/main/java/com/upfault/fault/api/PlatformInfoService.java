package com.upfault.fault.api;

import com.upfault.fault.api.types.TpsSnapshot;
import com.upfault.fault.api.types.Uptime;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Provides runtime information about the Minecraft server platform.
 * 
 * <p>Access this service through the ServicesManager:
 * <pre>{@code
 * PlatformInfoService service = Fault.service(PlatformInfoService.class);
 * if (service != null) {
 *     TpsSnapshot tps = service.getCurrentTps();
 *     System.out.println("Current TPS: " + tps.tps1m());
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All methods are safe to call from any thread.
 * TPS snapshots may be cached for short periods to reduce overhead.
 * 
 * @since 0.0.1
 * @apiNote This service provides read-only platform metrics
 */
public interface PlatformInfoService {

    /**
     * Gets the server brand (e.g., "Paper", "Spigot").
     * 
     * @return the server brand name
     * @since 0.0.1
     */
    @NotNull
    String getServerBrand();

    /**
     * Gets the server version string.
     * 
     * @return the server version
     * @since 0.0.1
     */
    @NotNull
    String getServerVersion();

    /**
     * Checks if the server is running in online mode.
     * 
     * @return true if online mode is enabled
     * @since 0.0.1
     */
    boolean isOnlineMode();

    /**
     * Gets the current TPS (ticks per second) metrics.
     * 
     * <p>The snapshot includes 1-minute, 5-minute, and 15-minute averages
     * plus the current MSPT (milliseconds per tick) average.
     * 
     * @return current TPS snapshot
     * @since 0.0.1
     */
    @NotNull
    TpsSnapshot getCurrentTps();

    /**
     * Gets the server uptime information.
     * 
     * @return server uptime details
     * @since 0.0.1
     */
    @NotNull
    Uptime getUptime();

    /**
     * Gets the current number of online players.
     * 
     * @return current online player count
     * @since 0.0.1
     */
    int getCurrentPlayerCount();

    /**
     * Gets the maximum allowed number of players.
     * 
     * @return maximum player count
     * @since 0.0.1
     */
    int getMaxPlayerCount();

    /**
     * Asynchronously retrieves detailed platform information.
     * 
     * <p>This may include additional metrics that require I/O operations
     * to collect, such as memory usage, thread counts, or plugin statistics.
     * 
     * @return future containing detailed platform info as a string
     * @since 0.0.1
     * @apiNote Use this for diagnostic or administrative displays
     */
    @NotNull
    CompletableFuture<String> getDetailedInfo();
}