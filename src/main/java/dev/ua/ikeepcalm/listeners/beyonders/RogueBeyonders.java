package dev.ua.ikeepcalm.listeners.beyonders;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Random;

public class RogueBeyonders implements Listener {

    private final HashMap<EntityType, Integer> spawnProbabilityTable;

    private static final double[] PROBABILITY_DISTRIBUTION = {0.001, 0.002, 0.009, 0.01, 0.02, 0.025, 0.03, 0.035, 0.04};
    private static final int MIN_VALUE = 1;

    public RogueBeyonders() {
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);
        spawnProbabilityTable = new HashMap<>();

        spawnProbabilityTable.put(EntityType.COW, 1);
        spawnProbabilityTable.put(EntityType.SHEEP, 1);
        spawnProbabilityTable.put(EntityType.SKELETON, 1);
        spawnProbabilityTable.put(EntityType.ZOMBIE_VILLAGER, 1);
        spawnProbabilityTable.put(EntityType.SPIDER, 1);
        spawnProbabilityTable.put(EntityType.HUSK, 1);
        spawnProbabilityTable.put(EntityType.CHICKEN, 1);
        spawnProbabilityTable.put(EntityType.WOLF, 1);
        spawnProbabilityTable.put(EntityType.ZOMBIE, 1);
        spawnProbabilityTable.put(EntityType.CREEPER, 1);
        spawnProbabilityTable.put(EntityType.MAGMA_CUBE, 1);
        spawnProbabilityTable.put(EntityType.PIGLIN, 1);
        spawnProbabilityTable.put(EntityType.ZOMBIFIED_PIGLIN, 1);
        spawnProbabilityTable.put(EntityType.ENDERMAN, 1);
        spawnProbabilityTable.put(EntityType.HORSE, 1);
        spawnProbabilityTable.put(EntityType.FOX, 1);

    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        Random random = new Random();

        if (!spawnProbabilityTable.containsKey(e.getEntity().getType()))
            return;

        if (random.nextInt(100) > spawnProbabilityTable.get(e.getEntity().getType()))
            return;

        boolean aggressive = (random.nextInt(4) == 0);
        int sequence = GeneralPurposeUtil.biasedRandomNumber(PROBABILITY_DISTRIBUTION, MIN_VALUE);
        int pathway = random.nextInt(5);

        if (LordOfTheMinecraft.instance.getCurrentRogueBeyonders().size() > 20) {
            LordOfTheMinecraft.instance.removeRogueBeyonder(LordOfTheMinecraft.instance.getCurrentRogueBeyonders().get((new Random()).nextInt(LordOfTheMinecraft.instance.getCurrentRogueBeyonders().size())));
        }

        spawnNPC(aggressive, sequence, pathway, e.getLocation());
    }

    public void spawnNPC(boolean aggressive, int sequence, int pathway, Location location) {
        RogueBeyonder rogueBeyonder = new RogueBeyonder(aggressive, sequence, pathway);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(rogueBeyonder, LordOfTheMinecraft.instance);
        rogueBeyonder.spawn(location);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (CitizensAPI.getNPCRegistry().getByUniqueId(e.getEntity().getUniqueId()) != null)
            e.setDeathMessage("");
    }
}
