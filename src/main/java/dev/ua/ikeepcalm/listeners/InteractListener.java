package dev.ua.ikeepcalm.listeners;

import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InteractListener implements Listener {

    private final HashMap<Player, Inventory> openInventories;

    public InteractListener() {
        openInventories = new HashMap<>();
    }

    @EventHandler
    //Check if Player is Beyonder and the item isn't air
    //Call the useAbility function from the Beyonder
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId()))
            return;
        if (e.getMaterial() == Material.AIR)
            return;

        LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getSequence().useAbility(e.getItem(), e);
    }


    @EventHandler
    //Check if Player is Beyonder
    //Call the destroyItem function from the Beyonder
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (!LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId()))
            return;
        LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getSequence().destroyItem(e.getItemDrop().getItemStack(), e);
    }

    @EventHandler
    public void onInventoryInteraction(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (!LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId()))
            return;

        if (e.getSlot() == 9) {
            if (e.isRightClick()) {
                ItemStack item = p.getInventory().getItem(9);
                if (NBT.get(item, (nbt) -> {
                    return nbt.getBoolean("openItems");
                })) {
                    if (openInventories.containsKey(p)) {
                        p.closeInventory();
                        openInventories.remove(p);
                    }
                    Inventory inv = Bukkit.createInventory(p, 27, LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getStringColor() + p.getName() + " - Items");
                    for (int i = LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getItems().returnItemsFromSequence(LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getSequence().getCurrentSequence()).size(); i < inv.getSize(); i++) {
                        inv.setItem(i, GeneralItemsUtil.getMagentaPane());
                    }

                    for (ItemStack tempItem : LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getItems().returnItemsFromSequence(LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getSequence().getCurrentSequence())) {
                        inv.addItem(tempItem);
                    }

                    openInventories.put(p, inv);
                    p.openInventory(inv);
                    e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
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


