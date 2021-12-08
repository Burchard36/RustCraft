package com.burchard36.rust.config;

import com.burchard36.Logger;
import com.burchard36.inventory.ItemWrapper;
import com.burchard36.rust.Rust;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DefaultYamlConfig extends YamlConfiguration {

    private final Rust pluginInstance;

    public DefaultYamlConfig(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        try {
            pluginInstance.saveResource("config.yml", false);
            this.load(this.getConfigFile());
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    public final boolean isDebugMode() {
        return this.getBoolean("DebugMode");
    }

    public final int nodeResetTimeMinutes() {
        return this.getInt("NodeSettings.ResetTimer");
    }

    public final int maxStoneNodes() {
        return this.getInt("WorldSettings.MaxStoneNodes");
    }

    public final int maxMetalNodes() {
        return this.getInt("WorldSettings.MaxMetalNodes");
    }

    public final List<Material> deniedBlocksUnderNodes() {
        final List<Material> materials = new ArrayList<>();

        this.getStringList("WorldSettings.DenyBlocksUnderNode").forEach((materialString) ->
                materials.add(Material.getMaterial(materialString)));
        return materials;
    }

    public final List<Biome> deniedStoneNodeBiomes() {
        final List<Biome> biomes = new ArrayList<>();
        this.getStringList("WorldSettings.DenyStoneBiomeNodeSpawn").forEach((biomeString) ->
                biomes.add(Biome.valueOf(biomeString)));
        return biomes;
    }

    public final List<Biome> deniedMetalNodeBiomes() {
        final List<Biome> biomes = new ArrayList<>();
        this.getStringList("WorldSettings.DenyMetalBiomeNodeSpawn").forEach((biomeString) ->
                biomes.add(Biome.valueOf(biomeString)));
        return biomes;
    }

    public final List<Biome> deniedSulfurNodeBiomes() {
        final List<Biome> biomes = new ArrayList<>();
        this.getStringList("WorldSettings.DenySulfurBiomeNodeSpawn").forEach((biomeString) ->
                biomes.add(Biome.valueOf(biomeString)));
        return biomes;
    }

    public final World getWorld() {
        String worldName = this.getString("WorldSettings.UseWorld");
        World world = Bukkit.getWorld(worldName);
        if (world != null) return world;
        world = new WorldCreator(worldName).createWorld();
        return world;
    }

    public final List<Material> getBreakableMaterials() {
        final List<Material> breakableMaterial = new ArrayList<>();
        this.getStringList("WorldSettings.BreakableBlocks").forEach((stringMaterial) ->
                breakableMaterial.add(Material.getMaterial(stringMaterial)));
        return breakableMaterial;
    }

    public final List<Material> getInteractableMaterials() {
        final List<Material> interactableMaterial = new ArrayList<>();
        this.getStringList("WorldSettings.InteractableBlocks").forEach((stringMaterial) ->
                interactableMaterial.add(Material.getMaterial(stringMaterial)));
        return interactableMaterial;
    }

    public final ItemWrapper getUncookedSulfurItem() {
        return this.getWrapperFromPath("Items.Sulfur.Ore");
    }

    public int getRockDropAmount() {
        return this.getInt("Items.Rock.HarvestAmount");
    }

    private ItemWrapper getWrapperFromPath(final String path) {
        ItemStack stack = new ItemStack(Material.DIRT);
        ItemWrapper wrapper = new ItemWrapper(stack);
        final ConfigurationSection sec = this.getConfigurationSection(path);
        if (sec == null) {
            Logger.error("Section was null when loading item from config, path: " + path);
            return null;
        }
        for (final String key : sec.getKeys(false)) {
            if (key.equalsIgnoreCase("Material")) stack.setType(Material.getMaterial(sec.getString(key)));
            if (key.equalsIgnoreCase("Name")) wrapper.setDisplayName(sec.getString(key));
            if (key.equalsIgnoreCase("Lore")) wrapper.setItemLore(sec.getStringList(key));
            if (key.equalsIgnoreCase("CustomModelData")) wrapper.setModelData(sec.getInt(key));
        }

        return wrapper;
    }

    public final int maxSulfurNodes() {
        return this.getInt("WorldSettings.MaxSulfurNodes");
    }

    public final int maxNodeGenerationX() {
        return this.getInt("WorldSettings.MaxNodeGenerationX");
    }

    public final int maxNodeGenerationZ() {
        return this.getInt("WorldSettings.MaxNodeGenerationZ");
    }

    public final Material getStoneWorldMaterial() {
        return Material.getMaterial(this.getString("WorldSettings.StoneMaterial"));
    }

    public final Material getMetalWorldMaterial() {
        return Material.getMaterial(this.getString("WorldSettings.MetalMaterial"));
    }

    public final Material getSulfurWorldMaterial() {
        return Material.getMaterial(this.getString("WorldSettings.SulfurMaterial"));
    }

    public final File getConfigFile() {
        return new File(this.pluginInstance.getDataFolder(), "config.yml");
    }
}
