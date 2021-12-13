package com.burchard36.rust.lib;

import com.burchard36.inventory.ItemWrapper;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("ConstantConditions")
public class RustItems {

    public static boolean isRustItem(final ItemStack stack) {
        return new ItemWrapper(stack).hasDataString("rust_item");
    }

    public static boolean notMatches(final ItemStack stack, final RustItemType type) {
        if (!isRustItem(stack)) return true;
        return getRustItemFrom(stack).itemType != type;
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
}
