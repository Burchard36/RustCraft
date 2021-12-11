package com.burchard36.rust.gui;

import com.burchard36.Logger;
import com.burchard36.inventory.ClickableItem;
import com.burchard36.inventory.ItemWrapper;
import com.burchard36.inventory.PluginInventory;
import com.burchard36.rust.Rust;
import com.burchard36.rust.gui.crafting.ToolCraftingSection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CraftingPageIndex {

    private final PluginInventory inventory;

    public CraftingPageIndex() {
        final ClickableItem background = new ClickableItem(new ItemWrapper(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).setDisplayName("&f "));
        final ClickableItem toolsSection = new ClickableItem(new ItemWrapper(new ItemStack(Material.STONE_PICKAXE))
                .setDisplayName("&e&lTools Section"))
                .onClick((onClick) -> {
                    Logger.debug("Tools section for CraftinPageIndex was clicked", Rust.INSTANCE);
                    final Player player = (Player) onClick.getWhoClicked();
                    new ToolCraftingSection(Rust.INSTANCE.getDefaultYamlConfig()).showTo(player);
                });
        this.inventory = new PluginInventory(27, "&3&l&oCrafting!")
                .fillWith(background)
                .addClickableItemAtSlot(10, toolsSection)
                .onClick((onClick) -> onClick.setCancelled(true));
    }

    public final void showTo(final Player player) {
        this.inventory.register(Rust.INSTANCE);
        this.inventory.open(player);
    }
}
