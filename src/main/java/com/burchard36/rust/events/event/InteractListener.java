package com.burchard36.rust.events.event;

import com.burchard36.Logger;
import com.burchard36.inventory.ItemWrapper;
import com.burchard36.rust.Rust;
import com.burchard36.rust.lib.RustItemType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

        if (block != null) {
            if (!this.pluginInstance.getDefaultYamlConfig().getInteractableMaterials().contains(block.getType()) &&
                    !player.hasPermission("rustcraft.interact.bypass")) {
                Logger.debug("Cancelling onInteract because player didnt have permission or interactle block wasnt interacted with", this.pluginInstance);
                e.setCancelled(true);
            }
        }

        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (stack == null) return;
            final ItemWrapper wrapper = new ItemWrapper(stack);
            if (wrapper.getStringDataValue("rust_item") != null) {
                final String value = wrapper.getStringDataValue("rust_item");

                assert block != null;
                if (value.equalsIgnoreCase(RustItemType.ROCK.getName()) && this.pluginInstance.getDefaultYamlConfig().getInteractableMaterials().contains(block.getType())) {
                    Logger.debug("Adding potion effect because player is holding rock", this.pluginInstance);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, (20 * 4), 4));
                }
            }
        }
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
