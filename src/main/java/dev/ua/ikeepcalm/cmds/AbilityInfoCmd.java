package dev.ua.ikeepcalm.cmds;

import dev.ua.ikeepcalm.mystical.Beyonder;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AbilityInfoCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender s, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (!(s instanceof Player p)) {
            s.sendMessage("§cВи повинні бути гравцем, щоб використовувати цю команду!");
            return true;
        }

        if (!LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId())) {
            s.sendMessage("§cВи повинні бути Потойбічним, щоб використовувати цю команду!");
            return true;
        }

        if (args.length != 1) {
            s.sendMessage("§cНеправильне використання: Використовуйте /ability-info [Послідовність]!");
            return true;
        }

        if (!GeneralPurposeUtil.isInteger(args[0])) {
            p.sendMessage("§cНеправильне використання: Використовуйте /ability-info [Послідовність]!");
            return true;
        }

        Beyonder beyonder = LordOfTheMinecraft.beyonders.get(p.getUniqueId());
        int sequence = GeneralPurposeUtil.parseInt(args[0]);

        if (beyonder.getPathway().getSequence().getCurrentSequence() > sequence) {
            p.sendMessage("§cВи ще не досягли " + sequence + " Послідовності!");
            return true;
        }

        p.sendMessage(beyonder.getPathway().getItems().getAbilityInfo().get(sequence));

        return true;
    }
}
