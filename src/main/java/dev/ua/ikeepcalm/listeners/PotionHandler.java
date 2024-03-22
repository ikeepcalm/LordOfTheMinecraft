package dev.ua.ikeepcalm.listeners;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import dev.ua.ikeepcalm.utils.LocalizationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

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
        if (e.getClickedBlock() == null)
            return;
        if (e.getClickedBlock().getType() != Material.CAULDRON)
            return;

        if (
                ((e.getClickedBlock().getMetadata("special").isEmpty()) || e.getClickedBlock().getMetadata("special").get(0).value() == null || !(e.getClickedBlock().getMetadata("special").get(0).value() instanceof Boolean metaDataTag) || !metaDataTag) &&
                        e.getClickedBlock().getLocation().subtract(0, 1, 0).getBlock().getType() != Material.SOUL_FIRE
        )
            return;

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
                    if (!isCorrect)
                        break;
                    if (!mainIngredients.get(j).isSimilar(potion.getMainIngredients(i)[j]))
                        isCorrect = false;
                }

                if (!isCorrect) {
                    if (!mainIngredients.isEmpty() && mainIngredients.get(0).isSimilar(LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(i, potion.getName(), potion.getStringColor()))) {
                        isCorrect = true;
                    }
                }

                for (int j = 0; j < supplementaryIngredients.size(); j++) {
                    if (!supplementaryIngredients.get(j).isSimilar(potion.getSupplIngredients(i)[j]))
                        isCorrect = false;
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

        Inventory inv = Bukkit.createInventory(p, 54, LocalizationUtil.getLocalizedString("general", "brew-potion"));
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, itemMap.get(invConfig[i]));
        }
        return inv;
    }
}
