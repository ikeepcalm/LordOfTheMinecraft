package dev.ua.ikeepcalm.listeners;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.Random;

public class GenerationListener implements Listener {

    private static final double[] PROBABILITY_DISTRIBUTION = {0.001, 0.003, 0.004, 0.005, 0.01, 0.02, 0.03, 0.04, 0.05};
    private static final int MIN_VALUE = 1;

    @EventHandler
    public void onLootGenerate(LootGenerateEvent e) {
        ItemStack item;
        Random random = new Random();

        if (random.nextInt(100) > 15)
            return;

        int sequence = GeneralPurposeUtil.biasedRandomNumber(PROBABILITY_DISTRIBUTION, MIN_VALUE);
        Potion potion = LordOfTheMinecraft.instance.getPotions().get(random.nextInt(LordOfTheMinecraft.instance.getPotions().size()));
        switch (random.nextInt(5)) {
            case 1 -> item = LordOfTheMinecraft.instance.getRecipe().getRecipeForSequence(potion, sequence);
            case 2 -> item = LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(sequence, potion.getName(), potion.getStringColor());
            case 3 -> item = GeneralItemsUtil.getRandomGrimoire();
            case 4 -> item = new ItemStack(Material.SOUL_SAND);
            default -> item = potion.returnPotionForSequence(sequence);
        }

        if (e.getInventoryHolder() == null)
            return;

        Inventory inv = e.getInventoryHolder().getInventory();
        inv.setItem(random.nextInt(inv.getSize()), item);

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Inventory inv = e.getInventory();
        if (inv.getHolder() instanceof Merchant merchant) {
            if (merchant.getRecipeCount() == 0) {
                for (int i = 0; i < merchant.getRecipeCount(); i++) {
                    Random random = new Random();
                    if (random.nextInt(100) < 30){
                        ItemStack[] trade = getRandomTrade();
                        if (random.nextBoolean()){
                            merchant.setRecipe(merchant.getRecipeCount(), new MerchantRecipe(trade[0], 1));
                        } else if (random.nextBoolean()){
                            merchant.setRecipe(merchant.getRecipeCount(), new MerchantRecipe(trade[0], 1));
                        } else {
                            merchant.setRecipe(merchant.getRecipeCount(), new MerchantRecipe(trade[0], 1));
                        }

                    }
                }

            }
        }
    }

    private ItemStack[] getRandomTrade() {
        ItemStack out;
        Random random = new Random();

        int sequence = GeneralPurposeUtil.biasedRandomNumber(PROBABILITY_DISTRIBUTION, MIN_VALUE);
        Potion potion = LordOfTheMinecraft.instance.getPotions().get(random.nextInt(LordOfTheMinecraft.instance.getPotions().size()));

        switch (random.nextInt(4)) {
            case 1 -> out = LordOfTheMinecraft.instance.getRecipe().getRecipeForSequence(potion, sequence);

            case 2 ->
                    out = LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(sequence, potion.getName(), potion.getStringColor());

            case 3 -> {
                out = GeneralItemsUtil.getCauldron();
                ItemStack in = new ItemStack(Material.GOLD_INGOT);
                in.setAmount(random.nextInt(20, 54));
                ItemStack in2 = new ItemStack(Material.DIAMOND);
                in.setAmount(random.nextInt(1, 4));
                return new ItemStack[]{in, in2, out};
            }

            default -> out = potion.returnPotionForSequence(sequence);
        }

        ItemStack in = new ItemStack(Material.EMERALD);
        in.setAmount(Math.round((random.nextInt(10, 63) / (float) sequence)));

        ItemStack in2 = new ItemStack(Material.DIAMOND);
        in.setAmount(Math.round((random.nextInt(10, 63) / (float) sequence)));

        return new ItemStack[]{in, in2, out};
    }
}
