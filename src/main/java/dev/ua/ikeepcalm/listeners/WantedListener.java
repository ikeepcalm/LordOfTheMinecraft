package dev.ua.ikeepcalm.listeners;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
            Bukkit.broadcastMessage("§7========================================");
            Bukkit.broadcastMessage("§c" + name + " знаходиться у розшуку!");
            Bukkit.broadcastMessage("§cЙого останні координати: " + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
            Bukkit.broadcastMessage("§7========================================");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        List<String> excList = LordOfTheMinecraft.instance.getExcConfig().getStringList("exc");
        if (excList.contains(name)) {
            Bukkit.broadcastMessage("§7========================================");
            Bukkit.broadcastMessage("§c" + name + " знаходиться у розшуку!");
            int x = p.getLocation().getBlockX() + (int) (Math.random() * 100);
            int y = p.getLocation().getBlockY() + (int) (Math.random() * 100);
            int z = p.getLocation().getBlockZ() + (int) (Math.random() * 100);
            Bukkit.broadcastMessage("Приблизні координати його зникнення: " + x + " " + y + " " + z);
            Bukkit.broadcastMessage("§7========================================");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        String name = p.getName();
        List<String> excList = LordOfTheMinecraft.instance.getExcConfig().getStringList("exc");
        if (excList.contains(name)) {
            p.teleportAsync(new Location(p.getWorld(), -544, 89, 1082));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();
        String name = p.getName();
        List<String> excList = LordOfTheMinecraft.instance.getExcConfig().getStringList("exc");
        Location firstCorner = new Location(p.getWorld(), -534, 110, 1091);
        Location secondCorner = new Location(p.getWorld(), -553, 86, 1057);
        if (excList.contains(name)) {
            if (isBlockInRegion(event.getBlock().getLocation(), firstCorner, secondCorner)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        String name = p.getName();
        List<String> excList = LordOfTheMinecraft.instance.getExcConfig().getStringList("exc");
        Location firstCorner = new Location(p.getWorld(), -534, 110, 1091);
        Location secondCorner = new Location(p.getWorld(), -553, 86, 1057);
        if (excList.contains(name)) {
            if (event.getClickedBlock() != null) {
                if (isBlockInRegion(event.getClickedBlock().getLocation(), firstCorner, secondCorner)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isBlockInRegion(Location blockLoc, Location corner1, Location corner2) {
        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return blockLoc.getX() >= minX && blockLoc.getX() <= maxX &&
               blockLoc.getY() >= minY && blockLoc.getY() <= maxY &&
               blockLoc.getZ() >= minZ && blockLoc.getZ() <= maxZ;
    }

}

