package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import dev.ua.ikeepcalm.utils.MathVectorUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Roar extends Ability {
    public Roar(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        player = pathway.getBeyonder().getPlayer();
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        executeAbility(player.getLocation(), player, getMultiplier());
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        final World world = loc.getWorld();

        if (world == null)
            return;

        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5, 1);

        // First scheduled task
        new BukkitRunnable() {
            final double circlePoints = 100;
            double radius = .1;

            final Location loc = caster.getLocation().add(0, 1.5, 0);
            final Vector dir = caster.getLocation().getDirection().normalize();

            final double pitch = (loc.getPitch() + 90.0F) * 0.017453292F;
            final double yaw = -loc.getYaw() * 0.017453292F;

            final double increment = (2 * Math.PI) / circlePoints;
            double circlePointOffset = 0;

            int counter = 0;

            @Override
            public void run() {
                try {
                    // Particle effects
                    for (int i = 0; i < circlePoints; i++) {
                        double angle = i * increment + circlePointOffset;
                        double x = radius * Math.cos(angle);
                        double z = radius * Math.sin(angle);

                        Vector vec = new Vector(x, 0, z);
                        MathVectorUtils.rotateAroundAxisX(vec, pitch);
                        MathVectorUtils.rotateAroundAxisY(vec, yaw);
                        loc.add(vec);

                        GeneralPurposeUtil.drawParticlesForNearbyPlayers(Particle.DUST, loc, 0, 0, 0, 0, 0);
                        loc.subtract(vec);
                    }
                    circlePointOffset += increment / 3;
                    if (circlePointOffset >= increment) {
                        circlePointOffset = 0;
                    }
                    loc.add(dir);
                    radius += .225;

                    // Check if hit Entity
                    if (!world.getNearbyEntities(loc, 5, 5, 5).isEmpty()) {
                        for (Entity entity : world.getNearbyEntities(loc, 5, 5, 5)) {
                            if (GeneralPurposeUtil.testForValidEntity(entity, caster, true, true)) {
                                world.createExplosion(loc, (int) (radius * 1.75f), false, false);
                                ((LivingEntity) entity).damage(20 * multiplier, caster);
                                entity.setVelocity(dir.clone().normalize());
                                cancel();
                                return;
                            }
                        }
                    }

                    counter++;

                    if (loc.getBlock().getType().isSolid() || counter >= 100) {
                        world.createExplosion(loc, (int) (radius * 1.75f), false, false);
                        if (counter >= 100)
                            cancel();
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Roar");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.FIREWORK_STAR, "Громовий Гул", "550", identifier);
    }
}
