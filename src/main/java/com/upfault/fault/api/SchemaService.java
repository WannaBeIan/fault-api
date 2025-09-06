package com.upfault.fault.api;

import com.upfault.fault.api.types.NamespacedId;
import com.upfault.fault.api.types.OperationResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing data schemas with validation, versioning, and migration support.
 * 
 * <p>This service provides a structured way to define, validate, and evolve data
 * schemas across plugin versions, ensuring data integrity and compatibility.
 * 
 * <p>Example usage:
 * <pre>{@code
 * SchemaService schemas = Fault.service(SchemaService.class);
 * if (schemas != null) {
 *     // Define a schema
 *     Schema playerSchema = Schema.builder("player_data")
 *         .version(1)
 *         .field("name", FieldType.STRING, true)
 *         .field("level", FieldType.INTEGER, false)
 *         .field("achievements", FieldType.STRING_ARRAY, false)
 *         .build();
 *     
 *     schemas.registerSchema(new NamespacedId("myplugin", "player"), playerSchema);
 *     
 *     // Validate data against schema
 *     Map<String, Object> data = Map.of(
 *         "name", "Steve",
 *         "level", 42,
 *         "achievements", List.of("first_steps", "getting_wood")
 *     );
 *     
 *     schemas.validate(new NamespacedId("myplugin", "player"), data).thenAccept(result -> {
 *         if (result instanceof ValidationResult.Valid) {
 *             // Data is valid, proceed with storage
 *         } else {
 *             // Handle validation errors
 *         }
 *     });
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations are thread-safe. Schema validation
 * is performed on background threads for performance.
 * 
 * @since 0.0.1
 * @apiNote Schemas support versioning and automatic migration between versions
 */
public interface SchemaService {

    /**
     * Registers a schema for validation and migration.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during registration.
     * 
     * @param id the schema identifier
     * @param schema the schema definition
     * @return future that completes when registration is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> registerSchema(@NotNull NamespacedId id, @NotNull Schema schema);

    /**
     * Unregisters a schema.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param id the schema identifier
     * @return future containing true if the schema was removed
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> unregisterSchema(@NotNull NamespacedId id);

    /**
     * Gets a registered schema by identifier.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param id the schema identifier
     * @return future containing the schema, or null if not found
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Schema> getSchema(@NotNull NamespacedId id);

    /**
     * Gets all registered schema identifiers.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing set of schema identifiers
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<NamespacedId>> getRegisteredSchemas();

    /**
     * Validates data against a schema.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes validation on background thread.
     * 
     * @param schemaId the schema identifier
     * @param data the data to validate
     * @return future containing validation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<ValidationResult> validate(@NotNull NamespacedId schemaId, @NotNull Map<String, Object> data);

    /**
     * Validates and migrates data to the latest schema version.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes migration on background thread.
     * 
     * @param schemaId the schema identifier
     * @param data the data to migrate
     * @param currentVersion the current data version
     * @return future containing migration result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<MigrationResult> migrate(@NotNull NamespacedId schemaId, @NotNull Map<String, Object> data, int currentVersion);

    /**
     * Registers a migration between schema versions.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during registration.
     * 
     * @param schemaId the schema identifier
     * @param fromVersion the source version
     * @param toVersion the target version
     * @param migration the migration implementation
     * @return future that completes when registration is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> registerMigration(@NotNull NamespacedId schemaId, int fromVersion, int toVersion, @NotNull SchemaMigration migration);

    /**
     * Gets the latest version of a schema.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param schemaId the schema identifier
     * @return future containing the latest version, or 0 if schema not found
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> getLatestVersion(@NotNull NamespacedId schemaId);

    /**
     * Checks if a migration path exists between two versions.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param schemaId the schema identifier
     * @param fromVersion the source version
     * @param toVersion the target version
     * @return future containing true if migration path exists
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> canMigrate(@NotNull NamespacedId schemaId, int fromVersion, int toVersion);

    /**
     * Schema definition with fields and validation rules.
     * 
     * @param name the schema name
     * @param version the schema version
     * @param fields map of field names to field definitions
     * 
     * @since 0.0.1
     */
    record Schema(
        @NotNull String name,
        int version,
        @NotNull Map<String, FieldDefinition> fields
    ) {

        /**
         * Creates a new schema builder.
         * 
         * @param name the schema name
         * @return new schema builder
         */
        public static @NotNull SchemaBuilder builder(@NotNull String name) {
            return new SchemaBuilder(name);
        }

        /**
         * Gets a field definition by name.
         * 
         * @param fieldName the field name
         * @return field definition, or null if not found
         */
        public @Nullable FieldDefinition getField(@NotNull String fieldName) {
            return fields.get(fieldName);
        }

        /**
         * Checks if a field is required.
         * 
         * @param fieldName the field name
         * @return true if the field is required
         */
        public boolean isRequired(@NotNull String fieldName) {
            FieldDefinition field = fields.get(fieldName);
            return field != null && field.required();
        }

        /**
         * Gets all required field names.
         * 
         * @return set of required field names
         */
        public @NotNull Set<String> getRequiredFields() {
            return fields.entrySet().stream()
                .filter(entry -> entry.getValue().required())
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
        }
    }

    /**
     * Field definition within a schema.
     * 
     * @param name the field name
     * @param type the field data type
     * @param required whether the field is required
     * @param defaultValue default value if field is missing
     * 
     * @since 0.0.1
     */
    record FieldDefinition(
        @NotNull String name,
        @NotNull FieldType type,
        boolean required,
        @Nullable Object defaultValue
    ) {

        /**
         * Creates a required field definition.
         * 
         * @param name the field name
         * @param type the field type
         * @return required field definition
         */
        public static @NotNull FieldDefinition required(@NotNull String name, @NotNull FieldType type) {
            return new FieldDefinition(name, type, true, null);
        }

        /**
         * Creates an optional field definition with default value.
         * 
         * @param name the field name
         * @param type the field type
         * @param defaultValue the default value
         * @return optional field definition
         */
        public static @NotNull FieldDefinition optional(@NotNull String name, @NotNull FieldType type, @Nullable Object defaultValue) {
            return new FieldDefinition(name, type, false, defaultValue);
        }

        /**
         * Validates a value against this field definition.
         * 
         * @param value the value to validate
         * @return true if valid for this field
         */
        public boolean isValid(@Nullable Object value) {
            if (value == null) {
                return !required || defaultValue != null;
            }
            return type.isValidValue(value);
        }
    }

    /**
     * Supported field data types.
     * 
     * @since 0.0.1
     */
    enum FieldType {
        STRING(String.class),
        INTEGER(Integer.class),
        LONG(Long.class),
        DOUBLE(Double.class),
        BOOLEAN(Boolean.class),
        STRING_ARRAY(String[].class),
        INTEGER_ARRAY(Integer[].class),
        MAP(Map.class),
        LIST(List.class);

        private final Class<?> javaType;

        FieldType(Class<?> javaType) {
            this.javaType = javaType;
        }

        /**
         * Gets the corresponding Java type.
         * 
         * @return the Java class
         */
        public @NotNull Class<?> getJavaType() {
            return javaType;
        }

        /**
         * Validates if a value is compatible with this field type.
         * 
         * @param value the value to check
         * @return true if the value matches this type
         */
        public boolean isValidValue(@Nullable Object value) {
            if (value == null) {
                return true;
            }
            return switch (this) {
                case STRING -> value instanceof String;
                case INTEGER -> value instanceof Integer || value instanceof Long;
                case LONG -> value instanceof Long || value instanceof Integer;
                case DOUBLE -> value instanceof Double || value instanceof Float || value instanceof Integer;
                case BOOLEAN -> value instanceof Boolean;
                case STRING_ARRAY -> value instanceof String[] || (value instanceof List<?> list && list.stream().allMatch(String.class::isInstance));
                case INTEGER_ARRAY -> value instanceof Integer[] || (value instanceof List<?> list && list.stream().allMatch(Integer.class::isInstance));
                case MAP -> value instanceof Map;
                case LIST -> value instanceof List;
            };
        }
    }

    /**
     * Result of schema validation.
     * 
     * @since 0.0.1
     */
    sealed interface ValidationResult permits ValidationResult.Valid, ValidationResult.Invalid {

        /**
         * Successful validation result.
         * 
         * @param data the validated data (may include defaults)
         */
        record Valid(@NotNull Map<String, Object> data) implements ValidationResult {

            /**
             * Gets the validated data.
             * 
             * @return the validated data map
             */
            public @NotNull Map<String, Object> getData() {
                return data;
            }
        }

        /**
         * Failed validation result.
         * 
         * @param errors list of validation errors
         */
        record Invalid(@NotNull List<String> errors) implements ValidationResult {

            /**
             * Creates an invalid result with a single error.
             * 
             * @param error the error message
             * @return invalid validation result
             */
            public static @NotNull Invalid single(@NotNull String error) {
                return new Invalid(List.of(error));
            }

            /**
             * Gets the first validation error.
             * 
             * @return the first error message
             */
            public @NotNull String getFirstError() {
                return errors.isEmpty() ? "Unknown validation error" : errors.get(0);
            }

            /**
             * Gets the number of validation errors.
             * 
             * @return error count
             */
            public int getErrorCount() {
                return errors.size();
            }
        }

        /**
         * Checks if the validation was successful.
         * 
         * @return true if validation passed
         */
        default boolean isValid() {
            return this instanceof Valid;
        }

        /**
         * Checks if the validation failed.
         * 
         * @return true if validation failed
         */
        default boolean isInvalid() {
            return this instanceof Invalid;
        }
    }

    /**
     * Result of schema migration.
     * 
     * @since 0.0.1
     */
    sealed interface MigrationResult permits MigrationResult.Success, MigrationResult.Failure {

        /**
         * Successful migration result.
         * 
         * @param data the migrated data
         * @param newVersion the target version after migration
         */
        record Success(@NotNull Map<String, Object> data, int newVersion) implements MigrationResult {

            /**
             * Gets the migrated data.
             * 
             * @return the migrated data map
             */
            public @NotNull Map<String, Object> getData() {
                return data;
            }
        }

        /**
         * Failed migration result.
         * 
         * @param error the migration error message
         * @param cause the underlying exception
         */
        record Failure(@NotNull String error, @Nullable Throwable cause) implements MigrationResult {

            /**
             * Creates a failure result with just an error message.
             * 
             * @param error the error message
             * @return migration failure result
             */
            public static @NotNull Failure of(@NotNull String error) {
                return new Failure(error, null);
            }

            /**
             * Creates a failure result with error and cause.
             * 
             * @param error the error message
             * @param cause the underlying exception
             * @return migration failure result
             */
            public static @NotNull Failure of(@NotNull String error, @NotNull Throwable cause) {
                return new Failure(error, cause);
            }
        }

        /**
         * Checks if the migration was successful.
         * 
         * @return true if migration succeeded
         */
        default boolean isSuccess() {
            return this instanceof Success;
        }

        /**
         * Checks if the migration failed.
         * 
         * @return true if migration failed
         */
        default boolean isFailure() {
            return this instanceof Failure;
        }
    }

    /**
     * Interface for schema migration implementations.
     * 
     * @since 0.0.1
     */
    interface SchemaMigration {

        /**
         * Migrates data from one schema version to another.
         * 
         * <p><strong>Threading:</strong> May be called on any thread, should be non-blocking.
         * 
         * @param data the source data to migrate
         * @return future containing the migrated data
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Map<String, Object>> migrate(@NotNull Map<String, Object> data);

        /**
         * Gets a description of what this migration does.
         * 
         * <p><strong>Threading:</strong> Thread-safe, non-blocking.
         * 
         * @return migration description
         * @since 0.0.1
         */
        default @NotNull String getDescription() {
            return "Schema migration";
        }
    }

    /**
     * Builder for creating schema definitions.
     * 
     * @since 0.0.1
     */
    class SchemaBuilder {
        private final String name;
        private int version = 1;
        private final Map<String, FieldDefinition> fields = new java.util.HashMap<>();

        private SchemaBuilder(@NotNull String name) {
            this.name = name;
        }

        /**
         * Sets the schema version.
         * 
         * @param version the version number
         * @return this builder
         */
        public @NotNull SchemaBuilder version(int version) {
            this.version = version;
            return this;
        }

        /**
         * Adds a required field to the schema.
         * 
         * @param name the field name
         * @param type the field type
         * @return this builder
         */
        public @NotNull SchemaBuilder field(@NotNull String name, @NotNull FieldType type) {
            return field(name, type, true, null);
        }

        /**
         * Adds a field to the schema.
         * 
         * @param name the field name
         * @param type the field type
         * @param required whether the field is required
         * @return this builder
         */
        public @NotNull SchemaBuilder field(@NotNull String name, @NotNull FieldType type, boolean required) {
            return field(name, type, required, null);
        }

        /**
         * Adds a field with default value to the schema.
         * 
         * @param name the field name
         * @param type the field type
         * @param required whether the field is required
         * @param defaultValue the default value
         * @return this builder
         */
        public @NotNull SchemaBuilder field(@NotNull String name, @NotNull FieldType type, boolean required, @Nullable Object defaultValue) {
            fields.put(name, new FieldDefinition(name, type, required, defaultValue));
            return this;
        }

        /**
         * Builds the schema.
         * 
         * @return the completed schema
         */
        public @NotNull Schema build() {
            return new Schema(name, version, Map.copyOf(fields));
        }
    }
}