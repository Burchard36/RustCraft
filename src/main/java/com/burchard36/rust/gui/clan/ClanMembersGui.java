package com.burchard36.rust.gui.clan;

import com.burchard36.inventory.ClickableItem;
import com.burchard36.inventory.ItemWrapper;
import com.burchard36.inventory.PluginInventory;
import com.burchard36.rust.Rust;
import com.burchard36.rust.data.json.JsonPlayerData;
import com.burchard36.rust.data.json.JsonRustClan;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.burchard36.ApiLib.convert;

public class ClanMembersGui {

    public final HashMap<Integer, PluginInventory> inventorys;
    public final JsonPlayerData playerData;
    public final JsonRustClan clanData;
    public final int page;

    public ClanMembersGui(final JsonPlayerData data, final JsonRustClan clan, final int page) {
        this.inventorys = new HashMap<>();
        this.playerData = data;
        this.clanData = clan;
        this.page = page;

        final HashMap<Integer, List<ClickableItem>> items = this.getClanMembersAsItems();
        this.getClanMembersAsItems().forEach((inventoryPage, itemList) -> {
            final PluginInventory inventory = new PluginInventory(54, "&b&lClan members");
            inventory.onClick((onClick) -> onClick.setCancelled(true))
                    .fillWith(items.get(inventoryPage), true)
                    .fillWith(this.background(), false)
                    .addClickableItemAtSlot(48, this.previousPageButton())
                    .addClickableItemAtSlot(50, this.nextPageButton());
        });
    }

    public ClanMembersGui(final ClanMembersGui gui, final int page) {
        this.inventorys = gui.inventorys;
        this.playerData = gui.playerData;
        this.clanData = gui.clanData;
        this.page = page;
    }

    public final void showTo(final Player player) {
        this.inventorys.get(this.page).register(Rust.INSTANCE).open(player);
    }

    private ClickableItem background() {
        final ClickableItem item = new ClickableItem(Material.BLACK_STAINED_GLASS_PANE, 1);
        item.onClick((onClick) -> onClick.setCancelled(true))
                .setDisplayName("&f ");
        return item;
    }

    private ClickableItem nextPageButton() {
        ClickableItem item = new ClickableItem(Material.ARROW, 1);
        item
                .onClick((onClick) -> {
                    onClick.getWhoClicked().closeInventory();
                    new ClanMembersGui(this, this.page + 1).showTo((Player) onClick.getWhoClicked());
                })
            .setDisplayName("&e&l&oNext Page");
        if (this.inventorys.keySet().size() <= 1) {
            item = new ClickableItem(Material.BARRIER, 1);
            item.setDisplayName("&c&oNo next page!");
        }
        return item;
    }

    private ClickableItem previousPageButton() {
        ClickableItem item = new ClickableItem(Material.ARROW, 1);
        item
                .onClick((onClick) -> {
                    onClick.getWhoClicked().closeInventory();
                    new ClanMembersGui(this, this.page - 1).showTo((Player) onClick.getWhoClicked());
                })
                .setDisplayName("&e&l&oPrevious Page");
        if (this.inventorys.keySet().size() <= 1) {
            item = new ClickableItem(Material.BARRIER, 1);
            item.setDisplayName("&c&oNo previous page!");
        }
        return item;
    }

    private HashMap<Integer, List<ClickableItem>> getClanMembersAsItems() {
        final HashMap<Integer, List<ClickableItem>> items = new HashMap<>();

        int currentPage = 0;
        final List<OfflinePlayer> clanMembers = this.clanData.getAllMembersAsOfflinePlayers();
        for (int x = 0; x <= (clanMembers.size() - 1); x++) {
            if (items.size() < currentPage) items.putIfAbsent(currentPage, new ArrayList<>());
            if (x >= (44 * (currentPage + 1))) currentPage++;
            final OfflinePlayer player = clanMembers.get(x);
            final ClickableItem item = new ClickableItem(Material.PLAYER_HEAD, 1);
            item.onClick((onClick) -> {
                onClick.setCancelled(true);
                final ClickType type = onClick.getClick();
                if (onClick.getCurrentItem() == null) return;
                if (onClick.getCurrentItem().getType() != Material.PLAYER_HEAD) return;
                final ItemWrapper wrapper = new ItemWrapper(onClick.getCurrentItem());
                final Player clicker = (Player) onClick.getWhoClicked();
                if (!wrapper.hasDataString("member_uuid")) return;
                // TODO: POTENTIAL VULNERABILITY!!! Make sure players inventories get closed when they get kicked from the clan!!!
                final JsonPlayerData targetPlayerData = Rust.INSTANCE.getPlayerDataManager().getPlayerData(UUID.fromString(wrapper.getStringDataValue("member_uuid")));
                final JsonPlayerData clickerPlayerData = Rust.INSTANCE.getPlayerDataManager().getPlayerData(onClick.getWhoClicked().getUniqueId());

                if (targetPlayerData.getClanId() != clickerPlayerData.getClanId() && targetPlayerData.getClanId() != null && clickerPlayerData.getClanId() != null) {
                    clicker.sendMessage(convert("&cYou do not match the target players clan, you cannot manage this player"));
                    clicker.closeInventory();
                    return;
                }

                final JsonRustClan clan = targetPlayerData.getRustClan(Rust.INSTANCE);
                switch (type) {
                    case LEFT -> clan.promotePlayer(clicker, targetPlayerData.getUniqueId());
                    case RIGHT -> clan.demotePlayer(clicker, targetPlayerData.getUniqueId());
                    case MIDDLE -> clan.kickPlayer(clicker, targetPlayerData.getUniqueId());
                }
            })
                    .setSkullTo(clanMembers.get(x).getUniqueId())
                    .setDisplayName("&e&o" + player.getName())
                    .addDataString("member_uuid", player.getUniqueId().toString())
                    .addItemLore("&f ")
                    .addItemLore("&7Clan Role: &e&l" + this.clanData.getRoleOf(player.getUniqueId()).name());

            if (this.clanData.hasAuthorityOver(this.playerData.getUniqueId(), player.getUniqueId())) {
                item.addItemLore("&e&oLeft-Click &7to promote this member!")
                        .addItemLore("&e&oRight-Click &7to demote this member!")
                        .addItemLore("&e&oMiddle-Click &7To kick this member!");
            } else item.addItemLore("&c&oYou do not have authority over this member");

            items.get(currentPage).add(item);
        }

        return items;
    }
}
