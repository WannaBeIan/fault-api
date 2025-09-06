package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

/**
 * Represents an item offer in a catalog with pricing and expiration information.
 * 
 * <p>Offers define items available for purchase from catalogs, with associated
 * pricing and optional expiration times. They are used by shop systems, vendor
 * NPCs, and other economic features.
 * 
 * @param id the unique identifier for this offer
 * @param price the cost to purchase this offer
 * @param expires optional expiration time for limited-time offers
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record Offer(
    @NotNull NamespacedId id,
    @NotNull Money price,
    @Nullable Duration expires
) {
    
    /**
     * Compact constructor with validation.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public Offer {
        if (id == null) {
            throw new IllegalArgumentException("Offer ID cannot be null");
        }
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (price.amount() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
    
    /**
     * Creates a permanent offer (never expires).
     * 
     * @param id the offer identifier
     * @param price the offer price
     * @return new permanent offer
     */
    public static @NotNull Offer permanent(@NotNull NamespacedId id, @NotNull Money price) {
        return new Offer(id, price, null);
    }
    
    /**
     * Creates a temporary offer with expiration time.
     * 
     * @param id the offer identifier
     * @param price the offer price
     * @param validFor how long this offer remains valid
     * @return new temporary offer
     */
    public static @NotNull Offer temporary(@NotNull NamespacedId id, @NotNull Money price, @NotNull Duration validFor) {
        return new Offer(id, price, validFor);
    }
    
    /**
     * Checks if this offer expires.
     * 
     * @return true if the offer has an expiration time
     */
    public boolean hasExpiration() {
        return expires != null;
    }
    
    /**
     * Checks if this offer has expired (if it has an expiration).
     * 
     * @param createdAt when this offer was created
     * @return true if the offer has expired, false if it's permanent or still valid
     */
    public boolean hasExpired(@NotNull Instant createdAt) {
        if (expires == null) {
            return false;
        }
        return Instant.now().isAfter(createdAt.plus(expires));
    }
    
    /**
     * Gets the expiration time for this offer.
     * 
     * @param createdAt when this offer was created
     * @return the expiration time, or empty if the offer is permanent
     */
    public @NotNull java.util.Optional<Instant> getExpirationTime(@NotNull Instant createdAt) {
        if (expires == null) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(createdAt.plus(expires));
    }
}