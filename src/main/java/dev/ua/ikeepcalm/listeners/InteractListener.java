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
                if (NBT.get(item, (nbt) -> {
                    return nbt.getBoolean("openAbilities");
                })) {
                    event.setCancelled(true);
                    event.getWhoClicked().setItemOnCursor(null);
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

                    Beyonder beyonder = LordOfTheMinecraft.beyonders.get(p.getUniqueId());
                    int sequence = beyonder.getPathway().getSequence().getCurrentSequence();
                    HashMap<Integer, String[]> abilityInfo = beyonder.getPathway().getItems().getAbilityInfo();
                    List<ItemStack> tempItems = beyonder.getPathway().getItems().returnItemsFromSequence(sequence);
                    int i = 9;
                    boolean isFirst = true;
                    for (ItemStack tempItem : tempItems) {
                        ItemStack originalItem = tempItem;
                        tempItem = new ItemStack(originalItem.getType());
                        ItemMeta meta = tempItem.getItemMeta();
                        meta.setDisplayName(ColorAPI.colorize(beyonder.getPathway().getStringColor() + originalItem.getItemMeta().getDisplayName()));

                        if (abilityInfo.get(i).length == 4){
                            List<String> lore = new ArrayList<>(formatLine(abilityInfo.get(i)[2], beyonder.getPathway().getStringColor()));
                            lore.add(abilityInfo.get(i)[3]);
                            meta.setLore(lore);
                            --i;
                        } else {
                            if (isFirst) {
                                meta.setLore(formatLore(i, abilityInfo, isFirst, beyonder.getPathway().getStringColor()));
                                isFirst = false;
                            } else {
                                meta.setLore(formatLore(i, abilityInfo, isFirst, beyonder.getPathway().getStringColor()));
                                --i;
                                isFirst = true;
                            }
                        }

                        tempItem.setItemMeta(meta);
                        Item simpleItem = new SimpleItem(tempItem, e -> {
                            if (e.getPlayer().getInventory().contains(originalItem))
                                return;
                            e.getPlayer().getInventory().addItem(originalItem);
                        });

                        gui.addItems(simpleItem);
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


    private List<String> formatLore(int index, HashMap<Integer, String[]> abilityInfo, boolean isFirst, String pathwayColor) {
        List<String> lore = new ArrayList<>();
        lore.add(abilityInfo.get(index)[0]);
        lore.add(abilityInfo.get(index)[1]);
        if (isFirst) {
            lore.addAll(formatLine(abilityInfo.get(index)[2], pathwayColor));
        } else {
            lore.addAll(formatLine(abilityInfo.get(index)[3], pathwayColor));
        } lore.add(abilityInfo.get(index)[4]);
        return lore;
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

        if (currentLine.length() > 0) {
            formattedLines.add(ColorAPI.colorize(pathwayColor + currentLine.toString().trim()));
        }

        return formattedLines;
    }

}


