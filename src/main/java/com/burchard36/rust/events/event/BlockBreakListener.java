package com.burchard36.rust.events.event;

import com.burchard36.Logger;
import com.burchard36.rust.Rust;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.data.json.JsonResourceNode;
import com.burchard36.rust.lib.RustItem;
import com.burchard36.rust.lib.RustItems;
import com.burchard36.rust.managers.ResourceNodeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
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
    public void onItemAdd(final EntityPickupItemEvent event) {
        Logger.debug("Firing pickup event", this.plugin);
        final DefaultYamlConfig config = this.plugin.getDefaultYamlConfig();
        if (event.getEntityType() != EntityType.PLAYER) return;
        final Player player = (Player) event.getEntity();

        if (!config.getWoodNodeMaterials().contains(event.getItem().getItemStack().getType())) {
            Logger.debug("cancelling item add event because it wasn't a wood material...", this.plugin);
            return;
        }

        Logger.debug("Was a wood piece", this.plugin);

        final ItemStack newStack = config.getUncookedWoodItem().getItem();
        newStack.setAmount(event.getItem().getItemStack().getAmount());
        event.getItem().setItemStack(newStack);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        event.setDropItems(false);
        final Block brokenBlock = event.getBlock();
        final Player breakingPlayer = event.getPlayer();
        final DefaultYamlConfig config = this.plugin.getDefaultYamlConfig();
        final Material blockType = brokenBlock.getType();

        final boolean brokeAllowedMaterials = config.getBreakableMaterials().contains(blockType);
        final boolean brokeWoodMaterial = config.getWoodNodeMaterials().contains(blockType);
        final boolean brokeSulfurMaterial = config.getSulfurWorldMaterial() == blockType;
        final boolean brokeStoneMaterial = config.getStoneWorldMaterial() == blockType;
        final boolean brokeMetalMaterial = config.getMetalWorldMaterial() == blockType;

        final boolean playerDidntBreakValidMaterial = !brokeMetalMaterial || !brokeAllowedMaterials
                || !brokeWoodMaterial || !brokeSulfurMaterial || !brokeStoneMaterial;

        if (!breakingPlayer.hasPermission("rustcraft.blockbreak.bypass") &&
            playerDidntBreakValidMaterial) {
            event.setCancelled(true);
            Logger.debug("Canceling block break event because the breaking player didn't have permission or broke a allowed broken material", this.plugin);
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
