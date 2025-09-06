package com.upfault.fault.api;

import com.upfault.fault.api.types.Money;
import com.upfault.fault.api.types.OperationResult;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract balance operations with atomic increment/decrement contracts.
 * 
 * <p>This facade provides a standard interface for economic operations across
 * different economy plugin implementations.
 * 
 * <p>Example usage:
 * <pre>{@code
 * EconomyFacade economy = Fault.service(EconomyFacade.class);
 * 
 * // Check player's balance
 * CompletableFuture<Money> balance = economy.getBalance(playerId);
 * balance.thenAccept(money -> {
 *     System.out.println("Player has: " + money.minorUnits() + " " + money.currency());
 * });
 * 
 * // Attempt to withdraw money
 * Money cost = new Money(5000, "USD"); // $50.00 in cents
 * CompletableFuture<OperationResult> result = economy.withdraw(playerId, cost);
 * result.thenAccept(res -> {
 *     switch (res) {
 *         case OperationResult.Success success -> 
 *             System.out.println("Purchase successful!");
 *         case OperationResult.Failure failure -> 
 *             System.out.println("Failed: " + failure.reason());
 *     }
 * });
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations are atomic and thread-safe.
 * Balance modifications return result types to indicate success or failure
 * rather than throwing exceptions.
 * 
 * @since 0.0.1
 * @apiNote Results use sealed types to enforce explicit error handling
 */
public interface EconomyFacade {

    /**
     * Gets a player's current balance.
     * 
     * @param playerId the player's UUID
     * @return future containing the player's current balance
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Money> getBalance(@NotNull UUID playerId);

    /**
     * Sets a player's balance to a specific amount.
     * 
     * @param playerId the player's UUID
     * @param amount the new balance amount
     * @return future containing the operation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<OperationResult> setBalance(@NotNull UUID playerId, @NotNull Money amount);

    /**
     * Adds money to a player's balance.
     * 
     * @param playerId the player's UUID
     * @param amount the amount to add
     * @return future containing the operation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<OperationResult> deposit(@NotNull UUID playerId, @NotNull Money amount);

    /**
     * Removes money from a player's balance.
     * 
     * <p>This operation will fail if the player has insufficient funds.
     * 
     * @param playerId the player's UUID
     * @param amount the amount to withdraw
     * @return future containing the operation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<OperationResult> withdraw(@NotNull UUID playerId, @NotNull Money amount);

    /**
     * Transfers money from one player to another.
     * 
     * <p>This operation is atomic - either both the withdrawal and deposit
     * succeed, or the entire operation fails.
     * 
     * @param fromPlayerId the source player's UUID
     * @param toPlayerId the target player's UUID
     * @param amount the amount to transfer
     * @return future containing the operation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<OperationResult> transfer(@NotNull UUID fromPlayerId, @NotNull UUID toPlayerId, @NotNull Money amount);

    /**
     * Checks if a player has at least the specified amount.
     * 
     * @param playerId the player's UUID
     * @param amount the amount to check for
     * @return future containing true if the player has sufficient funds
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> hasBalance(@NotNull UUID playerId, @NotNull Money amount);

    /**
     * Gets the default currency used by this economy system.
     * 
     * @return the default currency code
     * @since 0.0.1
     */
    @NotNull
    String getDefaultCurrency();

    /**
     * Formats a money amount for display to players.
     * 
     * @param amount the money amount to format
     * @return formatted money string (e.g., "$50.00", "1,000 coins")
     * @since 0.0.1
     */
    @NotNull
    String formatMoney(@NotNull Money amount);

    /**
     * Creates a Money instance with the default currency.
     * 
     * @param minorUnits the amount in the smallest currency unit (e.g., cents)
     * @return Money instance with the default currency
     * @since 0.0.1
     */
    @NotNull
    Money createMoney(long minorUnits);

    /**
     * Checks if the economy system is available and operational.
     * 
     * @return future containing true if the economy is available
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> isAvailable();

    /**
     * Creates a new account for a player if one doesn't exist.
     * 
     * @param playerId the player's UUID
     * @return future containing the operation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<OperationResult> createAccount(@NotNull UUID playerId);

    /**
     * Checks if a player has an economy account.
     * 
     * @param playerId the player's UUID
     * @return future containing true if the account exists
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> hasAccount(@NotNull UUID playerId);
}
