package dev.ua.ikeepcalm.listeners;

import cz.foresttech.api.ColorAPI;
import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.Beyonder;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public void onItemsInteraction(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();

        if (!LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId()))
            return;

        if (event.getSlot() == 9) {
            if (event.isLeftClick()) {
                ItemStack item = p.getInventory().getItem(9);
                assert item != null;
                if (item.getType().isAir()) return;
                if (NBT.get(item, (nbt) -> {
                    return nbt.getBoolean("openAbilities");
                })) {

                    if (p.getItemOnCursor().getType() != Material.AIR) {
                        p.getWorld().dropItem(p.getLocation(), p.getItemOnCursor());
                    }
                    p.setItemOnCursor(null);

                    Gui gui = Gui.normal()
                            .setStructure(
                                    "# # # # # # # # #",
                                    "# . . . . . . . #",
                                    "# . . . . . . . #",
                                    "# . . . . . . . #",
                                    "# # # # # # # # #")
                            .addIngredient('#', GeneralItemsUtil.getMagentaPane())
                            .build();

                    Beyonder beyonder = LordOfTheMinecraft.beyonders.get(p.getUniqueId());
                    int sequence = beyonder.getPathway().getSequence().getCurrentSequence();
                    HashMap<Integer, String[]> abilityInfo = beyonder.getPathway().getItems().getAbilityInfo();
                    List<ItemStack> tempItems = beyonder.getPathway().getItems().returnItemsFromSequence(sequence);
                    int i = 9;
                    int j = 2;
                    for (ItemStack tempItem : tempItems) {
                        ItemStack originalItem = tempItem;
                        tempItem = new ItemStack(originalItem.getType());
                        ItemMeta meta = tempItem.getItemMeta();
                        meta.setDisplayName(ColorAPI.colorize(beyonder.getPathway().getStringColor() + originalItem.getItemMeta().getDisplayName()));

                        String[] abilityStrings = abilityInfo.get(i);
                        List<String> lore = new ArrayList<>();
                        lore.add(abilityStrings[0]);
                        lore.add(abilityStrings[1]);

                        lore.addAll(formatLine(abilityStrings[j], beyonder.getPathway().getStringColor()));

                        lore.add(abilityStrings[abilityStrings.length - 1]);

                        meta.setLore(lore);
                        tempItem.setItemMeta(meta);

                        Item simpleItem = new SimpleItem(tempItem, e -> {
                            if (e.getPlayer().getInventory().contains(originalItem))
                                return;
                            e.getPlayer().getInventory().addItem(originalItem);
                        });

                        gui.addItems(simpleItem);

                        if (j == abilityStrings.length - 2) {
                            --i;
                            j = 2;
                        } else {
                            ++j;
                        }
                    }


                    Window window = Window.single()
                            .setViewer(p)
                            .setTitle(LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getStringColor() + p.getName() + " - Містичні знання")
                            .setGui(gui)
                            .build();

                    window.open();
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    private List<String> formatLine(String s, String pathwayColor) {
        List<String> formattedLines = new ArrayList<>();
        final int MAX_LINE_LENGTH = 60;

        if (s.length() <= MAX_LINE_LENGTH) {
            formattedLines.add(ColorAPI.colorize(s));
            return formattedLines;
        }

        String[] words = s.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() <= MAX_LINE_LENGTH) {
                currentLine.append(word).append(" ");
            } else {
                formattedLines.add(ColorAPI.colorize(pathwayColor + currentLine.toString().trim()));
                currentLine = new StringBuilder(word + " ");
            }
        }

        if (!currentLine.isEmpty()) {
            formattedLines.add(ColorAPI.colorize(pathwayColor + currentLine.toString().trim()));
        }

        return formattedLines;
    }

}


