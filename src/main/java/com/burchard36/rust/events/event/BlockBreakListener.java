package com.burchard36.rust.events.event;

import com.burchard36.rust.Rust;
import com.burchard36.rust.managers.ResourceNodeManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class BlockBreakListener implements Listener {

    private final Rust plugin;

    public BlockBreakListener(final Rust pluginInstance) {
        this.plugin = pluginInstance;
        this.plugin.getServer().getPluginManager().registerEvents(this, pluginInstance);

    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block brokenBlock = event.getBlock();

        new BukkitRunnable() {
            @Override
            public void run() {
                checkIfNode(brokenBlock);
            }
        }.runTaskLater(this.plugin, 1);
    }

    private void checkIfNode(final Block block) {
        final ResourceNodeManager manager = this.plugin.getNodeManager();
        manager.checkNode(block);
    }


    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
