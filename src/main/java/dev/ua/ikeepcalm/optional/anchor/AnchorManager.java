package dev.ua.ikeepcalm.optional.anchor;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import dev.ua.ikeepcalm.optional.anchor.tasks.RadiationTask;
import dev.ua.ikeepcalm.optional.anchor.tasks.TransformationTask;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AnchorManager implements Listener {

    private final JavaPlugin plugin;
    @Getter
    private Location anchorLocation;
    private TransformationTask transformationTask;
    private RadiationTask radiationTask;
    private final Map<Location, Material> transformedBlocks;
    private static final double[] PROBABILITY_DISTRIBUTION = {0.001, 0.003, 0.004, 0.05, 0.01, 0.2, 0.3, 0.4, 0.5};
    private static final int MIN_VALUE = 1;

    public AnchorManager() {
        this.plugin = LordOfTheMinecraft.instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        transformedBlocks = new HashMap<>();
        loadAnchorLocation();
    }

    public void setAnchorLocation(Location location) {
        this.anchorLocation = location;
        buildBedrockSquare(location);
        spawnAnchorHeart(location);
        startTasks();
    }

    public void loadAnchorLocation() {
        FileConfiguration config = plugin.getConfig();
        if (config.contains("anchor-location")) {
            anchorLocation = (Location) config.get("anchor-location");
            loadTransformedBlocks();
            startTasks();
        }
    }

    public void saveAnchorLocation() {
        if (anchorLocation != null) {
            plugin.getConfig().set("anchor-location", anchorLocation);
            saveTransformedBlocks();
            plugin.saveConfig();
        }
    }

    private void loadTransformedBlocks() {
        FileConfiguration config = plugin.getConfig();
        if (config.contains("transformed-blocks")) {
            for (String locString : config.getStringList("transformed-blocks")) {
                String[] parts = locString.split(",");
                World world = Bukkit.getWorld(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                Material originalType = Material.getMaterial(parts[4]);

                if (world != null && originalType != null) {
                    Location loc = new Location(world, x, y, z);
                    transformedBlocks.put(loc, originalType);
                }
            }
        }
    }


    private void saveTransformedBlocks() {
        Set<String> locStrings = new HashSet<>();
        for (Map.Entry<Location, Material> entry : transformedBlocks.entrySet()) {
            Location loc = entry.getKey();
            Material originalType = entry.getValue();
            locStrings.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + originalType.name());
        }
        plugin.getConfig().set("transformed-blocks", locStrings);
        plugin.saveConfig();
    }


    private void buildBedrockSquare(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -5; z <= 5; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= 5 && distance > 5 - 1) {
                        Location loc = new Location(world, cx + x, cy + y, cz + z);
                        loc.getBlock().setType(Material.BEDROCK);
                    }
                }
            }
        }
    }

    private void spawnAnchorHeart(Location location) {
        World world = location.getWorld();
        world.getBlockAt(location).setType(Material.CRACKED_NETHER_BRICKS);
    }

    private void startTasks() {
        if (transformationTask == null) {
            transformationTask = new TransformationTask(plugin, anchorLocation, transformedBlocks);
            transformationTask.runTaskTimer(plugin, 0, 20 * 10);
        }
        if (radiationTask == null) {
            radiationTask = new RadiationTask(transformedBlocks.keySet());
            radiationTask.runTaskTimer(plugin, 0, 20 * 4);
        }
    }

    public void startRestoration() {
        Iterator<Map.Entry<Location, Material>> iterator = transformedBlocks.entrySet().iterator();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (iterator.hasNext()) {
                    Map.Entry<Location, Material> entry = iterator.next();
                    Location loc = entry.getKey();
                    Material originalType = entry.getValue();

                    World world = loc.getWorld();
                    if (world != null && world.getBlockAt(loc).getType() == Material.MAGMA_BLOCK) {
                        world.getBlockAt(loc).setType(originalType);
                    }
                } else {
                    this.cancel();
                    Bukkit.broadcast(Component.text("Вплив Якоря більше не відчувається. Блоки відновлено.").color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
                    transformedBlocks.clear();
                    saveTransformedBlocks();
                }
            }
        }.runTaskTimer(plugin, 0L, 10);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (anchorLocation != null) {
            Location location = event.getBlock().getLocation();
            if (location.distance(location) < 2 && event.getBlock().getType() == Material.CRACKED_NETHER_BRICKS) {
                loadTransformedBlocks();
                saveTransformedBlocks();
                stopTasks();
                event.getBlock().setType(Material.AIR);
                event.getPlayer().sendMessage(Component.text("Ти знищив серце якоря! Вищі сутності нагороджують тебе...").color(NamedTextColor.GREEN));
                Bukkit.broadcast(Component.text("Серце якоря було знищено! Прокляття стихає...").color(NamedTextColor.GREEN));
                List<Block> blocks = GeneralPurposeUtil.getBlocksInSquare(location.getBlock(), 10, true);
                for (Block block : blocks) {
                    if (block.getType() == Material.BEDROCK) {
                        block.setType(Material.AIR);
                    }
                }
                int sequence = GeneralPurposeUtil.biasedRandomNumber(PROBABILITY_DISTRIBUTION, MIN_VALUE);
                Potion potion = LordOfTheMinecraft.instance.getPotions().get(new Random().nextInt(LordOfTheMinecraft.instance.getPotions().size()));
                location.getWorld().dropItemNaturally(location, potion.returnPotionForSequence(sequence));
            }
        }
    }

    private void stopTasks() {
        if (transformationTask != null) {
            transformationTask.cancel();
            transformationTask = null;
        }
        if (radiationTask != null) {
            radiationTask.cancel();
            radiationTask = null;
        }
        anchorLocation = null;
        startRestoration();
    }
}
