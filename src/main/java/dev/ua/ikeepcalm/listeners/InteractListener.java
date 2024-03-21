package dev.ua.ikeepcalm.listeners;

import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class InteractListener implements Listener {

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
    public void onInventoryInteraction(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();

        if (!LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId()))
            return;

        if (event.getSlot() == 9) {
            if (event.isLeftClick()) {
                ItemStack item = p.getInventory().getItem(9);
                assert item != null;
                if (NBT.get(item, (nbt) -> {
                    return nbt.getBoolean("openItems");
                })) {
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();
                    Gui gui = Gui.normal()
                            .setStructure(
                                    "# # # # # # # # #",
                                    "# . . . . . . . #",
                                    "# . . . . . . . #",
                                    "# . . . . . . . #",
                                    "# # # # # # # # #")
                            .addIngredient('#', GeneralItemsUtil.getMagentaPane())
                            .build();

                    for (ItemStack tempItem : LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getItems().returnItemsFromSequence(LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getSequence().getCurrentSequence())) {
                        Item single = new SimpleItem(tempItem, e -> {
                            if (e.getPlayer().getInventory().contains(tempItem))
                                return;
                            e.getPlayer().getInventory().addItem(tempItem);
                        });
                        gui.addItems(single);
                    }

                    Window window = Window.single()
                            .setViewer(p)
                            .setTitle(LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getStringColor() + p.getName() + " - Вміння")
                            .setGui(gui)
                            .build();

                    window.open();
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}


