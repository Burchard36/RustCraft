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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ResourceNodeGenerator {

    private final ResourceNodeManager nodeManager;
    private DefaultYamlConfig config;
    private int maxGenerationX;
    private int maxGenerationZ;
    private int maxStoneNodes;
    private int maxSulfurNodes;
    private int maxMetalNodes;
    private World currentWorld;

    private int currentStoneNodes = 0;
    private int currentSulfurNodes = 0;
    private int currentMetalNodes = 0;

    public ResourceNodeGenerator(final ResourceNodeManager nodeManager) {
        this.nodeManager = nodeManager;
        this.loadConfigValues();
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

    public List<JsonResourceNode> getStoneNodes() {
        List<JsonResourceNode> resourceNodes = new ArrayList<>();
        this.nodeManager.getNodes().forEach((node) -> {
            if (node.currentResourceType == NodeType.STONE) resourceNodes.add(node);
        });
        return resourceNodes;
    }

    public List<JsonResourceNode> getMetalNodes() {
        List<JsonResourceNode> resourceNodes = new ArrayList<>();
        this.nodeManager.getNodes().forEach((node) -> {
            if (node.currentResourceType == NodeType.METAL) resourceNodes.add(node);
        });
        return resourceNodes;
    }

    public List<JsonResourceNode> getSulfurNodes() {
        List<JsonResourceNode> resourceNodes = new ArrayList<>();
        this.nodeManager.getNodes().forEach((node) -> {
            if (node.currentResourceType == NodeType.SULFUR) resourceNodes.add(node);
        });
        return resourceNodes;
    }

    public void loadConfigValues() {
        this.config = this.nodeManager.defaultYamlConfig();
        this.maxGenerationX = this.config.maxNodeGenerationX();
        this.maxGenerationZ = this.config.maxNodeGenerationZ();
        this.maxMetalNodes = this.config.maxMetalNodes();
        this.maxSulfurNodes = this.config.maxSulfurNodes();
        this.maxStoneNodes = this.config.maxStoneNodes();
        this.currentWorld = this.config.getWorld();
    }

    public void reloadNodes() {
        this.loadConfigValues();

        Logger.log("Reloading stone nodes. . .");
        if (this.currentStoneNodes > this.maxStoneNodes) {
            while (this.currentStoneNodes > this.maxStoneNodes) {
                final JsonResourceNode node = this.getStoneNodes().get(0);
                this.nodeManager.deleteNode(node);
                this.currentStoneNodes--;
                Logger.debug("Deleted stone node", Rust.INSTANCE);
            }
        }

        Logger.log("Reloading sulfur nodes. . .");
        if (this.currentSulfurNodes > this.maxSulfurNodes) {
            while (this.currentSulfurNodes > this.maxSulfurNodes) {
                final JsonResourceNode node = this.getSulfurNodes().get(0);
                this.nodeManager.deleteNode(node);
                this.currentSulfurNodes--;
                Logger.debug("Deleted sulfur node", Rust.INSTANCE);
            }
        }

        Logger.log("Reloading Metal nodes. . .");
        if (this.currentMetalNodes > this.maxMetalNodes) {
            while (this.currentMetalNodes > this.maxMetalNodes) {
                final JsonResourceNode node = this.getMetalNodes().get(0);
                this.nodeManager.deleteNode(node);
                this.currentMetalNodes--;
                Logger.debug("Deleted metal node", Rust.INSTANCE);
            }
        }

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
            if (generateNode(NodeType.STONE)) continue;

            currentStoneNodes++;
        }
    }

    private void generateMetalNodes() {
        while (currentMetalNodes <= this.maxMetalNodes) {
            Logger.debug("Attempting to create Metal Node. . .", Rust.INSTANCE);
            if (generateNode(NodeType.METAL)) continue;

            currentMetalNodes++;
        }
    }

    private void generateSulfurNodes() {
        while (currentSulfurNodes <= this.maxSulfurNodes) {
            Logger.debug("Attempting to create Sulfur Node. . .", Rust.INSTANCE);
            if (generateNode(NodeType.SULFUR)) continue;

            currentSulfurNodes++;
        }
    }

    public final void regenerateNode(final JsonResourceNode node) {
        final Location spigotLocation = Rust.getRandomLocation(this.currentWorld, this.maxGenerationX, this.maxGenerationZ)
                .add(0, 1, 0);
        node.setNewRootLocation(spigotLocation);
        if (this.canNodeGenerate(node)) {
            this.regenerateNode(node);
        } else {
            node.createNode(this.config);
            Logger.debug("Successfully regenerated Node!", Rust.INSTANCE);
        }
    }

    public boolean generateNode(final NodeType type) {
        final Location spigotLocation = Rust.getRandomLocation(this.currentWorld, this.maxGenerationX, this.maxGenerationZ)
                .add(0, 1, 0);
        final RustLocation location = new RustLocation(spigotLocation);
        final JsonResourceNode node = new JsonResourceNode(type, location, UUID.randomUUID());

        if (this.canNodeGenerate(node)) return true;

        node.createNode(this.config);
        this.nodeManager.registerNode(node);
        Logger.debug("Node with type: " + type.name() + " was spawned in world: " + location.worldUuid +
                "at location X: " + location.x + " Y: " + location.y + " Z: " + location.z, Rust.INSTANCE);
        return false;
    }

    private boolean canNodeGenerate(final JsonResourceNode node) {
        if (!node.canGenerate()) {
            Logger.debug("Canceling node generation because node is not able to generate!", Rust.INSTANCE);
            return true;
        }

        if (!node.biomeCheck(this.config)) {
            Logger.debug("Canceling node generation because node does not respect its biomes check!", Rust.INSTANCE);
            return true;
        }

        if (!node.blockCheck(this.config)) {
            Logger.debug("Canceling node generation because node does not respect its block check!", Rust.INSTANCE);
            return true;
        }

        return false;
    }

}
