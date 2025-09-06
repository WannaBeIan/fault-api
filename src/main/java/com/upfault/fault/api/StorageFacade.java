package com.upfault.fault.api;

import com.upfault.fault.api.types.Page;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Repository-style storage contracts with query and transaction support.
 * 
 * <p>This facade provides a high-level interface for data persistence without
 * exposing implementation details like JDBC or specific database drivers.
 * 
 * <p>Example usage:
 * <pre>{@code
 * StorageFacade storage = Fault.service(StorageFacade.class);
 * 
 * // Get a repository for a specific entity type
 * Repository<PlayerData, UUID> playerRepo = storage.getRepository(
 *     PlayerData.class, UUID.class
 * );
 * 
 * // Save an entity
 * PlayerData player = new PlayerData(playerId, "Steve", 100);
 * playerRepo.save(player).thenRun(() -> 
 *     System.out.println("Player data saved!")
 * );
 * 
 * // Query with pagination
 * Query<PlayerData> query = storage.query(PlayerData.class)
 *     .where("level").greaterThan(50)
 *     .orderBy("name")
 *     .page(0, 20);
 * 
 * query.execute().thenAccept(page -> {
 *     System.out.println("Found " + page.totalElements() + " players");
 *     page.content().forEach(System.out::println);
 * });
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations return CompletableFuture and
 * may be executed on background threads. Repository instances are thread-safe.
 * 
 * @since 0.0.1
 * @apiNote Implementations should handle connection pooling and transaction management
 */
public interface StorageFacade {

    /**
     * Gets a repository for the specified entity type.
     * 
     * @param entityType the entity class
     * @param idType the ID type class
     * @param <T> the entity type
     * @param <ID> the ID type
     * @return repository instance for the entity type
     * @since 0.0.1
     */
    @NotNull
    <T, ID> Repository<T, ID> getRepository(@NotNull Class<T> entityType, @NotNull Class<ID> idType);

    /**
     * Creates a query builder for the specified entity type.
     * 
     * @param entityType the entity class to query
     * @param <T> the entity type
     * @return query builder instance
     * @since 0.0.1
     */
    @NotNull
    <T> QueryBuilder<T> query(@NotNull Class<T> entityType);

    /**
     * Begins a new transaction.
     * 
     * @return future containing the transaction handle
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<TransactionHandle> beginTransaction();

    /**
     * Executes a block of code within a transaction.
     * 
     * @param transactionBlock the code to execute in the transaction
     * @param <T> the return type
     * @return future containing the result of the transaction block
     * @since 0.0.1
     */
    @NotNull
    <T> CompletableFuture<T> withTransaction(@NotNull TransactionBlock<T> transactionBlock);

    /**
     * Checks if the storage system is available and healthy.
     * 
     * @return future containing true if storage is healthy
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> isHealthy();

    /**
     * Repository interface for CRUD operations on a specific entity type.
     * 
     * @param <T> the entity type
     * @param <ID> the ID type
     * @since 0.0.1
     */
    interface Repository<T, ID> {

        /**
         * Finds an entity by its ID.
         * 
         * @param id the entity ID
         * @return future containing the entity, or empty if not found
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Optional<T>> findById(@NotNull ID id);

        /**
         * Finds all entities of this type.
         * 
         * @return future containing list of all entities
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<List<T>> findAll();

        /**
         * Saves an entity (insert or update).
         * 
         * @param entity the entity to save
         * @return future containing the saved entity
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<T> save(@NotNull T entity);

        /**
         * Saves multiple entities.
         * 
         * @param entities the entities to save
         * @return future containing the saved entities
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<List<T>> saveAll(@NotNull Iterable<T> entities);

        /**
         * Deletes an entity by ID.
         * 
         * @param id the ID of the entity to delete
         * @return future that completes when deletion is done
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> deleteById(@NotNull ID id);

        /**
         * Deletes an entity.
         * 
         * @param entity the entity to delete
         * @return future that completes when deletion is done
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> delete(@NotNull T entity);

        /**
         * Checks if an entity with the given ID exists.
         * 
         * @param id the entity ID
         * @return future containing true if the entity exists
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Boolean> existsById(@NotNull ID id);

        /**
         * Counts the total number of entities.
         * 
         * @return future containing the entity count
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Long> count();
    }

    /**
     * Query builder for constructing database queries.
     * 
     * @param <T> the entity type being queried
     * @since 0.0.1
     */
    interface QueryBuilder<T> {

        /**
         * Starts a WHERE clause.
         * 
         * @param fieldName the field name to filter on
         * @return condition builder for the field
         * @since 0.0.1
         */
        @NotNull
        ConditionBuilder<T> where(@NotNull String fieldName);

        /**
         * Adds an ORDER BY clause.
         * 
         * @param fieldName the field to order by
         * @return this query builder
         * @since 0.0.1
         */
        @NotNull
        QueryBuilder<T> orderBy(@NotNull String fieldName);

        /**
         * Adds an ORDER BY DESC clause.
         * 
         * @param fieldName the field to order by descending
         * @return this query builder
         * @since 0.0.1
         */
        @NotNull
        QueryBuilder<T> orderByDesc(@NotNull String fieldName);

        /**
         * Limits the number of results.
         * 
         * @param limit the maximum number of results
         * @return this query builder
         * @since 0.0.1
         */
        @NotNull
        QueryBuilder<T> limit(int limit);

        /**
         * Sets pagination parameters.
         * 
         * @param page the page number (0-based)
         * @param size the page size
         * @return this query builder
         * @since 0.0.1
         */
        @NotNull
        QueryBuilder<T> page(int page, int size);

        /**
         * Executes the query and returns results.
         * 
         * @return future containing the query results
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<List<T>> execute();

        /**
         * Executes the query and returns paginated results.
         * 
         * @return future containing the paginated results
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Page<T>> executePage();

        /**
         * Executes the query and returns the first result.
         * 
         * @return future containing the first result, or empty
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Optional<T>> executeFirst();
    }

    /**
     * Builder for query conditions.
     * 
     * @param <T> the entity type being queried
     * @since 0.0.1
     */
    interface ConditionBuilder<T> {

        /**
         * Adds an equals condition.
         * 
         * @param value the value to compare against
         * @return the query builder
         * @since 0.0.1
         */
        @NotNull
        QueryBuilder<T> equalTo(@Nullable Object value);

        /**
         * Adds a not equals condition.
         * 
         * @param value the value to compare against
         * @return the query builder
         * @since 0.0.1
         */
        @NotNull
        QueryBuilder<T> notEquals(@Nullable Object value);

        /**
         * Adds a greater than condition.
         * 
         * @param value the value to compare against
         * @return the query builder
         * @since 0.0.1
         */
        @NotNull
        QueryBuilder<T> greaterThan(@NotNull Comparable<?> value);

        /**
         * Adds a less than condition.
         * 
         * @param value the value to compare against
         * @return the query builder
         * @since 0.0.1
         */
        @NotNull
        QueryBuilder<T> lessThan(@NotNull Comparable<?> value);

        /**
         * Adds an IN condition.
         * 
         * @param values the values to match against
         * @return the query builder
         * @since 0.0.1
         */
        @NotNull
        QueryBuilder<T> in(@NotNull Object... values);

        /**
         * Adds a LIKE condition for string matching.
         * 
         * @param pattern the pattern to match (supports % wildcards)
         * @return the query builder
         * @since 0.0.1
         */
        @NotNull
        QueryBuilder<T> like(@NotNull String pattern);
    }

    /**
     * Handle for managing database transactions.
     * 
     * @since 0.0.1
     */
    interface TransactionHandle {

        /**
         * Commits the transaction.
         * 
         * @return future that completes when commit is done
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> commit();

        /**
         * Rolls back the transaction.
         * 
         * @return future that completes when rollback is done
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Void> rollback();

        /**
         * Checks if the transaction is still active.
         * 
         * @return true if the transaction is active
         * @since 0.0.1
         */
        boolean isActive();
    }

    /**
     * Functional interface for transaction blocks.
     * 
     * @param <T> the return type
     * @since 0.0.1
     */
    @FunctionalInterface
    interface TransactionBlock<T> {

        /**
         * Executes the transaction block.
         * 
         * @param transaction the transaction handle
         * @return the result of the transaction
         * @throws Exception if the transaction fails
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<T> execute(@NotNull TransactionHandle transaction) throws Exception;
    }
}
