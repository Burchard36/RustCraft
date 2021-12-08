package com.burchard36.rust.lib;

public enum RustItem {
    ROCK("ROCK");

    public String name;

    RustItem(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
