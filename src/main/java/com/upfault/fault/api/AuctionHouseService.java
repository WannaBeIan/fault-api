package com.upfault.fault.api;

import com.upfault.fault.api.types.*;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing auction house listings and bidding.
 * 
 * <p>This service provides a complete auction house system where players can
 * list items for sale with starting prices and durations, and other players
 * can place bids. The service handles bid validation, timing, and automated
 * completion of auctions.
 * 
 * <p>All monetary operations are handled through the {@link Money} type and
 * integrate with the economy system. Listings expire automatically after
 * their duration, and winning bids are processed atomically.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and return CompletableFutures for async operations
 */
public interface AuctionHouseService {
    
    /**
     * Retrieves a paginated list of active auction listings.
     * 
     * <p>Results are typically sorted by creation time (newest first) or by
     * ending time (ending soonest first). The exact sorting behavior is
     * implementation-dependent.
     * 
     * @param page the page number (0-based)
     * @param size the page size (must be positive)
     * @return future containing the page of listings
     * @throws IllegalArgumentException if page is negative or size is non-positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Page<Listing>> browse(int page, int size);
    
    /**
     * Searches for auction listings matching specific criteria.
     * 
     * <p>The query string is implementation-dependent but typically supports
     * item name matching and basic filtering operations.
     * 
     * @param query the search query
     * @param page the page number (0-based) 
     * @param size the page size (must be positive)
     * @return future containing the page of matching listings
     * @throws IllegalArgumentException if query is null, page is negative, or size is non-positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Page<Listing>> search(@NotNull String query, int page, int size);
    
    /**
     * Creates a new auction listing for an item.
     * 
     * <p>The seller must have the specified item in their inventory, and it
     * will be removed when the listing is created. If the auction expires
     * without sale, the item is returned to the seller's inventory.
     * 
     * @param seller the player creating the listing
     * @param item the item to be sold
     * @param startPrice the minimum starting bid
     * @param duration how long the listing should remain active
     * @return future containing the operation result with listing ID on success
     * @throws IllegalArgumentException if any parameter is null or invalid
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          The seller's inventory is modified atomically.
     */
    @NotNull CompletableFuture<OperationResult> list(
        @NotNull UUID seller,
        @NotNull ItemModel item,
        @NotNull Money startPrice,
        @NotNull Duration duration
    );
    
    /**
     * Retrieves a specific listing by its ID.
     * 
     * @param listingId the listing identifier
     * @return future containing the listing, or empty if not found
     * @throws IllegalArgumentException if listingId is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Optional<Listing>> getListing(@NotNull NamespacedId listingId);
    
    /**
     * Places a bid on an auction listing.
     * 
     * <p>The bid must be higher than the current highest bid (or the starting
     * price if no bids exist). The bidder's money is reserved immediately,
     * and previous bidders are refunded if outbid.
     * 
     * @param listingId the listing to bid on
     * @param bidder the player placing the bid
     * @param amount the bid amount
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Money transactions are handled atomically.
     */
    @NotNull CompletableFuture<OperationResult> placeBid(
        @NotNull NamespacedId listingId,
        @NotNull UUID bidder,
        @NotNull Money amount
    );
    
    /**
     * Cancels an active listing if the seller has permission to do so.
     * 
     * <p>Listings can typically only be cancelled by their creator and only
     * if they haven't received bids yet (implementation-dependent policy).
     * 
     * @param listingId the listing to cancel
     * @param requester the player requesting cancellation
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<OperationResult> cancelListing(
        @NotNull NamespacedId listingId,
        @NotNull UUID requester
    );
    
    /**
     * Retrieves all active listings created by a specific player.
     * 
     * @param seller the player whose listings to retrieve
     * @param page the page number (0-based)
     * @param size the page size (must be positive) 
     * @return future containing the page of listings
     * @throws IllegalArgumentException if seller is null, page is negative, or size is non-positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Page<Listing>> getPlayerListings(@NotNull UUID seller, int page, int size);
    
    /**
     * Retrieves all listings where a specific player is the current highest bidder.
     * 
     * @param bidder the player whose bids to retrieve
     * @param page the page number (0-based)
     * @param size the page size (must be positive)
     * @return future containing the page of listings
     * @throws IllegalArgumentException if bidder is null, page is negative, or size is non-positive
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull CompletableFuture<Page<Listing>> getPlayerBids(@NotNull UUID bidder, int page, int size);
    
    /**
     * Forces completion of an expired auction.
     * 
     * <p>This method is typically called automatically by the system, but can
     * be triggered manually for administrative purposes. If the auction had
     * bids, the item goes to the highest bidder and payment goes to the seller.
     * If no bids were placed, the item is returned to the seller.
     * 
     * @param listingId the listing to complete
     * @return future containing the operation result
     * @throws IllegalArgumentException if listingId is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          All transfers are handled atomically.
     */
    @NotNull CompletableFuture<OperationResult> completeListing(@NotNull NamespacedId listingId);
}