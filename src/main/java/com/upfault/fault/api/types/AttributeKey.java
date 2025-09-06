package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Type-safe key for profile attributes.
 * 
 * @param namespace the namespace for this attribute
 * @param key the key within the namespace
 * @param type the expected value type class
 * @param <T> the value type
 * 
 * @since 0.0.1
 * @apiNote Provides type safety for profile attribute access
 */
public record AttributeKey<T>(
    @NotNull String namespace,
    @NotNull String key,
    @NotNull Class<T> type
) {
    
    /**
     * Creates a new AttributeKey with validation.
     * 
     * @param namespace the namespace (cannot be null or empty)
     * @param key the key (cannot be null or empty)
     * @param type the value type class (cannot be null)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public AttributeKey {
        if (namespace == null || namespace.trim().isEmpty()) {
            throw new IllegalArgumentException("Namespace cannot be null or empty");
        }
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
    }
    
    /**
     * Creates an AttributeKey with the specified parameters.
     * 
     * @param namespace the namespace
     * @param key the key
     * @param type the value type class
     * @param <T> the value type
     * @return new AttributeKey instance
     */
    public static <T> @NotNull AttributeKey<T> of(@NotNull String namespace, @NotNull String key, @NotNull Class<T> type) {
        return new AttributeKey<>(namespace, key, type);
    }
    
    /**
     * Creates a String AttributeKey.
     * 
     * @param namespace the namespace
     * @param key the key
     * @return String AttributeKey
     */
    public static @NotNull AttributeKey<String> stringKey(@NotNull String namespace, @NotNull String key) {
        return new AttributeKey<>(namespace, key, String.class);
    }
    
    /**
     * Creates an Integer AttributeKey.
     * 
     * @param namespace the namespace
     * @param key the key
     * @return Integer AttributeKey
     */
    public static @NotNull AttributeKey<Integer> intKey(@NotNull String namespace, @NotNull String key) {
        return new AttributeKey<>(namespace, key, Integer.class);
    }
    
    /**
     * Creates a Long AttributeKey.
     * 
     * @param namespace the namespace
     * @param key the key
     * @return Long AttributeKey
     */
    public static @NotNull AttributeKey<Long> longKey(@NotNull String namespace, @NotNull String key) {
        return new AttributeKey<>(namespace, key, Long.class);
    }
    
    /**
     * Creates a Double AttributeKey.
     * 
     * @param namespace the namespace
     * @param key the key
     * @return Double AttributeKey
     */
    public static @NotNull AttributeKey<Double> doubleKey(@NotNull String namespace, @NotNull String key) {
        return new AttributeKey<>(namespace, key, Double.class);
    }
    
    /**
     * Creates a Boolean AttributeKey.
     * 
     * @param namespace the namespace
     * @param key the key
     * @return Boolean AttributeKey
     */
    public static @NotNull AttributeKey<Boolean> booleanKey(@NotNull String namespace, @NotNull String key) {
        return new AttributeKey<>(namespace, key, Boolean.class);
    }
    
    /**
     * Gets the full namespaced key.
     * 
     * @return namespace:key format
     */
    public @NotNull String getFullKey() {
        return namespace + ":" + key;
    }
    
    /**
     * Checks if this key matches the given namespace.
     * 
     * @param namespace the namespace to check
     * @return true if the namespace matches
     */
    public boolean hasNamespace(@NotNull String namespace) {
        return this.namespace.equals(namespace);
    }
    
    /**
     * Checks if a value is compatible with this key's type.
     * 
     * @param value the value to check
     * @return true if the value can be assigned to this key's type
     */
    public boolean isCompatibleValue(@NotNull Object value) {
        return type.isInstance(value);
    }
    
    /**
     * Casts a value to this key's type.
     * 
     * @param value the value to cast
     * @return the cast value
     * @throws ClassCastException if the value is not compatible
     */
    public @NotNull T castValue(@NotNull Object value) {
        return type.cast(value);
    }
    
    @Override
    public @NotNull String toString() {
        return String.format("AttributeKey[%s:%s<%s>]", namespace, key, type.getSimpleName());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AttributeKey<?> that = (AttributeKey<?>) obj;
        return namespace.equals(that.namespace) && 
               key.equals(that.key) && 
               type.equals(that.type);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(namespace, key, type);
    }
}
