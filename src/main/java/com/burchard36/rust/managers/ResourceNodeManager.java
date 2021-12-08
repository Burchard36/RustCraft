package com.burchard36.rust.managers;

import com.burchard36.Logger;
import com.burchard36.json.PluginDataMap;
import com.burchard36.rust.Rust;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.data.json.JsonResourceNode;
import com.burchard36.rust.lib.RustLocation;
import com.burchard36.rust.managers.generators.ResourceNodeGenerator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;

public class ResourceNodeManager {

    private final Rust pluginInstance;
    private final BukkitTask runningTask;
    private final PluginDataMap resourceNodes;
    private final ResourceNodeGenerator nodeGenerator;

    public ResourceNodeManager(final PluginDataMap resourceNodes,
                               final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.resourceNodes = resourceNodes;
        this.nodeGenerator = new ResourceNodeGenerator(this);

        this.runningTask = new BukkitRunnable() {
            @Override
            public void run() {
                getNodes().forEach((jsonNode) -> {
                    if (!jsonNode.stillExists()) {
                        Logger.warn("Found a Node that had blocks that didnt exist. Regenerating it now, this is like due to a crash or world editing the resource node!");
                        nodeGenerator.regenerateNode(jsonNode);
                    }
                });
            }
        }.runTaskTimer(this.pluginInstance, 0, (20 * 60) * 5);
    }

    public void shutdown() {
        Logger.log("Shutting down ResourceNodeManager. . .");
        this.runningTask.cancel();
    }

    public final JsonResourceNode checkNode(final Block block) {
        final JsonResourceNode node = this.getNodeByLocation(block.getLocation());
        if (node == null) {
            Logger.debug("Block broken was not a Resource Node", this.pluginInstance);
            return null;
        }

        if (!node.stillExists()) {
            Logger.debug("The node no longer exists! Regenerating. . .", this.pluginInstance);
            this.nodeGenerator.regenerateNode(node);
        } else Logger.debug("The node still exists!", this.pluginInstance);

        return node;
    }

    public final JsonResourceNode getNodeByLocation(final Location loc) {
        Location location = loc.clone();
        Logger.debug("Checking location X: " + location.getBlockX() + " Y: " + location.getBlockY() + " Z: " + location.getBlockZ(), this.pluginInstance);
        JsonResourceNode resourceNode = null;
        for (final JsonResourceNode node : this.getNodes()) {
            for (final RustLocation rustLocation : node.nodeLocations) {
                if (rustLocation.isTheSame(loc)) {
                    resourceNode = node;
                    break;
                }
            }
            if (resourceNode != null) break;
        }
        return resourceNode;
    }

    public final void registerNode(final JsonResourceNode node) {
        this.resourceNodes.loadDataFile(node.getNodeUuid().toString(), node);
    }

    public final Collection<JsonResourceNode> getNodes() {
        final Collection<JsonResourceNode> nodes = new ArrayList<>();
        this.resourceNodes.getDatFiles().forEach((file) -> nodes.add((JsonResourceNode) file));
        return nodes;
    }

    public final DefaultYamlConfig defaultYamlConfig() {
        return this.pluginInstance.getDefaultYamlConfig();
    }

}
