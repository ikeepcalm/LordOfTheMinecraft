package dev.ua.ikeepcalm.mystical.pathways.sun.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.sun.SunItems;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockIterator;

public class SolarFlare extends Ability {

    private final int[] spirituality;

    public SolarFlare(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);

        spirituality = new int[]{
                15000,
                30000,
                45000,
                60000,
                59000,
                60000,
                65000,
                70000,
        };
    }

    int power = 1;

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        if (pathway.getBeyonder().getSpirituality() < spirituality[power - 1])
            return;

        pathway.getSequence().removeSpirituality(spirituality[power - 1]);

        // Get block player is looking at
        BlockIterator iter = new BlockIterator(player, 300);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        Location loc = lastBlock.getLocation();

        if (loc.getWorld() == null)
            return;

        BukkitScheduler scheduler = Bukkit.getScheduler();
        new BukkitRunnable() {
            int i = 0;
            final int tempPower = Math.min(power, 3);

            @Override
            public void run() {
                try {
                    scheduler.runTaskAsynchronously(LordOfTheMinecraft.instance, () -> {
                        Particle.DustOptions dust = new Particle.DustOptions(Color.fromBGR(255, 251, 0), 50f);
                        GeneralPurposeUtil.drawSphere(loc, (int) Math.round((i * power * 1.25)), 60, dust, null, .2);
                    });

                    i += (int) (tempPower * 1.25);
                    if (i >= (tempPower * 1.25 * 10)) {
                        cancel();
                        scheduler.runTask(LordOfTheMinecraft.instance, () -> {
                            loc.getWorld().createExplosion(loc, power * 10, true);
                            for (double i = 0; i < (power * 1.25 * 10); i += (power * 1.25)) {
                                loc.getWorld().createExplosion(loc.clone().add(0, 0, i), Math.round((power * .5 * 10)), false, false);
                                loc.getWorld().createExplosion(loc.clone().add(0, 0, -i), Math.round((power * .5 * 10)), false, false);
                                loc.getWorld().createExplosion(loc.clone().add(i, 0, 0), Math.round((power * .5 * 10)), false, false);
                                loc.getWorld().createExplosion(loc.clone().add(-i, 0, 0), Math.round((power * .5 * 10)), false, false);
                            }
                        });
                    }
                } catch (Exception e) {
                    LoggerUtil.logAbilityError(e, "Solar Flare");
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 2);
    }

    @Override
    public ItemStack getItem() {
        return SunItems.createItem(Material.SOUL_TORCH, "Спалах", "15000-70000", identifier);
    }

    @Override
    public void leftClick() {
        player = pathway.getBeyonder().getPlayer();
        power++;
        if (power > 7)
            power = 1;
        player.sendMessage("§6Потужність спалаху: " + power);
    }
}
