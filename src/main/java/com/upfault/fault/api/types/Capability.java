package com.upfault.fault.api.types;

/**
 * Standard capabilities that can be queried from the CapabilityService.
 *
 * @since 0.0.1
 * @apiNote Capabilities represent optional features that may not be available in all implementations
 */
public enum Capability {
    /**
     * Persistent data storage support.
     */
    PERSISTENT_DATA,
    
    /**
     * Economy integration support.
     */
    ECONOMY,
    
    /**
     * Permission system integration.
     */
    PERMISSIONS,
    
    /**
     * Chat system integration.
     */
    CHAT,
    
    /**
     * Party system support.
     */
    PARTIES,
    
    /**
     * Guild/clan system support.
     */
    GUILDS,
    
    /**
     * PvP system integration.
     */
    PVP,
    
    /**
     * Anti-cheat integration.
     */
    ANTI_CHEAT,
    
    /**
     * Resource pack support.
     */
    RESOURCE_PACKS,
    
    /**
     * Advanced GUI system.
     */
    ADVANCED_GUI,
    
    /**
     * Leaderboard system.
     */
    LEADERBOARDS,
    
    /**
     * Quest system support.
     */
    QUESTS,
    
    /**
     * Matchmaking system.
     */
    MATCHMAKING,
    
    /**
     * Privacy compliance features.
     */
    PRIVACY_COMPLIANCE,
    
    /**
     * Multi-server topology support.
     */
    MULTI_SERVER,
    
    /**
     * Webhook integration.
     */
    WEBHOOKS,
    
    /**
     * Advanced metrics collection.
     */
    ADVANCED_METRICS
}