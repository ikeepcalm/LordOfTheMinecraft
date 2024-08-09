package dev.ua.ikeepcalm.utils;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class BeyonderItemsUtil {

    private static final List<ItemStack> list = new ArrayList<>();
    private static final UUID characteristicUUID = UUID.fromString("4fba5f2f-cc36-4dc2-9b77-6064bb10788d");

    public static List<ItemStack> returnAllItems() {
        return list;
    }

    public static ItemStack getLavosSquidBlood() {
        final ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§4Кров Лавового Кальмара");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "lavos_squid_blood");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getMarlinBlood() {
        final ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§4Кров Глибоководного Марліна");
        itemMeta.setCustomModelData(2);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "marlin_blood");
        });
        list.add(item);

        return item;
    }

    public static ItemStack getSpiritRemains() {
        final ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§5Залишки Духу");
        itemMeta.setCustomModelData(295);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "spirit_remains");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getMutatedDoor() {
        final ItemStack item = new ItemStack(Material.IRON_DOOR);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§bМутована Брама");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "mutated_door");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getSpiritPouch() {
        final ItemStack item = new ItemStack(Material.GHAST_TEAR);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§bШлунок Пожирача Духів");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "spirit_pouch");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getStellarAquaCrystal() {
        final ItemStack item = new ItemStack(Material.PRISMARINE_CRYSTALS);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§3Зоряний Аквамарин");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "stellar_aqua_crystal");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getMeteoriteCrystal() {
        final ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§4Метеоритний Кристал");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "meteorite_crystal");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getGoatHorn() {
        final ItemStack item = new ItemStack(Material.GOAT_HORN);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§7Рог Сірої Гірської Кози Горнакіс");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "goat_horn");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getRose() {
        final ItemStack item = new ItemStack(Material.ROSE_BUSH);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§4Троянда з людським обличчям");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "rose");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getSunflower() {
        final ItemStack item = new ItemStack(Material.SUNFLOWER);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Кришталевий Соняшник");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "sunflower");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getSirenRock() {
        final ItemStack item = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§9Шматок Каменю Сирени");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "siren_rock");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getMagmaHeart() {
        final ItemStack item = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§4Серце Магмового Титану");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "magma_heart");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getRoosterComb() {
        final ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Гребінь Світанкового Півня");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "rooster_comb");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getSpiritTreeFruit() {
        final ItemStack item = new ItemStack(Material.GLOW_BERRIES);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Плід Дерева Договору Духа Сяйва");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "spirit_tree_fruit");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getBirdFeather() {
        final ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Перо Духовного Птаху Договору");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "bird_feather");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getCrystallizedRoot() {
        final ItemStack item = new ItemStack(Material.HANGING_ROOTS);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Кристалізований Корінь Дерева Старійшин");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "crystallized_root");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getWhiteBrillianceRock() {
        final ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Білий Блискучий Камінь");
        itemMeta.setCustomModelData(2);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "white_brilliance_rock");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getHolyBrillianceRock() {
        final ItemStack item = new ItemStack(Material.QUARTZ);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Священний Блискучий Камінь");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "holy_brilliance_rock");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getTailFeather() {
        final ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Перо з Хвоста Божественного Птаха");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "tail_feather");
        });

        list.add(item);

        return item;
    }


    public static ItemStack getRedRoosterComb() {
        final ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Червоний Гребінь Світанкового Півня");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "red_rooster_comb");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getRoot() {
        final ItemStack item = new ItemStack(Material.HANGING_ROOTS);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§6Корінь Туманного Дерева");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "root_of_mist_tree");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getPanther() {
        final ItemStack item = new ItemStack(Material.BLACK_DYE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§8Спинномозкова Рідина Чорнаплямої Пантери");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "panther");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getPituitaryGland() {
        final ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§5Мутований гіпофіз Тисячоликого Мисливця");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "pituitary_gland");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getShadowCharacteristic() {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§8Характеристика Людиноподібної Тіні");

        PlayerProfile profile = Bukkit.createPlayerProfile(characteristicUUID);
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(new URL("http://textures.minecraft.net/texture/3ccc8a690c89ebf01adf0440c0a3d540e2db89cfc97ad3b8e01810bf3289f67a"));
        } catch (MalformedURLException ignored) {
        }

        itemMeta.setOwnerProfile(profile);

        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "shadow_characteristic");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getWraithDust() {
        final ItemStack item = new ItemStack(Material.GUNPOWDER);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§7Пил Древніх Рейфів");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "wraith_dust");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getGargoyleCrystal() {
        final ItemStack item = new ItemStack(Material.CONDUIT);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§5Серцевинний Кристал Шестикрилої Горгульї");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "gargoyle_crystal");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getBizarroEye() {
        final ItemStack item = new ItemStack(Material.FERMENTED_SPIDER_EYE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§5Око Перевертня");
        itemMeta.setCustomModelData(1);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "bizarro_eye");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getPlundererBody() {
        final ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§5Духовне Тіло Розкрадача Духовного Світу");
        itemMeta.setCustomModelData(1);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "plunderer_body");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getWolfEye() {
        final ItemStack item = new ItemStack(Material.SPIDER_EYE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§5Око Пса Фулгріма");
        itemMeta.setCustomModelData(1);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "wolf_eye");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getWolfHeart() {
        final ItemStack item = new ItemStack(Material.COAL);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§5Трансформоване Серце Демонічного Вовка Завіси");
        itemMeta.setCustomModelData(1);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "wolf_heart");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getMagmaElfCore() {
        final ItemStack item = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§cЯдро магматичного ельфа");
        itemMeta.setCustomModelData(1);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "magma_elf_core");
        });

        list.add(item);

        return item;
    }

    public static ItemStack getFireSalamanderBlood() {
        final ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§cКров вогняної саламандри");
        itemMeta.setCustomModelData(2);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "fire_salamander_blood");
        });
        list.add(item);

        return item;
    }

    public static ItemStack getBlackSpiderEye() {
        final ItemStack item = new ItemStack(Material.SPIDER_EYE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§0Око мисливського павука");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "black_spider_eye");
        });
        list.add(item);

        return item;
    }

    public static ItemStack getSphinxBrain() {
        final ItemStack item = new ItemStack(Material.BRAIN_CORAL);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§fМозок Сфінксу");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "sphinx_brain");
        });
        list.add(item);

        return item;
    }

    public static ItemStack getWolfClaws() {
        final ItemStack item = new ItemStack(Material.TIPPED_ARROW);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§8Пазурі Демонічного Вовка");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "wolf_claws");
        });
        list.add(item);

        return item;
    }

    public static ItemStack getHunterTongue() {
        final ItemStack item = new ItemStack(Material.FERMENTED_SPIDER_EYE);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§9Язик лісового мисливця");
        itemMeta.setCustomModelData(1);
        item.setItemMeta(itemMeta);
        NBT.modify(item, nbt -> {
            nbt.setString("ingredient", "hunter_tongue");
        });
        list.add(item);

        return item;
    }


}
