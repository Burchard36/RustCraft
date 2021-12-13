package com.burchard36.rust.events.spigot;

import com.burchard36.rust.Rust;
import com.burchard36.rust.lib.RustItemType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public record PlayerRespawnListener(Rust pluginInstance) implements Listener {

    public PlayerRespawnListener(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent e) {
        final Player player = e.getPlayer();
        player.getInventory().setItem(0, this.pluginInstance.getDefaultYamlConfig().getRustItem(RustItemType.ROCK).getItem());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
