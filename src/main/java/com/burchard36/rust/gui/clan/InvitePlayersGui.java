package com.burchard36.rust.gui.clan;

import com.burchard36.inventory.ClickableItem;
import com.burchard36.inventory.ItemWrapper;
import com.burchard36.rust.Rust;
import com.burchard36.rust.data.json.JsonPlayerData;
import com.burchard36.rust.data.json.JsonRustClan;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

import static com.burchard36.ApiLib.convert;

public class InvitePlayersGui {

    public final JsonRustClan clanData;
    public final JsonPlayerData playerData;
    public final int page;

    public InvitePlayersGui(final JsonRustClan clanData, final JsonPlayerData playerData, final int page) {
        this.clanData = clanData;
        this.playerData = playerData;
        this.page = page;
    }

    private List<Player> getOnlinePlayers() {
        final List<Player> onlinePlayers = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach((player) -> onlinePlayers.add(player.getPlayer()));
        return onlinePlayers;
    }

    private HashMap<Integer, List<ClickableItem>> getOnlinePlayersWithoutClan() {
        final HashMap<Integer, List<ClickableItem>> items = new HashMap<>();
        final List<? extends Player> onlinePlayers = this.getOnlinePlayers();

        int currentPage = 0;
        for (int x = 0; x <= (onlinePlayers.size() - 1); x++) {
            if (x >= (44 * (currentPage + 1))) currentPage++;
            final Player player = onlinePlayers.get(x);
            final JsonPlayerData thatPlayersData = Rust.INSTANCE.getPlayerDataManager().getPlayerData(player.getUniqueId());
            if (thatPlayersData.isInAClan(Rust.INSTANCE)) continue; // Skip people who are already in a clan!
            final ClickableItem item = new ClickableItem(Material.PLAYER_HEAD, 1);
            item.onClick((onClick) -> {
                if (onClick.getCurrentItem() == null) return;
                if (onClick.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

                final Player clicker = (Player) onClick.getWhoClicked();
                final UUID uuid = UUID.fromString(new ItemWrapper(onClick.getCurrentItem()).getStringDataValue("player_uuid"));
                final JsonPlayerData clickerData = Rust.INSTANCE.getPlayerDataManager().getPlayerData(clicker.getUniqueId());

                if (!clickerData.isInAClan(Rust.INSTANCE)) {
                    player.closeInventory();
                    player.sendMessage(convert("&c&oYou were removed from your clan whilst trying to invite someone"));
                    return;
                }

                clickerData.getRustClan(Rust.INSTANCE).invitePlayer(clickerData.getUniqueId(), uuid);
            })
                    .setSkullTo(player.getUniqueId())
                    .addDataString("player_uuid", player.getUniqueId().toString())
                    .setDisplayName("&e&l&o" + player.getName())
                    .addItemLore("&f ")
                    .addItemLore("&e&oLeft-Click&7 to invite this player to your clan!");

            items.get(currentPage).add(item);
        }
        return items;
    }
}
