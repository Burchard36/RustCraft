package com.burchard36.rust.data.json;

import com.burchard36.Logger;
import com.burchard36.json.JsonDataFile;
import com.burchard36.rust.Rust;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.data.NodeType;
import com.burchard36.rust.lib.RustLocation;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public class JsonResourceNode extends JsonDataFile {

    @SerializedName(value = "node_uuid")
    public String nodeUuid;

    @SerializedName(value = "current_resource_type")
    public NodeType currentResourceType;

    @SerializedName(value = "node_location")
    public RustLocation nodeLocation;

    public JsonResourceNode(final NodeType nodeType,
                            final RustLocation nodeLocation,
                            final UUID uuid) {
        super(Rust.INSTANCE, "/data/nodes/" + uuid.toString() + ".json");
        this.nodeUuid = uuid.toString();
        this.currentResourceType = nodeType;
        this.nodeLocation = nodeLocation;
    }

    public JsonResourceNode(final UUID uuid) {
        super(Rust.INSTANCE, "/data/nodes/" + uuid.toString() + ".json");
        this.nodeUuid = uuid.toString();
    }

    public void deleteSelf() {
        Location location = this.nodeLocation.getSpigotLocation();
        location.getBlock().setType(Material.AIR);

        location.add(1, 0, 0);
        location.getBlock().setType(Material.AIR);

        location.subtract(2, 0, 0);
        location.getBlock().setType(Material.AIR);

        location.add(1, 0, 1);
        location.getBlock().setType(Material.AIR);

        location.subtract(0, 0, 2);
        location.getBlock().setType(Material.AIR);

        location.add(0, 1, 1);
        location.getBlock().setType(Material.AIR);
    }

    public boolean canGenerate() {
        Location location = this.nodeLocation.getSpigotLocation().clone(); // needs to be cloned
        if (location.getBlock().getType() != Material.AIR) return false;

        location = location.add(1, 0, 0);
        if (location.getBlock().getType() != Material.AIR) return false;

        location = location.subtract(2, 0, 0);
        if (location.getBlock().getType() != Material.AIR) return false;

        location = location.add(1, 0, 1);
        if (location.getBlock().getType() != Material.AIR) return false;

        location = location.subtract(0, 0, 2);
        if (location.getBlock().getType() != Material.AIR) return false;

        location = location.add(0, 1, 1);
        return location.getBlock().getType() == Material.AIR;
    }

    public boolean stillExists() {
        Location location = this.nodeLocation.getSpigotLocation().clone();
        if (location.getBlock().getType() == Material.AIR) return false;

        location.add(1, 0, 0);
        if (location.getBlock().getType() == Material.AIR) return false;

        location.subtract(2, 0, 0);
        if (location.getBlock().getType() == Material.AIR) return false;

        location.add(1, 0, 1);
        if (location.getBlock().getType() == Material.AIR) return false;

        location.subtract(0, 0, 2);
        if (location.getBlock().getType() == Material.AIR) return false;

        location.add(0, 1, 1);
        return location.getBlock().getType() != Material.AIR;
    }

    public final boolean blockCheck(final DefaultYamlConfig config) {
        final Location underneathBlock = this.nodeLocation.getSpigotLocation().clone().subtract(0, 1, 0);
        return !config.deniedBlocksUnderNodes().contains(underneathBlock.getBlock().getType());
    }

    public final boolean biomeCheck(final DefaultYamlConfig config) {
        switch (this.currentResourceType) {
            case STONE -> {
                if (config.deniedStoneNodeBiomes()
                        .contains(this.nodeLocation.getSpigotLocation().getBlock().getBiome())) return false;
            }
            case METAL -> {
                if (config.deniedMetalNodeBiomes()
                    .contains(this.nodeLocation.getSpigotLocation().getBlock().getBiome())) return false;
            }
            case SULFUR -> {
                if (config.deniedSulfurNodeBiomes()
                        .contains(this.nodeLocation.getSpigotLocation().getBlock().getBiome())) return false;
            }
        }
        return true;
    }

    public final void createNode(final DefaultYamlConfig config) {
        Logger.debug("Attempting to createNode", Rust.INSTANCE);
        Location location = this.nodeLocation.getSpigotLocation().clone();

        this.createBlock(config, location);
        location = location.add(1, 0, 0);
        this.createBlock(config, location);
        location = location.subtract(2, 0, 0);
        this.createBlock(config, location);
        location = location.add(1, 0, 1);
        this.createBlock(config, location);
        location = location.subtract(0, 0, 2);
        this.createBlock(config, location);
        location = location.add(0, 1, 1);
        this.createBlock(config, location);
        Logger.debug("Nodes Block Types were successfully set.", Rust.INSTANCE);
    }

    private void createBlock(final DefaultYamlConfig config,
                             final Location location) {
        switch (this.currentResourceType) {
            case STONE -> location.getBlock().setType(config.getStoneWorldMaterial());
            case SULFUR -> location.getBlock().setType(config.getSulfurWorldMaterial());
            case METAL -> location.getBlock().setType(config.getMetalWorldMaterial());
        }
    }

    public final UUID getNodeUuid() {
        return UUID.fromString(this.nodeUuid);
    }
}
