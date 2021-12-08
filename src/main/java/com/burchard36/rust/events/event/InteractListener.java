package com.burchard36.rust.events.event;

import com.burchard36.rust.Rust;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    private final Rust pluginInstance;

    public InteractListener(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent e) {

    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
