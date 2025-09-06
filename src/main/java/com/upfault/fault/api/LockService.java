package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Service for distributed locking and synchronization across plugin instances.
 * 
 * <p>This service provides distributed locks that can be used to coordinate
 * access to shared resources across multiple server instances or plugin
 * components. Locks have automatic expiration to prevent deadlocks from
 * crashed processes or network partitions.
 * 
 * <p>The primary method {@link #withLock} provides a safe way to execute
 * code within a lock context, automatically handling acquisition, release,
 * and error conditions.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and return CompletableFutures for async operations
 */
public interface LockService {
    
    /**
     * Executes a task while holding a distributed lock.
     * 
     * <p>This method attempts to acquire the specified lock, execute the
     * provided task, and then release the lock. If lock acquisition fails
     * (due to contention or timeout), the task is not executed and a
     * failure result is returned.
     * 
     * <p>The lock is automatically released when the task completes, whether
     * successfully or with an exception. If the lock expires during task
     * execution, the task continues but may conflict with other lock holders.
     * 
     * @param key the unique identifier for this lock
     * @param ttl how long to hold the lock before automatic expiration
     * @param body the task to execute while holding the lock
     * @return future containing the result of executing the task
     * @throws IllegalArgumentException if any parameter is null or ttl is not positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          The body task may run on a different thread than the caller.
     */
    @NotNull CompletableFuture<OperationResult> withLock(
        @NotNull NamespacedId key,
        @NotNull Duration ttl,
        @NotNull Supplier<CompletableFuture<OperationResult>> body
    );
    
    /**
     * Attempts to acquire a lock without executing any code.
     * 
     * <p>This provides lower-level lock control for cases where the
     * withLock pattern is not sufficient. The caller is responsible
     * for releasing the lock using {@link #releaseLock}.
     * 
     * @param key the unique identifier for this lock
     * @param ttl how long to hold the lock before automatic expiration
     * @return future containing the lock acquisition result
     * @throws IllegalArgumentException if any parameter is null or ttl is not positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Manual lock management requires careful release handling.
     */
    @NotNull CompletableFuture<OperationResult> acquireLock(@NotNull NamespacedId key, @NotNull Duration ttl);
    
    /**
     * Releases a previously acquired lock.
     * 
     * <p>Only the lock holder can release a lock. If the lock has already
     * expired or been released, this operation succeeds silently.
     * 
     * @param key the unique identifier for the lock to release
     * @return future containing the release result
     * @throws IllegalArgumentException if key is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Releasing an unowned lock returns a failure result.
     */
    @NotNull CompletableFuture<OperationResult> releaseLock(@NotNull NamespacedId key);
    
    /**
     * Extends the expiration time of a held lock.
     * 
     * <p>This can be used to extend a lock's lifetime during long-running
     * operations. Only the current lock holder can extend the lock.
     * 
     * @param key the unique identifier for the lock to extend
     * @param additionalTime additional time to add to the lock's expiration
     * @return future containing the extension result
     * @throws IllegalArgumentException if any parameter is null or additionalTime is not positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Extensions fail if the lock has already expired or is not held.
     */
    @NotNull CompletableFuture<OperationResult> extendLock(@NotNull NamespacedId key, @NotNull Duration additionalTime);
    
    /**
     * Checks if a lock is currently held and by whom.
     * 
     * @param key the unique identifier for the lock to check
     * @return future containing lock information, or empty if not held
     * @throws IllegalArgumentException if key is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<java.util.Optional<LockInfo>> getLockInfo(@NotNull NamespacedId key);
    
    /**
     * Lists all currently held locks (for debugging/monitoring).
     * 
     * @param page the page number (0-based)
     * @param size the page size (must be positive)
     * @return future containing the page of lock information
     * @throws IllegalArgumentException if page is negative or size is non-positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Large lock counts may impact performance.
     */
    @NotNull CompletableFuture<Page<LockInfo>> getAllLocks(int page, int size);
    
    /**
     * Information about a distributed lock.
     * 
     * @param key the lock identifier
     * @param holder identifier of the lock holder (implementation-specific)
     * @param acquiredAt when the lock was acquired
     * @param expiresAt when the lock will automatically expire
     */
    record LockInfo(
        @NotNull NamespacedId key,
        @NotNull String holder,
        @NotNull java.time.Instant acquiredAt,
        @NotNull java.time.Instant expiresAt
    ) {
        public LockInfo {
            if (key == null) throw new IllegalArgumentException("Lock key cannot be null");
            if (holder == null || holder.trim().isEmpty()) throw new IllegalArgumentException("Lock holder cannot be null or empty");
            if (acquiredAt == null) throw new IllegalArgumentException("Acquired timestamp cannot be null");
            if (expiresAt == null) throw new IllegalArgumentException("Expires timestamp cannot be null");
            if (expiresAt.isBefore(acquiredAt)) throw new IllegalArgumentException("Expiry cannot be before acquisition time");
        }
        
        /**
         * Checks if this lock has expired.
         * 
         * @return true if the current time is past the expiration time
         */
        public boolean isExpired() {
            return java.time.Instant.now().isAfter(expiresAt);
        }
        
        /**
         * Gets the remaining time until this lock expires.
         * 
         * @return duration until expiry, or Duration.ZERO if already expired
         */
        public @NotNull Duration getTimeRemaining() {
            var now = java.time.Instant.now();
            if (now.isAfter(expiresAt)) {
                return Duration.ZERO;
            }
            return Duration.between(now, expiresAt);
        }
        
        /**
         * Gets how long this lock has been held.
         * 
         * @return duration since acquisition
         */
        public @NotNull Duration getAge() {
            return Duration.between(acquiredAt, java.time.Instant.now());
        }
    }
}