package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Fog extends Ability {

    public Fog(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                player.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, player.getLocation(), 45, 3.5, 3.5, 3.5, 0);

                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10, 1, false, false, false));

                for (Entity entity : player.getNearbyEntities(3.5, 3.5, 3.5)) {
                    if (!(entity instanceof LivingEntity livingEntity))
                        continue;

                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 1, false, false, false));
                }

                if (!pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.LIGHT_GRAY_DYE, "Туман", "50", identifier);
    }
}
