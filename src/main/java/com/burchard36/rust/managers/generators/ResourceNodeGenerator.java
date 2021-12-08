package com.burchard36.rust.managers.generators;

import com.burchard36.Logger;
import com.burchard36.rust.Rust;
import com.burchard36.rust.config.DefaultYamlConfig;
import com.burchard36.rust.data.NodeType;
import com.burchard36.rust.data.json.JsonResourceNode;
import com.burchard36.rust.lib.RustLocation;
import com.burchard36.rust.managers.ResourceNodeManager;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;


public class ResourceNodeGenerator {

    private final ResourceNodeManager nodeManager;
    private final DefaultYamlConfig config;
    private final int maxGenerationX;
    private final int maxGenerationZ;
    private final int maxStoneNodes;
    private final int maxSulfurNodes;
    private final int maxMetalNodes;
    private final World currentWorld;

    private int currentStoneNodes = 0;
    private int currentSulfurNodes = 0;
    private int currentMetalNodes = 0;

    public ResourceNodeGenerator(final ResourceNodeManager nodeManager) {
        this.nodeManager = nodeManager;
        this.config = this.nodeManager.defaultYamlConfig();
        this.maxGenerationX = this.config.maxNodeGenerationX();
        this.maxGenerationZ = this.config.maxNodeGenerationZ();
        this.maxMetalNodes = this.config.maxMetalNodes();
        this.maxSulfurNodes = this.config.maxSulfurNodes();
        this.maxStoneNodes = this.config.maxStoneNodes();
        this.currentWorld = this.config.getWorld();
        Logger.debug("RustCraft will be using world named: " + this.currentWorld + " as the RustWorld!", Rust.INSTANCE);

        this.nodeManager.getNodes().forEach((node) -> {
            switch (node.currentResourceType) {
                case STONE -> this.currentStoneNodes++;
                case METAL -> this.currentMetalNodes++;
                case SULFUR -> this.currentSulfurNodes++;
            }
        });

        this.generateNodes();
    }

    public void generateNodes() {
        Logger.debug("Generating Stone nodes. . .", Rust.INSTANCE);
        this.generateStoneNodes();
        Logger.debug("Generating Metal nodes. . .", Rust.INSTANCE);
        this.generateMetalNodes();
        Logger.debug("Generating Sulfur nodes. . .", Rust.INSTANCE);
        this.generateSulfurNodes();
    }

    private void generateStoneNodes() {
        while (currentStoneNodes < this.maxStoneNodes) {
            Logger.debug("Attempting to create Stone Node. . .", Rust.INSTANCE);
            if (!generateNode(NodeType.STONE)) continue;

            currentStoneNodes++;
        }
    }

    private void generateMetalNodes() {
        while (currentMetalNodes <= this.maxMetalNodes) {
            Logger.debug("Attempting to create Metal Node. . .", Rust.INSTANCE);
            if (!generateNode(NodeType.METAL)) continue;

            currentMetalNodes++;
        }
    }

    private void generateSulfurNodes() {
        while (currentSulfurNodes <= this.maxSulfurNodes) {
            Logger.debug("Attempting to create Sulfur Node. . .", Rust.INSTANCE);
            if (!generateNode(NodeType.SULFUR)) continue;

            currentSulfurNodes++;
        }
    }

    public boolean generateNode(final NodeType type) {
        final Location spigotLocation = Rust.getRandomLocation(this.currentWorld, this.maxGenerationX, this.maxGenerationZ)
                .add(0, 1, 0);
        final RustLocation location = new RustLocation(spigotLocation);
        final JsonResourceNode node = new JsonResourceNode(type, location, UUID.randomUUID());

        if (!node.canGenerate()) {
            Logger.debug("Canceling node generation because node is not able to generate!", Rust.INSTANCE);
            return false;
        }

        if (!node.biomeCheck(this.config)) {
            Logger.debug("Canceling node generation because node does not respect its biome check!", Rust.INSTANCE);
            return false;
        }

        if (!node.blockCheck(this.config)) {
            Logger.debug("Canceling node generation because node does not respect its block check!", Rust.INSTANCE);
            return false;
        }

        node.createNode(this.config);
        this.nodeManager.registerNode(node);
        Logger.debug("Node with type: " + type.name() + " was spawned in world: " + location.worldUuid.toString() +
                "at location X: " + location.x + " Y: " + location.y + " Z: " + location.z, Rust.INSTANCE);
        return true;
    }

}
