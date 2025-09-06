package com.upfault.fault.api.types;

/**
 * Represents the status of an auction house listing.
 * 
 * <p>This enum tracks the lifecycle of auction listings from creation
 * to completion or cancellation.
 * 
 * @since 0.0.1
 */
public enum ListingStatus {
    
    /**
     * Listing is active and accepting bids.
     */
    ACTIVE,
    
    /**
     * Listing has expired due to timeout.
     */
    EXPIRED,
    
    /**
     * Listing was sold to a bidder.
     */
    SOLD,
    
    /**
     * Listing was cancelled by the seller.
     */
    CANCELLED,
    
    /**
     * Listing was removed by an administrator.
     */
    REMOVED
}