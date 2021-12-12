package com.burchard36.rust.events.event;

import com.burchard36.Logger;
import com.burchard36.rust.Rust;
import com.burchard36.rust.lib.RustItem;
import com.burchard36.rust.lib.RustItemType;
import com.burchard36.rust.lib.RustItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class FurnaceBurnListener implements Listener {

    private final Rust pluginInstance;

    public FurnaceBurnListener(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.pluginInstance.getServer().getPluginManager().registerEvents(this, this.pluginInstance);
    }

    @EventHandler
    public void onBurn(final FurnaceBurnEvent event) {
        final ItemStack fuelStack = event.getFuel();

        if (!RustItems.isRustItem(fuelStack) && RustItems.getItemType(fuelStack) != RustItemType.RUST_WOOD) {
            return;
        }

        Logger.debug("Handling furnace burn event", this.pluginInstance);
        event.setBurnTime(event.getBurnTime() + 200);
    }

    @EventHandler
    public void onSmelt(final FurnaceSmeltEvent event) {
        final ItemStack resultStack = event.getResult();
        final ItemStack sourceStack = event.getSource();

        if (!RustItems.isRustItem(sourceStack)) {
            Logger.debug("Cancelling FurnaceSmeltEvent because the source item is not a rust item!", this.pluginInstance);
            return;
        }

        final RustItemType rustItemType = RustItems.getItemType(sourceStack);
        if (rustItemType == null) {
            Logger.warn("Someone on your server is trying to smelt something other than a RustItem! Item type is: " + sourceStack.getType().name());
            event.setCancelled(true);
            return;
        }
        RustItem rustItem = null;

        switch (rustItemType) {

            case UNCOOKED_SULFUR -> rustItem = this.pluginInstance.getDefaultYamlConfig().getCookedSulfurItem();
            case UNCOOKED_METAL -> rustItem = this.pluginInstance.getDefaultYamlConfig().getMetalFragmentItem();
            case RUST_WOOD -> rustItem = this.pluginInstance.getDefaultYamlConfig().getCharcoalItem();
            default -> rustItem = null;
        }

        if (rustItem == null) {
            Logger.warn("Someone tried to smelt something on your server that wasnt a valid RustItem to smelt, were canceling this event.");
            event.setCancelled(true);
            return;
        }

        final ItemStack newResult = rustItem.getItem();
        newResult.setAmount(resultStack.getAmount());
        event.setResult(newResult);
        Logger.debug("Successfully smelted RustItemType: " + rustItemType.name, this.pluginInstance);
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }
}
