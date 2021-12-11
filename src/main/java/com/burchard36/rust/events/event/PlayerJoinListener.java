package com.burchard36.rust.events.event;

import com.burchard36.rust.Rust;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public record PlayerJoinListener(Rust pluginInstance) implements Listener {

    public PlayerJoinListener(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent joinEvent) {
        final Player player = joinEvent.getPlayer();

        if (!player.hasPlayedBefore() || player.getInventory().isEmpty()) {
            player.getInventory().clear();
            player.getInventory().setItem(0, this.pluginInstance.getDefaultYamlConfig().getRockItem().getItem());
        }
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
