package com.burchard36.rust;

import com.burchard36.Api;
import com.burchard36.ApiLib;
import com.burchard36.Logger;
import com.burchard36.rust.commands.RustCommandHandler;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.data.DataManager;
import com.burchard36.rust.data.NodeType;
import com.burchard36.rust.events.RustListenerHandler;
import com.burchard36.rust.managers.ResourceNodeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class Rust extends JavaPlugin implements Api {

    private final static Random rand = new Random();
    public static Rust INSTANCE;
    private ApiLib lib;
    private RustCommandHandler commandHandler;
    private RustListenerHandler listenerHandler;
    private DefaultYamlConfig defaultYamlConfig;
    private DataManager dataManager;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.lib = new ApiLib().initializeApi(this);
        this.defaultYamlConfig = new DefaultYamlConfig(this);
        this.dataManager = new DataManager(this);
        this.commandHandler = new RustCommandHandler(this);
        this.listenerHandler = new RustListenerHandler(this);

    }

    @Override
    public void onDisable() {
        INSTANCE = null;
        listenerHandler.unregisterAll();
        this.dataManager.getNodeManager().shutdown();
        this.dataManager.shutdown();
        Logger.log("DataManager successfully saved data");
    }

    public void reloadPluginConfigs() {
        this.defaultYamlConfig = new DefaultYamlConfig(this);
    }

    public DefaultYamlConfig getDefaultYamlConfig() {
        return this.defaultYamlConfig;
    }

    public ApiLib getLib() {
        return this.lib;
    }

    public ResourceNodeManager getNodeManager() {
        return this.dataManager.getNodeManager();
    }

    public static Location getRandomLocation(final World world, final int max, final int min) {
        final int randomX = getRandom(max);
        final int randomZ = getRandom(min);
        final int randomY = world.getHighestBlockYAt(randomX, randomZ);
        return new Location(world, randomX, randomY, randomZ);
    }

    public static Material getNodeMaterial(final NodeType  nodeType) {
        switch (nodeType) {
            case STONE -> {
                return INSTANCE.defaultYamlConfig.getStoneWorldMaterial();
            }
            case SULFUR -> {
                return INSTANCE.getDefaultYamlConfig().getSulfurWorldMaterial();
            }
            case METAL -> {
                return INSTANCE.getDefaultYamlConfig().getMetalWorldMaterial();
            }
        }
        return INSTANCE.defaultYamlConfig.getStoneWorldMaterial();
    }

    private static int getRandom(final int max) {
        return rand.nextInt(max - -max) + -max;
    }

    @Override
    public boolean isDebug() {
        return true;
    }

    @Override
    public String loggerPrefix() {
        return "&b&lMineRust ";
    }
}
