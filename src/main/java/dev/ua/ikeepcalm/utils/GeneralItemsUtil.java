package dev.ua.ikeepcalm.utils;

import cz.foresttech.api.ColorAPI;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneralItemsUtil {

    private static final List<ItemStack> list = new ArrayList<>();

    public static List<ItemStack> returnAllItems() {
        return list;
    }

    public static ItemStack getMagentaPane() {
        final ItemStack magentaPane = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);
        ItemMeta magentaPaneMeta = magentaPane.getItemMeta();
        assert magentaPaneMeta != null;
        magentaPaneMeta.setDisplayName(" ");
        magentaPaneMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        magentaPaneMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        magentaPane.setItemMeta(magentaPaneMeta);

        list.add(magentaPane);

        return magentaPane;
    }

    public static ItemStack getCauldron() {
        final ItemStack item = new ItemStack(Material.CAULDRON);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§5Казан");
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemMeta);

        list.add(item);

        return item;
    }

    public static ItemStack getPurplePane() {
        final ItemStack magentaPane = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta magentaPaneMeta = magentaPane.getItemMeta();
        assert magentaPaneMeta != null;
        magentaPaneMeta.setDisplayName(" ");
        magentaPaneMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        magentaPaneMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        magentaPane.setItemMeta(magentaPaneMeta);

        list.add(magentaPane);

        return magentaPane;
    }

    public static ItemStack getWhitePane() {
        final ItemStack magentaPane = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta magentaPaneMeta = magentaPane.getItemMeta();
        assert magentaPaneMeta != null;
        magentaPaneMeta.setDisplayName(" ");
        magentaPaneMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        magentaPaneMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        magentaPane.setItemMeta(magentaPaneMeta);

        list.add(magentaPane);

        return magentaPane;
    }

    public static ItemStack getDowsingRod() {
        final ItemStack dowsingStick = new ItemStack(Material.STICK);
        ItemMeta stickMeta = dowsingStick.getItemMeta();
        assert stickMeta != null;
        stickMeta.setDisplayName("§5Dowsing Rod Seeking");
        stickMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stickMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        dowsingStick.setItemMeta(stickMeta);

        list.add(dowsingStick);

        return dowsingStick;
    }

    public static ItemStack getDangerPremonition() {
        final ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemStack = item.getItemMeta();
        assert itemStack != null;
        itemStack.setDisplayName("§5Передчуття Небезпеки");
        itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemStack);

        list.add(item);

        return item;
    }

    public static ItemStack getDreamDivination() {
        final ItemStack item = new ItemStack(Material.RED_BED);
        ItemMeta itemStack = item.getItemMeta();
        assert itemStack != null;
        itemStack.setDisplayName("§5Ворожіння Снів");
        itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemStack);

        list.add(item);

        return item;
    }

    public static ItemStack getCowHead() {
        final ItemStack cowHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta cowMeta = (SkullMeta) cowHead.getItemMeta();
        assert cowMeta != null;
        cowMeta.setDisplayName("§6Сутності");
        String[] cowLore = {"§5Визначте місцезнаходження будь-якої сутності"};
        cowMeta.setLore(Arrays.asList(cowLore));
        PlayerProfile cowProfile = Bukkit.createPlayerProfile(LordOfTheMinecraft.randomUUID);
        PlayerTextures cowTextures = cowProfile.getTextures();
        try {
            cowTextures.setSkin(new URL("https://textures.minecraft.net/texture/c5a9cd58d4c67bccc8fb1f5f756a2d381c9ffac2924b7f4cb71aa9fa13fb5c"));
        } catch (MalformedURLException ignored) {
        }
        cowMeta.setOwnerProfile(cowProfile);
        cowHead.setItemMeta(cowMeta);

        list.add(cowHead);

        return cowHead;
    }

    public static ItemStack getGrassHead() {
        final ItemStack grassHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta grassMeta = (SkullMeta) grassHead.getItemMeta();
        assert grassMeta != null;
        grassMeta.setDisplayName("§6Біоми");
        String[] grassLore = {"§5Визначте місцезнаходження будь-якого біома"};
        grassMeta.setLore(Arrays.asList(grassLore));
        PlayerProfile grassProfile = Bukkit.createPlayerProfile(LordOfTheMinecraft.randomUUID);
        PlayerTextures grassTextures = grassProfile.getTextures();
        try {
            grassTextures.setSkin(new URL("http://textures.minecraft.net/texture/16bb9fb97ba87cb727cd0ff477f769370bea19ccbfafb581629cd5639f2fec2b"));
        } catch (MalformedURLException ignored) {
        }
        grassMeta.setOwnerProfile(grassProfile);
        grassHead.setItemMeta(grassMeta);

        list.add(grassHead);

        return grassHead;
    }

    public static ItemStack getDivinationHead() {
        final ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerMeta = (SkullMeta) playerHead.getItemMeta();
        assert playerMeta != null;
        playerMeta.setDisplayName("§6Гравці");
        String[] playerLore = {"§5Визначте місцезнаходження будь-якого гравця"};
        playerMeta.setLore(Arrays.asList(playerLore));
        PlayerProfile playerProfile = Bukkit.createPlayerProfile(LordOfTheMinecraft.randomUUID);
        PlayerTextures playerTextures = playerProfile.getTextures();
        try {
            playerTextures.setSkin(new URL("http://textures.minecraft.net/texture/4d9d043adc884b979b4f42bdb350f2a301327cab49c4ce2de42a8f4601fe9dbf"));
        } catch (MalformedURLException ignored) {
        }
        playerMeta.setOwnerProfile(playerProfile);
        playerHead.setItemMeta(playerMeta);

        list.add(playerHead);

        return playerHead;
    }

    public static ItemStack getMeteor() {
        final ItemStack meteor = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta meteorMeta = meteor.getItemMeta();
        assert meteorMeta != null;
        meteorMeta.setDisplayName("§4Викликати Метеорит");
        meteorMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meteorMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        meteor.setItemMeta(meteorMeta);

        list.add(meteor);

        return meteor;
    }

    public static ItemStack getTornado() {
        final ItemStack tornado = new ItemStack(Material.FEATHER);
        ItemMeta tornadoMeta = tornado.getItemMeta();
        assert tornadoMeta != null;
        tornadoMeta.setDisplayName("§fВикликати Торнадо");
        tornadoMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        tornadoMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        tornado.setItemMeta(tornadoMeta);

        list.add(tornado);

        return tornado;
    }

    public static ItemStack getLightning() {
        final ItemStack lightning = new ItemStack(Material.LIGHTNING_ROD);
        ItemMeta lightningMeta = lightning.getItemMeta();
        assert lightningMeta != null;
        lightningMeta.setDisplayName("§bВикликати Блискавку");
        lightningMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        lightningMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        lightning.setItemMeta(lightningMeta);

        list.add(lightning);

        return lightning;
    }

    public static ItemStack getSunnyWeather() {
        final ItemStack sun = new ItemStack(Material.SUNFLOWER);
        ItemMeta sunMeta = sun.getItemMeta();
        assert sunMeta != null;
        sunMeta.setDisplayName("§6Ясна Погода");
        sunMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        sunMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        sun.setItemMeta(sunMeta);

        list.add(sun);

        return sun;
    }

    public static ItemStack getRainyWeather() {
        final ItemStack rain = new ItemStack(Material.WATER_BUCKET);
        ItemMeta rainMeta = rain.getItemMeta();
        assert rainMeta != null;
        rainMeta.setDisplayName("§3Дощова Погода");
        rainMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        rainMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        rain.setItemMeta(rainMeta);

        list.add(rain);

        return rain;
    }

    public static ItemStack getStormyWeather() {
        final ItemStack storm = new ItemStack(Material.BLAZE_ROD);
        ItemMeta stormMeta = storm.getItemMeta();
        assert stormMeta != null;
        stormMeta.setDisplayName("§9Штормова Погода");
        stormMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stormMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        storm.setItemMeta(stormMeta);

        list.add(storm);

        return storm;
    }

    public static ItemStack getThreadLength() {
        final ItemStack lengthString = new ItemStack(Material.STRING);
        ItemMeta stringMeta = lengthString.getItemMeta();
        assert stringMeta != null;
        stringMeta.setDisplayName("§6Змінити Довжину Ниток");
        stringMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stringMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        lengthString.setItemMeta(stringMeta);

        list.add(lengthString);

        return lengthString;
    }

    public static ItemStack getExcludeEntities() {
        final ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§cВиключити Сутності");
        String[] lore = {
                "§cВведіть назву Сутності, яку ви бажаєте", " §6виключити §cз §4Маріонеткового Ткача",
                "§cУвімкнути §4виключення §cсутностей", " §cта вимкнути включення сутностей",
                "§aВведіть §2cancel§a, щоб скасувати, і §2reset§a, щоб скинути"

        };
        itemMeta.setLore(Arrays.asList(lore));
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemMeta);

        list.add(item);

        return item;
    }

    public static ItemStack getIncludeEntities() {
        final ItemStack item = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§aВключити Сутності");
        String[] lore = {
                "§aВведіть назву Сутності, яку ви бажаєте", " §2включити §aв §2Маріонетковий Ткач",
                "§aУвімкнути §2включення §aсутностей", " §aта вимкнути виключення сутностей",
                "§aВведіть §2cancel§a, щоб скасувати, і §2reset§a, щоб скинути"

        };
        itemMeta.setLore(Arrays.asList(lore));
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemMeta);

        list.add(item);

        return item;
    }

    public static ItemStack getAttack() {
        final ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§5Атакувати");
        String[] lore = {
                "§aНатисніть ПКМ, щоб атакувати!"
        };
        itemMeta.setLore(Arrays.asList(lore));
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemMeta);

        list.add(item);

        return item;
    }

    public static ItemStack getConfirmPotion() {
        final ItemStack item = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§aЗварити зілля");
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemMeta);

        list.add(item);

        return item;
    }

    public static ItemStack getMundanePotion() {
        final ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        assert potionMeta != null;
        potionMeta.setBasePotionType(PotionType.MUNDANE);
        potion.setItemMeta(potionMeta);
        return potion;
    }

    public static ItemStack getWaterPotion() {
        final ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        assert potionMeta != null;
        potionMeta.setBasePotionType(PotionType.WATER);
        potion.setItemMeta(potionMeta);
        return potion;
    }

    public static ItemStack getRegenPotion() {
        final ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        assert potionMeta != null;
        potionMeta.setBasePotionType(PotionType.REGENERATION);
        potion.setItemMeta(potionMeta);
        return potion;
    }

    public static ItemStack getRandomGrimoire() {
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        assert bookMeta != null;

        String[] s = {"tome-one", "tome-two", "tome-three", "tome-four"};

        Random ran = new Random();
        String tome = s[ran.nextInt(s.length)];

        bookMeta.setDisplayName(ColorAPI.colorize(LocalizationUtil.getLocalizedString("grimoires", tome,"grimoire-name")));

       List<String> content = LocalizationUtil.getLocalizedArray("grimoires", tome,"grimoire-lore");

        bookMeta.setPages(content);
        bookMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        book.setItemMeta(bookMeta);
        return book;
    }
}
