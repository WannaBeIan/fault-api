package com.upfault.fault.api;

import com.upfault.fault.api.types.ApiVersion;
import com.upfault.fault.api.types.Capability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Service for querying system capabilities and API versions.
 * 
 * <p>This service allows plugins to detect which optional features are available
 * and check API compatibility before using advanced functionality.
 * 
 * <p>Example usage:
 * <pre>{@code
 * CapabilityService capabilities = Fault.service(CapabilityService.class);
 * if (capabilities != null) {
 *     // Check if economy is available
 *     if (capabilities.has(Capability.ECONOMY)) {
 *         // Use economy features
 *         EconomyFacade economy = Fault.service(EconomyFacade.class);
 *     }
 *     
 *     // Check API compatibility
 *     ApiVersion current = capabilities.apiVersion();
 *     ApiVersion required = new ApiVersion(1, 2, 0);
 *     if (current.isCompatible(required)) {
 *         // Safe to use v1.2+ features
 *     }
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All methods are thread-safe and return immediately
 * without blocking. Capability state is immutable during runtime.
 * 
 * @since 0.0.1
 * @apiNote Capability detection is static during plugin lifecycle - capabilities don't change at runtime
 */
public interface CapabilityService {

    /**
     * Checks if a specific capability is available.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param capability the capability to check
     * @return true if the capability is available and functional
     * @since 0.0.1
     */
    boolean has(@NotNull Capability capability);

    /**
     * Gets the version of a specific capability.
     * 
     * <p>Returns null if the capability is not available.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param capability the capability to query
     * @return the capability version, or null if not available
     * @since 0.0.1
     */
    @Nullable
    ApiVersion versionOf(@NotNull Capability capability);

    /**
     * Gets the current API version.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return the current API version
     * @since 0.0.1
     */
    @NotNull
    ApiVersion apiVersion();

    /**
     * Checks if the current API version is compatible with the required version.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param required the minimum required API version
     * @return true if the current version is compatible
     * @since 0.0.1
     */
    boolean isCompatible(@NotNull ApiVersion required);

    /**
     * Gets all available capabilities.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return array of all available capabilities
     * @since 0.0.1
     */
    @NotNull
    Capability[] getAvailableCapabilities();

    /**
     * Gets diagnostic information about capability availability.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return human-readable capability status
     * @since 0.0.1
     */
    @NotNull
    String getDiagnosticInfo();
}