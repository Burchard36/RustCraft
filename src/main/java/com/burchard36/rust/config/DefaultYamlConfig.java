package com.burchard36.rust.config;

import com.burchard36.Logger;
import com.burchard36.inventory.ItemWrapper;
import com.burchard36.rust.Rust;
import com.burchard36.rust.lib.RustItem;
import com.burchard36.rust.lib.RustItemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


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

    public final int stoneNodeHarvestAmount() {
        return this.getInt("WorldSettings.StoneNodeHarvestAmount");
    }

    public final int sulfurNodeHarvestAmount() {
        return this.getInt("WorldSettings.SulfurNodeHarvestAmount");
    }

    public final int metalNodeHarvestAmount() {
        return this.getInt("WorldSettings.MetalNodeHarvestAmount");
    }

    public final int woodNodeHarvestAmount() {
        return this.getInt("WorldSettings.WoodNodeHarvestAmount");
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

    public static List<String> getPlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
    }

    public final List<Material> getInteractableMaterials() {
        final List<Material> interactableMaterial = new ArrayList<>();
        this.getStringList("WorldSettings.InteractableBlocks").forEach((stringMaterial) ->
                interactableMaterial.add(Material.getMaterial(stringMaterial)));
        return interactableMaterial;
    }

    public final List<Material> getWoodNodeMaterials() {
        final List<Material> woodMaterials = new ArrayList<>();
        this.getStringList("WorldSettings.WoodMaterials").forEach((material) ->
                woodMaterials.add(Material.getMaterial(material)));
        return woodMaterials;
    }

    public final RustItem getRockItem() {
        return this.getRustItemFromPath("Items.Rock", RustItemType.ROCK);
    }

    public final RustItem getRustStoneItem() {
        return this.getRustItemFromPath("Items.Stone.Ore", RustItemType.RUST_STONE);
    }

    public final RustItem getUncookedSulfurItem() {
        return this.getRustItemFromPath("Items.Sulfur.Ore", RustItemType.UNCOOKED_SULFUR);
    }

    public final RustItem getCookedSulfurItem() {
        return this.getRustItemFromPath("Items.Sulfur.Cooked", RustItemType.COOKED_SULFUR);
    }

    public final RustItem getUncookedMetalItem() {
        return this.getRustItemFromPath("Items.Metal.Ore", RustItemType.UNCOOKED_METAL);
    }

    public final RustItem getMetalFragmentItem() {
        return this.getRustItemFromPath("Items.Metal.Cooked", RustItemType.METAL_FRAGMENTS);
    }

    public final RustItem getUncookedWoodItem() {
        return this.getRustItemFromPath("Items.Wood.Ore", RustItemType.RUST_WOOD);
    }

    public final RustItem getCharcoalItem() {
        return this.getRustItemFromPath("Items.Wood.Cooked", RustItemType.RUST_CHARCOAL);
    }

    public final RustItem getStonePickaxe() {
        return this.getRustItemFromPath("Items.StonePickaxe", RustItemType.STONE_PICKAXE);
    }

    public final RustItem getStoneAxe() {
        return this.getRustItemFromPath("Items.StoneAxe", RustItemType.STONE_AXE);
    }

    private RustItem getRustItemFromPath(final String path,
                                         final RustItemType type) {
        ItemStack stack = new ItemStack(Material.DIRT);
        final RustItem rustItem = new RustItem(new ItemWrapper(stack), type);
        final ConfigurationSection sec = this.getConfigurationSection(path);
        if (sec == null) {
            Logger.error("Section was null when loading item from config, path: " + path);
            return null;
        }

        for (final String key : sec.getKeys(false)) {
            if (key.equalsIgnoreCase("Material"))
                stack.setType(Material.getMaterial(sec.getString(key)));

            if (key.equalsIgnoreCase("Name"))
                rustItem.wrapper.setDisplayName(sec.getString(key));

            if (key.equalsIgnoreCase("Lore"))
                rustItem.wrapper.setItemLore(sec.getStringList(key));

            if (key.equalsIgnoreCase("CustomModelData"))
                rustItem.wrapper.setModelData(sec.getInt(key));

            if (key.equalsIgnoreCase("HarvestMultiplier"))
                rustItem.harvestMultiplier = sec.getInt(key);

            if (key.equalsIgnoreCase("CraftRecipe")) {
                this.loadCraftRecipes(sec.getConfigurationSection(key), rustItem);
            }
        }

        return rustItem;
    }

    private void loadCraftRecipes(final ConfigurationSection sec, final RustItem item) {
        final HashMap<RustItemType, Integer> rustItemCost = new HashMap<>();
        final HashMap<Material, Integer> vanillaItemCost = new HashMap<>();
        for (final String key : sec.getKeys(false)) {

            if (key.equalsIgnoreCase("Rust")) {
                final List<String> rustList = sec.getStringList(key);
                for (String rustElement : rustList) {
                    final String[] elements = rustElement.split(":");
                    if (elements.length != 2) throw new RuntimeException("Error when parsing RustItemRecipe! Rust recipe did not contain a semicolon!");
                    final RustItemType type = RustItemType.valueOf(elements[0]);
                    if (type == null) throw new RuntimeException("Invalid RustItemType provided: " + elements[0]);

                    int amount = this.getIntFromString(elements[1]);
                    rustItemCost.putIfAbsent(type, amount);
                }

            }

            if (key.equalsIgnoreCase("Vanilla")) {
                final List<String> vanillaList = sec.getStringList(key);
                for (String vanillaElement : vanillaList) {
                    final String[] elements = vanillaElement.split(":");
                    if (elements.length != 2)
                        throw new RuntimeException("Error when parsing VanilllaItemRecipe! Rust recipe did not contain a semicolon!");
                    final Material type = Material.getMaterial(elements[0]);
                    if (type == null) throw new RuntimeException("Invalid VanillaItemType provided: " + elements[0]);

                    int amount = this.getIntFromString(elements[1]);
                    vanillaItemCost.putIfAbsent(type, amount);
                }
            }
        }

        item.rustItemCraftAmounts = rustItemCost;
        item.vanillaItemCraftAmounts = vanillaItemCost;
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

    private int getIntFromString(final String num) {
        try {
            return Integer.parseInt(num);
        } catch (final NumberFormatException ex) {
            throw new RuntimeException("Invalid number provided: " + num);
        }
    }
}
