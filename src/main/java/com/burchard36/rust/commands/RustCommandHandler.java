package com.burchard36.rust.commands;

import com.burchard36.ApiLib;
import com.burchard36.Logger;
import com.burchard36.command.ApiCommand;
import com.burchard36.rust.Rust;
import com.burchard36.rust.commands.subcommands.CreateNodeRespawnCommand;
import com.burchard36.rust.commands.subcommands.ListRecipesCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.burchard36.ApiLib.convert;

public class RustCommandHandler {

    private final Rust pluginInstance;

    public static String NODE_CREATOR_PERMISSION = "rust.commands.creator";

    public RustCommandHandler(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        final CreateNodeRespawnCommand createNodeRespawnCommand = new CreateNodeRespawnCommand(this);
        final ListRecipesCommand listRecipesCommand = new ListRecipesCommand(this);

        ApiLib.registerCommand(
                new ApiCommand("rust",
                "&7&oThe main rust plugin command",
                "&e/rust &7[&b<subCommand>&7]",
                new ArrayList<>())
                .onPlayerSender((playerSent) -> {
                    final Player player = playerSent.player();
                    final List<String> args = playerSent.args();

                    if (args.size() == 0) {
                        player.sendMessage(this.getHelpMessage(player));
                        return;
                    }

                    if (args.size() == 1) {
                        switch (args.get(0).toUpperCase()) {
                            case "CREATOR", "CREATENODE" -> handleCreateNode();
                        }
                    }

                }));
    }

    private String getHelpMessage(final Player player) {
        String message = "&b&l&m========= &e&l&oRust Help Commands &b&l&m=========\n";
        if (player.hasPermission(NODE_CREATOR_PERMISSION)) message += "&e/rust creator&b - &7&oGives you a block that allows you to create rust nodes";
        return convert(message);
    }


    private void handleCreateNode() {
        Logger.log("");
    }
}
