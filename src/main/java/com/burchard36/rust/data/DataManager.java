package com.burchard36.rust.data;

import com.burchard36.Logger;
import com.burchard36.json.PluginDataMap;
import com.burchard36.rust.Rust;
import com.burchard36.rust.data.json.JsonResourceNode;
import com.burchard36.rust.managers.ResourceNodeManager;

import java.io.File;
import java.util.UUID;

public class DataManager {

    private final Rust pluginInstance;

    private final ResourceNodeManager nodeManager;
    private final PluginDataMap resourceNodeMap;

    public DataManager(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.resourceNodeMap = new PluginDataMap(this.pluginInstance.getLib().getPluginDataManager().getJsonWriter());
        this.pluginInstance.getLib().getPluginDataManager().registerPluginMap(DataFile.RESOURCE_NODES, this.resourceNodeMap);

        this.loadResourceNodes();
        this.nodeManager = new ResourceNodeManager(this.resourceNodeMap, this.pluginInstance);
    }

    private void loadResourceNodes() {
        Logger.debug("Loading Resource Nodes directory. . .", this.pluginInstance);
        final File nodes = new File(this.pluginInstance.getDataFolder(), "/data/nodes");
        if (!nodes.exists()) if (nodes.mkdirs()) {
            Logger.log("Successfully created directory: /data/nodes");
        }
        final File[] nodeDirectory = nodes.listFiles();
        if (nodeDirectory == null) {
            Logger.error("DUDE! /data/nodes doesn't exist! Whats going on man are you deleting stuff you shouldn't be?");
            return;
        }

        for (final File nodeFile : nodeDirectory) {
            if (!nodeFile.getName().endsWith(".json")) {
                Logger.warn("You have a file in /data/nodes that is not a .json file! Please remove this dude!");
                continue;
            }

            final String fileNameUuid = nodeFile.getName().split("\\.")[0];
            Logger.debug("Name of filed after splitting it is: " + fileNameUuid, this.pluginInstance);
            final UUID uuid = UUID.fromString(fileNameUuid);

            this.resourceNodeMap.loadDataFile(uuid.toString(), new JsonResourceNode(uuid));
        }
    }

    public final ResourceNodeManager getNodeManager() {
        return this.nodeManager;
    }
}
