package dev.ua.ikeepcalm.listeners;

import cz.foresttech.api.ColorAPI;
import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Beyonder;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PotionListener implements Listener {

    @EventHandler
    public void onPotionInteract(PlayerItemConsumeEvent e) {
        ItemStack item = e.getItem();
        if (item.getType() == Material.AIR)
            return;

        if (item.getType() != Material.POTION)
            return;

        int sequence = 0;
        Potion potion = null;
        outerloop:
        for (Potion p : LordOfTheMinecraft.instance.getPotions()) {
            for (int i = 1; i < 10; i++) {
                if (p.returnPotionForSequence(i).equals(e.getItem())) {
                    sequence = i;
                    potion = p;
                    break outerloop;
                }
            }
        }
        if (sequence == 0)
            return;

        //Not a beyonder already
        if (!LordOfTheMinecraft.beyonders.containsKey(e.getPlayer().getUniqueId())) {
            //initializing new Pathway
            Pathway pathway = Pathway.initializeNew(potion.getName(), e.getPlayer().getUniqueId(), sequence);
            if (pathway == null) {
                e.getPlayer().sendMessage("§cНевдача! Можете вважати, що вам пощастило, що ви залишилися живі...");
                return;
            }
            //makes new Beyonder loose control accordingly
            switch (9 - sequence) {
                case 0 -> pathway.getBeyonder().looseControl(98, 20);
                case 1, 5 -> pathway.getBeyonder().looseControl(1, 20);
                case 2, 3, 4 -> pathway.getBeyonder().looseControl(1, 16);
                default -> pathway.getBeyonder().looseControl(0, 10);
            }

            if (sequence == 9)
                e.getPlayer().sendMessage(pathway.getStringColor() + "Вітаємо у світі Потойбічних, " + e.getPlayer().getName() + "!");

            setAbilitiesShortcut(pathway, e.getPlayer());
        }
        //Is a beyonder
        else {
            Beyonder beyonder = LordOfTheMinecraft.beyonders.get(e.getPlayer().getUniqueId());
            beyonder.consumePotion(sequence, potion);
            setAbilitiesShortcut(beyonder.getPathway(), e.getPlayer());
        }
    }

    private void setAbilitiesShortcut(Pathway pathway, Player player) {
        ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorAPI.colorize(LordOfTheMinecraft.beyonders.get(player.getUniqueId()).getPathway().getStringColor()) + "Знання");
        meta.setCustomModelData(pathway.getPathwayInt() + 1);
        item.setItemMeta(meta);
        NBT.modify(item, (nbt) -> {
            nbt.setBoolean("openAbilities", true);
        });
        player.getInventory().setItem(9, item);
    }
}
