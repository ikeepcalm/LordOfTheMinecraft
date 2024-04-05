package dev.ua.ikeepcalm.cmds;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class MI9ItemsCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender s, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (!s.hasPermission(new Permission("mi9items.use"))) {
            s.sendMessage("§cВи не маєте дозволу на використання цієї команди!");
            return true;
        }

        if (!(s instanceof Player p)) {
            s.sendMessage("§cВи повинні бути гравцем, щоб використовувати цю команду!");
            return true;
        }

        if (args.length != 1) {
            s.sendMessage("§cНеправильне використання: Використовуйте /mi9 <item>! (item: monocle, stick)");
            return true;
        }

        if (args[0].equalsIgnoreCase("monocle")) {
            ItemStack monocle = new ItemStack(Material.CLOCK);
            ItemMeta meta = monocle.getItemMeta();
            meta.setDisplayName("§6Монокль");
            meta.setCustomModelData(1);
            List<String> lore = List.of("§7Монокль для дослідження злочинів", "§7ЛКМ - змінити період дослідження", "§7ПКМ - провести дослідження");
            meta.setLore(lore);
            monocle.setItemMeta(meta);
            NBT.modify(monocle, (nbt) -> {
                nbt.setBoolean("mi9Monocle", true);
            });

            if (!p.getInventory().contains(monocle)) {
                p.getInventory().addItem(monocle);
                p.sendMessage("§aВи отримали монокль!");
                return true;
            } else {
                p.sendMessage("§cУ вас вже є монокль!");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("stick")) {
            ItemStack stick = new ItemStack(Material.STICK);
            ItemMeta meta = stick.getItemMeta();
            meta.setDisplayName("§6Палиця");
            meta.setCustomModelData(1);
            stick.setItemMeta(meta);
            NBT.modify(stick, (nbt) -> {
                nbt.setBoolean("mi9Stick", true);
            });

            if (!p.getInventory().contains(stick)) {
                p.getInventory().addItem(stick);
                p.sendMessage("§aВи отримали палицю!");
                return true;
            } else {
                p.sendMessage("§cУ вас вже є палиця!");
                return true;
            }
        } else {
            p.sendMessage("§cНеправильне використання: Використовуйте /mi9 <item>! (item: monocle, stick)");
        }

        return true;
    }
}
