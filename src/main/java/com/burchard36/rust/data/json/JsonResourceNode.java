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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JsonResourceNode extends JsonDataFile {

    @SerializedName(value = "node_uuid")
    public String nodeUuid;

    @SerializedName(value = "current_resource_type")
    public NodeType currentResourceType;

    @SerializedName(value = "node_location")
    public List<RustLocation> nodeLocations;

    public JsonResourceNode(final NodeType nodeType,
                            final RustLocation nodeLocation,
                            final UUID uuid) {
        super(Rust.INSTANCE, "/data/nodes/" + uuid.toString() + ".json");
        this.nodeUuid = uuid.toString();
        this.currentResourceType = nodeType;
        this.nodeLocations = new ArrayList<>(List.of(nodeLocation));
    }

    public JsonResourceNode(final UUID uuid) {
        super(Rust.INSTANCE, "/data/nodes/" + uuid.toString() + ".json");
        this.nodeUuid = uuid.toString();
    }

    public void deleteSelf() {
        this.nodeLocations.forEach((loc) -> loc.spigotLocation.getBlock().setType(Material.AIR));
    }

    public boolean canGenerate() {
        Location location = this.nodeLocations.get(0).getSpigotLocation().clone(); // needs to be cloned
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

    public boolean doesNotExist() {
        boolean oneBlockExisted = false;
        for (final RustLocation loc : this.nodeLocations) {
            if (loc.getSpigotLocation().getBlock().getType() ==
                    Rust.INSTANCE.getDefaultYamlConfig().getNodeMaterial(this.currentResourceType))
                oneBlockExisted = true;
        }
        return !oneBlockExisted;
    }

    public final boolean blockCheck(final DefaultYamlConfig config) {
        final Location underneathBlock = this.nodeLocations.get(0).getSpigotLocation().clone().subtract(0, 1, 0);
        return !config.deniedBlocksUnderNodes().contains(underneathBlock.getBlock().getType());
    }

    public final boolean biomeCheck(final DefaultYamlConfig config) {
        switch (this.currentResourceType) {
            case STONE -> {
                if (config.deniedStoneNodeBiomes()
                        .contains(this.nodeLocations.get(0).getSpigotLocation().getBlock().getBiome())) return false;
            }
            case METAL -> {
                if (config.deniedMetalNodeBiomes()
                    .contains(this.nodeLocations.get(0).getSpigotLocation().getBlock().getBiome())) return false;
            }
            case SULFUR -> {
                if (config.deniedSulfurNodeBiomes()
                        .contains(this.nodeLocations.get(0).getSpigotLocation().getBlock().getBiome())) return false;
            }
        }
        return true;
    }

    public final void createNode(final DefaultYamlConfig config) {
        Logger.debug("Attempting to createNode", Rust.INSTANCE);
        Location location = this.nodeLocations.get(0).getSpigotLocation().clone();


        this.nodeLocations.add(new RustLocation(location.clone().add(0, 1, 0)));
        this.nodeLocations.add(new RustLocation(location.clone().add(1, 0, 0)));
        this.nodeLocations.add(new RustLocation(location.clone().subtract(1, 0, 0)));
        this.nodeLocations.add(new RustLocation(location.clone().add(0, 0, 1)));
        this.nodeLocations.add(new RustLocation(location.clone().subtract(0, 0, 1)));

        this.nodeLocations.forEach((loc) -> loc.getSpigotLocation().getBlock().setType(config.getNodeMaterial(this.currentResourceType)));

        Logger.debug("Nodes Block Types were successfully set.", Rust.INSTANCE);
    }

    public final UUID getNodeUuid() {
        return UUID.fromString(this.nodeUuid);
    }

    public final void setNewRootLocation(final Location location) {
        this.nodeLocations.clear();
        this.nodeLocations.add(new RustLocation(location));
    }
}
