package com.burchard36.rust.lib;

import com.burchard36.Logger;
import com.burchard36.rust.Rust;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class RustPlayer {

    public static boolean hasRustItems(final Player player,
                                       final RustItemType item,
                                       final int amount) {
        final Inventory inventory = player.getInventory();
        int amountFound = 0;
        for (final ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) {
                Logger.debug("Skipping hasRustItem, itemStack was null.", Rust.INSTANCE);
                continue;
            }
            if (!RustItems.isRustItem(itemStack)) {
                Logger.debug("Skipping hasRustItem, itemStack was not a RustItem.", Rust.INSTANCE);
                continue;
            }
            if (RustItems.notMatches(itemStack, item)) {
                Logger.debug("Skipping hasRustItem, itemStack did not match RustItemType (It was a " + RustItems.getItemType(itemStack).name(), Rust.INSTANCE);
                continue;
            }

            amountFound += itemStack.getAmount();
            Logger.debug("RustItemType found! Amount found of " + item.name() + " found is now: " + amountFound, Rust.INSTANCE);
        }

        Logger.debug("Amount of " + item.name() + " found was: " + amountFound, Rust.INSTANCE);
        return amountFound >= amount;
    }

    public static boolean hasVanillaItem(final Player player,
                                         final Material material,
                                         final int amount) {
        return player.getInventory().contains(material, amount);
    }

    public static void removeRustItems(final Player player,
                                       final RustItemType type,
                                       int amount) {
        final Inventory inventory = player.getInventory();
        for (final ItemStack stack : inventory.getContents()) {
            if (stack == null) continue;
            if (RustItems.notMatches(stack, type)) continue;
            int toRemove = stack.getAmount();
            stack.setAmount(stack.getAmount() - amount);
            amount -= toRemove;
            if (amount <= 0) return;
        }
    }

    public static void removeVanillaItems(final Player player,
                                          final Material material,
                                          int amount) {
        final Inventory inventory = player.getInventory();
        for (final ItemStack stack : inventory.getContents()) {
            if (stack == null) continue;
            if (RustItems.isRustItem(stack)) return;
            if (stack.getType() != material) return;
            int toRemove = stack.getAmount();
            stack.setAmount(stack.getAmount() - amount);
            amount -= toRemove;
            if (amount <= 0) return;
        }
    }
}
