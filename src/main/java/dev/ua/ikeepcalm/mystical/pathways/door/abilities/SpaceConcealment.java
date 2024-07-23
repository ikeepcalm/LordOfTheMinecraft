package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import dev.ua.ikeepcalm.utils.MathVectorUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class SpaceConcealment extends Ability implements Listener {

    ArrayList<Entity> concealedEntities;
    private int radiusAdjust;

    private boolean stopped;

    public SpaceConcealment(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);

        radiusAdjust = 10;
        stopped = false;

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        player = pathway.getBeyonder().getPlayer();
        if (player == null) {
            stopped = true;
            return;
        }

        if (event.getPlayer() != null) {
            if (event.getPlayer().getName().equals(player.getName())) {
                stopped = true;
            }
        }
    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent e) {
        player = pathway.getBeyonder().getPlayer();

        if (e.getPlayer() != player)
            return;

        if (!e.isSneaking())
            return;

        stopped = true;
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        stopped = false;

        getPathway().getSequence().getUsesAbilities()[identifier - 1] = true;

        Location loc = player.getLocation().clone();
        Random random = new Random();
        Location doorLoc = loc.clone();
        doorLoc.setPitch(0);
        doorLoc.setYaw(random.nextInt(4) * 90);

        concealedEntities = new ArrayList<>(player.getNearbyEntities(radiusAdjust, radiusAdjust, radiusAdjust));
        concealedEntities.add(player);

        if (loc.getWorld() == null)
            return;

        int radius = radiusAdjust;

        new BukkitRunnable() {
            boolean doorInit = false;
            int counter = 0;

            @Override
            public void run() {
                try {
                    if (player == null) {
                        stopped = true;
                    }

                    if (counter >= 8) {
                        updateConcealedEntities(loc, radius);
                        counter = 0;
                    }
                    counter++;

                    for (Entity entity : concealedEntities) {
                        if (entity instanceof Player concealedPlayer) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.hidePlayer(LordOfTheMinecraft.instance, concealedPlayer);
                            }
                        }
                    }

                    drawSquare(loc, Material.BARRIER, radius, player, false);

                    if (!doorInit) {
                        initializeDoorLocation(random, loc, doorLoc, radius);
                        doorInit = true;
                    }

                    drawDoor(doorLoc, player);

                    if (doorLoc.getWorld() == null)
                        return;

                    teleportEntities(doorLoc);

                    if (stopped) {
                        drawSquare(loc, Material.AIR, radius, player, false);
                        getPathway().getSequence().getUsesAbilities()[identifier - 1] = false;
                        cancel();
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Space Concealment");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    private void updateConcealedEntities(Location loc, int radius) {
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (!concealedEntities.contains(entity))
                concealedEntities.add(entity);
        }

        if (!concealedEntities.isEmpty()) {
            for (Entity entity : concealedEntities) {
                if (!loc.getWorld().getNearbyEntities(loc, radius, radius, radius).contains(entity)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (entity instanceof Player concealedPlayer)
                            player.showPlayer(LordOfTheMinecraft.instance, concealedPlayer);
                    }
                }
            }
        }

        concealedEntities.removeIf(entity -> !loc.getWorld().getNearbyEntities(loc, radius, radius, radius).contains(entity));
    }

    private void initializeDoorLocation(Random random, Location loc, Location doorLoc, int radius) {
        for (int i = 0; i < 200; i++) {
            doorLoc.setX(random.nextDouble(loc.getX() - radius + 1, loc.getX() + radius - 1));
            doorLoc.setY(loc.getY());
            doorLoc.setZ(random.nextDouble(loc.getZ() - radius + 1, loc.getZ() + radius - 1));

            if (!doorLoc.getBlock().getType().isSolid())
                break;
        }

        for (int i = 0; i < radius * 20; i++) {
            if (doorLoc.getBlock().getType() == Material.BARRIER)
                break;

            switch ((int) doorLoc.getYaw()) {
                case 0, 360 -> doorLoc.add(0, 0, .25);
                case 90 -> doorLoc.add(-.25, 0, 0);
                case 180 -> doorLoc.add(0, 0, -.25);
                case 270 -> doorLoc.add(.25, 0, 0);
            }
        }
    }

    private void teleportEntities(Location doorLoc) {
        for (Entity entity : doorLoc.getWorld().getNearbyEntities(doorLoc, 1, 1, 1)) {

            int x2 = 0;
            int z2 = 0;

            switch ((int) doorLoc.getYaw()) {
                case 0, 360 -> z2 = 1;
                case 90 -> x2 = -1;
                case 180 -> z2 = -1;
                case 270 -> x2 = 1;
            }

            if (!concealedEntities.contains(entity)) {
                x2 *= -1;
                z2 *= -1;
            }

            for (int i = 4; i < 100; i++) {
                Location tempLoc = doorLoc.clone();

                tempLoc.add(x2 * i, 0, z2 * i);

                if (tempLoc.getBlock().getType().isSolid())
                    continue;

                entity.teleport(tempLoc);
                break;
            }
        }
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.IRON_DOOR, "Просторове Сховище", "950", identifier);
    }

    @Override
    public void leftClick() {
        player = pathway.getBeyonder().getPlayer();

        radiusAdjust++;

        if (radiusAdjust > 15)
            radiusAdjust = 4;

        player.sendMessage("§5Радіус встановлено на " + radiusAdjust);
    }

    final int o = 0;
    final int x = 1;
    final int y = 2;

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
    };

    private void drawDoor(Location loc, Player player) {

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
                    if (this.player.getInventory().getItemInMainHand().isSimilar(getItem()))
                        player.spawnParticle(Particle.DUST, loc, 3, .05, .05, .05, dust);

                    loc.subtract(v2);
                    loc.subtract(v);
                }
                x += space;
            }
            y -= space;
            x = defX;
        }
    }

    @SuppressWarnings("all")
    private void drawSquare(Location location, Material material, int radius, Player player, boolean npc) {
        for (int y = -radius; y < radius; y++) {
            for (int x = -radius; x < radius; x++) {
                for (int z = -radius; z < radius; z++) {
                    if (z == -radius || z == radius - 1 || y == -radius || y == radius - 1 || x == -radius || x == radius - 1) {
                        Block block = location.clone().add(x, y, z).getBlock();
                        if (!block.getType().isSolid() || block.getType() == Material.BARRIER) {
                            block.setType(material);
                            if (!npc && this.player.getInventory().getItemInMainHand().isSimilar(getItem()))
                                this.player.spawnParticle(Particle.WITCH, block.getLocation(), 2, 0, 0, 0, 0);
                            else if ((new Random().nextInt(4) == 0) && npc)
                                GeneralPurposeUtil.drawParticlesForNearbyPlayers(Particle.WITCH, block.getLocation(), 1, 0, 0, 0, 0);
                        }
                    }
                }
            }
        }
    }
}
