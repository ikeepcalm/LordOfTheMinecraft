package dev.ua.ikeepcalm.cmds;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Beyonder;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TestCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender s, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (args.length == 0 || !(s instanceof Player p))
            return true;

        if (args[0].equalsIgnoreCase("characteristic")) {
            p.getInventory().addItem(LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(GeneralPurposeUtil.parseInt(args[2]), args[1], "§" + args[3]));
        }

        if (args[0].equalsIgnoreCase("recipe")) {
            p.getInventory().addItem(LordOfTheMinecraft.instance.getRecipe().getRecipeForSequence(LordOfTheMinecraft.instance.getPotions().getFirst(), GeneralPurposeUtil.parseInt(args[1])));
        }

        if (args[0].equalsIgnoreCase("potion")) {
            p.getInventory().addItem(LordOfTheMinecraft.instance.getPotions().getFirst().returnPotionForSequence(GeneralPurposeUtil.parseInt(args[1])));
        }

        if (args[0].equalsIgnoreCase("instances")) {
            Bukkit.broadcastMessage(LordOfTheMinecraft.beyonders.size() + "");
        }

        if (args[0].equalsIgnoreCase("acting")) {
            Beyonder beyonder = LordOfTheMinecraft.beyonders.get(p.getUniqueId());
            p.sendMessage(beyonder.getActingProgress() + " / " + beyonder.getActingNeeded());
        }

        if (args[0].equalsIgnoreCase("tome")) {
            p.getInventory().addItem(GeneralItemsUtil.getRandomGrimoire());
        }

        return true;
    }

}
