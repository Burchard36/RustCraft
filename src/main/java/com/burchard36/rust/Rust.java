package com.burchard36.rust;

import com.burchard36.Api;
import com.burchard36.ApiLib;
import com.burchard36.rust.commands.RustCommandHandler;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.data.DataManager;
import com.burchard36.rust.events.RustListenerHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    }

    public void reloadPluginConfigs() {
        this.defaultYamlConfig = new DefaultYamlConfig(this);
    }

    public final DefaultYamlConfig getDefaultYamlConfig() {
        return this.defaultYamlConfig;
    }

    public final ApiLib getLib() {
        return this.lib;
    }

    public static Location getRandomLocation(final World world, final int max, final int min) {
        final int randomX = getRandom(max);
        final int randomZ = getRandom(min);
        final int randomY = world.getHighestBlockYAt(randomX, randomZ);
        return new Location(world, randomX, randomY, randomZ);
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
