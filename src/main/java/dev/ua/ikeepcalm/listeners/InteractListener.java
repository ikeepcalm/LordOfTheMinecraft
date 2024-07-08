package dev.ua.ikeepcalm.listeners;

import cz.foresttech.api.ColorAPI;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Beyonder;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.InventoryView;
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

        ItemStack item = e.getItem();

        if (item == null) return;

        if (e.getMaterial() == Material.AIR)
            return;

        if (!LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId())) {
            NBTItem nbtItem = new NBTItem(item);
            if (nbtItem.hasTag("spiritualityDrainage") || nbtItem.hasTag("openAbilities")) {
                p.getInventory().removeItem(item);
                p.sendMessage(Component.text("Містичне знання розсіюється прямо у вас в руках...").color(TextColor.color(255, 0, 0)));
            }
            return;
        }

        LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getSequence().useAbility(e.getItem(), e);
    }

    @EventHandler
    public void onInventoryInteraction(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getType() == Material.AIR) return;
        if (event.getWhoClicked() instanceof Player p) {
            if (!LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId())) {
                NBTItem nbtItem = new NBTItem(item);
                if (nbtItem.hasTag("spiritualityDrainage") || nbtItem.hasTag("openAbilities")) {
                    p.getInventory().removeItem(item);
                    p.sendMessage(Component.text("Містичне знання розсіюється прямо у вас в руках...").color(TextColor.color(255, 0, 0)));
                }
            } else {
                NBTItem nbtItem = new NBTItem(item);
                if (nbtItem.hasTag("spiritualityDrainage") || nbtItem.hasTag("openAbilities")) {
                    if (item.getAmount() > 1) {
                        item.setAmount(1);
                    }
                }
            }

        }
    }

    @EventHandler
    public void onSpectatorMove(PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            if (event.getPlayer().isOp()) {
                return;
            }
            Location from = event.getTo();
            if (from.getBlock().isSolid()) {
                event.getPlayer().teleport(event.getFrom());
                event.setCancelled(true);
                event.getPlayer().sendMessage(Component.text("Ваша сутність надто щільна для проходження через цей блок!").color(TextColor.color(255, 0, 0)));
            }
        }
    }

    @EventHandler
    public void onPlayerHeadPlacement(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item == null) return;
            if (item.getType() == Material.AIR) return;

            if (item.getType() == Material.PLAYER_HEAD) {
                NBTItem nbtItem = new NBTItem(item);
                if (nbtItem.hasTag("pathway")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Component.text("Ваші пальці не можуть відпустити цей предмет...").color(TextColor.color(255, 230, 120)));
                }
            }
        }
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


        InventoryView view = event.getView();
        if (view.getType() != InventoryType.CRAFTING) {
            return;
        }

        if (event.getSlot() == 9) {
            if (event.isLeftClick()) {
                ItemStack item = p.getInventory().getItem(9);
                if (item == null) return;
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


