package dev.ua.ikeepcalm.optional.anchor;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AnchorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("anchor")) {
            if (sender instanceof Player player) {
                if (!player.isOp()) {
                    sender.sendMessage("You don't have permission to use this command.");
                    return true;
                }

                if (LordOfTheMinecraft.instance.getAnchorManager() == null) {
                    LordOfTheMinecraft.instance.setAnchorManager(new AnchorManager());
                }

                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("start")) {
                        LordOfTheMinecraft.instance.getAnchorManager().startRestoration();
                        sender.sendMessage("Restoration started.");
                        return true;
                    } else {
                        sender.sendMessage("Invalid argument.");
                        return true;
                    }
                } else {
                    if (LordOfTheMinecraft.instance.getAnchorManager().getAnchorLocation() != null) {
                        sender.sendMessage("Anchor already set.");
                        return true;
                    }
                    LordOfTheMinecraft.instance.getAnchorManager().setAnchorLocation(player.getLocation());
                    sender.sendMessage("Anchor set at your current location.");
                }
            } else {
                sender.sendMessage("Only players can use this command.");
            }
            return true;
        }
        return false;
    }

}
