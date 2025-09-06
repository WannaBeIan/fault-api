package com.upfault.fault.api;

import com.upfault.fault.api.types.AttributeKey;
import com.upfault.fault.api.types.NamespacedId;
import com.upfault.fault.api.types.ProfileId;
import com.upfault.fault.api.types.ProfileSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Manages per-player profile lifecycle with attribute storage and versioning.
 * 
 * <p>This service provides a structured way to store and retrieve player-specific
 * data with namespace support, versioning, and async I/O patterns.
 * 
 * <p>Example usage:
 * <pre>{@code
 * ProfileService profiles = Fault.service(ProfileService.class);
 * 
 * // Define attribute keys
 * AttributeKey<Integer> LEVEL_KEY = AttributeKey.of("myplugin", "level", Integer.class);
 * AttributeKey<String> TITLE_KEY = AttributeKey.of("myplugin", "title", String.class);
 * 
 * // Load a profile
 * CompletableFuture<ProfileSnapshot> future = profiles.loadProfile(playerId);
 * future.thenAccept(profile -> {
 *     int level = profile.getAttribute(LEVEL_KEY).orElse(1);
 *     String title = profile.getAttribute(TITLE_KEY).orElse("Newcomer");
 *     
 *     // Modify attributes
 *     ProfileSnapshot updated = profile.toBuilder()
 *         .setAttribute(LEVEL_KEY, level + 1)
 *         .setAttribute(TITLE_KEY, "Veteran")
 *         .build();
 *     
 *     // Save changes
 *     profiles.saveProfile(updated);
 * });
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All I/O operations return CompletableFuture
 * and may be executed on background threads. Profile objects themselves are
 * immutable and thread-safe.
 * 
 * @since 0.0.1
 * @apiNote Profiles are namespaced to prevent conflicts between plugins
 */
public interface ProfileService {

    /**
     * Loads a player's profile asynchronously.
     * 
     * <p>If the profile doesn't exist, a new empty profile is created.
     * 
     * @param playerId the player's UUID
     * @return future containing the profile snapshot
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<ProfileSnapshot> loadProfile(@NotNull UUID playerId);

    /**
     * Loads a player's profile by ProfileId.
     * 
     * @param profileId the profile identifier
     * @return future containing the profile snapshot
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<ProfileSnapshot> loadProfile(@NotNull ProfileId profileId);

    /**
     * Saves a profile snapshot asynchronously.
     * 
     * @param profile the profile to save
     * @return future that completes when saving is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> saveProfile(@NotNull ProfileSnapshot profile);

    /**
     * Checks if a profile exists for the given player.
     * 
     * @param playerId the player's UUID
     * @return future containing true if the profile exists
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> profileExists(@NotNull UUID playerId);

    /**
     * Deletes a player's profile permanently.
     * 
     * @param playerId the player's UUID
     * @return future that completes when deletion is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> deleteProfile(@NotNull UUID playerId);

    /**
     * Gets all attribute keys used in a player's profile.
     * 
     * @param playerId the player's UUID
     * @return future containing set of attribute keys
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<AttributeKey<?>>> getProfileAttributeKeys(@NotNull UUID playerId);

    /**
     * Gets all attribute keys for a specific namespace.
     * 
     * @param playerId the player's UUID
     * @param namespace the namespace to filter by
     * @return future containing set of attribute keys in the namespace
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<AttributeKey<?>>> getProfileAttributeKeys(@NotNull UUID playerId, @NotNull NamespacedId namespace);

    /**
     * Creates a new empty profile for a player.
     * 
     * @param playerId the player's UUID
     * @return a new empty profile snapshot
     * @since 0.0.1
     */
    @NotNull
    ProfileSnapshot createEmptyProfile(@NotNull UUID playerId);

    /**
     * Gets the current version of the profile format.
     * 
     * @return the profile format version
     * @since 0.0.1
     */
    int getProfileVersion();

    /**
     * Migrates a profile from an older version to the current version.
     * 
     * @param profile the profile to migrate
     * @return future containing the migrated profile
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<ProfileSnapshot> migrateProfile(@NotNull ProfileSnapshot profile);

    /**
     * Bulk loads multiple profiles at once.
     * 
     * @param playerIds the player UUIDs to load
     * @return future containing map of player ID to profile snapshot
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Map<UUID, ProfileSnapshot>> loadProfiles(@NotNull Set<UUID> playerIds);

    /**
     * Bulk saves multiple profiles at once.
     * 
     * @param profiles the profiles to save
     * @return future that completes when all profiles are saved
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> saveProfiles(@NotNull Set<ProfileSnapshot> profiles);

    /**
     * Searches for profiles containing a specific attribute key.
     * 
     * @param attributeKey the attribute key to search for
     * @param <T> the attribute value type
     * @return future containing set of player UUIDs that have this attribute
     * @since 0.0.1
     */
    @NotNull
    <T> CompletableFuture<Set<UUID>> findProfilesWithAttribute(@NotNull AttributeKey<T> attributeKey);

    /**
     * Searches for profiles where an attribute matches a specific value.
     * 
     * @param attributeKey the attribute key to search for
     * @param value the value to match
     * @param <T> the attribute value type
     * @return future containing set of player UUIDs with matching attributes
     * @since 0.0.1
     */
    @NotNull
    <T> CompletableFuture<Set<UUID>> findProfilesWithAttributeValue(@NotNull AttributeKey<T> attributeKey, @NotNull T value);
}