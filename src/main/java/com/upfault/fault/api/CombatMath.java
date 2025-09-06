package com.upfault.fault.api;

import com.upfault.fault.api.types.AttributeMap;
import com.upfault.fault.api.types.DamageType;
import com.upfault.fault.api.types.Resistance;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Service for calculating damage with resistances and attributes.
 * 
 * <p>This service handles complex damage calculations involving multiple
 * resistances, attribute modifiers, and damage type interactions.
 * 
 * @since 0.0.1
 * @apiNote Thread safety: All methods are safe to call from any thread
 */
public interface CombatMath {
    
    /**
     * Applies damage calculation with resistances and attributes.
     * 
     * <p>The calculation process:
     * <ol>
     *   <li>Apply relevant attribute modifiers to base damage</li>
     *   <li>Apply resistances for the specified damage type</li>
     *   <li>Ensure final damage is not negative</li>
     * </ol>
     * 
     * <p>Multiple resistances of the same type stack additively, but the
     * total resistance is capped at 100% (complete immunity).
     * 
     * @param type the type of damage being dealt
     * @param base the base damage amount before modifications
     * @param resistances list of resistances to apply
     * @param attrs attribute map containing damage modifiers
     * @return the final damage amount after all calculations
     * 
     * @apiNote Safe to call from any thread. TRUE damage type ignores
     *          all resistances but still applies attribute modifiers.
     */
    double apply(
        @NotNull DamageType type,
        double base,
        @NotNull List<Resistance> resistances,
        @NotNull AttributeMap attrs
    );
    
    /**
     * Calculates total resistance percentage for a damage type.
     * 
     * <p>Multiple resistances of the same type stack additively, but are
     * capped at 100% (1.0) for complete immunity.
     * 
     * @param type the damage type to calculate resistance for
     * @param resistances list of resistances to sum
     * @return the total resistance percentage (0.0 to 1.0)
     * 
     * @apiNote Safe to call from any thread
     */
    double calculateResistance(@NotNull DamageType type, @NotNull List<Resistance> resistances);
    
    /**
     * Checks if the given resistances provide immunity to a damage type.
     * 
     * @param type the damage type to check
     * @param resistances list of resistances to check
     * @return true if total resistance is 100% or higher
     * 
     * @apiNote Safe to call from any thread. Always returns false for TRUE damage.
     */
    boolean isImmune(@NotNull DamageType type, @NotNull List<Resistance> resistances);
}