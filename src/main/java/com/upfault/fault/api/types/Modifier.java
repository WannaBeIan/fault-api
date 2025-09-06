package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an attribute modifier with operation type and execution order.
 * 
 * @param operation the modifier operation type
 * @param value the modifier value
 * @param order execution order (lower values execute first)
 * 
 * @since 0.0.1
 * @apiNote Modifiers are applied in order: ADD -> MULTIPLY -> FINAL_ADD
 */
public record Modifier(
    @NotNull Operation operation,
    double value,
    int order
) implements Comparable<Modifier> {
    
    /**
     * Creates a modifier with validation.
     * 
     * @param operation the operation type (cannot be null)
     * @param value the modifier value
     * @param order execution order
     * @throws IllegalArgumentException if operation is null
     */
    public Modifier {
        if (operation == null) {
            throw new IllegalArgumentException("Operation cannot be null");
        }
    }
    
    /**
     * Creates an ADD modifier with default order.
     * 
     * @param value the value to add
     * @return ADD modifier
     */
    public static @NotNull Modifier add(double value) {
        return new Modifier(Operation.ADD, value, 0);
    }
    
    /**
     * Creates a MULTIPLY modifier with default order.
     * 
     * @param value the multiplier
     * @return MULTIPLY modifier
     */
    public static @NotNull Modifier multiply(double value) {
        return new Modifier(Operation.MULTIPLY, value, 0);
    }
    
    /**
     * Creates a FINAL_ADD modifier with default order.
     * 
     * @param value the value to add after multiplication
     * @return FINAL_ADD modifier
     */
    public static @NotNull Modifier finalAdd(double value) {
        return new Modifier(Operation.FINAL_ADD, value, 0);
    }
    
    /**
     * Creates a modifier with custom order.
     * 
     * @param operation the operation type
     * @param value the modifier value
     * @param order execution order
     * @return modifier with custom order
     */
    public static @NotNull Modifier of(@NotNull Operation operation, double value, int order) {
        return new Modifier(operation, value, order);
    }
    
    @Override
    public int compareTo(@NotNull Modifier other) {
        // First sort by operation type priority
        int opCompare = this.operation.priority - other.operation.priority;
        if (opCompare != 0) {
            return opCompare;
        }
        
        // Then by order within the same operation type
        return Integer.compare(this.order, other.order);
    }
    
    /**
     * Modifier operation types.
     */
    public enum Operation {
        /**
         * Add to base value. Applied first.
         */
        ADD(0),
        
        /**
         * Multiply current value. Applied second.
         */
        MULTIPLY(1),
        
        /**
         * Add to final value after multiplication. Applied last.
         */
        FINAL_ADD(2);
        
        private final int priority;
        
        Operation(int priority) {
            this.priority = priority;
        }
        
        /**
         * Gets the execution priority of this operation.
         * 
         * @return priority level (lower executes first)
         */
        public int getPriority() {
            return priority;
        }
    }
    
    @Override
    public @NotNull String toString() {
        return operation + "(" + value + ", order=" + order + ")";
    }
}