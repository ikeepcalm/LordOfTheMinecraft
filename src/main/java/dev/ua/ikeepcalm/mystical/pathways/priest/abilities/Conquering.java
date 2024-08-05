package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Beyonder;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Conquering extends Ability {

    public Conquering(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        List<Player> players = player.getNearbyEntities(10, 10, 10).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .toList();

        BukkitScheduler scheduler = Bukkit.getScheduler();
        Location loc = player.getLocation().add(0, 1, 0);

        new BukkitRunnable() {
            double radius = 2.5;

            @Override
            public void run() {
                try {
                    scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
                        Particle.DustOptions dustRipple = new Particle.DustOptions(Color.fromBGR(20, 20, 255), 1f);
                        radius = radius + 0.75;
                        for (int j = 0; j < 30 * radius; j++) {
                            double x = radius * Math.cos(j);
                            double z = radius * Math.sin(j);
                            int finalJ = j;
                            scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                                Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.DUST, loc.getX() + x, loc.getY(), loc.getZ() + z, 5, 0.2, 1, 0.2, 0, dustRipple);
                                Random rand = new Random();
                                if (finalJ % (rand.nextInt(8) + 1) == 0)
                                    loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX() + x, loc.getY(), loc.getZ() + z, 1, 0.2, 1, 0.2, 0);
                            });
                        }

                        if (radius >= 20) {
                            cancel();
                            scheduler.runTask(LordOfTheMinecraft.instance, () -> pathway.getSequence().getUsesAbilities()[identifier - 1] = false);
                        }
                    });
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Conquering");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);

        players.forEach(p -> {
            if (LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId())) {
                Beyonder beyonder = LordOfTheMinecraft.beyonders.get(p.getUniqueId());
                if (beyonder.getPathway().getSequence().getCurrentSequence() == 1) {
                    p.sendMessage(Component.text("Ви змогли протистояти завоюванню!").color(NamedTextColor.DARK_GREEN));
                    return;
                }
            }
            pathway.getSequence().getSubordinates().add(p.getUniqueId());
            p.sendMessage(Component.text("Ви підкорилися завоюванню! Завойовник веде своє військо...").color(NamedTextColor.DARK_RED));
        });
    }

    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.MAGMA_CREAM, "Завоювання", "30000", identifier);
    }
}
