package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

/**
 * Immutable resource pack definition with metadata.
 * 
 * @param id unique identifier for this resource pack
 * @param url download URL for the resource pack
 * @param hash SHA-1 hash for verification (optional)
 * @param required whether the pack is required to join the server
 * @param prompt optional message shown to the player
 * 
 * @since 0.0.1
 * @apiNote Resource packs are immutable once created and identified by their ID
 */
public record ResourcePack(
    @NotNull NamespacedId id,
    @NotNull URI url,
    @Nullable String hash,
    boolean required,
    @Nullable String prompt
) {
    
    /**
     * Creates a resource pack with validation.
     * 
     * @param id unique identifier (cannot be null)
     * @param url download URL (cannot be null)
     * @param hash SHA-1 hash for verification (optional)
     * @param required whether pack is required
     * @param prompt message shown to player (optional)
     * @throws IllegalArgumentException if required parameters are null
     */
    public ResourcePack {
        if (id == null) {
            throw new IllegalArgumentException("Resource pack ID cannot be null");
        }
        if (url == null) {
            throw new IllegalArgumentException("Resource pack URL cannot be null");
        }
        if (hash != null && !isValidSha1(hash)) {
            throw new IllegalArgumentException("Invalid SHA-1 hash format: " + hash);
        }
    }
    
    /**
     * Creates a basic resource pack.
     * 
     * @param id the pack identifier
     * @param url the download URL
     * @return basic resource pack
     */
    public static @NotNull ResourcePack of(@NotNull NamespacedId id, @NotNull URI url) {
        return new ResourcePack(id, url, null, false, null);
    }
    
    /**
     * Creates a required resource pack.
     * 
     * @param id the pack identifier
     * @param url the download URL
     * @param hash the SHA-1 hash
     * @return required resource pack
     */
    public static @NotNull ResourcePack required(@NotNull NamespacedId id, @NotNull URI url, @NotNull String hash) {
        return new ResourcePack(id, url, hash, true, null);
    }
    
    /**
     * Creates a resource pack with a custom prompt.
     * 
     * @param id the pack identifier
     * @param url the download URL
     * @param prompt the custom prompt message
     * @return resource pack with prompt
     */
    public static @NotNull ResourcePack withPrompt(@NotNull NamespacedId id, @NotNull URI url, @NotNull String prompt) {
        return new ResourcePack(id, url, null, false, prompt);
    }
    
    /**
     * Checks if this pack has a verification hash.
     * 
     * @return true if hash is present
     */
    public boolean hasHash() {
        return hash != null;
    }
    
    /**
     * Checks if this pack has a custom prompt.
     * 
     * @return true if prompt is present
     */
    public boolean hasPrompt() {
        return prompt != null;
    }
    
    /**
     * Creates a new resource pack with a different URL.
     * 
     * @param newUrl the new download URL
     * @return new resource pack with updated URL
     */
    public @NotNull ResourcePack withUrl(@NotNull URI newUrl) {
        return new ResourcePack(id, newUrl, hash, required, prompt);
    }
    
    /**
     * Creates a new resource pack with a different hash.
     * 
     * @param newHash the new SHA-1 hash
     * @return new resource pack with updated hash
     */
    public @NotNull ResourcePack withHash(@Nullable String newHash) {
        return new ResourcePack(id, url, newHash, required, prompt);
    }
    
    /**
     * Creates a new resource pack with different required status.
     * 
     * @param newRequired the new required status
     * @return new resource pack with updated required status
     */
    public @NotNull ResourcePack withRequired(boolean newRequired) {
        return new ResourcePack(id, url, hash, newRequired, prompt);
    }
    
    /**
     * Creates a new resource pack with a different prompt.
     * 
     * @param newPrompt the new prompt message
     * @return new resource pack with updated prompt
     */
    public @NotNull ResourcePack withPrompt(@Nullable String newPrompt) {
        return new ResourcePack(id, url, hash, required, newPrompt);
    }
    
    /**
     * Validates SHA-1 hash format.
     * 
     * @param hash the hash to validate
     * @return true if valid SHA-1 format
     */
    private static boolean isValidSha1(@NotNull String hash) {
        return hash.matches("[a-fA-F0-9]{40}");
    }
    
    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ResourcePack[").append(id);
        sb.append(", url=").append(url);
        if (hasHash()) {
            sb.append(", hash=").append(hash.substring(0, 8)).append("...");
        }
        if (required) {
            sb.append(", required");
        }
        if (hasPrompt()) {
            sb.append(", prompt='").append(prompt).append("'");
        }
        sb.append("]");
        return sb.toString();
    }
}