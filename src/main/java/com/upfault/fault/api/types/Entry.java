package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/**
 * Represents a double-entry bookkeeping ledger entry.
 * 
 * <p>This record represents a single entry in a double-entry accounting system.
 * Each transaction consists of multiple entries that must balance (sum to zero).
 * Positive amounts represent debits, negative amounts represent credits.
 * 
 * <p>The account field uses namespaced IDs to support hierarchical account
 * structures like "economy:player:uuid" or "shop:revenue:daily".
 * 
 * @param account the account this entry affects
 * @param amount the monetary amount (positive = debit, negative = credit)
 * @param at the timestamp when this entry was recorded
 * @param memo optional description or reference for this entry
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record Entry(
    @NotNull NamespacedId account,
    @NotNull Money amount,
    @NotNull Instant at,
    @Nullable String memo
) {
    
    /**
     * Compact constructor with validation.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public Entry {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (at == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        if (memo != null && memo.trim().isEmpty()) {
            throw new IllegalArgumentException("Memo cannot be empty (use null instead)");
        }
    }
    
    /**
     * Creates a debit entry (positive amount).
     * 
     * @param account the account to debit
     * @param amount the amount to debit (must be positive)
     * @param memo optional memo
     * @return new debit entry
     * @throws IllegalArgumentException if amount is not positive
     */
    public static @NotNull Entry debit(@NotNull NamespacedId account, @NotNull Money amount, @Nullable String memo) {
        if (amount.amount() <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        return new Entry(account, amount, Instant.now(), memo);
    }
    
    /**
     * Creates a credit entry (negative amount).
     * 
     * @param account the account to credit
     * @param amount the amount to credit (must be positive, will be negated)
     * @param memo optional memo
     * @return new credit entry
     * @throws IllegalArgumentException if amount is not positive
     */
    public static @NotNull Entry credit(@NotNull NamespacedId account, @NotNull Money amount, @Nullable String memo) {
        if (amount.amount() <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        return new Entry(account, new Money(-amount.amount(), amount.currency()), Instant.now(), memo);
    }
    
    /**
     * Creates a timestamped debit entry.
     * 
     * @param account the account to debit
     * @param amount the amount to debit (must be positive)
     * @param at the timestamp
     * @param memo optional memo
     * @return new debit entry
     * @throws IllegalArgumentException if amount is not positive
     */
    public static @NotNull Entry debitAt(@NotNull NamespacedId account, @NotNull Money amount, @NotNull Instant at, @Nullable String memo) {
        if (amount.amount() <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        return new Entry(account, amount, at, memo);
    }
    
    /**
     * Creates a timestamped credit entry.
     * 
     * @param account the account to credit  
     * @param amount the amount to credit (must be positive, will be negated)
     * @param at the timestamp
     * @param memo optional memo
     * @return new credit entry
     * @throws IllegalArgumentException if amount is not positive
     */
    public static @NotNull Entry creditAt(@NotNull NamespacedId account, @NotNull Money amount, @NotNull Instant at, @Nullable String memo) {
        if (amount.amount() <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        return new Entry(account, new Money(-amount.amount(), amount.currency()), at, memo);
    }
    
    /**
     * Checks if this entry is a debit (positive amount).
     * 
     * @return true if the amount is positive
     */
    public boolean isDebit() {
        return amount.amount() > 0;
    }
    
    /**
     * Checks if this entry is a credit (negative amount).
     * 
     * @return true if the amount is negative
     */
    public boolean isCredit() {
        return amount.amount() < 0;
    }
    
    /**
     * Gets the absolute value of this entry's amount.
     * 
     * @return the absolute amount
     */
    public @NotNull Money absoluteAmount() {
        return new Money(Math.abs(amount.amount()), amount.currency());
    }
    
    /**
     * Creates a copy with a different memo.
     * 
     * @param newMemo the new memo (can be null)
     * @return new entry with updated memo
     */
    public @NotNull Entry withMemo(@Nullable String newMemo) {
        return new Entry(account, amount, at, newMemo);
    }
    
    /**
     * Creates a copy with a different timestamp.
     * 
     * @param newTimestamp the new timestamp
     * @return new entry with updated timestamp
     * @throws IllegalArgumentException if newTimestamp is null
     */
    public @NotNull Entry withTimestamp(@NotNull Instant newTimestamp) {
        return new Entry(account, amount, newTimestamp, memo);
    }
    
    /**
     * Creates the opposite entry (debit becomes credit and vice versa).
     * 
     * @return new entry with negated amount
     */
    public @NotNull Entry negate() {
        return new Entry(
            account,
            new Money(-amount.amount(), amount.currency()),
            at,
            memo
        );
    }
    
    @Override
    public @NotNull String toString() {
        var direction = isDebit() ? "DR" : "CR";
        var memoStr = memo != null ? " (" + memo + ")" : "";
        return String.format("%s %s %s%s", direction, account, absoluteAmount(), memoStr);
    }
}