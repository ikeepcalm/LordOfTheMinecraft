package dev.ua.ikeepcalm.optional.anchor.tasks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RadiationTask extends BukkitRunnable {

    private final Set<Location> transformedBlocks;
    private final Map<UUID, Long> lastMessageSentTime = new HashMap<>();

    public RadiationTask(Set<Location> transformedBlocks) {
        this.transformedBlocks = transformedBlocks;
    }

    public void sendMessage(Player player, String message) {
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (lastMessageSentTime.containsKey(playerUUID)) {
            long lastSentTime = lastMessageSentTime.get(playerUUID);
            if ((currentTime - lastSentTime) < 40 * 1000) {
                return;
            }
        }
        player.sendMessage(Component.text(message).color(NamedTextColor.RED));
        lastMessageSentTime.put(playerUUID, currentTime);
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
                    sendMessage(player, "Наближаючись до якоря, ви відчуваєте дивне відчуття...");
                }
            }
        }
    }
}
