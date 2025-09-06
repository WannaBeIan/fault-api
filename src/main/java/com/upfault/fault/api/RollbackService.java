package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Service for managing reversible block operations and world modifications.
 * 
 * <p>This service provides transactional block operations that can be rolled
 * back if needed. This is essential for features like world editing tools,
 * admin undo commands, or automated systems that need to be able to revert
 * their changes if something goes wrong.
 * 
 * <p>Each transaction is identified by a unique {@link NamespacedId} and
 * tracks all block changes made within that transaction scope. Transactions
 * can be committed (made permanent) or rolled back (reverted) atomically.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and return CompletableFutures for async operations
 */
public interface RollbackService {
    
    /**
     * Begins a new rollback transaction with a descriptive label.
     * 
     * <p>The returned transaction ID should be used for all subsequent
     * block operations that should be tracked under this transaction.
     * The label is used for logging and administrative purposes.
     * 
     * @param label descriptive label for this transaction
     * @return future containing the unique transaction ID
     * @throws IllegalArgumentException if label is null or empty
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Transaction IDs are globally unique.
     */
    @NotNull CompletableFuture<NamespacedId> begin(@NotNull String label);
    
    /**
     * Records a block change within an active transaction.
     * 
     * <p>This method should be called whenever a block is modified as part
     * of a tracked operation. The service will record both the previous
     * and current block states to enable rollback.
     * 
     * @param transactionId the transaction to record this change under
     * @param location the coordinates of the changed block
     * @param previousState the block state before the change
     * @param newState the block state after the change
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null or transaction doesn't exist
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Block states are serialized for storage.
     */
    @NotNull CompletableFuture<OperationResult> recordChange(
        @NotNull NamespacedId transactionId,
        @NotNull Coordinates location,
        @NotNull String previousState,
        @NotNull String newState
    );
    
    /**
     * Commits a transaction, making all changes permanent and clearing rollback data.
     * 
     * <p>Once a transaction is committed, it can no longer be rolled back.
     * The rollback data is discarded to free up storage space.
     * 
     * @param transactionId the transaction to commit
     * @return future containing the operation result
     * @throws IllegalArgumentException if transactionId is null or transaction doesn't exist
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Committed transactions cannot be undone.
     */
    @NotNull CompletableFuture<OperationResult> commit(@NotNull NamespacedId transactionId);
    
    /**
     * Rolls back a transaction, reverting all tracked changes.
     * 
     * <p>This will restore all modified blocks to their previous states
     * as they were when the transaction began. The rollback operation
     * is atomic - either all blocks are reverted or none are.
     * 
     * @param transactionId the transaction to roll back
     * @return future containing the operation result
     * @throws IllegalArgumentException if transactionId is null or transaction doesn't exist
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Large rollbacks may take significant time to complete.
     */
    @NotNull CompletableFuture<OperationResult> rollback(@NotNull NamespacedId transactionId);
    
    /**
     * Gets information about an active transaction.
     * 
     * @param transactionId the transaction to query
     * @return future containing transaction information, or empty if not found
     * @throws IllegalArgumentException if transactionId is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<java.util.Optional<TransactionInfo>> getTransaction(@NotNull NamespacedId transactionId);
    
    /**
     * Lists all active (uncommitted) transactions.
     * 
     * @param page the page number (0-based)
     * @param size the page size (must be positive)
     * @return future containing the page of active transactions
     * @throws IllegalArgumentException if page is negative or size is non-positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Page<TransactionInfo>> getActiveTransactions(int page, int size);
    
    /**
     * Information about a rollback transaction.
     * 
     * @param id the transaction ID
     * @param label the descriptive label
     * @param createdAt when the transaction was created
     * @param blockCount number of blocks modified in this transaction
     * @param status current status of the transaction
     */
    record TransactionInfo(
        @NotNull NamespacedId id,
        @NotNull String label,
        @NotNull java.time.Instant createdAt,
        int blockCount,
        @NotNull TransactionStatus status
    ) {
        public TransactionInfo {
            if (id == null) throw new IllegalArgumentException("Transaction ID cannot be null");
            if (label == null || label.trim().isEmpty()) throw new IllegalArgumentException("Label cannot be null or empty");
            if (createdAt == null) throw new IllegalArgumentException("Created timestamp cannot be null");
            if (blockCount < 0) throw new IllegalArgumentException("Block count cannot be negative");
            if (status == null) throw new IllegalArgumentException("Status cannot be null");
        }
    }
    
    /**
     * The status of a rollback transaction.
     */
    enum TransactionStatus {
        /** Transaction is active and recording changes */
        ACTIVE,
        /** Transaction has been committed (changes are permanent) */
        COMMITTED,
        /** Transaction has been rolled back (changes reverted) */
        ROLLED_BACK
    }
}