package dev.ua.ikeepcalm.mystical.pathways.door.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.Beyonder;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.DoorItems;
import dev.ua.ikeepcalm.utils.ErrorLoggerUtil;
import dev.ua.ikeepcalm.utils.MathVectorUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Exile extends Ability {

    public Exile(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    public void executeAbility(Location target, Entity caster, double multiplier) {
        Vector dir = caster.getLocation().getDirection().normalize();
        Location loc = caster.getLocation().add(0, 1.5, 0);

        for (int i = 0; i < 20; i++) {
            if (loc.getBlock().getType().isSolid())
                break;
            loc.add(dir);
        }

        loc.subtract(dir);
        loc.add(0, .4, 0);

        if (loc.getWorld() == null)
            return;

        Random random = new Random();
        Location[] locations = new Location[12];

        for (int i = 0; i < locations.length; i++) {
            locations[i] = loc.clone().add(random.nextInt(-4, 4), random.nextInt(-4, 4), random.nextInt(-4, 4));
            locations[i].setPitch(random.nextInt(45));
            locations[i].setYaw(random.nextInt(360));
        }

        new BukkitRunnable() {
            int counter = 0;
            int npcCounter = 20 * 5;

            @Override
            public void run() {
                try {

                    counter++;
                    if (counter >= 20 * 60) {
                        cancel();
                        return;
                    }

                    npcCounter--;

                    for (Location location : locations) {
                        drawDoor(location);
                    }

                    Bukkit.getScheduler().runTask(LordOfTheMinecraft.instance, () -> {
                        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 5, 5, 5)) {
                            if (entity == caster)
                                continue;

                            if (!(entity instanceof Mob) && !(entity instanceof Player))
                                continue;

                            handleEntityTeleport(entity, caster, random);
                        }
                    });

                    if (!pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                        cancel();
                    }
                } catch (Exception e) {
                    ErrorLoggerUtil.logAbility(e, "Exile");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    private void handleEntityTeleport(Entity entity, Entity caster, Random random) {
        if (entity instanceof Player player && LordOfTheMinecraft.beyonders.containsKey(player.getUniqueId())) {
            Beyonder beyonder = LordOfTheMinecraft.beyonders.get(player.getUniqueId());
            if (random.nextInt(Math.round(160f / beyonder.getPathway().getSequence().getCurrentSequence())) == 0) {
                Location startLoc = player.getLocation();
                Location teleportLoc = new Location(startLoc.getWorld(), 1000, 10000, 1000);
                teleportEntity(player, startLoc, teleportLoc, Math.pow(beyonder.getPathway().getSequence().getCurrentSequence(), 1.2) * 20 * 1.5);
            }
        } else if (random.nextInt(15) == 0) {
            Location startLoc = entity.getLocation();
            Location teleportLoc = new Location(startLoc.getWorld(), random.nextInt(1000, 2000), 10000, random.nextInt(1000, 2000));
            teleportEntity(entity, startLoc, teleportLoc, 20 * 30);
        }
    }

    private void teleportEntity(Entity entity, Location startLoc, Location teleportLoc, double duration) {
        new BukkitRunnable() {
            int c = 0;

            @Override
            public void run() {
                if (c >= duration) {
                    entity.teleport(startLoc);
                    cancel();
                    return;
                }
                c++;
                entity.teleport(teleportLoc);
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        pathway.getSequence().getUsesAbilities()[identifier - 1] = true;
        executeAbility(p.getLocation(), p, 1);
    }

    @Override
    public ItemStack getItem() {
        return DoorItems.createItem(Material.CRIMSON_DOOR, "Вигнання", "450", identifier);
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

    private void drawDoor(Location loc) {
        if (loc.getWorld() == null)
            return;

        double space = 0.24;
        double defX = loc.getX() - (space * shape[0].length / 2) + space;
        double x = defX;
        double y = loc.clone().getY() + 2.8;
        double fire = -((loc.getYaw() + 180) / 60);
        final double pitch = -loc.getPitch();
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
                    MathVectorUtils.rotateAroundAxisX(v, pitch);
                    v2.setY(0).multiply(-0.5);

                    loc.add(v);
                    loc.add(v2);

                    Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(255, 251, 0), .5f);
                    if (j == 1)
                        dust = new Particle.DustOptions(Color.fromBGR(150, 12, 171), .6f);
                    loc.getWorld().spawnParticle(Particle.DUST, loc, 1, .05, .05, .05, dust);

                    loc.subtract(v2);
                    loc.subtract(v);
                }
                x += space;
            }
            y -= space;
            x = defX;
        }
    }
}
