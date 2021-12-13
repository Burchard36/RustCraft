package com.burchard36.rust;

import com.burchard36.Api;
import com.burchard36.ApiLib;
import com.burchard36.Logger;
import com.burchard36.rust.commands.RustCommandHandler;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.data.DataManager;
import com.burchard36.rust.data.NodeType;
import com.burchard36.rust.events.RustListenerHandler;
import com.burchard36.rust.lib.RustItemType;
import com.burchard36.rust.managers.PlayerDataManager;
import com.burchard36.rust.managers.ResourceNodeManager;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
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

        RecipeChoice choice = new RecipeChoice.ExactChoice(this.defaultYamlConfig.getRustItem(RustItemType.RUST_WOOD).getItem());
        Bukkit.addRecipe(new FurnaceRecipe(new NamespacedKey(this, "charcoal_recipe"),
                this.defaultYamlConfig.getRustItem(RustItemType.RUST_CHARCOAL).getItem(), choice, 0, 60));
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

    public PlayerDataManager getPlayerDataManager() {
        return this.dataManager.getPlayerDataManager();
    }

    public static Location getRandomLocation(final World world, final int max, final int min) {
        final int randomX = getRandom(max);
        final int randomZ = getRandom(min);
        final int randomY = world.getHighestBlockYAt(randomX, randomZ);
        return new Location(world, randomX, randomY, randomZ);
    }

    public void reloadPlugin() {
        Logger.log("Reloading plugin! Please note that if you" +
                "edited your node values to be HIGHER than what they were before this reload, prepare for LAG!! As this will trigger all nodes to regenerate!");
        try {
            this.defaultYamlConfig.load(this.defaultYamlConfig.getConfigFile());
            Logger.log("Reloaded config.yml!");
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }

        this.dataManager.reload();
    }

    private static int getRandom(final int max) {
        return rand.nextInt(max - -max) + -max;
    }

    @Override
    public boolean isDebug() {
        return this.defaultYamlConfig.isDebugMode();
    }

    @Override
    public String loggerPrefix() {
        return "&b&lRustCraft ";
    }
}
