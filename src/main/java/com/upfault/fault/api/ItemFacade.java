package com.upfault.fault.api;

import com.upfault.fault.api.types.ItemDescription;
import com.upfault.fault.api.types.NamespacedId;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Immutable item description model with components and attributes.
 * 
 * <p>This facade provides an API-level abstraction over Minecraft items
 * without directly exposing ItemStack or other implementation details.
 * Implementations can provide mappers to convert to Paper ItemStack instances.
 * 
 * <p>Example usage:
 * <pre>{@code
 * ItemFacade items = Fault.service(ItemFacade.class);
 * 
 * // Create a custom sword
 * ItemDescription sword = items.createItem()
 *     .material("minecraft:diamond_sword")
 *     .displayName(Component.text("Legendary Blade"))
 *     .lore(List.of(
 *         Component.text("A sword of great power"),
 *         Component.text("Forged in ancient times")
 *     ))
 *     .customModelData(12345)
 *     .attribute("minecraft:generic.attack_damage", 10.0)
 *     .enchantment("minecraft:sharpness", 5)
 *     .build();
 * 
 * // Convert to ItemStack (implementation-specific)
 * ItemStackMapper mapper = items.getMapper();
 * org.bukkit.inventory.ItemStack bukkitItem = mapper.toItemStack(sword);
 * }</pre>
 * 
 * <p><strong>Threading:</strong> ItemDescription objects are immutable and
 * thread-safe. Builder operations and mappings may be performed asynchronously.
 * 
 * @since 0.0.1
 * @apiNote This provides a clean separation between API and implementation details
 */
public interface ItemFacade {

    /**
     * Creates a new item description builder.
     * 
     * @return a new item builder
     * @since 0.0.1
     */
    @NotNull
    ItemBuilder createItem();

    /**
     * Creates an item description from a material type.
     * 
     * @param material the material name (e.g., "minecraft:diamond_sword")
     * @return item description for the material
     * @since 0.0.1
     */
    @NotNull
    ItemDescription createItem(@NotNull String material);

    /**
     * Gets the item stack mapper for converting to implementation types.
     * 
     * @return the item stack mapper
     * @since 0.0.1
     */
    @NotNull
    ItemStackMapper getMapper();

    /**
     * Validates that a material name exists and is valid.
     * 
     * @param material the material name to validate
     * @return future containing true if the material is valid
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> isValidMaterial(@NotNull String material);

    /**
     * Gets a list of all valid material names.
     * 
     * @return future containing list of material names
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<String>> getAllMaterials();

    /**
     * Builder interface for creating item descriptions.
     * 
     * @since 0.0.1
     */
    interface ItemBuilder {

        /**
         * Sets the material type for this item.
         * 
         * @param material the material name (e.g., "minecraft:diamond_sword")
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder material(@NotNull String material);

        /**
         * Sets the amount/stack size for this item.
         * 
         * @param amount the stack size (1-64 typically)
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder amount(int amount);

        /**
         * Sets the display name for this item.
         * 
         * @param name the display name component
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder displayName(@Nullable Component name);

        /**
         * Sets the lore (description lines) for this item.
         * 
         * @param lore the lore components
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder lore(@Nullable List<Component> lore);

        /**
         * Adds a single line to the item's lore.
         * 
         * @param line the lore line to add
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder addLore(@NotNull Component line);

        /**
         * Sets the custom model data for this item.
         * 
         * @param customModelData the custom model data value
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder customModelData(@Nullable Integer customModelData);

        /**
         * Adds an enchantment to this item.
         * 
         * @param enchantment the enchantment name (e.g., "minecraft:sharpness")
         * @param level the enchantment level
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder enchantment(@NotNull String enchantment, int level);

        /**
         * Sets all enchantments for this item.
         * 
         * @param enchantments map of enchantment names to levels
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder enchantments(@NotNull Map<String, Integer> enchantments);

        /**
         * Adds an attribute modifier to this item.
         * 
         * @param attribute the attribute name (e.g., "minecraft:generic.attack_damage")
         * @param value the attribute value
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder attribute(@NotNull String attribute, double value);

        /**
         * Sets all attributes for this item.
         * 
         * @param attributes map of attribute names to values
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder attributes(@NotNull Map<String, Double> attributes);

        /**
         * Sets whether this item is unbreakable.
         * 
         * @param unbreakable true if the item should be unbreakable
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder unbreakable(boolean unbreakable);

        /**
         * Sets custom data for this item.
         * 
         * @param key the data key
         * @param value the data value
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder customData(@NotNull NamespacedId key, @NotNull String value);

        /**
         * Sets multiple custom data entries.
         * 
         * @param data map of custom data
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        ItemBuilder customData(@NotNull Map<NamespacedId, String> data);

        /**
         * Builds the item description.
         * 
         * @return the built item description
         * @since 0.0.1
         */
        @NotNull
        ItemDescription build();
    }

    /**
     * Mapper interface for converting between API items and implementation types.
     * 
     * @since 0.0.1
     */
    interface ItemStackMapper {

        /**
         * Converts an ItemDescription to a Bukkit ItemStack.
         * 
         * @param description the item description
         * @return the equivalent ItemStack
         * @since 0.0.1
         */
        @NotNull
        org.bukkit.inventory.ItemStack toItemStack(@NotNull ItemDescription description);

        /**
         * Converts a Bukkit ItemStack to an ItemDescription.
         * 
         * @param itemStack the ItemStack
         * @return the equivalent ItemDescription
         * @since 0.0.1
         */
        @NotNull
        ItemDescription fromItemStack(@NotNull org.bukkit.inventory.ItemStack itemStack);

        /**
         * Converts multiple ItemDescriptions to ItemStacks.
         * 
         * @param descriptions the item descriptions
         * @return array of equivalent ItemStacks
         * @since 0.0.1
         */
        @NotNull
        org.bukkit.inventory.ItemStack[] toItemStacks(@NotNull List<ItemDescription> descriptions);

        /**
         * Converts multiple ItemStacks to ItemDescriptions.
         * 
         * @param itemStacks the ItemStacks
         * @return list of equivalent ItemDescriptions
         * @since 0.0.1
         */
        @NotNull
        List<ItemDescription> fromItemStacks(@NotNull org.bukkit.inventory.ItemStack[] itemStacks);
    }
}
