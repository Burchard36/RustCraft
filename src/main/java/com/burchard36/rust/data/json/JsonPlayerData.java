package com.burchard36.rust.data.json;

import com.burchard36.json.JsonDataFile;
import com.burchard36.rust.Rust;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class JsonPlayerData extends JsonDataFile {

    @SerializedName(value = "player_uuid")
    public String uuid;

    @SerializedName(value = "clan_uuid")
    public String clanUuid;

    @SerializedName(value = "player_thirst_level")
    public double currentThirstLevel;

    public JsonPlayerData(final Player player) {
        super(Rust.INSTANCE, "/data/players/" + player.getUniqueId() + ".json");
        this.uuid = player.getUniqueId().toString();
        this.currentThirstLevel = 100;
        this.clanUuid = "";
    }

    public JsonPlayerData(final UUID playerUuid) {
        super(Rust.INSTANCE, "/data/players/" + playerUuid.toString() + ".json");
        this.uuid = playerUuid.toString();
        this.currentThirstLevel = 100;
        this.clanUuid = "";
    }

    public final Player getPlayer() {
        return Bukkit.getPlayer(UUID.fromString(this.uuid));
    }

    // TODO: Hook this to ClanAPI so it sees if this clan actually exists or not
    public boolean isInAClan(final Rust plugin) {
        if (!this.clanUuid.isBlank() || !this.clanUuid.isEmpty()) return false;
        final JsonRustClan clan = plugin.getClanManager().getRustClan(UUID.fromString(this.clanUuid));
        if (clan != null) return true;
        this.clanUuid = "";
        return false;
    }

}
