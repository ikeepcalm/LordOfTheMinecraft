package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Conceptualize extends Ability {

    public Conceptualize(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location targetLoc, Entity caster, double multiplier) {
        Vector dir = caster.getLocation().getDirection().normalize();
        Location loc = caster.getLocation().add(0, 1.5, 0);
        if (loc.getWorld() == null)
            return;

        LivingEntity target = findTargetEntity(loc, dir, caster);

        if (target == null) {
            player.sendMessage("§cСутність не знайдено!");
            return;
        }

        new BukkitRunnable() {
            int counter = 0;
            double timer = 1.0;
            double npcTimer = 20 * 4;

            @Override
            public void run() {
                try {

                    if (!target.isValid() || (!pathway.getSequence().getUsesAbilities()[identifier - 1])) {
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                        cancel();
                        return;
                    }

                    counter++;
                    npcTimer--;
                    target.damage(8, caster);

                    if (counter >= 20) {
                        counter = 0;
                        if (pathway.getBeyonder().getSpirituality() <= Math.pow(110, timer)) {
                            cancel();
                            return;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(LordOfTheMinecraft.instance, () -> pathway.getSequence().removeSpirituality(Math.pow(110, timer)));
                        timer += .08;
                    }

                    for (int i = 0; i < 3; i++) {
                        int j = i;
                        new BukkitRunnable() {
                            final double spiralRadius = 1;
                            double spiral = 0;
                            double height = j * .25;
                            double spiralX;
                            double spiralZ;

                            @Override
                            public void run() {
                                if (!target.isValid() || (!pathway.getSequence().getUsesAbilities()[identifier - 1])) {
                                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                                    cancel();
                                    return;
                                }

                                Location entityLoc = target.getLocation().clone();
                                entityLoc.add(0, 0.75, 0);

                                spiralX = spiralRadius * Math.cos(spiral);
                                spiralZ = spiralRadius * Math.sin(spiral);
                                spiral += 0.05;
                                height += .01;
                                if (height >= 2.5)
                                    height = 0;
                                if (entityLoc.getWorld() != null)
                                    entityLoc.getWorld().spawnParticle(Particle.ENCHANT, spiralX + entityLoc.getX(), height + entityLoc.getY(), spiralZ + entityLoc.getZ(), 1, 0, 0, 0, 0);
                            }
                        }.runTaskTimer(LordOfTheMinecraft.instance, j * 10, 1);
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Conceptualize");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    private LivingEntity findTargetEntity(Location loc, Vector dir, Entity caster) {
        for (int i = 0; i < 50; i++) {
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                if ((!(entity instanceof Mob) && !(entity instanceof Player)) || entity == caster)
                    continue;
                return (LivingEntity) entity;
            }
            loc.add(dir);
        }
        return null;
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;
        executeAbility(player.getEyeLocation(), player, getMultiplier());
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.FIREWORK_STAR, "Астральне Руннування", "збільшується з часом", identifier);
    }
}
