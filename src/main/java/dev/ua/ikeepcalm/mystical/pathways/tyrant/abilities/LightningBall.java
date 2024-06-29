package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantSequence;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Random;

public class LightningBall extends Ability {

    public LightningBall(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        p = pathway.getBeyonder().getPlayer();
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        executeAbility(GeneralPurposeUtil.getTargetLoc(200, p), p, getMultiplier());
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.PURPLE_DYE, "Громова Куля", "5000", identifier);
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        Location startLoc = caster.getLocation().add(0, .75, 0);
        Vector dir = loc.toVector().subtract(caster.getLocation().add(0, .75, 0).toVector()).normalize().multiply(2);

        Random random = new Random();

        Particle.DustOptions dustBlue = new Particle.DustOptions(Color.fromRGB(143, 255, 244), 1.5f);
        Particle.DustOptions dustPurple = new Particle.DustOptions(Color.fromRGB(87, 20, 204), 1.5f);

        if (startLoc.getWorld() == null)
            return;

        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();

        new BukkitRunnable() {
            int counter = 200;

            @Override
            public void run() {
                GeneralPurposeUtil.drawParticleSphere(startLoc, 2, 10, dustBlue, null, .05, Particle.DUST);
                GeneralPurposeUtil.drawParticleSphere(startLoc, 2, 10, dustPurple, null, .05, Particle.DUST);

                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    if (startLoc.getBlock().getType().isSolid() || (!startLoc.getWorld().getNearbyEntities(startLoc, 1, 1, 1).isEmpty() && !startLoc.getWorld().getNearbyEntities(startLoc, 1, 1, 1).contains(caster))) {
                        new BukkitRunnable() {
                            int counter = 16;

                            @Override
                            public void run() {
                                TyrantSequence.spawnLighting(startLoc.clone().add(random.nextInt(-1, 1), 0, random.nextInt(-1, 1)), caster, 10, false, 1);

                                counter--;
                                if (counter <= 0)
                                    cancel();
                            }
                        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 4);
                        this.counter = 0;
                    }
                });

                startLoc.add(dir);

                counter--;
                if (counter <= 0)
                    cancel();
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }
}
