package com.burchard36.rust.events.spigot;

import com.burchard36.rust.Rust;
import com.burchard36.rust.gui.CraftingPageIndex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class SwitchHandListener implements Listener {

    public SwitchHandListener(final Rust pluginInstance) {
        pluginInstance.getServer().getPluginManager().registerEvents(this, pluginInstance);
    }

    @EventHandler
    public void onHandSwitch(final PlayerSwapHandItemsEvent swapEvent) {
        swapEvent.setCancelled(true);

        new CraftingPageIndex().showTo(swapEvent.getPlayer());
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }
}
