package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for double-entry bookkeeping and financial transaction recording.
 * 
 * <p>This service provides a double-entry accounting system where every
 * transaction is recorded as a set of entries that must balance (sum to zero).
 * This ensures financial data integrity and provides an audit trail for
 * all monetary operations in the system.
 * 
 * <p>Accounts are identified using namespaced IDs, allowing for hierarchical
 * account structures. For example: "player:balance:uuid", "shop:revenue:daily",
 * or "tax:collected:monthly".
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and return CompletableFutures for async operations
 */
public interface LedgerService {
    
    /**
     * Posts a balanced set of ledger entries as a single transaction.
     * 
     * <p>All entries must balance - the sum of all amounts must equal zero.
     * This ensures double-entry accounting principles are maintained.
     * The operation is atomic: either all entries are recorded or none are.
     * 
     * @param entries the list of entries that make up this transaction
     * @return future containing the operation result
     * @throws IllegalArgumentException if entries is null, empty, or doesn't balance
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          All entries are recorded atomically.
     */
    @NotNull CompletableFuture<OperationResult> post(@NotNull List<Entry> entries);
    
    /**
     * Retrieves all entries for a specific account within a time range.
     * 
     * @param account the account to query
     * @param from the start time (inclusive)
     * @param to the end time (exclusive)
     * @param page the page number (0-based)
     * @param size the page size (must be positive)
     * @return future containing the page of entries
     * @throws IllegalArgumentException if any parameter is invalid
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Page<Entry>> getEntries(
        @NotNull NamespacedId account,
        @NotNull Instant from,
        @NotNull Instant to,
        int page,
        int size
    );
    
    /**
     * Calculates the balance for a specific account as of a given timestamp.
     * 
     * <p>This sums all entries for the account up to (and including) the
     * specified timestamp. For real-time balance, use {@code Instant.now()}.
     * 
     * @param account the account to calculate balance for
     * @param asOf the timestamp to calculate balance as of
     * @return future containing the account balance
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Balance calculation may be cached for performance.
     */
    @NotNull CompletableFuture<Money> getBalance(@NotNull NamespacedId account, @NotNull Instant asOf);
    
    /**
     * Gets the current balance for a specific account.
     * 
     * <p>This is equivalent to calling {@code getBalance(account, Instant.now())}.
     * 
     * @param account the account to get balance for
     * @return future containing the current account balance
     * @throws IllegalArgumentException if account is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Money> getCurrentBalance(@NotNull NamespacedId account);
    
    /**
     * Retrieves balances for multiple accounts efficiently.
     * 
     * @param accounts the accounts to get balances for
     * @param asOf the timestamp to calculate balances as of
     * @return future containing a map of account to balance
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Batch operations are more efficient than individual calls.
     */
    @NotNull CompletableFuture<java.util.Map<NamespacedId, Money>> getBalances(
        @NotNull List<NamespacedId> accounts,
        @NotNull Instant asOf
    );
    
    /**
     * Lists all accounts that have had activity within a time range.
     * 
     * <p>This is useful for generating reports or finding all active accounts
     * in a given period.
     * 
     * @param from the start time (inclusive)
     * @param to the end time (exclusive)
     * @param page the page number (0-based)
     * @param size the page size (must be positive)
     * @return future containing the page of active account IDs
     * @throws IllegalArgumentException if any parameter is invalid
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Page<NamespacedId>> getActiveAccounts(
        @NotNull Instant from,
        @NotNull Instant to,
        int page,
        int size
    );
    
    /**
     * Creates a simple transfer between two accounts.
     * 
     * <p>This is a convenience method that creates a balanced transaction
     * with two entries: a debit from the source account and a credit to
     * the destination account.
     * 
     * @param from the source account (will be debited)
     * @param to the destination account (will be credited)
     * @param amount the amount to transfer (must be positive)
     * @param memo optional description for the transfer
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is invalid
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          The transfer is atomic.
     */
    @NotNull CompletableFuture<OperationResult> transfer(
        @NotNull NamespacedId from,
        @NotNull NamespacedId to,
        @NotNull Money amount,
        @NotNull String memo
    );
}