package com.burchard36.rust.managers;

import com.burchard36.Logger;
import com.burchard36.json.PluginDataMap;
import com.burchard36.rust.Rust;
import com.burchard36.rust.data.json.JsonPlayerData;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerDataManager {

    private final Rust pluginInstance;
    private final PluginDataMap dataMap;

    public PlayerDataManager(final PluginDataMap pluginDataMap,
                             final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.dataMap = pluginDataMap;

    }

    public final void shutdown() {
        Logger.log("Shutting down PlayerDataManager");
    }

    public final JsonPlayerData getPlayerData(final Player player) {
        return this.getPlayerData(player.getUniqueId());
    }

    public final JsonPlayerData getPlayerData(final UUID uuid) {
        return (JsonPlayerData) this.dataMap.getDataFile(uuid.toString());
    }
}
