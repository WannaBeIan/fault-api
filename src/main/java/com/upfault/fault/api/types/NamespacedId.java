package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a namespaced identifier (namespace:value).
 * 
 * <p>Namespaced identifiers are used throughout Minecraft and this API
 * to prevent naming conflicts between different plugins and systems.
 * 
 * @param namespace the namespace (e.g., "minecraft", "myplugin")
 * @param value the value within the namespace (e.g., "diamond_sword", "config")
 * 
 * @since 0.0.1
 * @apiNote Follows Minecraft's namespaced key format
 */
public record NamespacedId(@NotNull String namespace, @NotNull String value) {
    
    /**
     * Pattern for valid namespace and value characters.
     * Allows lowercase letters, numbers, underscores, dots, and hyphens.
     */
    private static final String VALID_PATTERN = "[a-z0-9._-]+";
    
    /**
     * Creates a new NamespacedId with validation.
     * 
     * @param namespace the namespace (must be valid)
     * @param value the value (must be valid)
     * @throws IllegalArgumentException if namespace or value is invalid
     */
    public NamespacedId {
        if (namespace == null || namespace.isEmpty()) {
            throw new IllegalArgumentException("Namespace cannot be null or empty");
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
        if (!isValidIdentifier(namespace)) {
            throw new IllegalArgumentException("Invalid namespace: " + namespace + 
                " (must match " + VALID_PATTERN + ")");
        }
        if (!isValidIdentifier(value)) {
            throw new IllegalArgumentException("Invalid value: " + value + 
                " (must match " + VALID_PATTERN + ")");
        }
    }
    
    /**
     * Creates a NamespacedId from a string representation.
     * 
     * @param namespacedString the string in format "namespace:value"
     * @return the parsed NamespacedId
     * @throws IllegalArgumentException if the format is invalid
     */
    public static @NotNull NamespacedId fromString(@NotNull String namespacedString) {
        if (namespacedString == null || namespacedString.isEmpty()) {
            throw new IllegalArgumentException("Namespaced string cannot be null or empty");
        }
        
        int colonIndex = namespacedString.indexOf(':');
        if (colonIndex == -1) {
            throw new IllegalArgumentException("Invalid namespaced string format: " + namespacedString + 
                " (must be 'namespace:value')");
        }
        if (colonIndex == 0) {
            throw new IllegalArgumentException("Namespace cannot be empty in: " + namespacedString);
        }
        if (colonIndex == namespacedString.length() - 1) {
            throw new IllegalArgumentException("Value cannot be empty in: " + namespacedString);
        }
        
        String namespace = namespacedString.substring(0, colonIndex);
        String value = namespacedString.substring(colonIndex + 1);
        
        return new NamespacedId(namespace, value);
    }
    
    /**
     * Creates a NamespacedId with "minecraft" namespace.
     * 
     * @param value the value
     * @return NamespacedId with minecraft namespace
     */
    public static @NotNull NamespacedId minecraft(@NotNull String value) {
        return new NamespacedId("minecraft", value);
    }
    
    /**
     * Creates a NamespacedId with "bukkit" namespace.
     * 
     * @param value the value
     * @return NamespacedId with bukkit namespace
     */
    public static @NotNull NamespacedId bukkit(@NotNull String value) {
        return new NamespacedId("bukkit", value);
    }
    
    /**
     * Creates a NamespacedId with "paper" namespace.
     * 
     * @param value the value
     * @return NamespacedId with paper namespace
     */
    public static @NotNull NamespacedId paper(@NotNull String value) {
        return new NamespacedId("paper", value);
    }
    
    /**
     * Checks if an identifier string is valid.
     * 
     * @param identifier the identifier to check
     * @return true if the identifier matches the valid pattern
     */
    private static boolean isValidIdentifier(@NotNull String identifier) {
        return identifier.matches(VALID_PATTERN);
    }
    
    /**
     * Checks if this NamespacedId has the minecraft namespace.
     * 
     * @return true if namespace is "minecraft"
     */
    public boolean isMinecraft() {
        return "minecraft".equals(namespace);
    }
    
    /**
     * Checks if this NamespacedId has the bukkit namespace.
     * 
     * @return true if namespace is "bukkit"
     */
    public boolean isBukkit() {
        return "bukkit".equals(namespace);
    }
    
    /**
     * Checks if this NamespacedId has the paper namespace.
     * 
     * @return true if namespace is "paper"
     */
    public boolean isPaper() {
        return "paper".equals(namespace);
    }
    
    /**
     * Checks if this NamespacedId has the specified namespace.
     * 
     * @param namespace the namespace to check
     * @return true if the namespace matches
     */
    public boolean hasNamespace(@NotNull String namespace) {
        return this.namespace.equals(namespace);
    }
    
    /**
     * Creates a new NamespacedId with a different value in the same namespace.
     * 
     * @param newValue the new value
     * @return new NamespacedId with the same namespace but different value
     */
    public @NotNull NamespacedId withValue(@NotNull String newValue) {
        return new NamespacedId(this.namespace, newValue);
    }
    
    /**
     * Creates a new NamespacedId with a different namespace but same value.
     * 
     * @param newNamespace the new namespace
     * @return new NamespacedId with different namespace but same value
     */
    public @NotNull NamespacedId withNamespace(@NotNull String newNamespace) {
        return new NamespacedId(newNamespace, this.value);
    }
    
    /**
     * Gets the full string representation in "namespace:value" format.
     * 
     * @return the full namespaced string
     */
    public @NotNull String asString() {
        return namespace + ":" + value;
    }
    
    /**
     * Compares this NamespacedId to another for sorting.
     * 
     * @param other the other NamespacedId
     * @return comparison result
     */
    public int compareTo(@NotNull NamespacedId other) {
        int namespaceCompare = this.namespace.compareTo(other.namespace);
        if (namespaceCompare != 0) {
            return namespaceCompare;
        }
        return this.value.compareTo(other.value);
    }
    
    @Override
    public @NotNull String toString() {
        return asString();
    }
}
