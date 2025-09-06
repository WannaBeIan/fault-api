package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Service for managing versioned codecs tied to namespaced identifiers.
 * 
 * <p>This service provides a registry for data serialization codecs that
 * can evolve over time. Each codec is identified by a namespaced ID and
 * version number, allowing for backward compatibility and data migration
 * as schemas change.
 * 
 * <p>The registry supports multiple versions of the same schema, enabling
 * gradual migration of data formats while maintaining compatibility with
 * older versions during transition periods.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and support concurrent access
 */
public interface SchemaRegistry {
    
    /**
     * Registers a codec for a specific schema ID and version.
     * 
     * <p>If a codec with the same ID and version already exists, it will
     * be replaced with the new codec. This allows for codec updates and
     * improvements while maintaining version compatibility.
     * 
     * @param id the schema identifier
     * @param version the codec version
     * @param codec the codec implementation
     * @param <T> the type handled by the codec
     * @throws IllegalArgumentException if any parameter is null or version is negative
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    <T> void register(@NotNull NamespacedId id, int version, @NotNull Codec<T> codec);
    
    /**
     * Resolves a codec for a specific schema ID and version.
     * 
     * <p>Returns the codec registered for the exact ID and version combination.
     * If no codec is found for the specific version, this method does not
     * attempt to find compatible codecs - use {@link #resolveCompatible} for that.
     * 
     * @param id the schema identifier
     * @param version the codec version
     * @param <T> the expected type handled by the codec
     * @return the codec if found, empty otherwise
     * @throws IllegalArgumentException if id is null or version is negative
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    <T> @NotNull Optional<Codec<T>> resolve(@NotNull NamespacedId id, int version);
    
    /**
     * Resolves a compatible codec for a schema ID and version.
     * 
     * <p>This method first attempts to find an exact version match. If that
     * fails, it searches for a registered codec that declares compatibility
     * with the requested version through {@link Codec#canDecodeVersion(int)}.
     * 
     * @param id the schema identifier
     * @param version the requested version
     * @param <T> the expected type handled by the codec
     * @return a compatible codec if found, empty otherwise
     * @throws IllegalArgumentException if id is null or version is negative
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Compatibility checks may involve multiple codec evaluations.
     */
    <T> @NotNull Optional<Codec<T>> resolveCompatible(@NotNull NamespacedId id, int version);
    
    /**
     * Gets the latest version of a codec for a schema ID.
     * 
     * <p>Returns the codec with the highest version number registered for
     * the specified schema ID. This is useful for encoding new data with
     * the most current schema version.
     * 
     * @param id the schema identifier
     * @param <T> the expected type handled by the codec
     * @return the latest codec version if found, empty otherwise
     * @throws IllegalArgumentException if id is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    <T> @NotNull Optional<Codec<T>> latest(@NotNull NamespacedId id);
    
    /**
     * Gets the highest version number registered for a schema ID.
     * 
     * @param id the schema identifier
     * @return the highest version number, or empty if no codecs are registered
     * @throws IllegalArgumentException if id is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull Optional<Integer> getLatestVersion(@NotNull NamespacedId id);
    
    /**
     * Lists all version numbers registered for a schema ID.
     * 
     * @param id the schema identifier
     * @return list of registered version numbers in ascending order
     * @throws IllegalArgumentException if id is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull java.util.List<Integer> getVersions(@NotNull NamespacedId id);
    
    /**
     * Lists all registered schema IDs.
     * 
     * @return set of all schema identifiers that have registered codecs
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull java.util.Set<NamespacedId> getSchemaIds();
    
    /**
     * Unregisters a specific codec version.
     * 
     * <p>Removes the codec for the exact ID and version combination.
     * This should be used carefully as it may break compatibility for
     * systems expecting that codec version.
     * 
     * @param id the schema identifier
     * @param version the codec version to remove
     * @return true if a codec was removed, false if none was found
     * @throws IllegalArgumentException if id is null or version is negative
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Use with caution as removal may break existing dependencies.
     */
    boolean unregister(@NotNull NamespacedId id, int version);
    
    /**
     * Unregisters all versions of a schema.
     * 
     * <p>Removes all codecs registered under the specified schema ID.
     * This is a bulk operation that should be used with extreme caution.
     * 
     * @param id the schema identifier
     * @return the number of codec versions that were removed
     * @throws IllegalArgumentException if id is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          This is a destructive operation that may break existing systems.
     */
    int unregisterAll(@NotNull NamespacedId id);
    
    /**
     * Checks if a codec is registered for a specific ID and version.
     * 
     * @param id the schema identifier
     * @param version the codec version
     * @return true if a codec is registered for the exact ID and version
     * @throws IllegalArgumentException if id is null or version is negative
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    boolean hasCodec(@NotNull NamespacedId id, int version);
    
    /**
     * Gets registry statistics for monitoring and debugging.
     * 
     * @return current registry statistics
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull RegistryStats getStats();
    
    /**
     * Statistics about the schema registry.
     * 
     * @param totalSchemas number of unique schema IDs
     * @param totalCodecs total number of registered codecs (all versions)
     * @param averageVersionsPerSchema average number of versions per schema
     */
    record RegistryStats(
        int totalSchemas,
        int totalCodecs,
        double averageVersionsPerSchema
    ) {
        public RegistryStats {
            if (totalSchemas < 0) throw new IllegalArgumentException("Total schemas cannot be negative");
            if (totalCodecs < 0) throw new IllegalArgumentException("Total codecs cannot be negative");
            if (averageVersionsPerSchema < 0) throw new IllegalArgumentException("Average versions cannot be negative");
        }
    }
}