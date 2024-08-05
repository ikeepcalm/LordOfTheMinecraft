package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class Tracking extends Ability {

    public Tracking(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);

    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;
        Location loc = player.getLocation();
        new BukkitRunnable() {
            int drain = 0;

            @Override
            public void run() {
                applyGlowingEffect(loc, pathway, identifier, player);
                drain++;
                if (drain >= 20) {
                    drain = 0;
                    pathway.getSequence().removeSpirituality(5);
                }
                if (pathway.getBeyonder().getSpirituality() <= 20 || !pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                    cancel();
                }

            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    public void applyGlowingEffect(Location loc, Pathway pathway, int identifier, Player player) {
        for (Entity entity : Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc, 20, 20, 20)) {
            if (entity instanceof LivingEntity && pathway.getSequence().getUsesAbilities()[identifier - 1] && entity != player) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (entity.getWorld().getNearbyEntities(entity.getLocation(), 10, 10, 10).contains(player)) {
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 1));
                }
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player && onlinePlayer.getWorld().equals(loc.getWorld()) &&
                onlinePlayer.getLocation().distance(loc) <= 20) {
                if (!player.canSee(onlinePlayer) || onlinePlayer.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 1));
                    onlinePlayer.getWorld().spawnParticle(org.bukkit.Particle.END_ROD, onlinePlayer.getLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                }
            }
        }
    }


    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.ENDER_EYE, "Відстеження", "5/c", identifier);
    }
}