package com.burchard36.rust.data;

public enum ClanRole {
    OWNER(999), MODERATOR(500), MEMBER(100), NONE(0);

    private final int weight;

    ClanRole(final int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }
}
