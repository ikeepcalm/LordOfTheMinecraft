package de.firecreeper82.cmds;

import de.firecreeper82.lotm.Beyonder;
import de.firecreeper82.lotm.Plugin;
import de.firecreeper82.pathways.Pathway;
import de.firecreeper82.pathways.impl.sun.SunPathway;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class BeyonderCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(!s.isOp()) {
            s.sendMessage("§cYou don't have the permission to use this command!");
            return true;
        }
        if(!(s instanceof Player)) {
            s.sendMessage("§cYou have to be a player to use this command!");
            return true;
        }
        if(args.length != 2) {
            s.sendMessage("§cWrong usage: Use /beyonder <Pathway> <Sequence>!");
            return true;
        }

        int sequence = 9;
        try {
            sequence = Integer.parseInt(args[1]);
        }
        catch(Exception exc) {
            s.sendMessage("§cWrong usage: Use /beyonder <Pathway> <Sequence>!");
            return true;
        }

        Player p = (Player) s;

        if(Plugin.beyonders.containsKey(p.getUniqueId())) {
            Plugin.beyonders.remove(p.getUniqueId());
            Pathway pathway = Pathway.initializeNew(args[0].toLowerCase(), p.getUniqueId(), sequence);
            if(pathway == null) {
                p.sendMessage("§c" + args[0].toLowerCase() + " is not a valid Pathway");
                 return true;
            }
            Beyonder beyonder = new Beyonder(p.getUniqueId(), pathway);
            Plugin.beyonders.put(p.getUniqueId(), beyonder);
            Plugin.instance.getServer().getPluginManager().registerEvents(beyonder, Plugin.instance);
            p.sendMessage(pathway.getStringColor() + "Made you a Beyonder of the" + pathway.getName() + "pathway at Sequence " + sequence);
            return true;
        }


        Pathway pathway = Pathway.initializeNew(args[0].toLowerCase(), p.getUniqueId(), sequence);
        if(pathway == null) {
            p.sendMessage("§c" + args[0].toLowerCase() + " is not a valid Pathway");
            return true;
        }
        Beyonder beyonder = new Beyonder(p.getUniqueId(), pathway);
        Plugin.beyonders.put(p.getUniqueId(), beyonder);
        Plugin.instance.getServer().getPluginManager().registerEvents(beyonder, Plugin.instance);
        p.sendMessage(pathway.getStringColor() + "Made you a Beyonder of the" + pathway.getName() + "pathway at Sequence " + sequence);
        return true;
    }
}
