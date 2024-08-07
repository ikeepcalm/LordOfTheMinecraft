package dev.ua.ikeepcalm.optional.anchor.tasks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class RadiationTask extends BukkitRunnable {

    private final Set<Location> transformedBlocks;

    public RadiationTask(Set<Location> transformedBlocks) {
        this.transformedBlocks = transformedBlocks;
    }

    @Override
    public void run() {
        for (Location anchorLocation : transformedBlocks) {
            World world = anchorLocation.getWorld();
            if (world == null) continue;

            for (Player player : world.getPlayers()) {
                int radius = 5;
                if (player.getLocation().distance(anchorLocation) <= radius) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 1));
                    player.sendMessage(Component.text("Ти відчуваєш дивне поколювання на шкірі...").color(NamedTextColor.RED));
                }
            }
        }
    }
}
