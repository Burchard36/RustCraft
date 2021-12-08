package com.burchard36.rust.events.event;

import com.burchard36.rust.Rust;
import com.burchard36.rust.lib.RustItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {

    private final Rust pluginInstance;

    public PlayerJoinListener(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);
    }

    @EventHandler
    public final void onPlayerJoin(final PlayerJoinEvent joinEvent) {
        final Player player = joinEvent.getPlayer();

        if (!player.hasPlayedBefore() || player.getInventory().isEmpty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getInventory().clear();
                    player.getInventory().setItem(0, RustItems.getRockItem().getItemStack());
                }
            }.runTaskLater(this.pluginInstance, 1);
        }
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }

}
