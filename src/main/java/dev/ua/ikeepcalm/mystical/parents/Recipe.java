package dev.ua.ikeepcalm.mystical.parents;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;

public class Recipe {

    public ItemStack getRecipeForSequence(Potion potion, int sequence) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        assert bookMeta != null;
        bookMeta.title(Component.text("Містичний рецепт: " + translateName(potion.getName()) + " " + sequence).color(TextColor.color(0x00FF00)));
        StringBuilder mainIngredients = new StringBuilder();
        for (ItemStack item : potion.getMainIngredients(sequence)) {
            appendContent(mainIngredients, item);
        }

        StringBuilder supplIngredients = new StringBuilder();
        for (ItemStack item : potion.getSupplIngredients(sequence)) {
            appendContent(supplIngredients, item);
        }

        ArrayList<String> content = new ArrayList<>();

        String startPage = "Рецепт містичного зілля. Шляху назад немає! Вдумайся, перш ніж взятися за це!";
        String mainIngredientsPage = potion.getStringColor() + "Головні інгредієнти:\n\n" + mainIngredients;
        String supplIngredientsPage = potion.getStringColor() + "Додаткові інгредієнти:\n\n" + supplIngredients;
        String disclaimer = "Магічний рецепт має залишитися в таємниці. Не розголошуйте його нікому! Магічне зілля має бути зварене за один раз, якщо раптом вам не вистачає інгрідієнтів, не забудьте забрати викладені назад! Магічний котел дуже жадібний :P";

        content.add(startPage);
        content.add(mainIngredientsPage);
        content.add(supplIngredientsPage);
        content.add(disclaimer);

        bookMeta.setPages(content);
        bookMeta.setAuthor("...");
        bookMeta.setRarity(ItemRarity.EPIC);
        bookMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        book.setItemMeta(bookMeta);
        return book;
    }

    private void appendContent(StringBuilder supplIngredients, ItemStack item) {
        if (item.getItemMeta().hasDisplayName()) {
            supplIngredients.append(item.getItemMeta().getDisplayName()).append("\n");
        } else {
            supplIngredients.append(item.getType()).append("\n");
        }
        supplIngredients.append("\n");
    }

    private String translateName(String name) {
        return switch (name) {
            case "fool" -> "Шут";
            case "demoness" -> "Демонесса";
            case "sun" -> "Сонцеликий";
            case "tyrant" -> "Тиран";
            case "door" -> "Брама";
            default -> "...Помилка?";
        };
    }
}
