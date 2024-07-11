package dev.ua.ikeepcalm.cmds;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BoonCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (!s.isOp()) {
            s.sendMessage("§cВи не маєте дозволу на використання цієї команди!");
            return true;
        }

        if (args.length < 2 || args.length > 3) {
            s.sendMessage("§cНеправильне використання: Використовуйте /boon <Гравець?> <Шлях> <Послідовність>!");
            return true;
        }

        String pathwayName;
        int sequence;

        Player target = null;
        if (args.length == 3) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                s.sendMessage("§cГравець " + args[0] + " не знайдений!");
                return true;
            }
            pathwayName = args[1].toLowerCase();
            try {
                sequence = Integer.parseInt(args[2]);
            } catch (NumberFormatException exc) {
                s.sendMessage("§cНеправильне використання: Використовуйте /boon <Гравець> <Шлях> <Послідовність>!");
                return true;
            }
        } else {
            if (!(s instanceof Player p)) {
                s.sendMessage("§cВи повинні бути гравцем, щоб використовувати цю команду!");
                return true;
            }
            target = p;
            pathwayName = args[0].toLowerCase();
            try {
                sequence = Integer.parseInt(args[1]);
            } catch (NumberFormatException exc) {
                s.sendMessage("§cНеправильне використання: Використовуйте /boon <Шлях> <Послідовність>!");
                return true;
            }
        }

        if (sequence < 1 || sequence > 9) {
            s.sendMessage("§cВи можете обрати Послідовність тільки від 1 до 9!");
            return true;
        }

        if (target == null) {
            return true;
        }
        handlePathwayAssignment(target, pathwayName, sequence);
        return true;
    }

    private void handlePathwayAssignment(Player player, String pathwayName, int sequence) {
        if (LordOfTheMinecraft.beyonders.containsKey(player.getUniqueId())) {
            LordOfTheMinecraft.beyonders.get(player.getUniqueId()).removeBeyonder();
        }

        Pathway pathway = Pathway.initializeNew(pathwayName, player.getUniqueId(), sequence);
        if (pathway == null) {
            player.sendMessage("§c" + pathwayName + " не є дійсним Шляхом! Видалення вашого статусу Потойбічного");
        } else {
            player.sendMessage(pathway.getStringColor() + "Тепер ви - Потойбічній Шляху \"" + pathway.getName() + "\" " + sequence + " послідовності!");
        }
    }
}
