package dev.ua.ikeepcalm.cmds;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SpawnCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender s, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (!s.isOp()) {
            s.sendMessage("§cВи не маєте дозволу на використання цієї команди!");
            return true;
        }
        if (!(s instanceof Player p)) {
            s.sendMessage("§cВи повинні бути гравцем, щоб використовувати цю команду!");
            return true;
        }
        if (args.length != 1) {
            s.sendMessage("§cНеправильне використання: Використовуйте /spawn <ID>!");
            return true;
        }

        if (!LordOfTheMinecraft.instance.getBeyonderMobsHandler().spawnEntity(args[0], p.getLocation(), p.getWorld()))
            p.sendMessage("§cНе знайдено істоту з id: " + args[0]);

        return true;
    }
}
