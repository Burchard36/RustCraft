package com.burchard36.rust.events.event;

import com.burchard36.rust.Rust;
import com.burchard36.rust.lib.RustItem;
import com.burchard36.rust.lib.RustItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerRespawnListener implements Listener {

    private final Rust pluginInstance;

    public PlayerRespawnListener(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @EventHandler
    public final void onRespawn(final PlayerRespawnEvent e) {
        final Player player = e.getPlayer();
        player.getInventory().setItem(0, RustItems.getRockItem().getItemStack());
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }

}
