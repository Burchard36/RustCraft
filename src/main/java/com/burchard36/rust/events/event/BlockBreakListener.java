package com.burchard36.rust.events.event;

import com.burchard36.Logger;
import com.burchard36.inventory.ItemWrapper;
import com.burchard36.rust.Rust;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.data.json.JsonResourceNode;
import com.burchard36.rust.lib.RustItem;
import com.burchard36.rust.lib.RustItems;
import com.burchard36.rust.managers.ResourceNodeManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;


public class BlockBreakListener implements Listener {

    private final Rust plugin;

    public BlockBreakListener(final Rust pluginInstance) {
        this.plugin = pluginInstance;
        this.plugin.getServer().getPluginManager().registerEvents(this, pluginInstance);

    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block brokenBlock = event.getBlock();
        final Player breakingPlayer = event.getPlayer();
        final DefaultYamlConfig config = this.plugin.getDefaultYamlConfig();

        if (!breakingPlayer.hasPermission("rustcraft.blockbreak.bypass") &&
            !config.getBreakableMaterials().contains(brokenBlock.getType())) {
            event.setCancelled(true);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                checkIfNode(brokenBlock,
                        Objects.requireNonNull(breakingPlayer.getEquipment()).getItemInMainHand(),
                        breakingPlayer);
            }
        }.runTaskLater(this.plugin, 1);
    }

    private void checkIfNode(final Block block,
                             final ItemStack itemUsed,
                             final Player breakingPlayer) {
        final ResourceNodeManager manager = this.plugin.getNodeManager();
        final JsonResourceNode node = manager.checkNode(block);

        if (node == null) return;
        Logger.debug("(BlockBreakEvent) Node was not null, checking resource type. . ", this.plugin);
        ItemWrapper wrapper = null;
        switch (node.currentResourceType) {
            case SULFUR -> {
                Logger.debug("(BlockBreakEvent) Detected node as SULFUR ", this.plugin);
                wrapper = this.plugin.getDefaultYamlConfig().getUncookedSulfurItem();
            }
        }

        if (itemUsed != null && wrapper != null) {
            Logger.debug("(BlockBreakEvent) Wrapper and item used wasnt null", this.plugin);
            final int amount = Rust.getHarvestAmountOf(itemUsed);
            wrapper.getItemStack().setAmount(amount);
            Logger.debug("Amount pulled: " + amount, this.plugin);
        } else if (wrapper != null) {
            Logger.debug("Wrapper wasnt null, but itemUsed was", this.plugin);
            wrapper.getItemStack().setAmount(1);
        }

        if (wrapper == null) return;
        Logger.debug("(BlockBreakEvent) wrapper wasnt null, adding items to inventory", this.plugin);
        HashMap<Integer, ItemStack> leftovers = breakingPlayer.getInventory().addItem(wrapper.getItemStack());
        if (!leftovers.isEmpty()) {
            leftovers.values().forEach((item) -> breakingPlayer.getWorld().dropItem(breakingPlayer.getLocation(), item));
        }
    }

    public void giveBlock(final Block block,
                          final Player player) {

    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
