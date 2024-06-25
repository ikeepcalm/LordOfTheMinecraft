package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import dev.ua.ikeepcalm.utils.MathVectorUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class AirBullet extends Ability {

    private static HashMap<Integer, double[]> valuesForSequence;

    private int sequencePower;
    private boolean wasAdjustedOnce;

    public AirBullet(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);

        valuesForSequence = new HashMap<>();
        valuesForSequence.put(7, new double[]{0.25, 20, 0, 1});
        valuesForSequence.put(6, new double[]{0.35, 40, 0, 2.5});
        valuesForSequence.put(5, new double[]{0.5, 50, 2, 3});
        valuesForSequence.put(4, new double[]{0.85, 80, 6, 5});
        valuesForSequence.put(3, new double[]{1.25, 100, 11, 6});
        valuesForSequence.put(2, new double[]{1.25, 100, 12, 6});
        valuesForSequence.put(1, new double[]{1.25, 100, 13, 6});
        sequencePower = pathway.getSequence().getCurrentSequence();
        wasAdjustedOnce = false;

        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location loc, Entity caster) {
        if (pathway.getSequence().getCurrentSequence() > 3)
            sequencePower = pathway.getSequence().getCurrentSequence();
        else if (!wasAdjustedOnce) {
            sequencePower = pathway.getSequence().getCurrentSequence();
            wasAdjustedOnce = true;
        }
        double multiplier = (valuesForSequence.get(sequencePower) != null ? valuesForSequence.get(sequencePower)[3] : 3);

        double finalMultiplier = multiplier;

        final HashMap<Double, double[]> npcMultiplier = new HashMap<>();
        npcMultiplier.put(1.4, new double[]{0.25, 20, 0, 1});
        npcMultiplier.put(1.6, new double[]{0.35, 40, 0, 2.5});
        npcMultiplier.put(1.7, new double[]{0.5, 50, 2, 3});
        npcMultiplier.put(1.9, new double[]{0.85, 80, 6, 5});
        npcMultiplier.put(2.5, new double[]{1.25, 100, 11, 6});
        npcMultiplier.put(3.0, new double[]{1.25, 100, 12, 6});
        npcMultiplier.put(3.5, new double[]{1.25, 100, 13, 6});

        BukkitScheduler scheduler = LordOfTheMinecraft.instance.getServer().getScheduler();

        scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
            final double circlePoints = valuesForSequence.get(pathway.getSequence().getCurrentSequence()) != null
                    ? valuesForSequence.get(sequencePower)[1]
                    : 20;

            double radius = valuesForSequence.get(sequencePower) != null
                    ? valuesForSequence.get(sequencePower)[0]
                    : 0.25;

            final Location locCopy = loc.clone();
            final World world = loc.getWorld();
            final Vector dir = caster.getLocation().getDirection().normalize();

            final double pitch = (loc.getPitch() + 90.0F) * 0.017453292F;
            final double yaw = -loc.getYaw() * 0.017453292F;

            final double increment = (2 * Math.PI) / circlePoints;
            double circlePointOffset = 0;

            int counter = 0;

            while (counter < 50) {
                if (world == null)
                    return;

                // Particle effects
                double finalRadius = radius;
                double finalCirclePointOffset = circlePointOffset;
                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    for (int i = 0; i < circlePoints; i++) {
                        double angle = i * increment + finalCirclePointOffset;
                        double x = finalRadius * Math.cos(angle);
                        double z = finalRadius * Math.sin(angle);

                        Vector vec = new Vector(x, 0, z);
                        MathVectorUtils.rotateAroundAxisX(vec, pitch);
                        MathVectorUtils.rotateAroundAxisY(vec, yaw);
                        locCopy.add(vec);

                        world.spawnParticle(Particle.DUST, locCopy, 0, new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 255, 255), 1));
                        locCopy.subtract(vec);
                    }
                });

                circlePointOffset += increment / 3;
                if (circlePointOffset >= increment) {
                    circlePointOffset = 0;
                }
                locCopy.add(dir);
                radius -= (valuesForSequence.get(sequencePower) != null ? valuesForSequence.get(sequencePower)[0] : 0.25) / 70;

                // Check if hit Entity
                double finalRadius1 = radius;
                scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                    if (!world.getNearbyEntities(locCopy, 5, 5, 5).isEmpty()) {
                        for (Entity entity : world.getNearbyEntities(locCopy, 5, 5, 5)) {
                            Vector v1 = new Vector(
                                    locCopy.getX() + finalRadius1 / 2,
                                    locCopy.getY() + finalRadius1 / 2,
                                    locCopy.getZ() + finalRadius1 / 2
                            );
                            Vector v2 = new Vector(
                                    locCopy.getX() - finalRadius1 / 2,
                                    locCopy.getY() - finalRadius1 / 2,
                                    locCopy.getZ() - finalRadius1 / 2
                            );
                            if (entity.getBoundingBox().overlaps(v1, v2) && entity instanceof Damageable && entity != caster && entity.getType() != EntityType.ARMOR_STAND) {
                                if (valuesForSequence.get(sequencePower) != null && valuesForSequence.get(sequencePower)[2] > 1)
                                    world.createExplosion(entity.getLocation(), (int) (valuesForSequence.get(sequencePower)[2] - 1));
                                ((Damageable) entity).damage(7 * finalMultiplier, caster);
                                return;
                            }
                        }
                    }
                });

                if (locCopy.getBlock().getType().isSolid()) {
                    scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                        if (valuesForSequence.get(sequencePower) != null && valuesForSequence.get(sequencePower)[2] > 0)
                            world.createExplosion(locCopy, (int) (valuesForSequence.get(sequencePower)[2]));
                    });
                    return;
                }

                try {
                    Thread.sleep(50); // Sleep for 1 tick (50ms)
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                counter++;
            }

            scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                if (valuesForSequence.get(sequencePower) != null && valuesForSequence.get(sequencePower)[2] > 0)
                    world.createExplosion(locCopy, (int) (valuesForSequence.get(sequencePower)[2]));
            });
        });
    }

    @Override
    public void useAbility() {
        executeAbility(p.getLocation(), p);
    }

    @Override
    public void onHold() {
        p = pathway.getBeyonder().getPlayer();

        if (pathway.getSequence().getCurrentSequence() > 3)
            return;

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Обрана Послідовність: §8" + sequencePower));
    }

    @Override
    public void leftClick() {
        if (pathway.getSequence().getCurrentSequence() > 3)
            return;

        sequencePower--;
        if (sequencePower < pathway.getSequence().getCurrentSequence())
            sequencePower = 7;
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.GHAST_TEAR, "Повітряний Снаряд", "30", identifier);
    }
}
