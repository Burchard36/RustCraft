package com.burchard36.rust.commands;

import com.burchard36.command.ApiCommand;
import com.burchard36.rust.Rust;
import com.burchard36.rust.data.json.JsonPlayerData;
import com.burchard36.rust.managers.PlayerDataManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.burchard36.ApiLib.convert;

public class ClanCommandHandler {

    private final ApiCommand command;
    private final Rust pluginInstance;

    public ClanCommandHandler(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        final PlayerDataManager dataManager = this.pluginInstance.getPlayerDataManager();
        this.command = new ApiCommand("clan",
                "Main clan command",
                "&e&o/clan help",
                new ArrayList<>(Arrays.asList("clans", "c", "team", "teams")))
                .onPlayerSender((onPlayer) -> {
                    final List<String> args = onPlayer.getArguments();
                    final JsonPlayerData playerData = dataManager.getPlayerData(onPlayer.getSendingPlayer());

                    if (args.size() == 0) this.sendHelpMessage(onPlayer.getSendingPlayer());
                    if (args.size() == 1) {

                        switch (args.get(0)) {

                            case "CREATE" -> this.handleCreateClan(playerData);

                        }

                    }
                });

    }

    private void handleLeaveClan

    private void handleCreateClan(final JsonPlayerData data) {
        if (data.isInAClan(this.pluginInstance)) {
            data.getPlayer().sendMessage(convert("&c&oYou cannot create a clan when you already belong to one! Please leave your current clan"));
            return;
        }
    }

    public void sendHelpMessage(final Player player) {

    }
}
