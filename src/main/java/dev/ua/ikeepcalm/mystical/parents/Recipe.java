package dev.ua.ikeepcalm.mystical.parents;

import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Objects;

public class Recipe {

    public ItemStack getRecipeForSequence(Potion potion, int sequence) {
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        assert bookMeta != null;
        bookMeta.setDisplayName(potion.getStringColor() + GeneralPurposeUtil.capitalize("Знання: " + potion.getName()) + " " + sequence);

        StringBuilder mainIngredients = new StringBuilder();
        for (ItemStack item : potion.getMainIngredients(sequence)) {
            mainIngredients.append(Objects.requireNonNull(item.getItemMeta()).getDisplayName());
            mainIngredients.append("\n");
        }

        StringBuilder supplIngredients = new StringBuilder();
        for (ItemStack item : potion.getSupplIngredients(sequence)) {
            if (!Objects.requireNonNull(item.getItemMeta()).getDisplayName().equalsIgnoreCase(""))
                supplIngredients.append(Objects.requireNonNull(item.getItemMeta()).getDisplayName());
            else
                supplIngredients.append(GeneralPurposeUtil.capitalize(item.getType().name()));
            supplIngredients.append("\n");
        }

        ArrayList<String> content = new ArrayList<>();

        String page = "Од.. р.з по..вши, шляху н.... ...ає, пос..йн.. бо...с.. із за...зами і бо...ллям"
                + "\n" +
                potion.getStringColor() + "Го...вні ...єнти: \n" +
                        mainIngredients + "\n \n" +
                        potion.getStringColor() + "Дод... інг...ти:§r \n" +
                        supplIngredients + "\n";

        content.add(page);
        bookMeta.setPages(content);
        bookMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        book.setItemMeta(bookMeta);
        return book;
    }
}
