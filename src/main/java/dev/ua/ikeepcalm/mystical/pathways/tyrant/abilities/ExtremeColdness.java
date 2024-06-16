package dev.ua.ikeepcalm.mystical.pathways.tyrant.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.pathways.tyrant.TyrantItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class ExtremeColdness extends Ability {

    private final HashMap<Entity, Boolean> inUse;

    public ExtremeColdness(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        p = pathway.getBeyonder().getPlayer();
        inUse = new HashMap<>();
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        executeAbility(p.getLocation(), p, getMultiplier());
    }

    public void executeAbility(Location loc, Entity caster, double multiplier) {
        if (inUse.get(caster) != null && inUse.get(caster)) {
            inUse.replace(caster, false);
            return;
        }

        if (inUse.get(caster) == null) {
            inUse.put(caster, true);
        } else {
            inUse.replace(caster, true);
        }

        new BukkitRunnable() {

            UUID uuid = UUID.randomUUID();

            @Override
            public void run() {
                GeneralPurposeUtil.getNearbyBlocksInSphere(caster.getLocation(), 20, false, true, false).forEach(block -> {
                    if (block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE)
                        return;
                    if (block.getType().getHardness() <= .4f || block.getType() == Material.WATER) {
                        logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                        block.setType(Material.ICE);
                    } else {
                        logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                        block.setType(Material.PACKED_ICE);
                    }
                    GeneralPurposeUtil.drawDustsForNearbyPlayers(block.getLocation(), 1, 0, 0.5, 0, new Particle.DustOptions(Color.fromRGB(88, 200, 237), 1f));
                });

                caster.getWorld().getNearbyEntities(caster.getLocation(), 60, 60, 60).forEach(entity -> {
                    if (entity instanceof LivingEntity livingEntity && entity != caster) {
                        livingEntity.damage(8 * multiplier);
                        for (double x = -livingEntity.getWidth() + .5; x < livingEntity.getWidth() + .5; x++) {
                            for (double z = -livingEntity.getWidth() + .5; z < livingEntity.getWidth() + .5; z++) {
                                for (int i = 0; i < livingEntity.getHeight(); i++) {
                                    livingEntity.getLocation().add(x, i, z).getBlock().setType(Material.PACKED_ICE);
                                }
                            }
                        }
                    }

                });

                if (!caster.isValid() || inUse.get(caster) == null || !inUse.get(caster)) {
                    rollbackChanges(uuid);
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 8);
    }

    @Override
    public ItemStack getItem() {
        return TyrantItems.createItem(Material.BLUE_ICE, "Крижаний Катаклізм", "2000", identifier);
    }
}
