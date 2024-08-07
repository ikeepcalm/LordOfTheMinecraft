package dev.ua.ikeepcalm.listeners;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.BeyonderItemsUtil;
import net.lapismc.afkplus.playerdata.AFKPlusPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class FishingListener implements Listener {

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getCaught() instanceof Item caughtItem) {
            ItemStack item;
            Random random = new Random();
            if (LordOfTheMinecraft.instance.getBeyonderMobsHandler().getRedMoon()) {
                if (random.nextInt(100) > 2)
                    return;

                AFKPlusPlayer player = LordOfTheMinecraft.afkPlus.getPlayer(event.getPlayer().getUniqueId());
                if (player.isAFK()) {
                    List<ItemStack> beyonderItems = BeyonderItemsUtil.returnAllItems();
                    item = beyonderItems.get(random.nextInt(beyonderItems.size()));
                    caughtItem.setItemStack(item);
                } else {
                    Location eyeLocation = event.getPlayer().getEyeLocation();
                    LordOfTheMinecraft.instance.getBeyonderMobsHandler().spawnRandomEntity(event.getPlayer().getLocation(), eyeLocation, event.getPlayer().getWorld());
                }
            }
        }
    }

}
