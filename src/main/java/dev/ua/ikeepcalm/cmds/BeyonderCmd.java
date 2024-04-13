package dev.ua.ikeepcalm.cmds;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;


public class BeyonderCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (!s.isOp()) {
            s.sendMessage("§cВи не маєте дозволу на використання цієї команди!");
            return true;
        }
        if (!(s instanceof Player p)) {
            s.sendMessage("§cВи повинні бути гравцем, щоб використовувати цю команду!");
            return true;
        }
        if (args.length != 2) {
            s.sendMessage("§cНеправильне використання: Використовуйте /beyonder <Шлях> <Послідовність>!");
            return true;
        }

        int sequence;
        try {
            sequence = Integer.parseInt(args[1]);
        } catch (Exception exc) {
            s.sendMessage("§cНеправильне використання: Використовуйте /beyonder <Шлях> <Послідовність>!");
            return true;
        }

        if (sequence > 9 || sequence < 1) {
            s.sendMessage("§cВи можете обрати Послідовність тільки від 9 до 1!");
            return true;
        }


        //Check if Player is already a Beyonder.
        // If he is, then remove him from the pathway and initialize a new one for the player
        //If he isn't then just initialize a pathway for him
        if (LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId())) {
            LordOfTheMinecraft.beyonders.get(p.getUniqueId()).removeBeyonder();
            Pathway pathway = Pathway.initializeNew(args[0].toLowerCase(), p.getUniqueId(), sequence);
            if (pathway == null) {
                p.sendMessage("§c" + args[0].toLowerCase() + " не є дійсним Шляхом! Видалення вашого статусу Потойбічного");
                return true;
            }
            p.sendMessage(pathway.getStringColor() + "Тепер ви - Потойбічній Шляху \"" + pathway.getName() + "\" " + sequence + " послідовності!");
            return true;
        }


        Pathway pathway = Pathway.initializeNew(args[0].toLowerCase(), p.getUniqueId(), sequence);
        if (pathway == null) {
            p.sendMessage("§c" + args[0].toLowerCase() + " не є Шляхом");
            return true;
        }
        p.sendMessage(pathway.getStringColor() + "Тепер ви - Потойбічній Шляху \"" + pathway.getName() + "\" " + sequence + " послідовності!");
        return true;
    }
}
