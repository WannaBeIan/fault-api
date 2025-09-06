package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a paginated result set.
 * 
 * @param content the content items for this page
 * @param page the current page number (0-based)
 * @param size the size of each page
 * @param totalPages the total number of pages
 * @param totalElements the total number of elements across all pages
 * @param <T> the type of content items
 * 
 * @since 0.0.1
 * @apiNote Used for paginated query results and GUI pagination
 */
public record Page<T>(
    @NotNull List<T> content,
    int page,
    int size,
    int totalPages,
    long totalElements
) {
    
    /**
     * Creates a new Page with validation.
     * 
     * @param content the page content (cannot be null)
     * @param page the page number (must be non-negative)
     * @param size the page size (must be positive)
     * @param totalPages the total pages (must be non-negative)
     * @param totalElements the total elements (must be non-negative)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Page {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative: " + page);
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be positive: " + size);
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("Total pages cannot be negative: " + totalPages);
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("Total elements cannot be negative: " + totalElements);
        }
    }
    
    /**
     * Creates an empty page.
     * 
     * @param size the page size
     * @param <T> the content type
     * @return empty page with the specified size
     */
    public static <T> @NotNull Page<T> empty(int size) {
        return new Page<>(List.of(), 0, size, 0, 0);
    }
    
    /**
     * Creates a single page containing all elements.
     * 
     * @param content the content items
     * @param <T> the content type
     * @return page containing all the content
     */
    public static <T> @NotNull Page<T> of(@NotNull List<T> content) {
        return new Page<>(content, 0, content.size(), content.isEmpty() ? 0 : 1, content.size());
    }
    
    /**
     * Checks if this page is empty.
     * 
     * @return true if the page contains no content
     */
    public boolean isEmpty() {
        return content.isEmpty();
    }
    
    /**
     * Checks if this is the first page.
     * 
     * @return true if this is page 0
     */
    public boolean isFirst() {
        return page == 0;
    }
    
    /**
     * Checks if this is the last page.
     * 
     * @return true if this is the last page
     */
    public boolean isLast() {
        return page >= totalPages - 1;
    }
    
    /**
     * Checks if there is a next page.
     * 
     * @return true if there is a next page
     */
    public boolean hasNext() {
        return page < totalPages - 1;
    }
    
    /**
     * Checks if there is a previous page.
     * 
     * @return true if there is a previous page
     */
    public boolean hasPrevious() {
        return page > 0;
    }
    
    /**
     * Gets the next page number.
     * 
     * @return the next page number, or -1 if this is the last page
     */
    public int getNextPage() {
        return hasNext() ? page + 1 : -1;
    }
    
    /**
     * Gets the previous page number.
     * 
     * @return the previous page number, or -1 if this is the first page
     */
    public int getPreviousPage() {
        return hasPrevious() ? page - 1 : -1;
    }
    
    /**
     * Gets the number of elements on this page.
     * 
     * @return the number of elements in the content list
     */
    public int getNumberOfElements() {
        return content.size();
    }
    
    /**
     * Creates a new page with different content but same pagination info.
     * 
     * @param newContent the new content
     * @param <U> the new content type
     * @return new page with the mapped content
     */
    public <U> @NotNull Page<U> withContent(@NotNull List<U> newContent) {
        return new Page<>(newContent, page, size, totalPages, totalElements);
    }
    
    @Override
    public @NotNull String toString() {
        return String.format("Page[%d of %d, %d/%d items, %d total]",
                           page + 1, totalPages, getNumberOfElements(), size, totalElements);
    }
}
