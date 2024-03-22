package dev.ua.ikeepcalm.listeners;

import cz.foresttech.api.ColorAPI;
import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.mystical.Beyonder;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PotionListener implements Listener {

    @EventHandler
    public void onPotionInteract(PlayerInteractEvent e) {
        if (e.getItem() == null)
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

        e.getPlayer().getInventory().remove(e.getItem());

        //Not a beyonder already
        if (!LordOfTheMinecraft.beyonders.containsKey(e.getPlayer().getUniqueId())) {
            //initializing new Pathway
            Pathway pathway = Pathway.initializeNew(potion.getName(), e.getPlayer().getUniqueId(), sequence);
            if (pathway == null) {
                e.getPlayer().sendMessage("§cYour advancement has failed! You can call yourself lucky to still be alive...");
                return;
            }
            //makes new Beyonder loose control accordingly
            switch (9 - sequence) {
                case 0 -> pathway.getBeyonder().looseControl(93, 20);
                case 1 -> pathway.getBeyonder().looseControl(50, 20);
                case 2 -> pathway.getBeyonder().looseControl(25, 16);
                case 3, 4 -> pathway.getBeyonder().looseControl(15, 16);
                case 5 -> pathway.getBeyonder().looseControl(1, 20);
                default -> pathway.getBeyonder().looseControl(0, 10);
            }

            e.getPlayer().sendMessage(pathway.getItems().getAbilityInfo().get(sequence));
            setItemsShortcut(pathway, e.getPlayer());
        }
        //Is a beyonder
        else {
            Beyonder beyonder = LordOfTheMinecraft.beyonders.get(e.getPlayer().getUniqueId());
            beyonder.consumePotion(sequence, potion);
            setItemsShortcut(beyonder.getPathway(), e.getPlayer());
        }
    }

    private void setItemsShortcut(Pathway pathway, Player player) {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorAPI.colorize(LordOfTheMinecraft.beyonders.get(player.getUniqueId()).getPathway().getName()));
        meta.setCustomModelData(pathway.getPathwayInt());
        item.setItemMeta(meta);
        NBT.modify(item, (nbt) -> {
            nbt.setBoolean("openItems", true);
        });
        player.getInventory().setItem(9, item);
    }
}
