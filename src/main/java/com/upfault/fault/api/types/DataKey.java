package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Type-safe key for persistent data storage with namespace support.
 * 
 * <p>DataKeys provide compile-time type safety for storing and retrieving
 * data from persistent storage systems. Each key is associated with a specific
 * data type and namespace to prevent type confusion and key conflicts.
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Define keys for different data types
 * DataKey<Integer> PLAYER_LEVEL = DataKey.of("myplugin", "player_level", Integer.class);
 * DataKey<String> LAST_LOCATION = DataKey.of("myplugin", "last_location", String.class);
 * DataKey<Boolean> IS_VIP = DataKey.of("myplugin", "is_vip", Boolean.class);
 * 
 * // Keys can be used with DataView for type-safe operations
 * DataView playerData = // obtained from storage
 * int level = playerData.get(PLAYER_LEVEL).orElse(1);
 * playerData.set(PLAYER_LEVEL, level + 1);
 * }</pre>
 * 
 * <p><strong>Threading:</strong> DataKey instances are immutable and thread-safe.
 * 
 * @param <T> the type of data this key references
 * @since 0.0.1
 * @apiNote DataKeys are designed for use with persistent data storage systems
 */
public final class DataKey<T> {
    
    private final NamespacedId id;
    private final Class<T> type;
    private final T defaultValue;
    
    private DataKey(@NotNull NamespacedId id, @NotNull Class<T> type, T defaultValue) {
        this.id = Objects.requireNonNull(id, "DataKey ID cannot be null");
        this.type = Objects.requireNonNull(type, "DataKey type cannot be null");
        this.defaultValue = defaultValue;
    }
    
    /**
     * Creates a DataKey with the specified namespace, key, and type.
     * 
     * @param namespace the namespace for this key
     * @param key the key name
     * @param type the data type class
     * @param <T> the data type
     * @return new DataKey instance
     * @throws IllegalArgumentException if any parameter is null
     */
    public static <T> @NotNull DataKey<T> of(@NotNull String namespace, @NotNull String key, @NotNull Class<T> type) {
        return new DataKey<>(new NamespacedId(namespace, key), type, null);
    }
    
    /**
     * Creates a DataKey with a default value.
     * 
     * @param namespace the namespace for this key
     * @param key the key name
     * @param type the data type class
     * @param defaultValue the default value (may be null)
     * @param <T> the data type
     * @return new DataKey instance
     * @throws IllegalArgumentException if namespace, key, or type is null
     */
    public static <T> @NotNull DataKey<T> withDefault(@NotNull String namespace, @NotNull String key, @NotNull Class<T> type, T defaultValue) {
        return new DataKey<>(new NamespacedId(namespace, key), type, defaultValue);
    }
    
    /**
     * Creates a DataKey with a NamespacedId.
     * 
     * @param id the namespaced identifier
     * @param type the data type class
     * @param <T> the data type
     * @return new DataKey instance
     * @throws IllegalArgumentException if any parameter is null
     */
    public static <T> @NotNull DataKey<T> of(@NotNull NamespacedId id, @NotNull Class<T> type) {
        return new DataKey<>(id, type, null);
    }
    
    /**
     * Creates a DataKey with a NamespacedId and default value.
     * 
     * @param id the namespaced identifier
     * @param type the data type class
     * @param defaultValue the default value (may be null)
     * @param <T> the data type
     * @return new DataKey instance
     * @throws IllegalArgumentException if id or type is null
     */
    public static <T> @NotNull DataKey<T> withDefault(@NotNull NamespacedId id, @NotNull Class<T> type, T defaultValue) {
        return new DataKey<>(id, type, defaultValue);
    }
    
    /**
     * Gets the namespaced identifier for this key.
     * 
     * @return the key identifier
     */
    public @NotNull NamespacedId getId() {
        return id;
    }
    
    /**
     * Gets the data type class for this key.
     * 
     * @return the type class
     */
    public @NotNull Class<T> getType() {
        return type;
    }
    
    /**
     * Gets the default value for this key.
     * 
     * @return the default value, or null if none is set
     */
    public T getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Checks if this key has a default value.
     * 
     * @return true if a default value is set
     */
    public boolean hasDefaultValue() {
        return defaultValue != null;
    }
    
    /**
     * Gets the namespace portion of this key.
     * 
     * @return the namespace string
     */
    public @NotNull String getNamespace() {
        return id.namespace();
    }
    
    /**
     * Gets the key portion of this key.
     * 
     * @return the key string
     */
    public @NotNull String getKey() {
        return id.value();
    }
    
    /**
     * Validates that a value is compatible with this key's type.
     * 
     * @param value the value to validate
     * @return true if the value is assignable to this key's type
     */
    public boolean isValidValue(Object value) {
        return value == null || type.isInstance(value);
    }
    
    /**
     * Casts a value to this key's type with validation.
     * 
     * @param value the value to cast
     * @return the cast value
     * @throws ClassCastException if the value is not compatible with this key's type
     */
    @SuppressWarnings("unchecked")
    public T castValue(Object value) {
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new ClassCastException("Value of type " + value.getClass().getSimpleName() + 
                " cannot be cast to " + type.getSimpleName() + " for key " + id);
        }
        return (T) value;
    }
    
    /**
     * Creates a new DataKey with a different default value.
     * 
     * @param newDefault the new default value
     * @return new DataKey with updated default
     */
    public @NotNull DataKey<T> withDefault(T newDefault) {
        return new DataKey<>(id, type, newDefault);
    }
    
    /**
     * Creates a new DataKey without a default value.
     * 
     * @return new DataKey without default
     */
    public @NotNull DataKey<T> withoutDefault() {
        return new DataKey<>(id, type, null);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DataKey<?> other)) return false;
        return Objects.equals(id, other.id) && Objects.equals(type, other.type);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
    
    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DataKey[").append(id).append(", type=").append(type.getSimpleName());
        if (hasDefaultValue()) {
            sb.append(", default=").append(defaultValue);
        }
        sb.append("]");
        return sb.toString();
    }
}