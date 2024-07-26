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

        putSupplIntoHashMap(9, new ItemStack(Material.BOW), new ItemStack(Material.BONE));
        putSupplIntoHashMap(8, new ItemStack(Material.DIAMOND_SWORD), new ItemStack(Material.TNT));
        putSupplIntoHashMap(7, new ItemStack(Material.MAGMA_BLOCK), new ItemStack(Material.LAVA_BUCKET), new ItemStack(Material.FEATHER));
        putSupplIntoHashMap(6, new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.FIREWORK_STAR));
        putSupplIntoHashMap(5, new ItemStack(Material.NETHER_WART), new ItemStack(Material.NETHERITE_SWORD));
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
