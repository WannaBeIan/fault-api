package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Thread-safe view for reading and writing persistent data with type safety.
 * 
 * <p>DataView provides a structured interface for accessing persistent data
 * with compile-time type safety through DataKey objects. It supports nested
 * data structures and efficient bulk operations.
 * 
 * <p>Example usage:
 * <pre>{@code
 * DataView playerData = // obtained from storage service
 * 
 * // Type-safe data access
 * DataKey<Integer> LEVEL_KEY = DataKey.of("myplugin", "level", Integer.class);
 * DataKey<String> NAME_KEY = DataKey.of("myplugin", "display_name", String.class);
 * 
 * // Reading data with defaults
 * int level = playerData.get(LEVEL_KEY).orElse(1);
 * String name = playerData.get(NAME_KEY).orElse("Unknown");
 * 
 * // Writing data
 * playerData.set(LEVEL_KEY, level + 1);
 * playerData.set(NAME_KEY, "VIP Player");
 * 
 * // Bulk operations
 * Map<DataKey<?>, Object> changes = Map.of(
 *     LEVEL_KEY, 50,
 *     NAME_KEY, "Max Level Player"
 * );
 * playerData.setAll(changes);
 * 
 * // Nested data views
 * DataView statsView = playerData.getView("stats");
 * if (statsView != null) {
 *     DataKey<Long> PLAYTIME_KEY = DataKey.of("core", "playtime", Long.class);
 *     long playtime = statsView.get(PLAYTIME_KEY).orElse(0L);
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations are thread-safe. Modifications
 * may be atomic or eventually consistent depending on the implementation.
 * 
 * @since 0.0.1
 * @apiNote DataViews may be backed by different storage mechanisms with varying consistency guarantees
 */
public interface DataView {

    /**
     * Gets a value for the specified key with type safety.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the data key
     * @param <T> the expected data type
     * @return optional containing the value, or empty if not found
     * @since 0.0.1
     */
    @NotNull
    <T> Optional<T> get(@NotNull DataKey<T> key);

    /**
     * Gets a value or returns the key's default value.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the data key (must have a default value)
     * @param <T> the expected data type
     * @return the value or key's default value
     * @throws IllegalArgumentException if key has no default value
     * @since 0.0.1
     */
    @NotNull
    <T> T getOrDefault(@NotNull DataKey<T> key);

    /**
     * Gets a value or returns the specified default.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the data key
     * @param defaultValue the default value to return if not found
     * @param <T> the expected data type
     * @return the value or provided default
     * @since 0.0.1
     */
    @NotNull
    <T> T getOrDefault(@NotNull DataKey<T> key, @NotNull T defaultValue);

    /**
     * Sets a value for the specified key.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during write.
     * 
     * @param key the data key
     * @param value the value to store (may be null)
     * @param <T> the data type
     * @since 0.0.1
     */
    <T> void set(@NotNull DataKey<T> key, @Nullable T value);

    /**
     * Removes a value for the specified key.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during removal.
     * 
     * @param key the data key
     * @return true if a value was removed
     * @since 0.0.1
     */
    boolean remove(@NotNull DataKey<?> key);

    /**
     * Checks if a key has a value stored.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the data key
     * @return true if the key has a value
     * @since 0.0.1
     */
    boolean contains(@NotNull DataKey<?> key);

    /**
     * Gets all keys that have values in this view.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return set of keys with values
     * @since 0.0.1
     */
    @NotNull
    Set<DataKey<?>> getKeys();

    /**
     * Gets all keys for a specific namespace.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param namespace the namespace to filter by
     * @return set of keys in the namespace
     * @since 0.0.1
     */
    @NotNull
    Set<DataKey<?>> getKeys(@NotNull String namespace);

    /**
     * Gets the number of stored values.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return number of key-value pairs
     * @since 0.0.1
     */
    int size();

    /**
     * Checks if this view is empty.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return true if no values are stored
     * @since 0.0.1
     */
    boolean isEmpty();

    /**
     * Clears all data from this view.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during clear.
     * 
     * @since 0.0.1
     */
    void clear();

    /**
     * Clears all data for a specific namespace.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during clear.
     * 
     * @param namespace the namespace to clear
     * @return number of entries removed
     * @since 0.0.1
     */
    int clear(@NotNull String namespace);

    /**
     * Sets multiple key-value pairs atomically.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during bulk write.
     * 
     * @param data map of keys to values
     * @since 0.0.1
     */
    void setAll(@NotNull Map<DataKey<?>, Object> data);

    /**
     * Gets multiple values at once.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param keys the keys to retrieve
     * @return map of keys to their values (only contains keys that have values)
     * @since 0.0.1
     */
    @NotNull
    Map<DataKey<?>, Object> getAll(@NotNull Set<DataKey<?>> keys);

    /**
     * Removes multiple keys at once.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during bulk removal.
     * 
     * @param keys the keys to remove
     * @return number of keys that were removed
     * @since 0.0.1
     */
    int removeAll(@NotNull Set<DataKey<?>> keys);

    /**
     * Gets a nested view for hierarchical data organization.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param path the nested path (e.g., "stats", "inventory.armor")
     * @return nested data view, or null if path doesn't exist
     * @since 0.0.1
     */
    @Nullable
    DataView getView(@NotNull String path);

    /**
     * Creates or gets a nested view for hierarchical data organization.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during creation.
     * 
     * @param path the nested path (e.g., "stats", "inventory.armor")
     * @return nested data view (created if necessary)
     * @since 0.0.1
     */
    @NotNull
    DataView createView(@NotNull String path);

    /**
     * Checks if a nested path exists.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param path the nested path
     * @return true if the path exists
     * @since 0.0.1
     */
    boolean hasView(@NotNull String path);

    /**
     * Gets all nested view paths.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return set of nested paths
     * @since 0.0.1
     */
    @NotNull
    Set<String> getViewPaths();

    /**
     * Removes a nested view and all its data.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during removal.
     * 
     * @param path the nested path to remove
     * @return true if the view was removed
     * @since 0.0.1
     */
    boolean removeView(@NotNull String path);

    /**
     * Exports all data as a plain map for serialization.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return map representation of all data
     * @since 0.0.1
     */
    @NotNull
    Map<String, Object> toMap();

    /**
     * Imports data from a plain map.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during import.
     * 
     * @param data the data to import
     * @param replace whether to replace existing data or merge
     * @since 0.0.1
     */
    void fromMap(@NotNull Map<String, Object> data, boolean replace);

    /**
     * Creates a read-only copy of this view's current state.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return immutable snapshot of current data
     * @since 0.0.1
     */
    @NotNull
    DataView snapshot();

    /**
     * Gets metadata about this data view.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return view metadata
     * @since 0.0.1
     */
    @NotNull
    ViewMetadata getMetadata();

    /**
     * Metadata about a DataView's state and capabilities.
     * 
     * @param readOnly whether this view is read-only
     * @param persistent whether changes are persisted automatically
     * @param path the hierarchical path of this view
     * @param lastModified when this view was last modified (may be null)
     * 
     * @since 0.0.1
     */
    record ViewMetadata(
        boolean readOnly,
        boolean persistent,
        @NotNull String path,
        @Nullable java.time.Instant lastModified
    ) {

        /**
         * Creates metadata for a root view.
         * 
         * @param readOnly whether the view is read-only
         * @param persistent whether changes are persisted
         * @return root view metadata
         */
        public static @NotNull ViewMetadata root(boolean readOnly, boolean persistent) {
            return new ViewMetadata(readOnly, persistent, "", null);
        }

        /**
         * Creates metadata for a nested view.
         * 
         * @param readOnly whether the view is read-only
         * @param persistent whether changes are persisted
         * @param path the nested path
         * @return nested view metadata
         */
        public static @NotNull ViewMetadata nested(boolean readOnly, boolean persistent, @NotNull String path) {
            return new ViewMetadata(readOnly, persistent, path, null);
        }

        /**
         * Checks if this is a root view.
         * 
         * @return true if path is empty
         */
        public boolean isRoot() {
            return path.isEmpty();
        }

        /**
         * Checks if this view supports modifications.
         * 
         * @return true if not read-only
         */
        public boolean isWritable() {
            return !readOnly;
        }

        /**
         * Gets the depth of nesting (number of path segments).
         * 
         * @return nesting depth
         */
        public int getDepth() {
            return path.isEmpty() ? 0 : path.split("\\.").length;
        }
    }
}