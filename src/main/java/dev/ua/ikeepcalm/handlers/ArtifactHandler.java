package dev.ua.ikeepcalm.handlers;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ArtifactHandler implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null)
            return;
        if (e.getClickedBlock().getType() != Material.ANVIL)
            return;
        if (LordOfTheMinecraft.instance.getCharacteristic().getCharacteristicInfo(e.getItem())[0] == -1)
            return;

        assert e.getItem() == null;

        e.setCancelled(true);
        e.getPlayer().getInventory().remove(e.getItem());
        ItemStack item = LordOfTheMinecraft.instance.getSealedArtifacts().generateArtifact(LordOfTheMinecraft.instance.getCharacteristic().getCharacteristicInfo(e.getItem())[0], LordOfTheMinecraft.instance.getCharacteristic().getCharacteristicInfo(e.getItem())[1], false);
        if (item == null) {
            e.getClickedBlock().getWorld().createExplosion(e.getClickedBlock().getLocation().add(0, 1, 0), 3);
            return;
        }
        e.getPlayer().getInventory().addItem(item);
    }

}