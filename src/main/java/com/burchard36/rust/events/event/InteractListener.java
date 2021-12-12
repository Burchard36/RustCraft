package com.burchard36.rust.events.event;

import com.burchard36.rust.Rust;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    private final Rust pluginInstance;

    public InteractListener(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final ItemStack stack = e.getItem();
        final Block block = e.getClickedBlock();
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
