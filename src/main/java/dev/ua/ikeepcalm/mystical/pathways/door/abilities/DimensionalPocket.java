package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DimensionalPocket extends Ability {

    public DimensionalPocket(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        p.openInventory(p.getEnderChest());
    }

    @Override
    public ItemStack getItem() {
        p = pathway.getBeyonder().getPlayer();
        ItemStack currentItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) currentItem.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("§bКишеня Енду");
        itemMeta.addEnchant(Enchantment.CHANNELING, identifier, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.values());
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§5Натисніть щоб використати");
        lore.add("§5Духовність: §7150");
        lore.add("§8§l-----------------");
        lore.add("§bВрата - Послідовність (" + sequence + ")");
        lore.add("§8" + p);
        itemMeta.setLore(lore);

        PlayerProfile profile = Bukkit.createPlayerProfile(LordOfTheMinecraft.randomUUID);
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL("http://textures.minecraft.net/texture/ddcc189633c789cb6d5e78d13a5043b26e7b40cdb7cfc4e23aa2279574967b4"));
        } catch (MalformedURLException ignored) {
        }

        itemMeta.setOwnerProfile(profile);
        currentItem.setItemMeta(itemMeta);
        return currentItem;
    }
}
