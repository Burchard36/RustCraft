package com.burchard36.rust.managers;

import com.burchard36.json.PluginDataMap;
import com.burchard36.rust.Rust;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.data.NodeType;
import com.burchard36.rust.data.json.JsonResourceNode;
import com.burchard36.rust.managers.generators.ResourceNodeGenerator;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ResourceNodeManager {

    private final Rust pluginInstance;
    private final PluginDataMap resourceNodes;
    private final ResourceNodeGenerator nodeGenerator;

    public ResourceNodeManager(final PluginDataMap resourceNodes,
                               final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.resourceNodes = resourceNodes;
        this.nodeGenerator = new ResourceNodeGenerator(this);
    }

    public void shutDown() {

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
