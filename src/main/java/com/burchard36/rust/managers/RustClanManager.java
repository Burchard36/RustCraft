package com.burchard36.rust.managers;

import com.burchard36.json.PluginDataMap;
import com.burchard36.rust.Rust;
import com.burchard36.rust.data.json.JsonRustClan;

import java.util.UUID;

public class RustClanManager {

    private final PluginDataMap dataMap;
    private final Rust pluginInstance;

    public RustClanManager(final PluginDataMap clanMap,
                           final Rust pluginInstance) {
        this.dataMap = clanMap;
        this.pluginInstance = pluginInstance;
    }

    public final JsonRustClan getRustClan(final UUID clanUuid) {
        return (JsonRustClan) this.dataMap.getDataFile(clanUuid.toString());
    }
}
