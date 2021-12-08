package com.burchard36.rust.events.event;

import com.burchard36.rust.Rust;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class BlockBreakListener implements Listener {

    public BlockBreakListener(final Rust pluginInstance) {
        pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);

    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block brokenBlock = event.getBlock();
    }


    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
