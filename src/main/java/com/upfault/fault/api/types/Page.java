package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * Represents a page of results from a paginated query.
 * 
 * <p>This record encapsulates pagination metadata along with the actual page content,
 * allowing APIs to provide paginated results with full navigation information.
 * 
 * @param <T> the type of items in this page
 * @param content the items on this page
 * @param page the current page number (0-based)
 * @param size the requested page size
 * @param totalElements the total number of elements across all pages
 * @param totalPages the total number of pages
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record Page<T>(
    @NotNull List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    
    /**
     * Compact constructor with validation.
     * 
     * @param content the page content (will be defensively copied)
     * @param page the current page number (must be non-negative)
     * @param size the page size (must be positive)
     * @param totalElements the total number of elements (must be non-negative)
     * @param totalPages the total number of pages (must be non-negative)
     * @throws IllegalArgumentException if validation fails
     */
    public Page {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative: " + page);
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must be positive: " + size);
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("Total elements cannot be negative: " + totalElements);
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("Total pages cannot be negative: " + totalPages);
        }
        
        // Validate page consistency
        if (totalPages == 0 && totalElements > 0) {
            throw new IllegalArgumentException("Total pages cannot be 0 when total elements > 0");
        }
        if (totalPages > 0 && page >= totalPages) {
            throw new IllegalArgumentException("Page number " + page + " exceeds total pages " + totalPages);
        }
        
        // Defensive copy
        content = List.copyOf(content);
    }
    
    /**
     * Creates an empty page.
     * 
     * @param page the page number
     * @param size the page size
     * @param <T> the content type
     * @return empty page
     */
    public static <T> @NotNull Page<T> empty(int page, int size) {
        return new Page<>(List.of(), page, size, 0, 0);
    }
    
    /**
     * Creates a single page containing all the provided content.
     * 
     * @param content the page content
     * @param <T> the content type
     * @return single page containing all content
     */
    public static <T> @NotNull Page<T> single(@NotNull List<T> content) {
        return new Page<>(content, 0, content.size(), content.size(), content.isEmpty() ? 0 : 1);
    }
    
    /**
     * Creates a page of results.
     * 
     * @param content the page content
     * @param page the page number (0-based)
     * @param size the page size
     * @param totalElements the total number of elements
     * @param <T> the content type
     * @return new page
     */
    public static <T> @NotNull Page<T> of(@NotNull List<T> content, int page, int size, long totalElements) {
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        return new Page<>(content, page, size, totalElements, totalPages);
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
        return totalPages == 0 || page == totalPages - 1;
    }
    
    /**
     * Checks if there is a next page.
     * 
     * @return true if there is a next page
     */
    public boolean hasNext() {
        return !isLast();
    }
    
    /**
     * Checks if there is a previous page.
     * 
     * @return true if there is a previous page
     */
    public boolean hasPrevious() {
        return !isFirst();
    }
    
    /**
     * Checks if this page is empty.
     * 
     * @return true if the page has no content
     */
    public boolean isEmpty() {
        return content.isEmpty();
    }
    
    /**
     * Gets the number of elements on this page.
     * 
     * @return the number of elements
     */
    public int getNumberOfElements() {
        return content.size();
    }
    
    /**
     * Gets the index of the first element on this page relative to all pages.
     * 
     * @return the start index, or 0 if empty
     */
    public long getStartIndex() {
        return isEmpty() ? 0 : (long) page * size + 1;
    }
    
    /**
     * Gets the index of the last element on this page relative to all pages.
     * 
     * @return the end index, or 0 if empty
     */
    public long getEndIndex() {
        return isEmpty() ? 0 : getStartIndex() + getNumberOfElements() - 1;
    }
    
    /**
     * Maps the content of this page to a different type.
     * 
     * @param mapper the mapping function
     * @param <U> the target type
     * @return new page with mapped content
     * @throws IllegalArgumentException if mapper is null
     */
    public <U> @NotNull Page<U> map(@NotNull Function<T, U> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper function cannot be null");
        }
        List<U> mappedContent = content.stream()
                                      .map(mapper)
                                      .toList();
        return new Page<>(mappedContent, page, size, totalElements, totalPages);
    }
    
    /**
     * Creates pagination info without the actual content.
     * 
     * @return pagination metadata
     */
    public @NotNull PageInfo toPageInfo() {
        return new PageInfo(page, size, totalElements, totalPages);
    }
    
    /**
     * Pagination metadata without content.
     * 
     * @param page the current page number (0-based)
     * @param size the page size
     * @param totalElements the total number of elements
     * @param totalPages the total number of pages
     */
    public record PageInfo(
        int page,
        int size,
        long totalElements,
        int totalPages
    ) {
        public PageInfo {
            if (page < 0) throw new IllegalArgumentException("Page cannot be negative");
            if (size < 1) throw new IllegalArgumentException("Size must be positive");
            if (totalElements < 0) throw new IllegalArgumentException("Total elements cannot be negative");
            if (totalPages < 0) throw new IllegalArgumentException("Total pages cannot be negative");
        }
        
        public boolean isFirst() { return page == 0; }
        public boolean isLast() { return totalPages == 0 || page == totalPages - 1; }
        public boolean hasNext() { return !isLast(); }
        public boolean hasPrevious() { return !isFirst(); }
    }
    
    @Override
    public @NotNull String toString() {
        return String.format("Page[page=%d, size=%d, totalElements=%d, totalPages=%d, content=%d items]",
                           page, size, totalElements, totalPages, content.size());
    }
}