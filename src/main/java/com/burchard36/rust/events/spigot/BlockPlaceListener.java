package com.burchard36.rust.events.spigot;

import com.burchard36.rust.Rust;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    public BlockPlaceListener(final Rust pluginInstance) {
        pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent placeEvent) {
        placeEvent.setCancelled(true);
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }
}
