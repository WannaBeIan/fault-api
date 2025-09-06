package com.upfault.fault.api.types;

/**
 * Represents different types of damage that can be dealt to entities.
 * 
 * <p>This enum categorizes damage for use with resistance calculations,
 * combat mechanics, and damage processing systems.
 * 
 * @since 0.0.1
 */
public enum DamageType {
    
    /**
     * Damage dealt by melee attacks.
     * 
     * <p>This includes sword strikes, punching, and other close-combat attacks.
     */
    MELEE,
    
    /**
     * Damage dealt by ranged attacks.
     * 
     * <p>This includes arrows, tridents, and other projectile-based damage.
     */
    RANGED,
    
    /**
     * Damage dealt by magic attacks.
     * 
     * <p>This includes spells, potions, and other magical effects.
     */
    MAGIC,
    
    /**
     * True damage that ignores all resistances.
     * 
     * <p>This type of damage cannot be reduced by armor or resistance effects.
     */
    TRUE,
    
    /**
     * Fire-based damage.
     * 
     * <p>This includes burning, lava, fire blocks, and fire spells.
     */
    FIRE,
    
    /**
     * Fall damage from dropping or being knocked down.
     * 
     * <p>This includes damage from falling from heights or being pushed.
     */
    FALL,
    
    /**
     * Explosion damage from TNT, creepers, and other explosive sources.
     * 
     * <p>This type of damage often has area-of-effect properties.
     */
    EXPLOSION
}