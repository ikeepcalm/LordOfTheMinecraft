package dev.ua.ikeepcalm.mystical.pathways.door;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import dev.ua.ikeepcalm.utils.BeyonderItemsUtil;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class DoorPotions extends Potion {

    public DoorPotions() {
        name = "door";
        stringColor = "§b";
        mainIngredients = new HashMap<>();
        supplementaryIngredients = new HashMap<>();

        putMainIntoHashMap(9, BeyonderItemsUtil.getMutatedDoor());
        putMainIntoHashMap(8, BeyonderItemsUtil.getMarlinBlood(), BeyonderItemsUtil.getSpiritPouch());
        putMainIntoHashMap(7, BeyonderItemsUtil.getLavosSquidBlood(), BeyonderItemsUtil.getMeteoriteCrystal());
        putMainIntoHashMap(6, BeyonderItemsUtil.getWraithDust());
        putMainIntoHashMap(5, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(5, "door", stringColor), BeyonderItemsUtil.getPituitaryGland());
        putMainIntoHashMap(4, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(4, "door", stringColor), BeyonderItemsUtil.getBizarroEye());
        putMainIntoHashMap(3, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(3, "door", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(3, "door", stringColor));
        putMainIntoHashMap(2, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(2, "door", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(2, "door", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(2, "door", stringColor));
        putMainIntoHashMap(1, LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(1, "door", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(1, "door", stringColor), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(1, "door", stringColor));

        putSupplIntoHashMap(9, new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.OAK_DOOR));
        putSupplIntoHashMap(8, GeneralItemsUtil.getMundanePotion(), new ItemStack(Material.ROSE_BUSH));
        putSupplIntoHashMap(7, new ItemStack(Material.BEEF), new ItemStack(Material.DIAMOND), new ItemStack(Material.STICK));
        putSupplIntoHashMap(6, new ItemStack(Material.BOOK), new ItemStack(Material.FEATHER));
        putSupplIntoHashMap(5, new ItemStack(Material.CHERRY_DOOR), new ItemStack(Material.NETHER_WART));
        putSupplIntoHashMap(4, new ItemStack(Material.ELYTRA), new ItemStack(Material.DIAMOND), new ItemStack(Material.STICK));
        putSupplIntoHashMap(3, new ItemStack(Material.NETHER_STAR), new ItemStack(Material.ANCIENT_DEBRIS), new ItemStack(Material.BOOKSHELF));
        putSupplIntoHashMap(2, new ItemStack(Material.NETHER_STAR), new ItemStack(Material.DRAGON_BREATH), new ItemStack(Material.WITHER_ROSE));
        putSupplIntoHashMap(1, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
    }

    @Override
    public ItemStack returnPotionForSequence(int sequence) {
        return Potion.createPotion(
                "§b",
                sequence,
                Objects.requireNonNull(Pathway.getNamesForPathway(name)).get(sequence),
                Color.fromBGR(255, 251, 0),
                ""
        );
    }
}
