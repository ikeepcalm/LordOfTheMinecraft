package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import dev.ua.ikeepcalm.utils.MathVectorUtils;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TravelersDoor extends Ability implements Listener {

    private final HashMap<Player, GameMode> teleportedPlayers;

    public TravelersDoor(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
        teleportedPlayers = new HashMap<>();
    }

    private boolean isTeleporting;
    private GameMode prevGameMode;

    private boolean isTeleportingToCoordinates;

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        if (isTeleportingToCoordinates)
            isTeleportingToCoordinates = false;

        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;

        Vector dir = p.getEyeLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();

        for (int i = 0; i < 5; i++) {
            if (loc.getBlock().getType().isSolid())
                break;
            loc.add(dir);
        }

        loc.subtract(dir);
        loc.add(0, .4, 0);

        if (loc.getWorld() == null)
            return;

        boolean teleportToCoordinates = p.isSneaking();
        if (!teleportToCoordinates) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    drawDoor(loc);

                    if (!pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                        cancel();
                        return;
                    }

                    for (Entity entity : loc.getWorld().getNearbyEntities(loc, .5, .5, .5)) {
                        if (!(entity instanceof Player player))
                            continue;

                        if (player != p) {
                            if (teleportedPlayers.containsKey(player))
                                continue;
                            GameMode prevGameModeTeleport = player.getGameMode();
                            teleportedPlayers.put(player, prevGameModeTeleport);
                            player.setGameMode(GameMode.SPECTATOR);

                            new BukkitRunnable() {
                                int counter = 0;

                                @Override
                                public void run() {
                                    counter++;
                                    if (counter >= 20 * 30 && !isTeleporting) {
                                        cancel();
                                        player.setGameMode(teleportedPlayers.get(player));
                                        teleportedPlayers.remove(player);
                                        return;
                                    }

                                    if (isTeleporting)
                                        cancel();
                                }
                            }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
                            continue;
                        }

                        prevGameMode = p.getGameMode();
                        p.setGameMode(GameMode.SPECTATOR);
                        isTeleporting = true;
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;

                        new BukkitRunnable() {
                            int counter = 0;

                            @Override
                            public void run() {
                                if (!isTeleporting)
                                    cancel();
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§bLeft Click to travel here"));

                                for (Player teleportedPlayer : teleportedPlayers.keySet()) {
                                    teleportedPlayer.teleport(p.getLocation());
                                }

                                if (counter >= 20 * 60 * 2) {
                                    stopTeleporting();
                                    cancel();
                                    return;
                                }

                                counter++;
                            }
                        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);

                        cancel();
                    }
                }
            }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
        } else {
            p.sendMessage("§bType in the coordinates you want to travel to");
            isTeleportingToCoordinates = true;
        }
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        p = pathway.getBeyonder().getPlayer();

        if (p == null || !p.isValid())
            return;

        if (!isTeleportingToCoordinates)
            return;


        isTeleportingToCoordinates = false;
        e.setCancelled(true);

        Vector dir = p.getEyeLocation().getDirection().normalize();
        Location loc = p.getEyeLocation();

        for (int i = 0; i < 5; i++) {
            if (loc.getBlock().getType().isSolid())
                break;
            loc.add(dir);
        }

        loc.subtract(dir);
        loc.add(0, .4, 0);

        if (loc.getWorld() == null)
            return;

        String[] args = e.getMessage().split(" ");
        if (args.length < 3) {
            pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
            p.sendMessage("§cCoordinates are not valid");
            return;
        }

        final boolean[] areValid = new boolean[1];
        areValid[0] = true;
        Arrays.stream(args).forEach(s -> {
            if (!GeneralPurposeUtil.isInteger(s))
                areValid[0] = false;
        });

        if (!areValid[0]) {
            pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
            p.sendMessage("§cCoordinates are not valid");
            return;
        }

        int x = GeneralPurposeUtil.parseInt(args[0]);
        int y = GeneralPurposeUtil.parseInt(args[1]);
        int z = GeneralPurposeUtil.parseInt(args[2]);
        Location teleportLoc = new Location(loc.getWorld(), x, y, z);

        new BukkitRunnable() {
            int counter = 20 * 60 * 2;

            @Override
            public void run() {
                drawDoor(loc);

                counter--;

                if (!pathway.getSequence().getUsesAbilities()[identifier - 1] || counter <= 0) {
                    pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                    isTeleportingToCoordinates = false;
                    cancel();
                    return;
                }

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, .5, .5, .5)) {
                    entity.teleport(teleportLoc);

                    if (entity == p) {
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                        cancel();
                    }
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.WARPED_DOOR, "Travelers Door", "75", identifier, 8, pathway.getBeyonder().getPlayer().getName());
    }

    int o = 0;
    int x = 1;
    int y = 2;

    private final int[][] shape = {
            {o, o, o, o, o, o, o, o, o, o, o, o, o, o, o, o},
            {o, o, o, o, o, o, o, x, x, o, o, o, o, o, o, o},
            {o, o, o, o, o, x, x, x, x, x, x, o, o, o, o, o},
            {o, o, o, o, x, x, x, y, y, x, x, x, o, o, o, o},
            {o, o, o, x, x, x, y, y, y, y, x, x, x, o, o, o},
            {o, o, o, x, x, y, y, y, y, y, y, x, x, o, o, o},
            {o, o, o, x, x, y, y, y, y, y, y, x, x, o, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
            {o, o, x, x, y, y, y, y, y, y, y, y, x, x, o, o},
    };

    private void stopTeleporting() {
        if (!isTeleporting)
            return;
        p = pathway.getBeyonder().getPlayer();
        isTeleporting = false;

        p.spawnParticle(Particle.SPELL_WITCH, p.getEyeLocation().subtract(0, .5, 0), 75, .75, .75, .75, 0);

        for (Map.Entry<Player, GameMode> entry : teleportedPlayers.entrySet()) {
            entry.getKey().setGameMode(entry.getValue());
            Bukkit.broadcastMessage(entry.getKey() + " -- " + entry.getValue());
        }

        teleportedPlayers.clear();

        p.setGameMode(prevGameMode);
    }

    private void drawDoor(Location loc) {

        if (loc.getWorld() == null)
            return;

        double space = 0.24;
        double defX = loc.getX() - (space * shape[0].length / 2) + space;
        double x = defX;
        double y = loc.clone().getY() + 2.8;
        double fire = -((loc.getYaw() + 180) / 60);
        fire += (loc.getYaw() < -180 ? 3.25 : 2.985);

        for (int[] i : shape) {
            for (int j : i) {
                if (j != 0) {

                    Location target = loc.clone();
                    target.setX(x);
                    target.setY(y);

                    Vector v = target.toVector().subtract(loc.toVector());
                    Vector v2 = MathVectorUtils.getBackVector(loc);
                    v = MathVectorUtils.rotateAroundAxisY(v, fire);
                    v2.setY(0).multiply(-0.5);

                    loc.add(v);
                    loc.add(v2);

                    Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(255, 251, 0), .4f);
                    if (j == 1)
                        dust = new Particle.DustOptions(Color.fromBGR(150, 12, 171), .55f);
                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 3, .05, .05, .05, dust);

                    loc.subtract(v2);
                    loc.subtract(v);
                }
                x += space;
            }
            y -= space;
            x = defX;
        }
    }

    @EventHandler
    public void onInterAct(PlayerInteractEvent e) {
        p = pathway.getBeyonder().getPlayer();
        if (!isTeleporting || e.getPlayer() != p)
            return;

        stopTeleporting();
    }
}
