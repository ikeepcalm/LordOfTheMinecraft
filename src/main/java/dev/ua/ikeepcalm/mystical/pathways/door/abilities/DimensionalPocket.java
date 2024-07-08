package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
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
        SkullMeta skullMeta = (SkullMeta) currentItem.getItemMeta();
        assert skullMeta != null;
        skullMeta.setDisplayName("§bКишеня Енду");
        skullMeta.addEnchant(Enchantment.CHANNELING, identifier, true);
        skullMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        skullMeta.addItemFlags(ItemFlag.values());
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§5Натисніть щоб використати");
        lore.add("§5Духовність: §7150");
        lore.add("§8§l-----------------");
        lore.add("§bВрата - Послідовність (" + sequence + ")");
        lore.add("§8" + p);
        skullMeta.setLore(lore);

        PlayerProfile playerProfile = Bukkit.createProfile(LordOfTheMinecraft.randomUUID);
        PlayerTextures playerTextures = playerProfile.getTextures();
        try {
            playerTextures.setSkin(new URL("http://textures.minecraft.net/texture/ddcc189633c789cb6d5e78d13a5043b26e7b40cdb7cfc4e23aa2279574967b4"));
        } catch (MalformedURLException ignored) {
        }
        playerProfile.setTextures(playerTextures);
        skullMeta.setPlayerProfile(playerProfile);


        currentItem.setItemMeta(skullMeta);
        return currentItem;
    }
}
