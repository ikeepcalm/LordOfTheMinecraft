package dev.ua.ikeepcalm.cmds;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Beyonder;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AdvanceCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (!(s instanceof Player player)) {
            s.sendMessage("§cВи повинні бути гравцем, щоб використовувати цю команду!");
            return true;
        }

        Beyonder beyonder = LordOfTheMinecraft.beyonders.get(player.getUniqueId());
        if (beyonder == null) {
            player.sendMessage("§cВи повинні бути потойбічним, щоб використовувати цю команду!");
            return true;
        }

        if (beyonder.getActingProgress() > beyonder.getActingNeeded() && beyonder.getActingNeeded() != 0 && beyonder.isDigested()) {
            player.sendMessage(Component.text("Ваше тіло і розум готові до подальшого розвитку!").color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Ви ще не готові до подальшого розвитку!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
        }
        return true;
    }
}
