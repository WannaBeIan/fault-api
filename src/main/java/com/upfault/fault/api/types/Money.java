package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a monetary amount with currency information.
 * 
 * <p>Uses minor units (e.g., cents) to avoid floating-point precision issues.
 * For example, $1.50 USD would be represented as minorUnits=150, currency="USD".
 * 
 * @param minorUnits the amount in the smallest currency unit (e.g., cents)
 * @param currency the currency code (e.g., "USD", "coins")
 * 
 * @since 0.0.1
 * @apiNote Always use minor units to avoid precision issues with currency
 */
public record Money(long minorUnits, @NotNull String currency) {
    
    /**
     * Creates a new Money instance with validation.
     * 
     * @param minorUnits the amount in minor units (can be negative for debts)
     * @param currency the currency code (cannot be null or empty)
     * @throws IllegalArgumentException if currency is invalid
     */
    public Money {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
    }
    
    /**
     * Gets the amount in minor units.
     * 
     * @return the amount in minor units
     */
    public long amount() {
        return minorUnits;
    }
    
    /**
     * Creates Money from a decimal amount and currency.
     * 
     * <p>The decimal amount is converted to minor units by multiplying by 100
     * and rounding to the nearest whole number.
     * 
     * @param amount the decimal amount (e.g., 1.50 for $1.50)
     * @param currency the currency code
     * @return Money instance with the converted amount
     */
    public static @NotNull Money fromDecimal(double amount, @NotNull String currency) {
        long minorUnits = Math.round(amount * 100);
        return new Money(minorUnits, currency);
    }
    
    /**
     * Creates Money from a BigDecimal amount and currency.
     * 
     * @param amount the decimal amount
     * @param currency the currency code
     * @return Money instance with the converted amount
     */
    public static @NotNull Money fromDecimal(@NotNull BigDecimal amount, @NotNull String currency) {
        long minorUnits = amount.multiply(BigDecimal.valueOf(100))
                                .setScale(0, RoundingMode.HALF_UP)
                                .longValue();
        return new Money(minorUnits, currency);
    }
    
    /**
     * Creates zero money in the specified currency.
     * 
     * @param currency the currency code
     * @return Money instance with zero amount
     */
    public static @NotNull Money zero(@NotNull String currency) {
        return new Money(0, currency);
    }
    
    /**
     * Gets the decimal value of this money amount.
     * 
     * @return the amount as a decimal (e.g., 150 minor units = 1.50)
     */
    public double toDecimal() {
        return minorUnits / 100.0;
    }
    
    /**
     * Gets the decimal value as a BigDecimal.
     * 
     * @return the amount as a BigDecimal with 2 decimal places
     */
    public @NotNull BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(minorUnits, 2); // Scale of 2 for 2 decimal places
    }
    
    /**
     * Adds another Money amount to this one.
     * 
     * @param other the amount to add
     * @return new Money instance with the sum
     * @throws IllegalArgumentException if currencies don't match
     */
    public @NotNull Money add(@NotNull Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies: " + 
                                             this.currency + " and " + other.currency);
        }
        return new Money(this.minorUnits + other.minorUnits, this.currency);
    }
    
    /**
     * Subtracts another Money amount from this one.
     * 
     * @param other the amount to subtract
     * @return new Money instance with the difference
     * @throws IllegalArgumentException if currencies don't match
     */
    public @NotNull Money subtract(@NotNull Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot subtract different currencies: " + 
                                             this.currency + " and " + other.currency);
        }
        return new Money(this.minorUnits - other.minorUnits, this.currency);
    }
    
    /**
     * Multiplies this Money by a scalar.
     * 
     * @param multiplier the multiplier
     * @return new Money instance with the multiplied amount
     * @throws IllegalArgumentException if multiplier is negative
     */
    public @NotNull Money multiply(long multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative: " + multiplier);
        }
        return new Money(this.minorUnits * multiplier, this.currency);
    }
    
    /**
     * Multiplies this Money by a decimal multiplier.
     * 
     * @param multiplier the multiplier
     * @return new Money instance with the multiplied amount
     * @throws IllegalArgumentException if multiplier is negative
     */
    public @NotNull Money multiply(double multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative: " + multiplier);
        }
        long newMinorUnits = Math.round(this.minorUnits * multiplier);
        return new Money(newMinorUnits, this.currency);
    }
    
    /**
     * Checks if this money amount is zero.
     * 
     * @return true if the amount is zero
     */
    public boolean isZero() {
        return minorUnits == 0;
    }
    
    /**
     * Checks if this money amount is positive (greater than zero).
     * 
     * @return true if the amount is positive
     */
    public boolean isPositive() {
        return minorUnits > 0;
    }
    
    /**
     * Checks if this amount is greater than or equal to another amount.
     * 
     * @param other the amount to compare against
     * @return true if this amount is >= other amount
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isGreaterThanOrEqualTo(@NotNull Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare different currencies: " + 
                                             this.currency + " and " + other.currency);
        }
        return this.minorUnits >= other.minorUnits;
    }
    
    /**
     * Checks if this amount is less than another amount.
     * 
     * @param other the amount to compare against
     * @return true if this amount is less than the other amount
     * @throws IllegalArgumentException if currencies don't match
     */
    public boolean isLessThan(@NotNull Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare different currencies: " + 
                                             this.currency + " and " + other.currency);
        }
        return this.minorUnits < other.minorUnits;
    }
    
    /**
     * Compares this money to another money amount.
     * 
     * @param other the other money amount
     * @return negative if less, 0 if equal, positive if greater
     * @throws IllegalArgumentException if currencies don't match
     */
    public int compareTo(@NotNull Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare different currencies: " + 
                                             this.currency + " and " + other.currency);
        }
        return Long.compare(this.minorUnits, other.minorUnits);
    }
    
    @Override
    public @NotNull String toString() {
        if (minorUnits % 100 == 0) {
            // Whole currency units
            long wholeUnits = minorUnits / 100;
            return wholeUnits + " " + currency;
        } else {
            // Has fractional part
            return String.format("%.2f %s", toDecimal(), currency);
        }
    }
}
