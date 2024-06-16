package dev.ua.ikeepcalm.listeners;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler
    //Check if Entity that dies is a FakePlayer from the hashMap in the Plugin class and remove him onDeath if he is
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (LordOfTheMinecraft.fakePlayers.containsKey(e.getEntity().getUniqueId())) {
            e.setDeathMessage(null);
            Location loc = e.getEntity().getLocation();
            if (loc.getWorld() != null)
                loc.getWorld().spawnParticle(Particle.CLOUD, loc.clone().subtract(0, 0.25, 0), 100, 0.35, 1, 0.35, 0);
        }
    }
}
