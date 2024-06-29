package dev.ua.ikeepcalm.mystical.parents;

import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Objects;

public class Recipe {

    public ItemStack getRecipeForSequence(Potion potion, int sequence) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        assert bookMeta != null;
        bookMeta.setDisplayName(potion.getStringColor() + GeneralPurposeUtil.capitalize("Містичний рецепт: " + potion.getName()) + " " + sequence);

        StringBuilder mainIngredients = new StringBuilder();
        for (ItemStack item : potion.getMainIngredients(sequence)) {
            if (item.getItemMeta().hasDisplayName()) {
                mainIngredients.append(PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(item.getItemMeta().displayName()))).append("\n");
            } else {
                mainIngredients.append(PlainTextComponentSerializer.plainText().serialize(Component.translatable(Objects.requireNonNull(item.getType().getItemTranslationKey())))).append("\n");
            }
            mainIngredients.append("\n");
        }

        StringBuilder supplIngredients = new StringBuilder();
        for (ItemStack item : potion.getSupplIngredients(sequence)) {
            if (item.getItemMeta().hasDisplayName()) {
                supplIngredients.append(PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(item.getItemMeta().displayName()))).append("\n");
            } else {
                supplIngredients.append(PlainTextComponentSerializer.plainText().serialize(Component.translatable(Objects.requireNonNull(item.getType().getItemTranslationKey())))).append("\n");
            }
            supplIngredients.append("\n");
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
}
