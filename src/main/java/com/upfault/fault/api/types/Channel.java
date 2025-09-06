package com.upfault.fault.api.types;

/**
 * Represents different notification channels for message delivery.
 * 
 * <p>Each channel has different characteristics and is appropriate for
 * different types of content and urgency levels.
 * 
 * @since 0.0.1
 */
public enum Channel {
    
    /**
     * Regular chat messages that appear in the player's chat window.
     * 
     * <p>This is the standard channel for general communication and messages
     * that players should be able to scroll back and read. Messages persist
     * in the chat history.
     */
    CHAT,
    
    /**
     * Action bar messages that appear above the hotbar.
     * 
     * <p>This channel is ideal for short status updates, temporary information,
     * and non-intrusive notifications. Action bar messages don't clutter the
     * chat and are automatically replaced by newer messages.
     */
    ACTIONBAR,
    
    /**
     * Title messages that appear as large text overlaid on the screen.
     * 
     * <p>This channel is best for important announcements, dramatic effect,
     * or information that needs immediate player attention. Titles can have
     * subtitles and customizable timing.
     */
    TITLE,
    
    /**
     * Boss bar messages that appear as a colored bar at the top of the screen.
     * 
     * <p>Boss bars are excellent for progress indicators, ongoing status
     * information, and persistent notifications that need to remain visible
     * while players continue playing.
     */
    BOSSBAR,
    
    /**
     * External webhook notifications sent to Discord or other services.
     * 
     * <p>This channel allows integration with external communication systems,
     * useful for admin notifications, logging, or keeping communities informed
     * of in-game events even when they're not online.
     */
    WEBHOOK,
    
    /**
     * Toast notifications that appear in the corner of the screen.
     * 
     * <p>Similar to system notifications, these are brief popup messages
     * that appear and fade away. Good for achievements, level-ups, or
     * other celebratory messages.
     */
    TOAST;
    
    /**
     * Checks if this channel delivers messages in-game to players.
     * 
     * @return true if this is an in-game channel
     */
    public boolean isInGame() {
        return this != WEBHOOK;
    }
    
    /**
     * Checks if this channel supports external delivery.
     * 
     * @return true if this channel can deliver to external services
     */
    public boolean isExternal() {
        return this == WEBHOOK;
    }
    
    /**
     * Checks if messages in this channel persist for players to read later.
     * 
     * @return true if messages persist in history
     */
    public boolean isPersistent() {
        return this == CHAT;
    }
    
    /**
     * Checks if this channel supports real-time updates (messages can be replaced).
     * 
     * @return true if messages can be updated in place
     */
    public boolean supportsUpdates() {
        return this == ACTIONBAR || this == BOSSBAR;
    }
    
    /**
     * Gets a human-readable description of this channel.
     * 
     * @return description of the channel's purpose and characteristics
     */
    public String getDescription() {
        return switch (this) {
            case CHAT -> "Regular chat messages with history";
            case ACTIONBAR -> "Non-intrusive status updates above hotbar";
            case TITLE -> "Large overlay text for important announcements";
            case BOSSBAR -> "Persistent colored bar for ongoing status";
            case WEBHOOK -> "External notifications to Discord/webhooks";
            case TOAST -> "Brief popup notifications in screen corner";
        };
    }
}