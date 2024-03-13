package dev.ua.ikeepcalm.cmds;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
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
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;

public class ItemsCmd implements CommandExecutor, Listener {

    private final HashMap<Player, Inventory> openInventories;

    public ItemsCmd() {
        openInventories = new HashMap<>();
    }

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

        if (args.length != 0) {
            s.sendMessage("§cНеправильне використання: Використовуйте /items!");
            return true;
        }

        if (openInventories.containsKey(p)) {
            p.closeInventory();
            openInventories.remove(p);
        }


        //Create Inventory and loop through the for the player available items and add them to the inv
        //Put the Player in the openInventories HashMap
        Inventory inv = Bukkit.createInventory(p, 27, LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getStringColor() + p.getName() + " - Items");
        for (int i = LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getItems().returnItemsFromSequence(LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getSequence().getCurrentSequence()).size(); i < inv.getSize(); i++) {
            inv.setItem(i, GeneralItemsUtil.getMagentaPane());
        }

        for (ItemStack tempItem : LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getItems().returnItemsFromSequence(LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getSequence().getCurrentSequence())) {
            inv.addItem(tempItem);
        }

        openInventories.put(p, inv);
        p.openInventory(inv);
        return true;
    }

    @EventHandler
    //Check if Player is in the opeInventories HashMap
    //If he is, cancel the event and give the player the item if he doesn't already have it
    public void onInventoryInteract(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p))
            return;

        if (!openInventories.containsKey(p))
            return;

        e.setCancelled(true);

        if (e.getClickedInventory() != openInventories.get(p)) {
            return;
        }

        if (p.getInventory().contains(e.getCurrentItem()))
            return;

        if (e.getCurrentItem() == null)
            return;

        if (GeneralItemsUtil.getMagentaPane().isSimilar(e.getCurrentItem()))
            return;

        p.getInventory().addItem(e.getCurrentItem());
    }

    @EventHandler
    //remove player from openInventories HashMap
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player p))
            return;

        if (!openInventories.containsKey(p))
            return;

        openInventories.remove(p);
    }
}
