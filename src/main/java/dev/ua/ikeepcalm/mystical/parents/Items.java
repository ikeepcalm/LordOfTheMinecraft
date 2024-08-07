package dev.ua.ikeepcalm.mystical.parents;

import cz.foresttech.api.ColorAPI;
import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Items {

    @Setter
    @Getter
    protected Pathway pathway;
    private static Pathway internalPathway;
    @Setter
    @Getter
    protected ArrayList<ItemStack> items;
    protected HashMap<Integer, Integer> sequenceItems;
    @Getter
    protected HashMap<Integer, String[]> abilityInfo;

    public void addToSequenceItems(int index, int value) {
        sequenceItems.put(index, value);
    }

    public Items(Pathway pathway) {
        this.pathway = pathway;
        internalPathway = pathway;
    }

    public abstract ArrayList<ItemStack> returnItemsFromSequence(int sequence);

    @SuppressWarnings("unused")
    public HashMap<Integer, Integer> getSequenceItems() {
        return sequenceItems;
    }

    @SuppressWarnings("unused")
    public void setSequenceItems(HashMap<Integer, Integer> sequenceItems) {
        this.sequenceItems = sequenceItems;
    }

    public int getSequenceOfAbility(Ability a) {
        return sequenceItems.get(a.getIdentifier() - 1);
    }

    protected String[] formatAbilityInfo(String pathwayColor, String sequenceName, String... s) {
        String[] formatted = new String[3 + s.length];
        formatted[0] = ColorAPI.colorize(pathwayColor + "Знання " + sequenceName);
        formatted[1] = ColorAPI.colorize(pathwayColor + "-----------------------------");
        for (int i = 0; i < s.length; i++) {
            s[i] = ColorAPI.colorize(pathwayColor + s[i]);
        }
        formatted[2 + s.length] = ColorAPI.colorize(pathwayColor + "-----------------------------");

        System.arraycopy(s, 0, formatted, 2, s.length);

        return formatted;
    }

    public static ItemStack createItem(Material item, String name, String spirituality, int id) {
        spirituality = spirituality.replace("/с", "");
        ItemStack currentItem = new ItemStack(item);
        ItemMeta itemMeta = currentItem.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6" + name);
        itemMeta.addEnchant(Enchantment.CHANNELING, id, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.values());
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§6Витрати духовності: §7" + spirituality);
        if (internalPathway.getSequence() != null) {
            if (internalPathway.getSequence().getIds().contains(id)) {
                lore.add("§6Дію цієї здібності можна відмінити вручну");
            }
        }
        itemMeta.setLore(lore);
        currentItem.setItemMeta(itemMeta);
        String finalSpirituality = spirituality;
        NBT.modify(currentItem, (nbt) -> {
            nbt.setString("spiritualityDrainage", finalSpirituality);
        });
        return currentItem;
    }

}
