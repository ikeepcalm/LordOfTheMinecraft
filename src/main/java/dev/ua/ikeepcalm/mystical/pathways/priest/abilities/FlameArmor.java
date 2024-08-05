package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class FlameArmor extends Ability implements Listener {

    public FlameArmor(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;
        new BukkitRunnable() {
            int drain = 0;

            @Override
            public void run() {
                player.getWorld().spawnParticle(Particle.SMALL_FLAME, player.getEyeLocation(), 10, 1.1, 1.1, 1.1, 0);
                player.removePotionEffect(PotionEffectType.POISON);
                player.removePotionEffect(PotionEffectType.WEAKNESS);
                drain++;
                if (drain >= 20) {
                    drain = 0;
                    pathway.getSequence().removeSpirituality(10);
                }
                if (pathway.getBeyonder().getSpirituality() <= 20 || !pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                    cancel();
                }

            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @EventHandler
    private void damage(EntityDamageEvent e) {
        if (pathway == null) {
            return;
        }
        if (pathway.getSequence() == null) {
            return;
        }
        if (pathway.getSequence().getUsesAbilities()[identifier - 1] && e.getEntity() == player && e.getCause() == EntityDamageEvent.DamageCause.CONTACT) {
            Objects.requireNonNull(e.getEntity().getLastDamageCause()).getEntity().setFireTicks(20);
        }
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.GOLDEN_CHESTPLATE, "Полум'яна броня", "10/c", identifier);
    }
}
