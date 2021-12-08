package com.burchard36.rust.lib;

import com.burchard36.inventory.ItemWrapper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RustItems {

    public static ItemWrapper getRockItem() {
        final ItemStack stack = new ItemStack(Material.CONDUIT, 1);
        final ItemWrapper wrapper = new ItemWrapper(stack);
        wrapper.addDataString("rust_item", RustItem.ROCK.getName());
        wrapper.setDisplayName("&f&l&oRock");
        return wrapper;
    }
}
