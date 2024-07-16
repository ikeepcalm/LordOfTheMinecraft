package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import dev.ua.ikeepcalm.utils.ErrorLoggerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class AirPipe extends Ability {

    public AirPipe(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                try {

                scheduler.runTask(LordOfTheMinecraft.instance, () -> player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 60, 1, false, false)));

                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    Location particleLoc = player.getEyeLocation().clone().add(player.getLocation().getDirection().normalize().multiply(0.25));
                    while (particleLoc.getBlock().getType() == Material.WATER) {
                        if (particleLoc.getWorld() != null) {
                            particleLoc.getWorld().spawnParticle(Particle.BUBBLE, particleLoc, 1, 0, 0, 0, 0);
                        }
                        particleLoc.add(0, .5, 0);
                    }
                });

                if (counter >= 20) {
                    counter = 0;
                    scheduler.runTask(LordOfTheMinecraft.instance, () -> pathway.getBeyonder().setSpirituality(pathway.getBeyonder().getSpirituality() - 5));
                }

                counter++;

                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    if (pathway.getBeyonder().getSpirituality() <= 2 || !pathway.getSequence().getUsesAbilities()[identifier - 1] || !pathway.getBeyonder().online) {
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                        cancel();
                    }
                });
                } catch (Exception e) {
                    ErrorLoggerUtil.logAbility(e, "Air Pipe");
                    cancel();
                }}
        }.runTaskTimerAsynchronously(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.PRISMARINE_CRYSTALS, "Подих Життя", "5", identifier);
    }
}
