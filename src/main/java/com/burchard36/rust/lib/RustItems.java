package com.burchard36.rust.lib;

import com.burchard36.inventory.ItemWrapper;
import com.burchard36.rust.Rust;
import com.burchard36.rust.config.DefaultYamlConfig;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class RustItems {

    public static boolean isRustItem(final ItemStack stack) {
        return new ItemWrapper(stack).hasDataString("rust_item");
    }

    public static boolean isRustItem(final ItemWrapper wrapper) {
        return wrapper.hasDataString("rust_item");
    }

    public static boolean matches(final ItemStack stack, final RustItemType type) {
        if (!isRustItem(stack)) return false;
        return getRustItemFrom(stack).itemType == type;
    }

    public static RustItemType getItemType(final ItemStack stack) {
        if (!isRustItem(stack)) return null;

        final ItemWrapper wrapper = new ItemWrapper(stack);
        return RustItemType.valueOf(wrapper.getStringDataValue("rust_item"));
    }

    public static RustItem getRustItemFrom(final ItemStack stack) {
        if (!isRustItem(stack)) return null;

        return new RustItem(stack, getItemType(stack));
    }

    public static RustItem getRustItemFrom(final RustItemType type) {
        final DefaultYamlConfig conf = Rust.INSTANCE.getDefaultYamlConfig();
        RustItem item = null;
        switch (type) {
            case ROCK -> item = conf.getRockItem();
            case RUST_CHARCOAL -> item = conf.getCharcoalItem();
            case RUST_STONE -> item = conf.getRustStoneItem();
            case RUST_WOOD -> item = conf.getUncookedWoodItem();
            case STONE_AXE -> item = conf.getStoneAxe();
            case COOKED_SULFUR -> item = conf.getCookedSulfurItem();
            case STONE_PICKAXE -> item = conf.getStonePickaxe();
            case UNCOOKED_METAL -> item = conf.getUncookedMetalItem();
            case METAL_FRAGMENTS -> item = conf.getMetalFragmentItem();
            case UNCOOKED_SULFUR -> item = conf.getUncookedSulfurItem();
        }
        if (item == null) throw new IllegalArgumentException("RustItemType did not exist in index when getting by RustItemType. Please contact a developer.");
        return item;
    }
}
