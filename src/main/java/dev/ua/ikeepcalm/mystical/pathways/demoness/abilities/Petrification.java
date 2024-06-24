package dev.ua.ikeepcalm.mystical.pathways.demoness.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.entities.custom.CustomLocation;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.pathways.demoness.DemonessItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Petrification extends Ability {

    private final ArrayList<Entity> cooldownEntities;

    public Petrification(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        cooldownEntities = new ArrayList<>();
    }

    public void executeAbility(Location targetLoc, Entity caster, double multiplier) {
        Vector dir = caster.getLocation().getDirection().normalize();
        Location loc = caster.getLocation().add(0, 1.5, 0);
        if (loc.getWorld() == null)
            return;

        LivingEntity target = null;

        outerloop:
        for (int i = 0; i < 50; i++) {
            for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                if ((!(entity instanceof Mob) && !(entity instanceof Player)) || entity == caster)
                    continue;
                target = (LivingEntity) entity;
                break outerloop;
            }

            loc.add(dir);

            if (loc.getBlock().getType().isSolid()) {
                petrifyLoc(loc.clone().subtract(dir));
                break;
            }
        }

        if (target == null) {
            return;
        }

        LivingEntity finalTarget = target;

        if (cooldownEntities.contains(finalTarget)) {
            caster.sendMessage("§cВи ще не можете повторно скам'яніти цю сутність!");
            return;
        }
        cooldownEntities.add(finalTarget);


        HashMap<Block, Material> blocks = new HashMap<>();
        final Location eLoc = finalTarget.getLocation();

        new BukkitRunnable() {
            int counter = 120 * 20;
            boolean cancelled = false;
            UUID uuid = UUID.randomUUID();

            @Override
            public void run() {
                counter--;

                if (counter <= 0) {
                    cancelled = true;
                }

                if (!finalTarget.isValid()) {
                    cooldownEntities.remove(finalTarget);
                    cancelled = true;
                }

                if (eLoc.distance(finalTarget.getLocation()) > 3) {
                    cancelled = true;
                }

                if (cancelled) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            cooldownEntities.remove(finalTarget);
                        }
                    }.runTaskLater(LordOfTheMinecraft.instance, 20 * 20);
                    cancel();
                    return;
                }

                for (Map.Entry<Block, Material> entry : blocks.entrySet()) {
                    entry.getKey().setType(entry.getValue());
                }

                blocks.clear();

                finalTarget.setVelocity(new Vector(0, 0, 0));

                if (counter % 10 == 0) {
                    Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(100, 100, 100), 2f);
                    finalTarget.getWorld().spawnParticle(Particle.DUST, finalTarget.getEyeLocation().clone().subtract(0, .5, 0), 50, .5, 1, .5, dust);
                }

                if (counter % 20 == 0) {
                    finalTarget.damage(25, p);
                }

                finalTarget.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 120, 120));

                for (double x = -finalTarget.getWidth() + .5; x < finalTarget.getWidth() + .5; x++) {
                    for (double z = -finalTarget.getWidth() + .5; z < finalTarget.getWidth() + .5; z++) {
                        for (int i = 0; i < finalTarget.getHeight(); i++) {
                            blocks.put(finalTarget.getLocation().clone().add(x, i, z).getBlock(), finalTarget.getLocation().clone().add(0, i, 0).getBlock().getType());
                        }
                    }
                }

                for (Block block : blocks.keySet()) {
                    logBlockBreak(uuid, new CustomLocation(block.getLocation()));
                    block.setType(Material.STONE);
                }
            }

            @Override
            public void cancel() {
                super.cancel();
                rollbackChanges(uuid);
            }

        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        executeAbility(p.getEyeLocation(), p, getMultiplier());
    }

    private void petrifyLoc(Location loc) {

        ArrayList<Block> blocks = GeneralPurposeUtil.getNearbyBlocksInSphere(loc, 6, false, true, true);
        for (Block block : blocks) {
            if (block.getType().getHardness() < 0 || !block.getType().isSolid())
                continue;
            block.setType(Material.STONE);
        }
    }

    @Override
    public ItemStack getItem() {
        return DemonessItems.createItem(Material.ENDER_EYE, "Скам'яніння", "1500", identifier);
    }
}
