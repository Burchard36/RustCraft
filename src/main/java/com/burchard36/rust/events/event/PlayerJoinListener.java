package com.burchard36.rust.events.event;

import com.burchard36.rust.Rust;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    public PlayerJoinListener(final Rust pluginInstance) {
        pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);
    }

    @EventHandler
    public final void onPlayerJoin(final PlayerJoinEvent joinEvent) {
        final Player player = joinEvent.getPlayer();
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }

}
