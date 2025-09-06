package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents an auction house listing with bidding information.
 * 
 * <p>This record encapsulates all the information needed for auction house
 * functionality, including the item being sold, pricing, timing, and bidding state.
 * 
 * @param id unique identifier for this listing
 * @param seller the UUID of the player who created the listing
 * @param item the item being sold
 * @param startPrice the minimum starting bid
 * @param currentBid the current highest bid, if any
 * @param currentBidder the UUID of the current highest bidder, if any
 * @param createdAt when the listing was created
 * @param expiresAt when the listing expires
 * @param status the current status of the listing
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record Listing(
    @NotNull NamespacedId id,
    @NotNull UUID seller,
    @NotNull ItemModel item,
    @NotNull Money startPrice,
    @Nullable Money currentBid,
    @Nullable UUID currentBidder,
    @NotNull Instant createdAt,
    @NotNull Instant expiresAt,
    @NotNull ListingStatus status
) {
    
    /**
     * Compact constructor with validation.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public Listing {
        if (id == null) {
            throw new IllegalArgumentException("Listing ID cannot be null");
        }
        if (seller == null) {
            throw new IllegalArgumentException("Seller UUID cannot be null");
        }
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (startPrice == null) {
            throw new IllegalArgumentException("Start price cannot be null");
        }
        if (startPrice.amount() <= 0) {
            throw new IllegalArgumentException("Start price must be positive");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created timestamp cannot be null");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expiry timestamp cannot be null");
        }
        if (expiresAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Expiry cannot be before creation time");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (currentBid != null && currentBidder == null) {
            throw new IllegalArgumentException("Current bidder must be set if current bid exists");
        }
        if (currentBid == null && currentBidder != null) {
            throw new IllegalArgumentException("Current bid must be set if current bidder exists");
        }
        if (currentBid != null && currentBid.amount() < startPrice.amount()) {
            throw new IllegalArgumentException("Current bid cannot be less than start price");
        }
    }
    
    /**
     * Creates a new listing without any bids.
     * 
     * @param id the listing ID
     * @param seller the seller UUID
     * @param item the item being sold
     * @param startPrice the starting price
     * @param duration how long the listing should run
     * @return new listing
     */
    public static @NotNull Listing create(
        @NotNull NamespacedId id,
        @NotNull UUID seller,
        @NotNull ItemModel item,
        @NotNull Money startPrice,
        @NotNull Duration duration
    ) {
        var now = Instant.now();
        return new Listing(
            id,
            seller,
            item,
            startPrice,
            null,
            null,
            now,
            now.plus(duration),
            ListingStatus.ACTIVE
        );
    }
    
    /**
     * Checks if this listing has any bids.
     * 
     * @return true if there are bids
     */
    public boolean hasBids() {
        return currentBid != null;
    }
    
    /**
     * Checks if this listing has expired.
     * 
     * @return true if the listing has passed its expiry time
     */
    public boolean hasExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    /**
     * Gets the time remaining until expiry.
     * 
     * @return duration until expiry, or Duration.ZERO if expired
     */
    public @NotNull Duration timeRemaining() {
        var now = Instant.now();
        if (now.isAfter(expiresAt)) {
            return Duration.ZERO;
        }
        return Duration.between(now, expiresAt);
    }
    
    /**
     * Gets the current price (current bid or start price).
     * 
     * @return the current effective price
     */
    public @NotNull Money currentPrice() {
        return currentBid != null ? currentBid : startPrice;
    }
    
    /**
     * Checks if a player can bid on this listing.
     * 
     * @param player the player UUID
     * @return true if the player can bid
     */
    public boolean canBid(@NotNull UUID player) {
        return status == ListingStatus.ACTIVE &&
               !hasExpired() &&
               !seller.equals(player) &&
               !player.equals(currentBidder);
    }
    
    /**
     * Creates a copy with a new bid.
     * 
     * @param bidAmount the new bid amount
     * @param bidder the bidder UUID
     * @return new listing with updated bid information
     */
    public @NotNull Listing withBid(@NotNull Money bidAmount, @NotNull UUID bidder) {
        return new Listing(
            id, seller, item, startPrice, bidAmount, bidder,
            createdAt, expiresAt, status
        );
    }
    
    /**
     * Creates a copy with a new status.
     * 
     * @param newStatus the new status
     * @return new listing with updated status
     */
    public @NotNull Listing withStatus(@NotNull ListingStatus newStatus) {
        return new Listing(
            id, seller, item, startPrice, currentBid, currentBidder,
            createdAt, expiresAt, newStatus
        );
    }
    
    /**
     * The status of an auction house listing.
     */
    public enum ListingStatus {
        /** The listing is active and accepting bids */
        ACTIVE,
        /** The listing has been sold */
        SOLD,
        /** The listing has expired without being sold */
        EXPIRED,
        /** The listing has been cancelled by the seller */
        CANCELLED
    }
}