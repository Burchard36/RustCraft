package com.burchard36.rust.events.event;

import com.burchard36.rust.Rust;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    private final Rust pluginInstance;

    public PlayerRespawnListener(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent e) {
        final Player player = e.getPlayer();
        player.getInventory().setItem(0, this.pluginInstance.getDefaultYamlConfig().getRockItem().getItem());
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }

}
