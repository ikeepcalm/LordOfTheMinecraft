package dev.ua.ikeepcalm.mystical.pathways.demoness;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import dev.ua.ikeepcalm.utils.BeyonderItemsUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class DemonessPotions extends Potion {

    public DemonessPotions() {
        name = "demoness";
        stringColor = "§d";
        mainIngredients = new HashMap<>();
        supplementaryIngredients = new HashMap<>();

        putMainIntoHashMap(9, BeyonderItemsUtil.getRose());
        putMainIntoHashMap(8, BeyonderItemsUtil.getTailFeather());
        putMainIntoHashMap(7, BeyonderItemsUtil.getMeteoriteCrystal());
        putMainIntoHashMap(6, BeyonderItemsUtil.getGoatHorn());
        putMainIntoHashMap(5, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(5, "demoness", stringColor));
        putMainIntoHashMap(4, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(4, "demoness", stringColor));
        putMainIntoHashMap(3, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(3, "demoness", stringColor));
        putMainIntoHashMap(2, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(2, "demoness", stringColor));
        putMainIntoHashMap(1, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(1, "demoness", stringColor));

        putSupplIntoHashMap(9, new ItemStack(Material.DIAMOND_SWORD));
        putSupplIntoHashMap(8, new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.MUTTON));
        putSupplIntoHashMap(7, new ItemStack(Material.FLOWER_POT));
        putSupplIntoHashMap(6, new ItemStack(Material.SKELETON_SKULL), new ItemStack(Material.SOUL_SAND), new ItemStack(Material.GOLDEN_CARROT));
        putSupplIntoHashMap(5, new ItemStack(Material.FROSTED_ICE), new ItemStack(Material.ICE));
        putSupplIntoHashMap(4, new ItemStack(Material.BEEF), new ItemStack(Material.DIAMOND), new ItemStack(Material.STICK));
        putSupplIntoHashMap(3, new ItemStack(Material.NETHER_STAR), new ItemStack(Material.ANCIENT_DEBRIS), new ItemStack(Material.BOOKSHELF));
        putSupplIntoHashMap(2, new ItemStack(Material.NETHER_STAR), new ItemStack(Material.DRAGON_BREATH), new ItemStack(Material.WITHER_ROSE));
        putSupplIntoHashMap(1, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
    }

    @Override
    public ItemStack returnPotionForSequence(int sequence) {
        return Potion.createPotion(
                "§d",
                sequence,
                Objects.requireNonNull(Pathway.getNamesForPathway(name)).get(sequence),
                Color.fromBGR(32, 165, 218),
                ""
        );
    }
}
