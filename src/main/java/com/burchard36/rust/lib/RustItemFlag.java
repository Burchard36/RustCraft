package com.burchard36.rust.lib;

public enum RustItemFlag {

    ALLOW_INTERACTION("ALLOW_INTERACTION"),
    DENY_INTERACTION("DENY_INTERACTION"),
    ALLOW_PLACE("ALLOW_PLACE"),
    DENY_PLACE("DENY_PLACE");

    public final String name;

    RustItemFlag(final String flag) {
        this.name = flag;
    }

    public String getName() {
        return this.name;
    }
}
