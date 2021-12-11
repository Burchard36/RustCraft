package com.burchard36.rust.commands;

import com.burchard36.ApiLib;
import com.burchard36.command.ApiCommand;
import com.burchard36.rust.Rust;
import com.burchard36.rust.commands.subcommands.ListRecipesCommand;
import com.burchard36.rust.commands.subcommands.ReloadCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.burchard36.ApiLib.convert;

public class RustCommandHandler {

    private final Rust pluginInstance;

    public static String PLUGIN_RELOAD_PERMISSION = "rust.commands.creator";

    public RustCommandHandler(final Rust pluginInstance) {
        this.pluginInstance = pluginInstance;
        final ListRecipesCommand listRecipesCommand = new ListRecipesCommand(this);
        final ReloadCommand reloadCommand = new ReloadCommand();

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
                            case "RELOAD" -> handleReload(player, args);
                        }
                    }

                }));
    }

    private void handleReload(final Player player, final List<String> args) {
        if (!player.hasPermission(PLUGIN_RELOAD_PERMISSION)) {
            player.sendMessage(convert("&cYou dont have permission for this! &7(&b" + PLUGIN_RELOAD_PERMISSION + "&7)"));
            return;
        }

        this.pluginInstance.reloadPlugin();
        player.sendMessage(convert("&aSuccessfully reloaded the plugin!"));
    }

    private String getHelpMessage(final Player player) {
        String message = "&b&l&m========= &e&l&oRust Help Commands &b&l&m=========\n";
        return convert(message);
    }
}
