package com.burchard36.rust.lib;

import com.burchard36.inventory.ItemWrapper;
import org.bukkit.inventory.ItemStack;

public class RustItems {

    public static boolean isRustItem(final ItemStack stack) {
        return new ItemWrapper(stack).hasDataString("rust_item");
    }

    public static boolean isRustItem(final ItemWrapper wrapper) {
        return wrapper.hasDataString("rust_item");
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
