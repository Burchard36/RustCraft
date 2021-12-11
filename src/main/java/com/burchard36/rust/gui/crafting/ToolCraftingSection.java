package com.burchard36.rust.gui.crafting;

import com.burchard36.inventory.ClickableItem;
import com.burchard36.inventory.ItemWrapper;
import com.burchard36.inventory.PluginInventory;
import com.burchard36.inventory.interfaces.GuiClickAction;
import com.burchard36.rust.Rust;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.lib.RustItem;
import com.burchard36.rust.lib.RustItemType;
import com.burchard36.rust.lib.RustItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ToolCraftingSection {

    private final PluginInventory inventory;

    public ToolCraftingSection(final DefaultYamlConfig config) {
        final ClickableItem background = new ClickableItem(new ItemWrapper(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).setDisplayName("&f "))
                .onClick(this::handleCraft);
        final ClickableItem stonePickaxe = new ClickableItem(new ItemWrapper(config.getStonePickaxe().getItem()))
                .onClick(this::handleCraft);
        final ClickableItem stoneAxe = new ClickableItem(new ItemWrapper(config.getStoneAxe().getItem()));
        this.inventory = new PluginInventory(54, "&3&lTool Crafting Section")
                .fillWith(background)
                .addClickableItemAtSlot(0, stonePickaxe)
                .addClickableItemAtSlot(1, stoneAxe);

    }

    private final void handleCraft(final InventoryClickEvent action) {
        final RustItem item = new RustItem(action.getCurrentItem(),
                RustItems.getItemType(action.getCurrentItem()));
    }

    public final void showTo(final Player player) {
        this.inventory.register(Rust.INSTANCE).open(player);
    }
}
