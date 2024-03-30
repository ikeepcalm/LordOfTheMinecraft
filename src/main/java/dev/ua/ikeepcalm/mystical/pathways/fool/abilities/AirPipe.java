package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class AirPipe extends Ability {

    public AirPipe(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {

                p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 60, 1, false, false));

                Location particleLoc = p.getEyeLocation().clone().add(p.getLocation().getDirection().normalize().multiply(0.25));
                while (particleLoc.getBlock().getType() == Material.WATER) {
                    if (particleLoc.getWorld() != null) {
                        particleLoc.getWorld().spawnParticle(Particle.SPELL, particleLoc, 1, 0, 0, 0, 0);
                    }
                    particleLoc.add(0, .5, 0);
                }

                if (counter >= 20) {
                    counter = 0;
                    pathway.getBeyonder().setSpirituality(pathway.getBeyonder().getSpirituality() - 5);
                }

                counter++;

                if (pathway.getBeyonder().getSpirituality() <= 2 || !pathway.getSequence().getUsesAbilities()[identifier - 1] || !pathway.getBeyonder().online) {
                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.PRISMARINE_CRYSTALS, "Подих Життя", "5", identifier);
    }
}
