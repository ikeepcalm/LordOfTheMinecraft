package dev.ua.ikeepcalm.listeners;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
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

        Player p = e.getPlayer();
        if (!LordOfTheMinecraft.beyonders.containsKey(p.getUniqueId())) {
            p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
            p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE).getDefaultValue());
            p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).getDefaultValue());
        }
    }

    @EventHandler
    public void onRampagerDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if (!entity.getMetadata("pathway").isEmpty()) {
            entity.getWorld().spawnParticle(Particle.CLOUD, entity.getLocation().clone().add(0, 0.5, 0), 100, 0.35, 1, 0.35, 0);
            String pathway = entity.getMetadata("pathway").getFirst().asString();
            String sequence = entity.getMetadata("sequence").getFirst().asString();
            entity.getWorld().dropItem(entity.getLocation(), LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(GeneralPurposeUtil.parseInt(sequence), pathway, "§a"));
        }
    }
}
