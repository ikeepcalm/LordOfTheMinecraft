package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FogOfWar extends Ability implements Listener {

    private boolean executed = false;
    private Set<UUID> allies = new HashSet<>();
    private Set<UUID> affectedPlayers = new HashSet<>();
    private Location center;
    private final int size = 50;
    private long cooldown = 600L;

    public FogOfWar(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        if (!executed) {
            executed = true;
            center = player.getLocation();
            allies = pathway.getSequence().getSubordinates();

            showBorderParticles();
            manageZone();

        } else {
            player.sendMessage(Component.text("Туман війни ще не готовий!", NamedTextColor.RED));
        }
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.SPLASH_POTION, "Туман Війни", "3000", identifier);
    }

    private void showBorderParticles() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!executed) {
                    this.cancel();
                    return;
                }

                for (int x = -size / 2; x <= size / 2; x++) {
                    for (int z = -size / 2; z <= size / 2; z++) {
                        for (int y = 0; y <= 10; y++) { // Extend particles vertically
                            if (Math.abs(x) == size / 2 || Math.abs(z) == size / 2) {
                                Location particleLocation = center.clone().add(x, y, z);
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (!allies.contains(player.getUniqueId())) {
                                        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.GRAY, 1.0f);
                                        player.spawnParticle(Particle.DUST, particleLocation, 10, 0.5, 0.5, 0.5, 0, dustOptions);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 20); // Run every second
    }


    private void manageZone() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!executed) {
                    this.cancel();
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    Location loc = player.getLocation();
                    if (loc.distance(center) <= (double) size / 4) {
                        continue;
                    }
                    if (!allies.contains(player.getUniqueId())) {
                        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.GRAY, 1.0f);
                        player.spawnParticle(Particle.DUST, loc.clone(), 30, 0.5, 0.5, 0.5, 0, dustOptions);
                    }
                    updateVisibility(player);
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 5);

        new BukkitRunnable() {
            @Override
            public void run() {
                executed = false;
                restoreVisibility();
                center = null;
            }
        }.runTaskLater(LordOfTheMinecraft.instance, cooldown);
    }

    private void updateVisibility(Player player) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (player.equals(other)) continue;

            if (allies.contains(player.getUniqueId())) {
                player.showPlayer(LordOfTheMinecraft.instance, other);
                other.showPlayer(LordOfTheMinecraft.instance, player);
            } else {
                boolean isWithinDistance = player.getLocation().distance(other.getLocation()) <= 10;
                boolean canSee = allies.contains(other.getUniqueId()) && isWithinDistance;
                if (canSee) {
                    player.showPlayer(LordOfTheMinecraft.instance, other);
                    affectedPlayers.remove(player.getUniqueId());
                    affectedPlayers.remove(other.getUniqueId());
                } else {
                    player.hidePlayer(LordOfTheMinecraft.instance, other);
                    affectedPlayers.add(player.getUniqueId());
                    affectedPlayers.add(other.getUniqueId());
                }
            }
        }
    }

    private void restoreVisibility() {
        for (UUID playerId : affectedPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    player.showPlayer(LordOfTheMinecraft.instance, other);
                    other.showPlayer(LordOfTheMinecraft.instance, player);
                }
            }
        }
        affectedPlayers.clear();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (center != null) {
            if (isCrossingBorder(center, event.getFrom(), event.getTo())) {
                player.damage(4.0 * getMultiplier());
                player.setVelocity(player.getLocation().getDirection().multiply(-1));
            }
        }
    }

    private boolean isCrossingBorder(Location center, Location from, Location to) {
        double fromDistanceSquared = center.distanceSquared(from);
        double toDistanceSquared = center.distanceSquared(to);
        double reducedSize = (double) size / 2;
        double radiusSquared = reducedSize * reducedSize;

        return (fromDistanceSquared <= radiusSquared && toDistanceSquared > radiusSquared) ||
               (fromDistanceSquared > radiusSquared && toDistanceSquared <= radiusSquared);
    }

}
