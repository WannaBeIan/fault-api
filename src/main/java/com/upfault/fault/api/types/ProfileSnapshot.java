package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Immutable snapshot of a player profile with attributes.
 * 
 * @param profileId the profile identifier
 * @param lastSaved when the profile was last saved
 * @param version the profile format version
 * @param attributes the profile attributes map
 * 
 * @since 0.0.1
 * @apiNote Immutable snapshot that requires builder for modifications
 */
public record ProfileSnapshot(
    @NotNull ProfileId profileId,
    @NotNull Instant lastSaved,
    int version,
    @NotNull Map<String, Object> attributes
) {
    
    public ProfileSnapshot {
        if (profileId == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
        }
        if (lastSaved == null) {
            throw new IllegalArgumentException("Last saved time cannot be null");
        }
        if (version < 0) {
            throw new IllegalArgumentException("Version cannot be negative: " + version);
        }
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes cannot be null");
        }
        // Make the attributes map immutable
        attributes = Map.copyOf(attributes);
    }
    
    /**
     * Creates an empty profile snapshot.
     * 
     * @param playerId the player UUID
     * @param version the profile version
     * @return empty profile snapshot
     */
    public static @NotNull ProfileSnapshot empty(@NotNull UUID playerId, int version) {
        return new ProfileSnapshot(
            ProfileId.of(playerId),
            Instant.now(),
            version,
            Map.of()
        );
    }
    
    /**
     * Gets an attribute value by key.
     * 
     * @param key the attribute key
     * @param <T> the expected value type
     * @return optional containing the value if present and correct type
     */
    public <T> @NotNull Optional<T> getAttribute(@NotNull AttributeKey<T> key) {
        Object value = attributes.get(key.getFullKey());
        if (value != null && key.isCompatibleValue(value)) {
            return Optional.of(key.castValue(value));
        }
        return Optional.empty();
    }
    
    /**
     * Gets an attribute value with a default.
     * 
     * @param key the attribute key
     * @param defaultValue the default value if not present
     * @param <T> the expected value type
     * @return the attribute value or default
     */
    public <T> @NotNull T getAttributeOrDefault(@NotNull AttributeKey<T> key, @NotNull T defaultValue) {
        return getAttribute(key).orElse(defaultValue);
    }
    
    /**
     * Checks if an attribute exists.
     * 
     * @param key the attribute key
     * @return true if the attribute exists
     */
    public boolean hasAttribute(@NotNull AttributeKey<?> key) {
        return attributes.containsKey(key.getFullKey());
    }
    
    /**
     * Gets all attribute keys that match a namespace.
     * 
     * @param namespace the namespace to filter by
     * @return set of attribute keys in the namespace
     */
    public @NotNull Set<String> getAttributeKeysInNamespace(@NotNull String namespace) {
        String prefix = namespace + ":";
        return attributes.keySet().stream()
                        .filter(key -> key.startsWith(prefix))
                        .collect(java.util.stream.Collectors.toSet());
    }
    
    /**
     * Gets the player UUID from the profile ID.
     * 
     * @return the player UUID
     */
    public @NotNull UUID getPlayerId() {
        return profileId.playerId();
    }
    
    /**
     * Creates a builder for modifying this profile.
     * 
     * @return profile builder with current values
     */
    public @NotNull Builder toBuilder() {
        return new Builder(this);
    }
    
    /**
     * Builder for creating and modifying ProfileSnapshot instances.
     */
    public static class Builder {
        private ProfileId profileId;
        private Instant lastSaved;
        private int version;
        private Map<String, Object> attributes;
        
        /**
         * Creates a new builder.
         * 
         * @param playerId the player UUID
         * @param version the profile version
         */
        public Builder(@NotNull UUID playerId, int version) {
            this.profileId = ProfileId.of(playerId);
            this.lastSaved = Instant.now();
            this.version = version;
            this.attributes = new HashMap<>();
        }
        
        /**
         * Creates a builder from an existing snapshot.
         * 
         * @param snapshot the snapshot to copy
         */
        public Builder(@NotNull ProfileSnapshot snapshot) {
            this.profileId = snapshot.profileId;
            this.lastSaved = snapshot.lastSaved;
            this.version = snapshot.version;
            this.attributes = new HashMap<>(snapshot.attributes);
        }
        
        /**
         * Sets an attribute value.
         * 
         * @param key the attribute key
         * @param value the value to set
         * @param <T> the value type
         * @return this builder
         */
        public <T> @NotNull Builder setAttribute(@NotNull AttributeKey<T> key, @NotNull T value) {
            if (!key.isCompatibleValue(value)) {
                throw new IllegalArgumentException("Value type incompatible with key: " + 
                                                 value.getClass() + " vs " + key.type());
            }
            attributes.put(key.getFullKey(), value);
            return this;
        }
        
        /**
         * Removes an attribute.
         * 
         * @param key the attribute key
         * @return this builder
         */
        public @NotNull Builder removeAttribute(@NotNull AttributeKey<?> key) {
            attributes.remove(key.getFullKey());
            return this;
        }
        
        /**
         * Sets the last saved timestamp.
         * 
         * @param lastSaved the timestamp
         * @return this builder
         */
        public @NotNull Builder lastSaved(@NotNull Instant lastSaved) {
            this.lastSaved = lastSaved;
            return this;
        }
        
        /**
         * Sets the profile version.
         * 
         * @param version the version
         * @return this builder
         */
        public @NotNull Builder version(int version) {
            this.version = version;
            return this;
        }
        
        /**
         * Builds the profile snapshot.
         * 
         * @return the built snapshot
         */
        public @NotNull ProfileSnapshot build() {
            return new ProfileSnapshot(profileId, lastSaved, version, attributes);
        }
    }
    
    @Override
    public @NotNull String toString() {
        return String.format("ProfileSnapshot[%s, v%d, %d attributes, saved %s]",
                           profileId.playerId(), version, attributes.size(), lastSaved);
    }
}
