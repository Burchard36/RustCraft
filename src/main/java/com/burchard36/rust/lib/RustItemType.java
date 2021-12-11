package com.burchard36.rust.lib;

public enum RustItemType {
    ROCK("ROCK"),

    UNCOOKED_SULFUR("UNCOOKED_SULFUR"),
    COOKED_SULFUR("COOKED_SULFUR"),

    UNCOOKED_METAL("UNCOOKED_METAL"),
    METAL_FRAGMENTS("METAL_FRAGMENTS"),

    RUST_WOOD("RUST_WOOD"),
    RUST_CHARCOAL("RUST_CHARCOAL"),

    RUST_STONE("RUST_STONE"),

    STONE_PICKAXE("STONE_PICKAXE"),
    STONE_AXE("STONE_AXE");

    public String name;

    RustItemType(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
