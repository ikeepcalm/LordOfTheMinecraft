package de.firecreeper82.cmds;

import de.firecreeper82.lotm.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ItemsCmd implements CommandExecutor, Listener {

    private HashMap<Player, Inventory> openInventories;

    public ItemsCmd() {
        openInventories = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(!(s instanceof Player)) {
            s.sendMessage("§cYou have to be a player to use this command!");
            return true;
        }

        Player p = (Player) s;

        if(!Plugin.beyonders.containsKey(p.getUniqueId())) {
            s.sendMessage("§cYou have to be a Beyonder to use this command!");
            return true;
        }

        if(args.length != 0) {
            s.sendMessage("§cWrong usage: Use /items!");
            return true;
        }

        if(openInventories.containsKey(p)) {
            p.closeInventory();
            openInventories.remove(p);
        }

        Inventory inv = Bukkit.createInventory(p, 27, Plugin.beyonders.get(p.getUniqueId()).getPathway().getStringColor() + p.getName() + " - Items");

        for(ItemStack tempItem : Plugin.beyonders.get(p.getUniqueId()).getPathway().getItems().returnSequence(Plugin.beyonders.get(p.getUniqueId()).getPathway().getSequence().currentSequence))  {
            inv.addItem(tempItem);
        }

        openInventories.put(p, inv);
        p.openInventory(inv);
        return true;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player))
            return;

        Player p = (Player) e.getWhoClicked();

        if(!openInventories.containsKey(p))
            return;

        e.setCancelled(true);

        if(e.getClickedInventory() != openInventories.get(p)) {
            return;
        }

        if(p.getInventory().contains(e.getCurrentItem()))
            return;

        if(e.getCurrentItem() == null)
            return;

        p.getInventory().addItem(e.getCurrentItem());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!(e.getPlayer() instanceof Player))
            return;

        Player p = (Player) e.getPlayer();

        if(!openInventories.containsKey(p))
            return;

        openInventories.remove(p);
    }
}
