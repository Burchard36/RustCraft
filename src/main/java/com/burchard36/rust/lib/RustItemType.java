package com.burchard36.rust.lib;

public enum RustItemType {
    ROCK("ROCK", "Items.Rock"),

    UNCOOKED_SULFUR("UNCOOKED_SULFUR", "Items.Sulfur.Ore"),
    COOKED_SULFUR("COOKED_SULFUR", "Items.Sulfur.Cooked"),

    UNCOOKED_METAL("UNCOOKED_METAL", "Item.Metal.Ore"),
    METAL_FRAGMENTS("METAL_FRAGMENTS", "Item.Metal.Cooked"),

    RUST_WOOD("RUST_WOOD", "Item.Wood.Ore"),
    RUST_CHARCOAL("RUST_CHARCOAL", "Item.Wood.Cooked"),

    RUST_STONE("RUST_STONE", "Item.Stone.Ore"),

    STONE_PICKAXE("STONE_PICKAXE", "Item.StonePickaxe"),
    STONE_AXE("STONE_AXE", "Item.StoneAxe");

    public String name;
    public String configPath;

    RustItemType(final String name,
                 final String configPath) {
        this.name = name;
        this.configPath = configPath;
    }

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.configPath;
    }
}
