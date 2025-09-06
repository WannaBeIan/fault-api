package com.upfault.fault.api.events;

import com.upfault.fault.api.types.Money;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Called when a player's economy balance changes.
 * 
 * <p>This event is fired after the balance change has been successfully
 * applied to the player's account.
 * 
 * @since 0.0.1
 * @apiNote This event is not cancellable as the transaction has already completed
 */
public class EconomyChangeEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final UUID playerId;
    private final Money oldBalance;
    private final Money newBalance;
    private final Money changeAmount;
    private final ChangeType changeType;
    private final String reason;
    
    /**
     * Creates a new EconomyChangeEvent.
     * 
     * @param playerId the player's UUID
     * @param oldBalance the balance before the change
     * @param newBalance the balance after the change
     * @param changeType the type of change
     * @param reason optional reason for the change
     */
    public EconomyChangeEvent(@NotNull UUID playerId, @NotNull Money oldBalance, @NotNull Money newBalance, 
                              @NotNull ChangeType changeType, @Nullable String reason) {
        this.playerId = playerId;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.changeAmount = new Money(Math.abs(newBalance.minorUnits() - oldBalance.minorUnits()), newBalance.currency());
        this.changeType = changeType;
        this.reason = reason;
    }
    
    /**
     * Gets the player's UUID.
     * 
     * @return the player's UUID
     */
    public @NotNull UUID getPlayerId() {
        return playerId;
    }
    
    /**
     * Gets the balance before the change.
     * 
     * @return the old balance
     */
    public @NotNull Money getOldBalance() {
        return oldBalance;
    }
    
    /**
     * Gets the balance after the change.
     * 
     * @return the new balance
     */
    public @NotNull Money getNewBalance() {
        return newBalance;
    }
    
    /**
     * Gets the amount that changed (always positive).
     * 
     * @return the change amount
     */
    public @NotNull Money getChangeAmount() {
        return changeAmount;
    }
    
    /**
     * Gets the type of change.
     * 
     * @return the change type
     */
    public @NotNull ChangeType getChangeType() {
        return changeType;
    }
    
    /**
     * Gets the reason for the change.
     * 
     * @return the reason, or null if not provided
     */
    public @Nullable String getReason() {
        return reason;
    }
    
    /**
     * Checks if this was an increase in balance.
     * 
     * @return true if the balance increased
     */
    public boolean wasIncrease() {
        return newBalance.minorUnits() > oldBalance.minorUnits();
    }
    
    /**
     * Checks if this was a decrease in balance.
     * 
     * @return true if the balance decreased
     */
    public boolean wasDecrease() {
        return newBalance.minorUnits() < oldBalance.minorUnits();
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    /**
     * Types of economy changes.
     */
    public enum ChangeType {
        /**
         * Money was deposited to the account.
         */
        DEPOSIT,
        
        /**
         * Money was withdrawn from the account.
         */
        WITHDRAWAL,
        
        /**
         * Balance was set to a specific amount.
         */
        SET,
        
        /**
         * Money was transferred from this account to another.
         */
        TRANSFER_OUT,
        
        /**
         * Money was transferred from another account to this account.
         */
        TRANSFER_IN,
        
        /**
         * Other type of change.
         */
        OTHER
    }
}
