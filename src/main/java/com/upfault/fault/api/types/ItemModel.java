package com.upfault.fault.api.types;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Represents a Minecraft item stack model with type-safe NBT data.
 * 
 * <p>This record encapsulates all the essential properties of an item stack
 * without exposing Bukkit-specific types, making it safe for API boundaries.
 * 
 * @param type the item type as a namespaced identifier
 * @param count the stack size (1-127)
 * @param displayName the custom display name, if any
 * @param lore the item's lore lines
 * @param customModelData custom model data for resource packs
 * @param nbt additional NBT data as key-value pairs
 * @param enchantments enchantments as level mappings
 * @param durability current durability (null for non-damageable items)
 * @param maxDurability maximum durability (null for non-damageable items)
 * 
 * @since 0.0.1
 * @apiNote This record is immutable and thread-safe
 */
public record ItemModel(
    @NotNull NamespacedId type,
    int count,
    @Nullable Component displayName,
    @NotNull List<Component> lore,
    @Nullable Integer customModelData,
    @NotNull Map<String, Object> nbt,
    @NotNull Map<NamespacedId, Integer> enchantments,
    @Nullable Integer durability,
    @Nullable Integer maxDurability
) {
    
    /**
     * Compact constructor with validation.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public ItemModel {
        if (type == null) {
            throw new IllegalArgumentException("Item type cannot be null");
        }
        if (count < 1 || count > 127) {
            throw new IllegalArgumentException("Count must be between 1 and 127, got: " + count);
        }
        if (lore == null) {
            throw new IllegalArgumentException("Lore list cannot be null");
        }
        if (nbt == null) {
            throw new IllegalArgumentException("NBT map cannot be null");
        }
        if (enchantments == null) {
            throw new IllegalArgumentException("Enchantments map cannot be null");
        }
        if (durability != null && durability < 0) {
            throw new IllegalArgumentException("Durability cannot be negative: " + durability);
        }
        if (maxDurability != null && maxDurability < 1) {
            throw new IllegalArgumentException("Max durability must be positive: " + maxDurability);
        }
        if (durability != null && maxDurability != null && durability > maxDurability) {
            throw new IllegalArgumentException("Current durability cannot exceed max durability");
        }
        
        // Defensive copying
        lore = List.copyOf(lore);
        nbt = Map.copyOf(nbt);
        enchantments = Map.copyOf(enchantments);
    }
    
    /**
     * Creates a simple item model with just type and count.
     * 
     * @param type the item type
     * @param count the stack size
     * @return new item model
     */
    public static @NotNull ItemModel of(@NotNull NamespacedId type, int count) {
        return new ItemModel(
            type, 
            count, 
            null, 
            List.of(), 
            null, 
            Map.of(), 
            Map.of(), 
            null, 
            null
        );
    }
    
    /**
     * Creates an item model with display name.
     * 
     * @param type the item type
     * @param count the stack size
     * @param displayName the display name
     * @return new item model
     */
    public static @NotNull ItemModel of(@NotNull NamespacedId type, int count, @NotNull Component displayName) {
        return new ItemModel(
            type, 
            count, 
            displayName, 
            List.of(), 
            null, 
            Map.of(), 
            Map.of(), 
            null, 
            null
        );
    }
    
    /**
     * Checks if this item has custom display data.
     * 
     * @return true if the item has display name or lore
     */
    public boolean hasDisplayData() {
        return displayName != null || !lore.isEmpty();
    }
    
    /**
     * Checks if this item has enchantments.
     * 
     * @return true if the item has any enchantments
     */
    public boolean isEnchanted() {
        return !enchantments.isEmpty();
    }
    
    /**
     * Checks if this item is damageable.
     * 
     * @return true if the item has durability data
     */
    public boolean isDamageable() {
        return maxDurability != null;
    }
    
    /**
     * Gets the damage percentage (0.0 = broken, 1.0 = full durability).
     * 
     * @return damage percentage, or 1.0 if not damageable
     */
    public double getDamagePercentage() {
        if (!isDamageable() || durability == null) {
            return 1.0;
        }
        return (double) durability / maxDurability;
    }
    
    /**
     * Creates a builder for this item model.
     * 
     * @return new builder
     */
    public @NotNull Builder toBuilder() {
        return new Builder(this);
    }
    
    /**
     * Creates a new builder.
     * 
     * @param type the item type
     * @return new builder
     */
    public static @NotNull Builder builder(@NotNull NamespacedId type) {
        return new Builder(type);
    }
    
    /**
     * Builder for ItemModel instances.
     */
    public static final class Builder {
        private final NamespacedId type;
        private int count = 1;
        private Component displayName;
        private List<Component> lore = List.of();
        private Integer customModelData;
        private Map<String, Object> nbt = Map.of();
        private Map<NamespacedId, Integer> enchantments = Map.of();
        private Integer durability;
        private Integer maxDurability;
        
        private Builder(@NotNull NamespacedId type) {
            this.type = type;
        }
        
        private Builder(@NotNull ItemModel model) {
            this.type = model.type;
            this.count = model.count;
            this.displayName = model.displayName;
            this.lore = model.lore;
            this.customModelData = model.customModelData;
            this.nbt = model.nbt;
            this.enchantments = model.enchantments;
            this.durability = model.durability;
            this.maxDurability = model.maxDurability;
        }
        
        public @NotNull Builder count(int count) {
            this.count = count;
            return this;
        }
        
        public @NotNull Builder displayName(@Nullable Component displayName) {
            this.displayName = displayName;
            return this;
        }
        
        public @NotNull Builder lore(@NotNull List<Component> lore) {
            this.lore = lore;
            return this;
        }
        
        public @NotNull Builder customModelData(@Nullable Integer customModelData) {
            this.customModelData = customModelData;
            return this;
        }
        
        public @NotNull Builder nbt(@NotNull Map<String, Object> nbt) {
            this.nbt = nbt;
            return this;
        }
        
        public @NotNull Builder enchantments(@NotNull Map<NamespacedId, Integer> enchantments) {
            this.enchantments = enchantments;
            return this;
        }
        
        public @NotNull Builder durability(@Nullable Integer durability) {
            this.durability = durability;
            return this;
        }
        
        public @NotNull Builder maxDurability(@Nullable Integer maxDurability) {
            this.maxDurability = maxDurability;
            return this;
        }
        
        public @NotNull ItemModel build() {
            return new ItemModel(
                type, count, displayName, lore, customModelData,
                nbt, enchantments, durability, maxDurability
            );
        }
    }
}