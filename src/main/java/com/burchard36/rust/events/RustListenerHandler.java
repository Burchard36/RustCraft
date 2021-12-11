package com.burchard36.rust.events;

import com.burchard36.rust.Rust;
import com.burchard36.rust.events.event.*;

public class RustListenerHandler {

    private final Rust pluginInstance;
    private final InteractListener interactListener;
    private final BlockBreakListener blockBreakListener;
    private final PlayerRespawnListener playerRespawnListener;
    private final PlayerJoinListener playerJoinListener;
    private final SwitchHandListener switchHandListener;

    public RustListenerHandler(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;

        this.interactListener = new InteractListener(this.pluginInstance);
        this.blockBreakListener = new BlockBreakListener(this.pluginInstance);
        this.playerRespawnListener = new PlayerRespawnListener(this.pluginInstance);
        this.playerJoinListener = new PlayerJoinListener(this.pluginInstance);
        this.switchHandListener = new SwitchHandListener(this.pluginInstance);
    }

    public void unregisterAll() {
        this.interactListener.unregister();
        this.blockBreakListener.unregister();
        this.playerRespawnListener.unregister();
        this.playerJoinListener.unregister();
        this.switchHandListener.unregister();
    }

}
