package dev.ua.ikeepcalm.handlers;

import cz.foresttech.api.ColorAPI;
import de.tr7zw.nbtapi.NBTItem;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import dev.ua.ikeepcalm.utils.LocalizationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PotionHandler implements Listener {

    private final ItemStack[] itemsInInv;
    private final HashMap<Player, Inventory> openInvs;

    public PotionHandler() {
        openInvs = new HashMap<>();
        itemsInInv = new ItemStack[]{
                GeneralItemsUtil.getMagentaPane(),
                GeneralItemsUtil.getConfirmPotion(),
                GeneralItemsUtil.getPurplePane(),
                GeneralItemsUtil.getWhitePane()
        };
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }

        if (e.getClickedBlock().getType() != Material.CAULDRON) {
            return;
        }

        if (e.getClickedBlock().getLocation().subtract(0, 1, 0).getBlock().getType() != Material.SOUL_CAMPFIRE) {
            return;
        }

        if (e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        e.setCancelled(true);

        Player p = e.getPlayer();
        Inventory inv = createInventory(p);
        if (openInvs.containsKey(p))
            openInvs.replace(p, inv);
        else
            openInvs.put(p, inv);
        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null)
            return;

        ItemStack clickedItem = null;

        for (ItemStack item : itemsInInv) {
            if (item.isSimilar(e.getCurrentItem())) {
                clickedItem = item;
                break;
            }
        }

        if (clickedItem == null)
            return;

        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player p))
            return;

        if (clickedItem.isSimilar(GeneralItemsUtil.getConfirmPotion())) {
            brewPotion(p);
        }
    }

    private void brewPotion(Player p) {
        if (!openInvs.containsKey(p)) {
            p.closeInventory();
            return;
        }

        Inventory inv = openInvs.get(p);

        ArrayList<ItemStack> mainIngredients = new ArrayList<>();
        ArrayList<ItemStack> supplementaryIngredients = new ArrayList<>();

        //Add the ingredients in the spot for the main ingredients to an ArrayList
        addToIngredients(inv, mainIngredients, 10);
        addToIngredients(inv, mainIngredients, 11);
        addToIngredients(inv, mainIngredients, 19);
        addToIngredients(inv, mainIngredients, 20);

        //Add the ingredients in the spot for the supplementary ingredients to an ArrayList
        addToIngredients(inv, supplementaryIngredients, 15);
        addToIngredients(inv, supplementaryIngredients, 16);
        addToIngredients(inv, supplementaryIngredients, 24);
        addToIngredients(inv, supplementaryIngredients, 25);

        ItemStack correctPotion = null;

        outerloop:
        for (Potion potion : LordOfTheMinecraft.instance.getPotions()) {
            for (int i = 9; i > 0; i--) {
                if (potion.getSupplIngredients(i) == null || potion.getMainIngredients(i) == null)
                    continue;

                if (supplementaryIngredients.size() != potion.getSupplIngredients(i).length)
                    continue;

                boolean isCorrect = mainIngredients.size() == potion.getMainIngredients(i).length;

                for (int j = 0; j < mainIngredients.size(); j++) {
                    if (!isCorrect) {
                        break;
                    }

                    ItemStack mainIngredient = mainIngredients.get(j);
                    ItemStack expectedMainIngredient = potion.getMainIngredients(i)[j];
                    boolean matchesMaterial = mainIngredient.getType() == expectedMainIngredient.getType();
                    boolean matchesDisplayName = Objects.equals(mainIngredient.getItemMeta().displayName(), expectedMainIngredient.getItemMeta().displayName());
                    boolean matchesAmount = mainIngredient.getAmount() == expectedMainIngredient.getAmount();

                    if (!(matchesMaterial && matchesDisplayName && matchesAmount)) {
                        NBTItem actual = new NBTItem(mainIngredients.get(j));
                        NBTItem expected = new NBTItem(LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(i, potion.getName(), potion.getStringColor()));
                        if (!actual.getString("pathway").equals(expected.getString("pathway"))) {
                            isCorrect = false;
                        } else {
                            if (!actual.getString("sequence").equals(expected.getString("sequence"))) {
                                isCorrect = false;
                            }
                        }
                    }
                }

                if (!isCorrect)
                    continue;

                for (int j = 0; j < supplementaryIngredients.size(); j++) {
                    if (!supplementaryIngredients.get(j).isSimilar(potion.getSupplIngredients(i)[j])) {
                        isCorrect = false;
                    }
                }

                if (!isCorrect)
                    continue;

                correctPotion = potion.returnPotionForSequence(i);
                break outerloop;
            }
        }

        if (correctPotion == null)
            return;

        inv.setItem(10, new ItemStack(Material.AIR));
        inv.setItem(11, new ItemStack(Material.AIR));
        inv.setItem(15, new ItemStack(Material.AIR));
        inv.setItem(16, new ItemStack(Material.AIR));
        inv.setItem(19, new ItemStack(Material.AIR));
        inv.setItem(20, new ItemStack(Material.AIR));
        inv.setItem(24, new ItemStack(Material.AIR));
        inv.setItem(25, new ItemStack(Material.AIR));

        inv.setItem(49, correctPotion);

    }

    private void addToIngredients(Inventory inv, ArrayList<ItemStack> ingredients, int index) {
        if (inv.getItem(index) != null)
            ingredients.add(inv.getItem(index));
    }

    private Inventory createInventory(Player p) {

        final int[] invConfig = {
                1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 0, 0, 2, 2, 2, 0, 0, 1,
                1, 0, 0, 2, 2, 2, 0, 0, 1,
                1, 3, 2, 2, 2, 2, 2, 3, 1,
                1, 3, 3, 3, 3, 3, 3, 4, 1,
                1, 1, 1, 1, 0, 1, 1, 1, 1
        };

        HashMap<Integer, ItemStack> itemMap = new HashMap<>();
        itemMap.put(0, new ItemStack(Material.AIR));
        itemMap.put(1, GeneralItemsUtil.getPurplePane());
        itemMap.put(2, GeneralItemsUtil.getMagentaPane());
        itemMap.put(3, GeneralItemsUtil.getWhitePane());
        itemMap.put(4, GeneralItemsUtil.getConfirmPotion());

        Inventory inv = Bukkit.createInventory(p, 54, ColorAPI.colorize(LocalizationUtil.getLocalizedString("general", "brew-potion")));
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, itemMap.get(invConfig[i]));
        }
        return inv;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getPlayer();

        if (openInvs.containsKey(p) && openInvs.get(p).equals(inv)) {
            // Remove the inventory from the map since it's being closed
            openInvs.remove(p);

            // Collect items to give back to the player
            for (int i : new int[]{10, 11, 15, 16, 19, 20, 24, 25, 49}) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    p.getInventory().addItem(item);
                }
            }
        }
    }
}
