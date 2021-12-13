package com.burchard36.rust.data;

import com.burchard36.Logger;
import com.burchard36.json.PluginDataMap;
import com.burchard36.rust.Rust;
import com.burchard36.rust.data.json.JsonPlayerData;
import com.burchard36.rust.data.json.JsonResourceNode;
import com.burchard36.rust.data.json.JsonRustClan;
import com.burchard36.rust.managers.PlayerDataManager;
import com.burchard36.rust.managers.ResourceNodeManager;
import com.burchard36.rust.managers.RustClanManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class DataManager {

    private final Rust pluginInstance;
    private final BukkitTask saveTask;
    private final ResourceNodeManager nodeManager;
    private final PlayerDataManager playerDataManager;
    private final RustClanManager clanManager;
    private final PluginDataMap resourceNodeMap;
    private final PluginDataMap playerDataMap;
    private final PluginDataMap clansDataMap;

    public DataManager(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.resourceNodeMap = new PluginDataMap(this.pluginInstance.getLib().getPluginDataManager().getJsonWriter());
        this.playerDataMap = new PluginDataMap(this.pluginInstance.getLib().getPluginDataManager().getJsonWriter());
        this.clansDataMap = new PluginDataMap(this.pluginInstance.getLib().getPluginDataManager().getJsonWriter());
        this.pluginInstance.getLib().getPluginDataManager().registerPluginMap(DataFile.RESOURCE_NODES, this.resourceNodeMap);
        this.pluginInstance.getLib().getPluginDataManager().registerPluginMap(DataFile.PLAYER_DATA, this.playerDataMap);
        this.pluginInstance.getLib().getPluginDataManager().registerPluginMap(DataFile.RUST_CLANS, this.clansDataMap);

        this.loadResourceNodes();
        this.nodeManager = new ResourceNodeManager(this.resourceNodeMap, this.pluginInstance);
        this.loadPlayerData();
        this.playerDataManager = new PlayerDataManager(this.playerDataMap, this.pluginInstance);
        this.loadClansData();
        this.clanManager = new RustClanManager(this.clansDataMap, this.pluginInstance);


        this.saveTask = new BukkitRunnable() {
            @Override
            public void run() {
                Logger.log("Saving all Resource node-data asynchronously. . .");
                resourceNodeMap.saveAll();
                Logger.log("Finished saving Resource node-data!");
                playerDataMap.saveAll();
                Logger.log("Finished saving Player-data!");
                clansDataMap.saveAll();
                Logger.log("Finished saving RustClans-data!");
                Logger.log("Successfully saved all plugin-data!");
            }
        }.runTaskTimerAsynchronously(this.pluginInstance, (20 * 60) * 5, (20 * 60) * 5);
    }

    public final void reload() {
        this.nodeManager.reload();
    }

    public final void shutdown() {
        Logger.log("Shutting down DataManager. . .");
        this.saveTask.cancel();
        this.resourceNodeMap.saveAll();
        this.playerDataMap.saveAll();
        this.clansDataMap.saveAll();
    }

    private void loadResourceNodes() {
        Logger.debug("Loading Resource Nodes directory. . .", this.pluginInstance);
        final File nodes = new File(this.pluginInstance.getDataFolder(), "/data/nodes");
        if (!nodes.exists()) if (nodes.mkdirs()) Logger.log("Successfully created directory: /data/nodes");

        final File[] nodeDirectory = nodes.listFiles();
        if (this.directoryIsEmpty(nodeDirectory)) return;

        for (final File nodeFile : nodeDirectory) {
            if (this.notEndsWithJson(nodeFile)) continue;

            final UUID uuid = this.getUuidFromFile(nodeFile);
            this.resourceNodeMap.loadDataFile(uuid.toString(), new JsonResourceNode(uuid));
        }
    }


    private void loadPlayerData() {
        Logger.log("Loading Player Data directory. . .");
        final File players = new File(this.pluginInstance.getDataFolder(), "/data/players");
        if (!players.exists()) if (players.mkdirs()) Logger.log("Successfully created directory: /data/players");

        final File[] playerDirectory = players.listFiles();
        if (this.directoryIsEmpty(playerDirectory)) return;

        for (final File dataFile : playerDirectory) {
            if (this.notEndsWithJson(dataFile)) continue;

            final UUID uuid = this.getUuidFromFile(dataFile);
            this.playerDataMap.loadDataFile(uuid.toString(), new JsonPlayerData(uuid));
        }
    }

    private void loadClansData() {
        Logger.log("Loading Clans Data directory. . .");
        final File clans = new File(this.pluginInstance.getDataFolder(), "/data/clans/");
        if (!clans.exists()) if (clans.mkdirs()) Logger.log("Successfully created directory: /data/clans");

        final File[] clansDirectory = clans.listFiles();
        if (this.directoryIsEmpty(clansDirectory)) return;

        for (final File dataFile : clansDirectory) {
            if (this.notEndsWithJson(dataFile)) continue;

            final UUID uuid = this.getUuidFromFile(dataFile);
            this.clansDataMap.loadDataFile(uuid.toString(), new JsonRustClan(uuid));
        }
    }

    private UUID getUuidFromFile(final File file) {
        final String fileNameUuid = file.getName().split("\\.")[0];
        return UUID.fromString(fileNameUuid);
    }

    private boolean notEndsWithJson(final File file) {
        if (!file.getName().endsWith(".json")) {
            Logger.warn("You have a data directory that is not a JSON file! Please remove this dude!");
            return true;
        } else return false;
    }

    private boolean directoryIsEmpty(final File[] files) {
        if (files == null) {
            Logger.error("A data directory did not exist! Disabling plugin to prevent data corruption.");
            this.pluginInstance.getServer().getPluginManager().disablePlugin(this.pluginInstance);
            return true;
        } else return files.length == 0;
    }

    public final ResourceNodeManager getNodeManager() {
        return this.nodeManager;
    }

    public final PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public final RustClanManager getClanManager() {
        return this.clanManager;
    }
}
