package com.burchard36.rust.gui;

import com.burchard36.inventory.ClickableItem;
import com.burchard36.inventory.PluginInventory;
import com.burchard36.rust.Rust;
import com.burchard36.rust.data.json.JsonPlayerData;
import com.burchard36.rust.data.json.JsonRustClan;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

import static com.burchard36.ApiLib.convert;

public class ClanPageIndex {

    private final PluginInventory inventory;
    private final JsonPlayerData data;

    public ClanPageIndex(final JsonPlayerData playerData) {
        this.data = playerData;
        this.inventory = new PluginInventory(27, "&b&lClan Page");

        final ClickableItem playerSkull = this.getPlayerSkull();
        final ClickableItem membersList = this.getMembersList();
        final ClickableItem background = this.getBackground();

        this.inventory.fillWith(background).addClickableItemAtSlot(12, playerSkull)
                .addClickableItemAtSlot(14, membersList);
    }

    public final void showTo() {
        this.inventory.register(Rust.INSTANCE).open(this.data.getPlayer());
    }

    private ClickableItem getBackground() {
        final ClickableItem item = new ClickableItem(Material.BLACK_STAINED_GLASS, 1);
        item.onClick((onClick) -> onClick.setCancelled(true))
                .setDisplayName("&f ");
        return item;
    }

    private ClickableItem getMembersList() {
        final List<String> itemLore = new ArrayList<>();
        itemLore.add("&f ");
        Material material = Material.BARRIER;
        final boolean inClan = this.data.isInAClan(Rust.INSTANCE);
        if (inClan) material = Material.FLOWER_BANNER_PATTERN;
        if (inClan) {
            itemLore.add("&7&oClick to view or manage clan members!");
        } else {
            itemLore.add("&c&oYou are not in a clan! Go out in");
            itemLore.add("&c&oin the world and find one to join");
            itemLore.add("&c&oor make one yourself with &e&o/clan create &7&o<name>");
        }

        final ClickableItem item = new ClickableItem(material, 1);
        item.onClick((onClick) -> {
            onClick.setCancelled(true);

        })
            .setDisplayName("&e&oClan members list")
            .setItemLore(itemLore);
        return item;
    }

    private ClickableItem getPlayerSkull() {
        final List<String> itemLore = new ArrayList<>();
        itemLore.add(convert("&f "));
        if (this.data.isInAClan(Rust.INSTANCE)) {
            final JsonRustClan clan = data.getRustClan(Rust.INSTANCE);
            itemLore.add("&7&oYour clan name: &e" + clan.clanName);
        } else {
            itemLore.add("&c&oYou are not in a clan! Go out in");
            itemLore.add("&c&oin the world and find one to join");
            itemLore.add("&c&oor make one yourself with &e&o/clan create &7&o<name>");
        }

        final ClickableItem item = new ClickableItem(Material.PLAYER_HEAD, 1);
        item.onClick((onClick) -> onClick.setCancelled(true))
                .setDisplayName("&e&oYour clan information!")
                .setSkullTo(data.getPlayer().getUniqueId())
                .setItemLore(itemLore);
        return item;
    }
}
