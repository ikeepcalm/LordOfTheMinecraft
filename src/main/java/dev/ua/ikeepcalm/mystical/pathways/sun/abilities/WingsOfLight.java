package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.MathVectorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class WingsOfLight extends Ability {

    public WingsOfLight(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    boolean x = true;
    boolean o = false;

    private final boolean[][] shape = {
            {o, o, o, x, o, o, o, o, o, o, o, o, x, o, o, o},
            {o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, o},
            {o, o, x, x, o, o, o, o, o, o, o, o, x, x, o, o},
            {o, x, x, x, x, o, o, o, o, o, o, x, x, x, x, o},
            {o, x, x, x, x, o, o, o, o, o, o, x, x, x, x, o},
            {o, o, x, x, x, x, o, o, o, o, x, x, x, x, o, o},
            {o, o, x, x, x, x, x, o, o, x, x, x, x, x, o, o},
            {o, o, o, x, x, x, x, x, x, x, x, x, x, o, o, o},
            {o, o, o, o, o, x, x, x, x, x, x, o, o, o, o, o},
            {o, o, o, o, o, x, x, x, x, x, x, o, o, o, o, o},
            {o, o, o, o, x, x, x, o, o, x, x, x, o, o, o, o},
            {o, o, o, x, x, x, x, o, o, x, x, x, x, o, o, o},
            {o, o, o, x, x, x, o, o, o, o, x, x, x, o, o, o},
            {o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o},
            {o, o, o, x, x, o, o, o, o, o, o, x, x, o, o, o},
    };

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();
        if (pathway.getSequence().getCurrentSequence() > 1) {
            player.setVelocity(new Vector(0, 1, 0));
            pathway.getSequence().getUsesAbilities()[identifier - 1] = true;
            new BukkitRunnable() {
                int counter = 0;
                int counterVelocity = 0;

                @Override
                public void run() {
                    counter++;

                    if (counter >= 20) {
                        pathway.getBeyonder().setSpirituality(pathway.getBeyonder().getSpirituality() - 500);
                        counter = 0;
                    }

                    if (counterVelocity < 4)
                        counterVelocity++;
                    else if (counterVelocity == 4) {
                        player.setVelocity(new Vector(0, 0, 0));
                        counterVelocity = 5;
                    }

                    Location loc = player.getLocation();
                    drawParticles(loc);
                    player.setGravity(false);


                    if (pathway.getBeyonder().getSpirituality() <= 500 || !pathway.getBeyonder().online) {
                        player.setGravity(true);
                        pathway.getSequence().getUsesAbilities()[identifier - 1] = false;
                        cancel();
                    }

                    if (!pathway.getSequence().getUsesAbilities()[identifier - 1]) {
                        player.setGravity(true);
                        cancel();
                    }
                }
            }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
            return;
        }

        player.setFallDistance(0);
        player.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(1.75));
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (counter > 20) {
                    cancel();
                    return;
                }

                counter++;
                drawParticles(player.getLocation());
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public void leftClick() {
        player = pathway.getBeyonder().getPlayer();
        if (pathway.getSequence().getCurrentSequence() > 1)
            return;

        player.setVelocity(new Vector(0, 0, 0));
        player.setFallDistance(0);
        player.setGravity(!player.hasGravity());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.hasGravity()) {
                    cancel();
                    return;
                }

                drawParticles(player.getLocation());
                player.setVelocity(new Vector(0, 0, 0));
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.FEATHER, "Крила Світла", "500/c", identifier);
    }

    private void drawParticles(Location loc) {
        double space = 0.24;
        double defX = loc.getX() - (space * shape[0].length / 2) + space;
        double x = defX;
        double y = loc.clone().getY() + 2.8;
        double fire = -((loc.getYaw() + 180) / 60);
        fire += (loc.getYaw() < -180 ? 3.25 : 2.985);

        for (boolean[] booleans : shape) {
            for (boolean aBoolean : booleans) {
                if (aBoolean) {

                    Location target = loc.clone();
                    target.setX(x);
                    target.setY(y);

                    Vector v = target.toVector().subtract(loc.toVector());
                    Vector v2 = MathVectorUtils.getBackVector(loc);
                    v = MathVectorUtils.rotateAroundAxisY(v, fire);
                    v2.setY(0).multiply(-0.5);

                    loc.add(v);
                    loc.add(v2);
                    for (int k = 0; k < 3; k++)
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0.02, 0.02, 0.02, 0);
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