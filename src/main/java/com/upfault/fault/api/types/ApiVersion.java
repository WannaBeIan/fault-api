package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a semantic version for API compatibility checking.
 * 
 * @param major major version number (breaking changes)
 * @param minor minor version number (backward-compatible additions)
 * @param patch patch version number (backward-compatible fixes)
 * 
 * @since 0.0.1
 * @apiNote Follows semantic versioning principles for API compatibility
 */
public record ApiVersion(int major, int minor, int patch) implements Comparable<ApiVersion> {
    
    /**
     * Creates a new API version with validation.
     * 
     * @param major major version (must be non-negative)
     * @param minor minor version (must be non-negative)
     * @param patch patch version (must be non-negative)
     * @throws IllegalArgumentException if any version component is negative
     */
    public ApiVersion {
        if (major < 0) {
            throw new IllegalArgumentException("Major version cannot be negative: " + major);
        }
        if (minor < 0) {
            throw new IllegalArgumentException("Minor version cannot be negative: " + minor);
        }
        if (patch < 0) {
            throw new IllegalArgumentException("Patch version cannot be negative: " + patch);
        }
    }
    
    /**
     * Creates an API version from a string representation.
     * 
     * @param version version string in "major.minor.patch" format
     * @return parsed API version
     * @throws IllegalArgumentException if version format is invalid
     */
    public static @NotNull ApiVersion parse(@NotNull String version) {
        if (version == null || version.trim().isEmpty()) {
            throw new IllegalArgumentException("Version string cannot be null or empty");
        }
        
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Version must be in 'major.minor.patch' format: " + version);
        }
        
        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = Integer.parseInt(parts[2]);
            return new ApiVersion(major, minor, patch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid version format: " + version, e);
        }
    }
    
    /**
     * Checks if this version is compatible with another version.
     * 
     * <p>Compatibility follows semantic versioning rules:
     * <ul>
     * <li>Same major version required</li>
     * <li>This version's minor must be >= other's minor</li>
     * <li>Patch versions are ignored for compatibility</li>
     * </ul>
     * 
     * @param other the version to check compatibility with
     * @return true if versions are compatible
     */
    public boolean isCompatible(@NotNull ApiVersion other) {
        return this.major == other.major && this.minor >= other.minor;
    }
    
    /**
     * Checks if this is a newer version than the other.
     * 
     * @param other the version to compare against
     * @return true if this version is newer
     */
    public boolean isNewerThan(@NotNull ApiVersion other) {
        return this.compareTo(other) > 0;
    }
    
    /**
     * Checks if this is an older version than the other.
     * 
     * @param other the version to compare against
     * @return true if this version is older
     */
    public boolean isOlderThan(@NotNull ApiVersion other) {
        return this.compareTo(other) < 0;
    }
    
    @Override
    public int compareTo(@NotNull ApiVersion other) {
        int majorCompare = Integer.compare(this.major, other.major);
        if (majorCompare != 0) {
            return majorCompare;
        }
        
        int minorCompare = Integer.compare(this.minor, other.minor);
        if (minorCompare != 0) {
            return minorCompare;
        }
        
        return Integer.compare(this.patch, other.patch);
    }
    
    /**
     * Gets the string representation in "major.minor.patch" format.
     * 
     * @return version string
     */
    @Override
    public @NotNull String toString() {
        return major + "." + minor + "." + patch;
    }
}