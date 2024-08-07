package dev.ua.ikeepcalm.optional.anchor.tasks;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TransformationTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final Location anchorLocation;
    private final Random random = new Random();
    private final Map<Location, Material> convertedBlocks;
    private final List<Location> directions = new ArrayList<>();

    public TransformationTask(JavaPlugin plugin, Location anchorLocation, Map<Location, Material> convertedBlocks) {
        this.plugin = plugin;
        this.anchorLocation = anchorLocation;
        this.convertedBlocks = convertedBlocks;
        chooseInitialDirections();
    }

    private void chooseInitialDirections() {
        int[][] deltas = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
        while (directions.size() < 3) {
            int[] delta = deltas[random.nextInt(deltas.length)];
            Location direction = anchorLocation.clone().add(delta[0], delta[1], delta[2]);
            if (!directions.contains(direction)) {
                directions.add(direction);
            }
        }
    }

    @Override
    public void run() {
        World world = anchorLocation.getWorld();
        if (world == null) return;

        if (convertedBlocks.isEmpty()) {
            convertedBlocks.put(anchorLocation, world.getBlockAt(anchorLocation).getType());
        }

        List<Location> newConverted = new ArrayList<>();

        for (Location loc : convertedBlocks.keySet()) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (Math.abs(dx) == Math.abs(dz) && dy == 0) continue;
                        Location adj = loc.clone().add(dx, dy, dz);
                        if (!convertedBlocks.containsKey(adj) && !newConverted.contains(adj)) {
                            if (world.getBlockAt(adj).getType() != Material.CRACKED_NETHER_BRICKS) {
                                newConverted.add(adj);
                            }
                        }
                    }
                }
            }
        }

        if (!newConverted.isEmpty()) {
            Location loc = newConverted.get(random.nextInt(newConverted.size()));
            if (convertedBlocks.size() % 500 == 0) {
                if (loc.getBlock().getType() == Material.BEDROCK) {
                    Bukkit.broadcast(Component.text("Якір прорвав свою захисну оболонку. Його вплив на світ стає сильнішим...").color(net.kyori.adventure.text.format.NamedTextColor.RED));
                } else {
                    Bukkit.broadcast(Component.text("Якір поширює свій вплив...").color(net.kyori.adventure.text.format.NamedTextColor.RED));
                }
            }

            // Save the original block type and set transformed type
            Material originalType = world.getBlockAt(loc).getType();
            if (originalType == Material.BEDROCK) {
                originalType = Material.AIR;
            }
            convertedBlocks.put(loc.clone(), originalType);

            // Transform the block
            world.getBlockAt(loc).setType(Material.MAGMA_BLOCK);
            saveTransformedBlocks();
        }
    }

    private void saveTransformedBlocks() {
        Set<String> locStrings = new HashSet<>();
        for (Map.Entry<Location, Material> entry : convertedBlocks.entrySet()) {
            Location loc = entry.getKey();
            Material originalType = entry.getValue();
            locStrings.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + originalType.name());
        }
        plugin.getConfig().set("transformed-blocks", locStrings);
        plugin.saveConfig();
    }

}
