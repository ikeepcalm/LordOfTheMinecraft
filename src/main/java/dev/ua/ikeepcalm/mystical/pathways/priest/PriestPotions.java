package dev.ua.ikeepcalm.mystical.pathways.priest;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class PriestPotions extends Potion {

    public PriestPotions() {
        name = "priest";
        stringColor = "§c";
        mainIngredients = new HashMap<>();
        supplementaryIngredients = new HashMap<>();

        putMainIntoHashMap(9, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(9, "priest", stringColor));
        putMainIntoHashMap(8, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(8, "priest", stringColor));
        putMainIntoHashMap(7, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(7, "priest", stringColor));
        putMainIntoHashMap(6, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(6, "priest", stringColor));
        putMainIntoHashMap(5, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(5, "priest", stringColor));
        putMainIntoHashMap(4, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(4, "priest", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(4, "priest", stringColor));
        putMainIntoHashMap(3, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(3, "priest", stringColor));
        putMainIntoHashMap(2, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(2, "priest", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(2, "priest", stringColor));
        putMainIntoHashMap(1, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(1, "priest", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(1, "priest", stringColor));

        putSupplIntoHashMap(9, new ItemStack(Material.BOW), new ItemStack(Material.BONE));
        putSupplIntoHashMap(8, new ItemStack(Material.DIAMOND_SWORD), new ItemStack(Material.TNT));
        putSupplIntoHashMap(7, new ItemStack(Material.MAGMA_BLOCK), new ItemStack(Material.LAVA_BUCKET), new ItemStack(Material.FEATHER));
        putSupplIntoHashMap(6, new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.FIREWORK_STAR));
        putSupplIntoHashMap(5, new ItemStack(Material.NETHER_WART), new ItemStack(Material.NETHERITE_SWORD));
        putSupplIntoHashMap(4, new ItemStack(Material.ELYTRA), new ItemStack(Material.IRON_SWORD), new ItemStack(Material.GOLDEN_SWORD));
        putMainIntoHashMap(3, new ItemStack(Material.NETHERITE_INGOT), new ItemStack(Material.FIRE_CHARGE));
        putMainIntoHashMap(2, new ItemStack(Material.SNOWBALL), new ItemStack(Material.FIRE_CORAL), new ItemStack(Material.POISONOUS_POTATO));
        putMainIntoHashMap(1, new ItemStack(Material.NETHERITE_BLOCK), new ItemStack(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE), new ItemStack(Material.GOLDEN_CARROT));
    }

    @Override
    public ItemStack returnPotionForSequence(int sequence) {
        return Potion.createPotion(
                "§c",
                sequence,
                Objects.requireNonNull(Pathway.getNamesForPathway(name)).get(sequence),
                Color.fromBGR(32, 165, 218),
                ""
        );
    }
}
