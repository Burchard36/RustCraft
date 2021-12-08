package com.burchard36.rust.events.event;

import com.burchard36.inventory.ItemWrapper;
import com.burchard36.rust.Rust;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {

    public BlockPlaceListener(final Rust pluginInstance) {
        pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent placeEvent) {
        final Player placingPlayer = placeEvent.getPlayer();
        final Block placedBlock = placeEvent.getBlockPlaced();;
        final ItemStack placedItem = placeEvent.getItemInHand();
        placeEvent.setCancelled(true);

        this.handleNodeCreatorItem(placingPlayer, placedItem);
    }

    /**
     * Handles the NodeCreator item
     * @param placingPlayer player who placed item
     * @param placedItem ItemStack that got placed
     */
    private void handleNodeCreatorItem(final Player placingPlayer,
                                       final ItemStack placedItem) {
        final ItemWrapper wrapper = new ItemWrapper(placedItem);
        if (wrapper.getStringDataValue("creator_item") == null) return;


    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }
}
