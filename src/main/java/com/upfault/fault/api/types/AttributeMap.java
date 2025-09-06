package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe map of attribute keys to their modifier collections.
 * 
 * <p>This class manages attribute modifiers with proper ordering and calculation.
 * All operations are thread-safe and modifiers are automatically sorted by their
 * operation type and execution order.
 * 
 * @since 0.0.1
 * @apiNote All modifier calculations follow the standard order: ADD -> MULTIPLY -> FINAL_ADD
 */
public class AttributeMap {
    
    private final Map<AttributeKey<?>, List<Modifier>> modifiers = new ConcurrentHashMap<>();
    
    /**
     * Creates an empty attribute map.
     */
    public AttributeMap() {
    }
    
    /**
     * Adds a modifier for the specified attribute.
     * 
     * <p><strong>Threading:</strong> Thread-safe.
     * 
     * @param key the attribute key
     * @param modifier the modifier to add
     */
    public void addModifier(@NotNull AttributeKey<?> key, @NotNull Modifier modifier) {
        modifiers.computeIfAbsent(key, k -> new ArrayList<>()).add(modifier);
        // Re-sort the modifiers list
        modifiers.get(key).sort(Modifier::compareTo);
    }
    
    /**
     * Removes a specific modifier for an attribute.
     * 
     * <p><strong>Threading:</strong> Thread-safe.
     * 
     * @param key the attribute key
     * @param modifier the modifier to remove
     * @return true if the modifier was removed
     */
    public boolean removeModifier(@NotNull AttributeKey<?> key, @NotNull Modifier modifier) {
        List<Modifier> list = modifiers.get(key);
        if (list == null) {
            return false;
        }
        boolean removed = list.remove(modifier);
        if (list.isEmpty()) {
            modifiers.remove(key);
        }
        return removed;
    }
    
    /**
     * Removes all modifiers for an attribute.
     * 
     * <p><strong>Threading:</strong> Thread-safe.
     * 
     * @param key the attribute key
     * @return the number of modifiers removed
     */
    public int clearModifiers(@NotNull AttributeKey<?> key) {
        List<Modifier> list = modifiers.remove(key);
        return list != null ? list.size() : 0;
    }
    
    /**
     * Gets all modifiers for an attribute.
     * 
     * <p><strong>Threading:</strong> Thread-safe, returns immutable copy.
     * 
     * @param key the attribute key
     * @return immutable list of modifiers (may be empty)
     */
    public @NotNull List<Modifier> getModifiers(@NotNull AttributeKey<?> key) {
        List<Modifier> list = modifiers.get(key);
        return list != null ? List.copyOf(list) : List.of();
    }
    
    /**
     * Calculates the final attribute value with all modifiers applied.
     * 
     * <p>Calculation order:
     * <ol>
     * <li>Start with base value</li>
     * <li>Apply ADD modifiers in order</li>
     * <li>Apply MULTIPLY modifiers in order</li>
     * <li>Apply FINAL_ADD modifiers in order</li>
     * </ol>
     * 
     * <p><strong>Threading:</strong> Thread-safe.
     * 
     * @param key the attribute key
     * @param baseValue the base value before modifiers
     * @return the final calculated value
     */
    public double calculate(@NotNull AttributeKey<?> key, double baseValue) {
        List<Modifier> list = modifiers.get(key);
        if (list == null || list.isEmpty()) {
            return baseValue;
        }
        
        double value = baseValue;
        
        // Apply ADD modifiers
        for (Modifier modifier : list) {
            if (modifier.operation() == Modifier.Operation.ADD) {
                value += modifier.value();
            }
        }
        
        // Apply MULTIPLY modifiers
        for (Modifier modifier : list) {
            if (modifier.operation() == Modifier.Operation.MULTIPLY) {
                value *= modifier.value();
            }
        }
        
        // Apply FINAL_ADD modifiers
        for (Modifier modifier : list) {
            if (modifier.operation() == Modifier.Operation.FINAL_ADD) {
                value += modifier.value();
            }
        }
        
        return value;
    }
    
    /**
     * Gets all attribute keys that have modifiers.
     * 
     * <p><strong>Threading:</strong> Thread-safe, returns immutable copy.
     * 
     * @return set of attribute keys with modifiers
     */
    public @NotNull Set<AttributeKey<?>> getAttributeKeys() {
        return Set.copyOf(modifiers.keySet());
    }
    
    /**
     * Checks if an attribute has any modifiers.
     * 
     * <p><strong>Threading:</strong> Thread-safe.
     * 
     * @param key the attribute key
     * @return true if the attribute has modifiers
     */
    public boolean hasModifiers(@NotNull AttributeKey<?> key) {
        List<Modifier> list = modifiers.get(key);
        return list != null && !list.isEmpty();
    }
    
    /**
     * Gets the total number of modifiers across all attributes.
     * 
     * <p><strong>Threading:</strong> Thread-safe.
     * 
     * @return total modifier count
     */
    public int size() {
        return modifiers.values().stream().mapToInt(List::size).sum();
    }
    
    /**
     * Checks if this attribute map is empty.
     * 
     * <p><strong>Threading:</strong> Thread-safe.
     * 
     * @return true if no modifiers are present
     */
    public boolean isEmpty() {
        return modifiers.isEmpty();
    }
    
    /**
     * Clears all modifiers from this map.
     * 
     * <p><strong>Threading:</strong> Thread-safe.
     */
    public void clear() {
        modifiers.clear();
    }
    
    /**
     * Creates a copy of this attribute map.
     * 
     * <p><strong>Threading:</strong> Thread-safe.
     * 
     * @return a new attribute map with the same modifiers
     */
    public @NotNull AttributeMap copy() {
        AttributeMap copy = new AttributeMap();
        for (Map.Entry<AttributeKey<?>, List<Modifier>> entry : modifiers.entrySet()) {
            copy.modifiers.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }
    
    @Override
    public @NotNull String toString() {
        return "AttributeMap{" + size() + " modifiers across " + modifiers.size() + " attributes}";
    }
}