package dev.ua.ikeepcalm.optional.nicknames;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class NicknameListener implements Listener {


    @EventHandler
    public void onShiftClick(PlayerInteractAtEntityEvent event) {
        if (!event.getPlayer().isSneaking()) {
            return;
        }

        if (event.getRightClicked() instanceof Player player) {
            if (LordOfTheMinecraft.disguises.contains(player.getUniqueId())) {
                PlayerDisguise disguise = (PlayerDisguise) DisguiseAPI.getDisguise(player);
                if (disguise != null) {
                    event.getPlayer().sendActionBar(Component.text(disguise.getName()).color(TextColor.color(0xFFF3FF)));
                } else {
                    event.getPlayer().sendActionBar(Component.text("§cВи не можете побачити нікнейм цього гравця!"));
                }
            } else {
                event.getPlayer().sendActionBar(Component.text(player.getName()).color(TextColor.color(0xFFF3FF)));
            }
        }
    }
}
