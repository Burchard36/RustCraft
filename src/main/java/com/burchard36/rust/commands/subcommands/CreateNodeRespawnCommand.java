package com.burchard36.rust.commands.subcommands;

import com.burchard36.inventory.ItemWrapper;
import com.burchard36.rust.commands.RustCommandHandler;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class CreateNodeRespawnCommand implements Listener {

    public static ItemWrapper nodeCreatorItem = null;
    private final RustCommandHandler command;

    public CreateNodeRespawnCommand(final RustCommandHandler command) {
        this.command = command;

        nodeCreatorItem = new ItemWrapper(new ItemStack(Material.RED_CONCRETE, 1))
                .setDisplayName("&e&lNode Creator Item")
                .setItemLore(Collections.singletonList("\n&e&oPlacing this will allow you to create a node!"))
                .addDataString("creator_item", "creator_item");
    }
}
