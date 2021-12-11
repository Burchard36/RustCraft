package com.burchard36.rust.events.event;

import com.burchard36.Logger;
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
import org.bukkit.inventory.ItemStack;
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
        RustItem nodeReward = null;
        int harvestAmount = 1;
        switch (node.currentResourceType) {
            case SULFUR -> {
                Logger.debug("(BlockBreakEvent) Detected node as SULFUR ", this.plugin);
                nodeReward = this.plugin.getDefaultYamlConfig().getUncookedSulfurItem();
                harvestAmount = this.plugin.getDefaultYamlConfig().sulfurNodeHarvestAmount();
            }

            case STONE -> {
                Logger.debug("(BlockBreakEvent) Detected node as STONE ", this.plugin);
                nodeReward = this.plugin.getDefaultYamlConfig().getRustStoneItem();
                harvestAmount = this.plugin.getDefaultYamlConfig().stoneNodeHarvestAmount();
            }

            case METAL -> {
                Logger.debug("(BlockBreakEvent) Detected node as METAL ", this.plugin);
                nodeReward = this.plugin.getDefaultYamlConfig().getUncookedMetalItem();
                harvestAmount = this.plugin.getDefaultYamlConfig().metalNodeHarvestAmount();
            }
        }

        if (RustItems.isRustItem(itemUsed)) {
            final RustItem item = RustItems.getRustItemFrom(itemUsed);
            assert item != null;
            harvestAmount = harvestAmount * item.getItemStackMultiplier();
        }

        nodeReward.getItem().setAmount(harvestAmount);
        final HashMap<Integer, ItemStack> leftOvers = breakingPlayer.getInventory().addItem(nodeReward.getItem());
        for (ItemStack stack : leftOvers.values()) breakingPlayer.getWorld().dropItem(breakingPlayer.getLocation(), stack);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
