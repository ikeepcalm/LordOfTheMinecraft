package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Invisibility extends Ability {

    public Invisibility(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                player.setInvisible(true);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.hidePlayer(LordOfTheMinecraft.instance, Invisibility.this.player);
                }

                if (pathway == null || pathway.getSequence() == null) {
                    cancel();
                    return;
                }

                if (pathway.getBeyonder().getSpirituality() <= 8)
                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;

                counter++;

                if (counter >= 20) {
                    counter = 0;
                    pathway.getSequence().removeSpirituality(5);
                }

                if (!pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.showPlayer(LordOfTheMinecraft.instance, Invisibility.this.player);
                    }
                    cancel();
                    player.setInvisible(false);
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.GHAST_TEAR, "Невидимість", "5/c", identifier);
    }
}
