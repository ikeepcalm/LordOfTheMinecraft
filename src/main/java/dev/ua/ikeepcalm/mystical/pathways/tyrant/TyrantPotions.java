package dev.ua.ikeepcalm.mystical.pathways.tyrant;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import dev.ua.ikeepcalm.utils.BeyonderItemsUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class TyrantPotions extends Potion {

    public TyrantPotions() {
        name = "tyrant";
        stringColor = "§9";
        mainIngredients = new HashMap<>();
        supplementaryIngredients = new HashMap<>();

        putMainIntoHashMap(9, BeyonderItemsUtil.getSirenRock());
        putMainIntoHashMap(8, BeyonderItemsUtil.getStellarAquaCrystal());
        putMainIntoHashMap(7, BeyonderItemsUtil.getSpiritTreeFruit());
        putMainIntoHashMap(6, BeyonderItemsUtil.getCrystallizedRoot());
        putMainIntoHashMap(5, BeyonderItemsUtil.getWhiteBrillianceRock());
        putMainIntoHashMap(4, BeyonderItemsUtil.getCrystallizedRoot(), BeyonderItemsUtil.getHolyBrillianceRock());
        putMainIntoHashMap(3, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(3, "tyrant", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(3, "tyrant", stringColor));
        putMainIntoHashMap(2, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(2, "tyrant", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(2, "tyrant", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(2, "tyrant", stringColor));
        putMainIntoHashMap(1, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(1, "tyrant", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(1, "tyrant", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(1, "tyrant", stringColor));

        putSupplIntoHashMap(9, new ItemStack(Material.SHORT_GRASS), new ItemStack(Material.SUNFLOWER));
        putSupplIntoHashMap(8, new ItemStack(Material.FISHING_ROD), new ItemStack(Material.SWEET_BERRIES));
        putSupplIntoHashMap(7, new ItemStack(Material.OBSIDIAN), new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.FEATHER));
        putSupplIntoHashMap(6, BeyonderItemsUtil.getSunflower(), new ItemStack(Material.FERN), new ItemStack(Material.GLOW_BERRIES));
        putSupplIntoHashMap(5, new ItemStack(Material.GLOW_INK_SAC), new ItemStack(Material.NETHER_WART));
        putSupplIntoHashMap(4, BeyonderItemsUtil.getWhiteBrillianceRock(), new ItemStack(Material.ELYTRA));
        putSupplIntoHashMap(3, new ItemStack(Material.NETHER_STAR), new ItemStack(Material.ANCIENT_DEBRIS), new ItemStack(Material.BOOKSHELF));
        putSupplIntoHashMap(2, new ItemStack(Material.NETHERITE_SWORD), new ItemStack(Material.DRAGON_BREATH), new ItemStack(Material.WITHER_ROSE));
        putSupplIntoHashMap(1, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
    }

    @Override
    public ItemStack returnPotionForSequence(int sequence) {
        return Potion.createPotion(
                "§9",
                sequence,
                Objects.requireNonNull(Pathway.getNamesForPathway(name)).get(sequence),
                Color.fromBGR(32, 165, 218),
                ""
        );
    }
}
