package com.burchard36.rust.lib;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class RustLocation {

    @SerializedName(value = "location_x")
    public int x;

    @SerializedName(value = "location_y")
    public int y;

    @SerializedName(value = "location_z")
    public int z;

    @SerializedName(value = "world_uuid")
    public String worldUuid;

    private transient Location spigotLocation;

    public RustLocation(final Location location) {
        this.spigotLocation = location;
        this.x = this.spigotLocation.getBlockX();
        this.y = this.spigotLocation.getBlockY();
        this.z = this.spigotLocation.getBlockZ();
        this.worldUuid = this.spigotLocation.getWorld().getUID().toString();

    }

    public final Location getSpigotLocation() {
        if (spigotLocation != null) return this.spigotLocation;
        this.spigotLocation = new Location(Bukkit.getWorld(UUID.fromString(this.worldUuid)), this.x, this.y, this.z);
        return this.spigotLocation.clone();
    }
}
