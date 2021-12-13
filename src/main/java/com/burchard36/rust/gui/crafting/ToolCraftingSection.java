package com.burchard36.rust.gui.crafting;

import com.burchard36.Logger;
import com.burchard36.inventory.ClickableItem;
import com.burchard36.inventory.ItemWrapper;
import com.burchard36.inventory.PluginInventory;
import com.burchard36.rust.Rust;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.lib.RustItem;
import com.burchard36.rust.lib.RustItemType;
import com.burchard36.rust.lib.RustItems;
import com.burchard36.rust.lib.RustPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.burchard36.ApiLib.convert;

public class ToolCraftingSection {

    private final PluginInventory inventory;

    public ToolCraftingSection(final DefaultYamlConfig config) {
        final ClickableItem background = new ClickableItem(new ItemWrapper(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).setDisplayName("&f "))
                .onClick(this::handleCraft);
        final ClickableItem stonePickaxe = new ClickableItem(new ItemWrapper(config.getRustItem(RustItemType.STONE_PICKAXE).getItem()))
                .onClick(this::handleCraft);
        final ClickableItem stoneAxe = new ClickableItem(new ItemWrapper(config.getRustItem(RustItemType.STONE_AXE).getItem()))
                .onClick(this::handleCraft);
        this.inventory = new PluginInventory(54, "&3&lTool Crafting Section")
                .fillWith(background)
                .onClick((onClick) -> onClick.setCancelled(true))
                .addClickableItemAtSlot(0, stonePickaxe)
                .addClickableItemAtSlot(1, stoneAxe);

    }

    private void handleCraft(final InventoryClickEvent action) {
        action.setCancelled(true);
        if (action.getCurrentItem() == null) return;
        if (!RustItems.isRustItem(action.getCurrentItem())) return;
        final Player player = (Player) action.getWhoClicked();
        final RustItem item = new RustItem(action.getCurrentItem(),
                RustItems.getItemType(action.getCurrentItem()));

        final HashMap<RustItemType, Integer> rustItemRecipes = item.getRustCraftRecipe();
        final HashMap<Material, Integer> vanillaItemRecipes = item.getVanillaCraftRecipe();

        final List<Boolean> hasItems = new ArrayList<>();
        for (final RustItemType type : rustItemRecipes.keySet()) {
            boolean bool = RustPlayer.hasRustItems(player, type, rustItemRecipes.get(type));
            if (!bool) hasItems.add(false);
            if (bool) {
                Logger.debug("RustItem was found, hasRequiredItem set to true", Rust.INSTANCE);
            } else Logger.debug("RustItem was not found, hasRequiredItem set to false", Rust.INSTANCE);
        }

        if (hasItems.isEmpty()) {
            for (final Material type : vanillaItemRecipes.keySet()) {
                boolean bool = RustPlayer.hasVanillaItem(player, type, vanillaItemRecipes.get(type));
                if (!bool) hasItems.add(false);
                if (bool) {
                    Logger.debug("VanillaItem was found, hasRequiredItem set to true", Rust.INSTANCE);
                } else Logger.debug("VanillaItem was not found, hasRequiredItem set to false", Rust.INSTANCE);
            }
        }

        if (!hasItems.isEmpty()) {
            player.sendMessage(convert("&cYou do not have enough resources to craft this item!"));
            return;
        }

        rustItemRecipes.forEach((K, V) -> {
            Logger.debug("Removing RustItem: " + K.name(), Rust.INSTANCE);
            RustPlayer.removeRustItems(player, K, V);
        });
        vanillaItemRecipes.forEach((K, V) -> {
            Logger.debug("Removing VanillaItem: " + K.name(), Rust.INSTANCE);
            RustPlayer.removeVanillaItems(player, K, V);
        });

        player.getInventory().addItem(item.getItem());
        player.sendMessage(convert("&a&oYou successfully crafted the item!"));
    }

    public final void showTo(final Player player) {
        this.inventory.register(Rust.INSTANCE).open(player);
    }
}
