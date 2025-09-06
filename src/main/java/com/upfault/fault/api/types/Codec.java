package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for encoding and decoding objects to/from byte arrays.
 * 
 * <p>Codecs provide versioned serialization and deserialization for objects
 * that need to be stored or transmitted. They are used by the schema registry
 * to handle evolution of data structures over time.
 * 
 * <p>Implementations should be thread-safe and handle version compatibility
 * appropriately for their use case.
 * 
 * @param <T> the type of object this codec handles
 * 
 * @since 0.0.1
 * @apiNote Implementations must be thread-safe
 */
public interface Codec<T> {
    
    /**
     * Encodes an object to a byte array.
     * 
     * <p>The encoded bytes should contain enough information to recreate
     * the object using the decode method, including any necessary version
     * or type information.
     * 
     * @param object the object to encode
     * @return the encoded byte array
     * @throws IllegalArgumentException if the object cannot be encoded
     * @throws RuntimeException if encoding fails
     * 
     * @apiNote This method must be thread-safe
     */
    byte @NotNull [] encode(@NotNull T object);
    
    /**
     * Decodes an object from a byte array.
     * 
     * <p>The decoder should handle version compatibility and migration
     * as appropriate for the specific codec implementation.
     * 
     * @param data the byte array to decode
     * @return the decoded object
     * @throws IllegalArgumentException if the data cannot be decoded
     * @throws RuntimeException if decoding fails
     * 
     * @apiNote This method must be thread-safe
     */
    @NotNull T decode(byte @NotNull [] data);
    
    /**
     * Gets the version of this codec.
     * 
     * <p>Codec versions are used to handle evolution of data structures.
     * Higher version numbers indicate newer codec versions.
     * 
     * @return the codec version number
     * 
     * @apiNote This method must be thread-safe
     */
    int getVersion();
    
    /**
     * Checks if this codec can decode data from a specific version.
     * 
     * <p>This is used to determine if a codec can handle data that was
     * encoded with an older (or newer) version of the codec.
     * 
     * @param version the version to check compatibility with
     * @return true if this codec can decode data from the specified version
     * 
     * @apiNote This method must be thread-safe
     */
    boolean canDecodeVersion(int version);
    
    /**
     * Gets the type of object this codec handles.
     * 
     * @return the class of objects this codec encodes/decodes
     * 
     * @apiNote This method must be thread-safe
     */
    @NotNull Class<T> getType();
}