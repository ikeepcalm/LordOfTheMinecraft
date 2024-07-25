package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FireRaven extends Ability {

    private final List<ShulkerBullet> shulkerBullets;
    private double angle = 0;

    public FireRaven(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        shulkerBullets = new ArrayList<>();
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        if (shulkerBullets.isEmpty()) {
            pathway.getSequence().addSpirituality(50);
            return;
        }

        ShulkerBullet bullet = shulkerBullets.removeFirst();
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        new BukkitRunnable() {
            private int distanceTraveled = 0;

            @Override
            public void run() {
                if (!bullet.isValid()) {
                    cancel();
                    return;
                }
                Location bulletLocation = bullet.getLocation();
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromBGR(20, 20, 200), 1.2f);
                bulletLocation.getWorld().spawnParticle(Particle.DUST, bulletLocation, 8, dustOptions);
                bullet.setVelocity(direction);


                List<Block> blocks = GeneralPurposeUtil.getBlocksInCircleRadius(bulletLocation.getBlock(), 2, true);
                for (Block block : blocks) {
                    if (block.getType().isSolid()) {
                        bullet.getWorld().createExplosion(bulletLocation, 2, true, false);
                        bullet.remove();
                        cancel();
                        return;
                    }
                }

                distanceTraveled++;
                if (distanceTraveled >= 50) {
                    bullet.remove();
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public void leftClick() {
        if (player == null) {
            return;
        }

        if (shulkerBullets.size() >= 4) {
            player.sendMessage("§cВи вже викликали максимальну кількість вогняних воронів!");
            return;
        }

        ShulkerBullet bullet = player.getWorld().spawn(player.getLocation().add(0, 3, 0), ShulkerBullet.class);
        bullet.setVelocity(new Vector(0, 0, 0));
        shulkerBullets.add(bullet);
        player.sendMessage("§cВикликано вогняного ворона!");

        int radius = 1 + shulkerBullets.size() - 1;

        new BukkitRunnable() {
            private int timeLived = 0;

            @Override
            public void run() {
                if (!bullet.isValid() || !player.isOnline()) {
                    bullet.remove();
                    cancel();
                    return;
                }

                if (!shulkerBullets.contains(bullet)) {
                    cancel();
                    return;
                }

                // Increment the angle to create circular motion
                angle += Math.PI / 20;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                Location loc = player.getLocation().add(x, radius + 2, z);
                bullet.teleport(loc);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromBGR(20, 20, 200), 1.2f);
                loc.getWorld().spawnParticle(Particle.DUST, loc, 8, dustOptions);

                // Check if the bullet has lived more than 30 seconds
                timeLived++;
                if (timeLived >= 600) { // 30 seconds * 20 ticks per second
                    player.sendMessage("§cВогняний ворон видалився із часом!");
                    bullet.remove();
                    shulkerBullets.remove(bullet);
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.MAGMA_CREAM, "§cВогняний ворон", "50", identifier);
    }
}
