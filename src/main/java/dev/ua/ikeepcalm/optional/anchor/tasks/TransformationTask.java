package dev.ua.ikeepcalm.optional.anchor.tasks;

import dev.ua.ikeepcalm.optional.anchor.AnchorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TransformationTask extends BukkitRunnable {

    private final Random random = new Random();
    private final String configFileName = "anchor.yml";
    private final Map<Location, Material> transformedBlocks;

    public TransformationTask(Map<Location, Material> transformedBlocks) {
        this.transformedBlocks = transformedBlocks;
    }

    @Override
    public void run() {
        if (transformedBlocks.isEmpty()) return;

        List<Location> transformedLocations = new ArrayList<>(transformedBlocks.keySet());
        Location randomTransformedLocation = transformedLocations.get(random.nextInt(transformedLocations.size()));
        World world = randomTransformedLocation.getWorld();
        if (world == null) return;

        int[][] directions = {
                {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}
        };

        List<Location> potentialLocations = new ArrayList<>();
        for (int[] direction : directions) {
            Location adjacentLocation = randomTransformedLocation.clone().add(direction[0], direction[1], direction[2]);
            if (!transformedBlocks.containsKey(adjacentLocation)) {
                Material currentType = adjacentLocation.getBlock().getType();
                if (currentType != Material.MAGMA_BLOCK && currentType != Material.NETHER_BRICKS) {
                    potentialLocations.add(adjacentLocation);
                }
            }
        }

        if (!potentialLocations.isEmpty()) {
            Location selectedLocation = potentialLocations.get(random.nextInt(potentialLocations.size()));
            transformedBlocks.put(selectedLocation, selectedLocation.getBlock().getType());
            selectedLocation.getBlock().setType(Material.MAGMA_BLOCK);
            if (transformedBlocks.size() % 500 == 0) {
                Bukkit.broadcast(Component.text("Вплив якоря поглиблюється...").color(NamedTextColor.RED));
            }
        }

        saveTransformedBlocks();
    }

    private void saveTransformedBlocks() {
        FileConfiguration config = AnchorUtil.getCustomConfig(configFileName);
        config.set("transformed-blocks", null);

        for (Map.Entry<Location, Material> entry : transformedBlocks.entrySet()) {
            Location loc = entry.getKey();
            Material originalType = entry.getValue();
            String worldName = loc.getWorld().getName();
            String path = String.format("transformed-blocks.%s.%d,%d,%d", worldName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            config.set(path, originalType.name());
        }

        AnchorUtil.saveCustomConfig(configFileName, config);
    }
}
