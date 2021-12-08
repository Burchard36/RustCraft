package com.burchard36.rust.events;

import com.burchard36.rust.Rust;
import com.burchard36.rust.events.event.BlockBreakListener;
import com.burchard36.rust.events.event.InteractListener;

public class RustListenerHandler {

    private final Rust pluginInstance;
    private final InteractListener interactListener;
    private final BlockBreakListener blockBreakListener;

    public RustListenerHandler(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.interactListener = new InteractListener(this.pluginInstance);
        this.blockBreakListener = new BlockBreakListener(this.pluginInstance);
    }

    public void unregisterAll() {
        this.interactListener.unregister();
        this.blockBreakListener.unregister();
    }

}
