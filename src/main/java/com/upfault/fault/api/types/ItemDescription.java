package com.upfault.fault.api.types;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Immutable description of a Minecraft item without implementation details.
 * 
 * @param material the material name (e.g., "minecraft:diamond_sword")
 * @param amount the stack size
 * @param displayName the custom display name
 * @param lore the item lore lines
 * @param customModelData the custom model data value
 * @param enchantments map of enchantment names to levels
 * @param attributes map of attribute names to values
 * @param unbreakable whether the item is unbreakable
 * @param customData custom data entries
 * 
 * @since 0.0.1
 * @apiNote Abstraction layer over ItemStack for API usage
 */
public record ItemDescription(
    @NotNull String material,
    int amount,
    @Nullable Component displayName,
    @NotNull List<Component> lore,
    @Nullable Integer customModelData,
    @NotNull Map<String, Integer> enchantments,
    @NotNull Map<String, Double> attributes,
    boolean unbreakable,
    @NotNull Map<NamespacedId, String> customData
) {
    
    public ItemDescription {
        if (material == null || material.isEmpty()) {
            throw new IllegalArgumentException("Material cannot be null or empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive: " + amount);
        }
        if (lore == null) {
            throw new IllegalArgumentException("Lore cannot be null");
        }
        if (enchantments == null) {
            throw new IllegalArgumentException("Enchantments cannot be null");
        }
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes cannot be null");
        }
        if (customData == null) {
            throw new IllegalArgumentException("Custom data cannot be null");
        }
        
        // Make collections immutable
        lore = List.copyOf(lore);
        enchantments = Map.copyOf(enchantments);
        attributes = Map.copyOf(attributes);
        customData = Map.copyOf(customData);
    }
    
    /**
     * Creates a simple ItemDescription with just a material.
     * 
     * @param material the material name
     * @return basic item description
     */
    public static @NotNull ItemDescription of(@NotNull String material) {
        return new ItemDescription(
            material, 1, null, List.of(), null,
            Map.of(), Map.of(), false, Map.of()
        );
    }
    
    /**
     * Creates a simple ItemDescription with material and amount.
     * 
     * @param material the material name
     * @param amount the stack size
     * @return basic item description with amount
     */
    public static @NotNull ItemDescription of(@NotNull String material, int amount) {
        return new ItemDescription(
            material, amount, null, List.of(), null,
            Map.of(), Map.of(), false, Map.of()
        );
    }
    
    /**
     * Checks if this item has a custom display name.
     * 
     * @return true if display name is set
     */
    public boolean hasDisplayName() {
        return displayName != null;
    }
    
    /**
     * Checks if this item has lore.
     * 
     * @return true if lore is not empty
     */
    public boolean hasLore() {
        return !lore.isEmpty();
    }
    
    /**
     * Checks if this item has custom model data.
     * 
     * @return true if custom model data is set
     */
    public boolean hasCustomModelData() {
        return customModelData != null;
    }
    
    /**
     * Checks if this item has enchantments.
     * 
     * @return true if enchantments map is not empty
     */
    public boolean hasEnchantments() {
        return !enchantments.isEmpty();
    }
    
    /**
     * Checks if this item has attributes.
     * 
     * @return true if attributes map is not empty
     */
    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }
    
    /**
     * Checks if this item has custom data.
     * 
     * @return true if custom data map is not empty
     */
    public boolean hasCustomData() {
        return !customData.isEmpty();
    }
    
    /**
     * Gets the level of a specific enchantment.
     * 
     * @param enchantment the enchantment name
     * @return the enchantment level, or 0 if not present
     */
    public int getEnchantmentLevel(@NotNull String enchantment) {
        return enchantments.getOrDefault(enchantment, 0);
    }
    
    /**
     * Checks if this item has a specific enchantment.
     * 
     * @param enchantment the enchantment name
     * @return true if the enchantment is present
     */
    public boolean hasEnchantment(@NotNull String enchantment) {
        return enchantments.containsKey(enchantment);
    }
    
    /**
     * Gets the value of a specific attribute.
     * 
     * @param attribute the attribute name
     * @return the attribute value, or 0.0 if not present
     */
    public double getAttributeValue(@NotNull String attribute) {
        return attributes.getOrDefault(attribute, 0.0);
    }
    
    /**
     * Checks if this item has a specific attribute.
     * 
     * @param attribute the attribute name
     * @return true if the attribute is present
     */
    public boolean hasAttribute(@NotNull String attribute) {
        return attributes.containsKey(attribute);
    }
    
    /**
     * Gets custom data by key.
     * 
     * @param key the custom data key
     * @return the custom data value, or null if not present
     */
    public @Nullable String getCustomData(@NotNull NamespacedId key) {
        return customData.get(key);
    }
    
    /**
     * Checks if this item has specific custom data.
     * 
     * @param key the custom data key
     * @return true if the custom data is present
     */
    public boolean hasCustomData(@NotNull NamespacedId key) {
        return customData.containsKey(key);
    }
    
    /**
     * Creates a builder for modifying this item description.
     * 
     * @return item builder with current values
     */
    public @NotNull Builder toBuilder() {
        return new Builder(this);
    }
    
    /**
     * Builder class for ItemDescription.
     */
    public static class Builder {
        private String material;
        private int amount;
        private Component displayName;
        private List<Component> lore;
        private Integer customModelData;
        private Map<String, Integer> enchantments;
        private Map<String, Double> attributes;
        private boolean unbreakable;
        private Map<NamespacedId, String> customData;
        
        public Builder(@NotNull String material) {
            this.material = material;
            this.amount = 1;
            this.lore = new java.util.ArrayList<>();
            this.enchantments = new java.util.HashMap<>();
            this.attributes = new java.util.HashMap<>();
            this.customData = new java.util.HashMap<>();
        }
        
        public Builder(@NotNull ItemDescription item) {
            this.material = item.material;
            this.amount = item.amount;
            this.displayName = item.displayName;
            this.lore = new java.util.ArrayList<>(item.lore);
            this.customModelData = item.customModelData;
            this.enchantments = new java.util.HashMap<>(item.enchantments);
            this.attributes = new java.util.HashMap<>(item.attributes);
            this.unbreakable = item.unbreakable;
            this.customData = new java.util.HashMap<>(item.customData);
        }
        
        public @NotNull Builder amount(int amount) {
            this.amount = amount;
            return this;
        }
        
        public @NotNull Builder displayName(@Nullable Component displayName) {
            this.displayName = displayName;
            return this;
        }
        
        public @NotNull Builder lore(@NotNull List<Component> lore) {
            this.lore = new java.util.ArrayList<>(lore);
            return this;
        }
        
        public @NotNull Builder addLore(@NotNull Component line) {
            this.lore.add(line);
            return this;
        }
        
        public @NotNull Builder customModelData(@Nullable Integer customModelData) {
            this.customModelData = customModelData;
            return this;
        }
        
        public @NotNull Builder enchantment(@NotNull String enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }
        
        public @NotNull Builder attribute(@NotNull String attribute, double value) {
            this.attributes.put(attribute, value);
            return this;
        }
        
        public @NotNull Builder unbreakable(boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }
        
        public @NotNull Builder customData(@NotNull NamespacedId key, @NotNull String value) {
            this.customData.put(key, value);
            return this;
        }
        
        public @NotNull ItemDescription build() {
            return new ItemDescription(
                material, amount, displayName, lore, customModelData,
                enchantments, attributes, unbreakable, customData
            );
        }
    }
    
    @Override
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ItemDescription[");
        sb.append(material);
        if (amount != 1) {
            sb.append(" x").append(amount);
        }
        if (hasDisplayName()) {
            sb.append(", name='").append(displayName).append("'");
        }
        if (hasEnchantments()) {
            sb.append(", enchantments=").append(enchantments.size());
        }
        sb.append("]");
        return sb.toString();
    }
}
