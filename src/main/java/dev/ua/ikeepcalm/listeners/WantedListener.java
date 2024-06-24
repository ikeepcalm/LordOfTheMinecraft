package dev.ua.ikeepcalm.listeners;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class WantedListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        List<String> excList = LordOfTheMinecraft.instance.getExcConfig().getStringList("exc");
        if (excList.contains(name)) {
            Bukkit.broadcastMessage("========================================");
            Bukkit.broadcastMessage("§c" + name + " знаходиться у розшуку!");
            Bukkit.broadcastMessage("§cЙого останні координати: " + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
            Bukkit.broadcastMessage("§cЖивим або мертвим!");
            Bukkit.broadcastMessage("========================================");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        List<String> excList = LordOfTheMinecraft.instance.getExcConfig().getStringList("exc");
        if (excList.contains(name)) {
            Bukkit.broadcastMessage("========================================");
            Bukkit.broadcastMessage("§c" + name + " знаходиться у розшуку!");
            int x = p.getLocation().getBlockX() + (int) (Math.random() * 100);
            int y = p.getLocation().getBlockY() + (int) (Math.random() * 100);
            int z = p.getLocation().getBlockZ() + (int) (Math.random() * 100);
            Bukkit.broadcastMessage("Приблизні координати його зникнення: " + p.getLocation().getBlockX()  + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
            Bukkit.broadcastMessage("§cЖивим або мертвим!");
            Bukkit.broadcastMessage("========================================");
        }
    }

}

