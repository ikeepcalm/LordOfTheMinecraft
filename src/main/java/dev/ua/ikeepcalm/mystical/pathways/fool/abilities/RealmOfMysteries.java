package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class RealmOfMysteries extends Ability implements Listener {

    private int radius;

    private ArrayList<Entity> concealedEntities;

    public RealmOfMysteries(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);

        items.addToSequenceItems(identifier - 1, sequence);
        radius = 12;

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
        concealedEntities = new ArrayList<>();
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;
        Location loc = p.getLocation();

        concealedEntities = new ArrayList<>();
        concealedEntities.addAll(p.getNearbyEntities(radius, radius, radius));
        concealedEntities.add(p);

        LordOfTheMinecraft.instance.addToConcealedEntities(concealedEntities);

        Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(0, 0, 0), 85f);

        new BukkitRunnable() {
            final int currentRadius = radius;
            final long max = Math.max(20, Math.min(55, Math.round(currentRadius * 2.5)));

            @Override
            public void run() {
                GeneralPurposeUtil.drawSphere(loc, currentRadius, (int) max, dust, Material.BARRIER, .2);

                if (loc.getWorld() == null)
                    return;

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                    if (!concealedEntities.contains(entity)) {
                        Vector dir = new Vector(0, .5, 1);
                        Location entLoc = entity.getLocation();
                        while (entLoc.distance(loc) < (radius + 5) || entLoc.getBlock().getType().isSolid()) {
                            entLoc.add(dir);
                        }
                        entity.teleport(entLoc);
                    }
                }

                for (Entity entity : concealedEntities) {
                    if (!(entity instanceof LivingEntity livingEntity))
                        continue;
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 12, 1, false, false));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 220, 1, false, false));
                }

                if (!pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                    GeneralPurposeUtil.drawSphere(loc, currentRadius, (int) max, dust, Material.AIR, .2);
                    LordOfTheMinecraft.instance.removeFromConcealedEntities(concealedEntities);
                    concealedEntities.clear();
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 10);
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.BLACK_DYE, "Царство Таємниць", "20000", identifier);
    }

    @Override
    public void leftClick() {
        p = pathway.getBeyonder().getPlayer();
        radius++;
        if (radius > 30)
            radius = 5;

        p.sendMessage("§5Радіус встановлено на " + radius);
    }


    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!concealedEntities.contains(e.getEntity()))
            return;

        if (!concealedEntities.contains(e.getDamager()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!concealedEntities.contains(e.getPlayer()))
            return;

        for (Player player : e.getRecipients()) {
            if (concealedEntities.contains(player))
                continue;
            e.getRecipients().remove(player);
        }
    }
}
