package dev.ua.ikeepcalm.listeners;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class GenerationListener implements Listener {

    private static final double[] PROBABILITY_DISTRIBUTION = {0.001, 0.003, 0.004, 0.005, 0.01, 0.02, 0.03, 0.04, 0.05};
    private static final int MIN_VALUE = 1;

    @EventHandler
    public void onLootGenerate(LootGenerateEvent e) {
        ItemStack item;
        Random random = new Random();

        if (random.nextInt(100) > 20)
            return;

        int sequence = GeneralPurposeUtil.biasedRandomNumber(PROBABILITY_DISTRIBUTION, MIN_VALUE);
        Potion potion = LordOfTheMinecraft.instance.getPotions().get(random.nextInt(LordOfTheMinecraft.instance.getPotions().size()));
        switch (random.nextInt(6)) {
            case 1 -> item = LordOfTheMinecraft.instance.getRecipe().getRecipeForSequence(potion, sequence);
            case 2 -> item = LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(sequence, potion.getName(), potion.getStringColor());
            case 3 -> item = GeneralItemsUtil.getRandomGrimoire();
            case 4 -> item = new ItemStack(Material.SOUL_SAND);
            case 5 -> item = new ItemStack(Material.WITHER_ROSE);
            default -> item = potion.returnPotionForSequence(sequence);
        }

        if (e.getInventoryHolder() == null)
            return;

        int randomIndex = random.nextInt(e.getLoot().size());
        e.getLoot().remove(randomIndex);
        e.getLoot().add(item);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Inventory inv = e.getInventory();
        if (inv.getHolder() instanceof WanderingTrader merchant) {
            if (merchant.getRecipeCount() != 0) {
                NamespacedKey key = new NamespacedKey(LordOfTheMinecraft.instance, "beyonder-things");
                if (merchant.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN)) {
                    return;
                } else {
                    merchant.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
                }

                for (int i = 0; i < merchant.getRecipeCount(); i++) {
                    Random random = new Random();
                    if (random.nextInt(100) < 30) {
                        ItemStack[] trade = getRandomTrade();
                        if (random.nextBoolean()) {
                            MerchantRecipe recipe = new MerchantRecipe(trade[2], 1);
                            recipe.addIngredient(trade[1]);
                            recipe.addIngredient(trade[0]);
                            merchant.setRecipe(i, recipe);
                        } else if (random.nextBoolean()) {
                            MerchantRecipe recipe = new MerchantRecipe(trade[2], 1);
                            recipe.addIngredient(trade[0]);
                            recipe.addIngredient(trade[1]);
                            merchant.setRecipe(i, recipe);
                        } else {
                            MerchantRecipe recipe = new MerchantRecipe(trade[2], 1);
                            recipe.addIngredient(trade[0]);
                            recipe.addIngredient(trade[0]);
                            merchant.setRecipe(i, recipe);
                        }
                        return;
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

        switch (random.nextInt(5)) {
            case 1 -> out = LordOfTheMinecraft.instance.getRecipe().getRecipeForSequence(potion, sequence);

            case 2 ->
                    out = LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(sequence, potion.getName(), potion.getStringColor());

            case 3 -> out = new ItemStack(Material.SOUL_SAND);

            case 4 -> out = new ItemStack(Material.WITHER_ROSE);

            default -> out = potion.returnPotionForSequence(sequence);
        }

        ItemStack in;
        if (random.nextBoolean()) {
            in = new ItemStack(Material.NETHERITE_INGOT);
            in.setAmount(random.nextInt(10) + 1);
        } else {
            in = new ItemStack(Material.EMERALD);
            in.setAmount(random.nextInt(60) + 1);
        }
        ItemStack in2 = new ItemStack(Material.DIAMOND);
        in2.setAmount(random.nextInt(60) + 1);

        return new ItemStack[]{in, in2, out};
    }
}
