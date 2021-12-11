package com.burchard36.rust.lib;

import com.burchard36.inventory.ItemWrapper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class RustItem {

    public ItemWrapper wrapper;
    public RustItemType itemType;
    public int harvestMultiplier;
    public HashMap<RustItemType, Integer> rustItemCraftAmounts;
    public HashMap<Material, Integer> vanillaItemCraftAmounts;

    RustItemFlag allowInteraction  = RustItemFlag.ALLOW_INTERACTION;
    RustItemFlag allowPlace = RustItemFlag.DENY_PLACE;

    public RustItem(final ItemWrapper wrapper,
            final RustItemType type) {
        this.wrapper = wrapper;
        this.itemType = type;
        this.harvestMultiplier = 1;
        this.rustItemCraftAmounts = new HashMap<>();
        this.vanillaItemCraftAmounts = new HashMap<>();
    }

    public RustItem(final ItemStack stack,
                    final RustItemType type) {
        this.wrapper = new ItemWrapper(stack);
        this.itemType = type;
        this.harvestMultiplier = 1;
        this.rustItemCraftAmounts = new HashMap<>();
        this.vanillaItemCraftAmounts = new HashMap<>();
    }

    /**
     * If you created this instance with an ItemStack, use this method for getting the multiplier
     */
    public final int getItemStackMultiplier() {
        if (!this.wrapper.hasDataString("harvest_multiplier")) return 1;
        return this.wrapper.getIntegerDataValue("harvest_multiplier");
    }

    public final HashMap<RustItemType, Integer> getRustCraftRecipe() {
        if (!this.rustItemCraftAmounts.isEmpty()) return this.rustItemCraftAmounts;
        final String recipesString = this.wrapper.getStringDataValue("rust_craft_cost");

        for (final String ingredient : recipesString.split(",")) {
            final String[] ingredients = ingredient.split(":");
            this.rustItemCraftAmounts.putIfAbsent(RustItemType.valueOf(ingredients[0]), Integer.parseInt(ingredients[1]));
        }

        return this.rustItemCraftAmounts;
    }

    public final ItemStack getItem() {
        this.wrapper.addDataString("rust_item", this.itemType.name);
        this.wrapper.addDataString("allow_interact", this.allowInteraction.getName());
        this.wrapper.addDataString("allow_place", this.allowPlace.getName());
        this.wrapper.addDataInteger("harvest_multiplier", this.harvestMultiplier);
        String rustCraftCost = "";
        for (final RustItemType type : this.rustItemCraftAmounts.keySet()) {
            String item = type.getName() + ":" + this.rustItemCraftAmounts.get(type) + ",";
            rustCraftCost = rustCraftCost + item;
        }
        this.wrapper.addDataString("rust_craft_cost", rustCraftCost);
        String vanillaCraftCost = "";
        for (final Material type : this.vanillaItemCraftAmounts.keySet()) {
            String item = type.name() + ":" + this.vanillaItemCraftAmounts.get(type) + ",";
            rustCraftCost = rustCraftCost + item;
        }
        this.wrapper.addDataString("vanilla_craft_cost", vanillaCraftCost);

        return this.wrapper.getItemStack();
    }

}
