package dev.ua.ikeepcalm.mystical.pathways.priest.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.priest.PriestItems;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Transmutation extends Ability {

    private Category selectedCategory = Category.CONVERSION;
    private final Category[] categories = Category.values();
    private int selected = 0;

    private final List<IronGolem> summoned = new ArrayList<>();
    private final int maxSummons = 2;
    private final long lifespan = 300L;

    private final long duration = 200L;

    public Transmutation(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);
        items.addToSequenceItems(identifier - 1, sequence);
    }

    enum Category {
        CONVERSION("§fКонверсія"),
        SUMMONING("§bВиклик"),
        MANIPULATION("§9Маніпуляція");
        private final String name;

        Category(String name) {
            this.name = name;
        }
    }

    @Override
    public void useAbility() {
        player = pathway.getBeyonder().getPlayer();

        switch (selectedCategory) {
            case CONVERSION -> convertFireToIron();
            case SUMMONING -> summonIronGolem();
            case MANIPULATION -> manipulateFireAndIron();
        }
    }

    private void manipulateFireAndIron() {
        if (player == null)
            player = pathway.getBeyonder().getPlayer();

        List<Block> ironBlocks = new ArrayList<>();

        // Initial collection of iron blocks
        collectIronBlocksAroundPlayer(ironBlocks);

        new BukkitRunnable() {
            long elapsedTicks = 0;

            @Override
            public void run() {
                if (elapsedTicks >= duration || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                // Collect more iron blocks as the player moves
                collectIronBlocksAroundPlayer(ironBlocks);

                // Create a path below the player using iron blocks
                Location playerLocation = player.getLocation();
                Location pathLocation = playerLocation.clone().add(player.getLocation().getDirection()).subtract(0, 1, 0);

                // Check if the block below the player is not solid
                if (pathLocation.getBlock().getType() == Material.AIR) {
                    if (!ironBlocks.isEmpty()) {
                        Block block = ironBlocks.removeFirst();
                        block.setType(Material.AIR);
                        pathLocation.getBlock().setType(Material.IRON_BLOCK);
                        Particle.DustOptions dust = new Particle.DustOptions(org.bukkit.Color.fromRGB(220, 220, 220), 2f);
                        playerLocation.getWorld().spawnParticle(Particle.DUST, playerLocation, 45, .5, .5, .5, dust);
                    }
                }

                player.sendActionBar(Component.text("Маніпуляція: " + (duration - elapsedTicks)).color(NamedTextColor.GRAY));
                elapsedTicks += 1; // Increment by 1 tick since we are running continuously
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0L, 0);
    }

    private void collectIronBlocksAroundPlayer(List<Block> ironBlocks) {
        // Define the radius to search for iron blocks
        int radius = 8;
        List<Block> blocks = GeneralPurposeUtil.getBlocksInCircleRadius(player.getLocation().getBlock(), radius, true);

        // Collect iron blocks and add them to the list
        for (Block block : blocks) {
            if (block.getType() == Material.IRON_BLOCK && !ironBlocks.contains(block)) {
                ironBlocks.add(block);
            }
        }
    }


    private void convertFireToIron() {
        if (player == null)
            player = pathway.getBeyonder().getPlayer();

        int radius = 8;
        List<Block> blocks = GeneralPurposeUtil.getBlocksInCircleRadius(player.getLocation().getBlock(), radius, true);

        for (Block block : blocks) {
            if (block.getType() == Material.FIRE) {
                block.setType(Material.IRON_BLOCK);
            }
        }
    }

    private void summonIronGolem() {
        if (player == null)
            player = pathway.getBeyonder().getPlayer();

        if (summoned.size() >= maxSummons) {
            player.sendMessage(Component.text("Ти можеш викликати лише до " + maxSummons + " маріонеток одночасно!").color(NamedTextColor.RED));
            return;
        }

        int radius = 8;
        List<Block> blocks = GeneralPurposeUtil.getBlocksInCircleRadius(player.getLocation().getBlock(), radius, true);

        // Collect iron blocks
        List<Block> ironBlocks = new ArrayList<>();
        for (Block block : blocks) {
            if (block.getType() == Material.IRON_BLOCK) {
                ironBlocks.add(block);
            }
        }

        // Check if there are at least four iron blocks
        if (ironBlocks.size() >= 4) {
            // Destroy the iron blocks
            for (int i = 0; i < 4; i++) {
                ironBlocks.get(i).setType(Material.AIR);
            }

            // Spawn the Iron Golem behind the player
            Location spawnLocation = player.getLocation().clone().add(player.getLocation().getDirection().multiply(-2));
            IronGolem ironGolem = (IronGolem) player.getWorld().spawnEntity(spawnLocation, EntityType.IRON_GOLEM);
            ironGolem.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(80);
            ironGolem.setHealth(80);
            ironGolem.setCustomName("Сталева Маріонетка");
            ironGolem.setCustomNameVisible(true);
            ironGolem.setPlayerCreated(true);

            summoned.add(ironGolem);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (ironGolem.isDead()) {
                        summoned.remove(ironGolem);
                        this.cancel();
                        return;
                    }

                    ironGolem.remove();
                    summoned.remove(ironGolem);
                    Particle.DustOptions dust = new Particle.DustOptions(org.bukkit.Color.fromRGB(220, 220, 220), 2f);
                    ironGolem.getWorld().spawnParticle(Particle.DUST, ironGolem.getLocation(), 45, .5, .5, .5, dust);
                    player.sendMessage(Component.text("Сталевий дух розсіюється...").color(NamedTextColor.GRAY));
                    this.cancel();
                }
            }.runTaskLater(LordOfTheMinecraft.instance, lifespan);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (ironGolem.isDead() || !player.isOnline()) {
                        this.cancel();
                        return;
                    }

                    if (ironGolem.getLocation().distance(player.getLocation()) > 10) {
                        if (!ironGolem.getPathfinder().hasPath()) {
                            ironGolem.getPathfinder().moveTo(player);
                        }
                    }

                    // Target entities that attack the player or are attacked by the player
                    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                        if (entity instanceof Monster || entity instanceof Player) {
                            if (entity.getLastDamageCause() != null) {
                                DamageSource source = entity.getLastDamageCause().getDamageSource();
                                if (source.getDirectEntity() instanceof Player damagePlayer) {
                                    if (damagePlayer.getUniqueId() == player.getUniqueId()) {
                                        ironGolem.setTarget((LivingEntity) entity);
                                    }
                                }
                            }
                        }
                    }
                }
            }.runTaskTimer(LordOfTheMinecraft.instance, 0L, 20L);
        }
    }


    @Override
    public void leftClick() {
        selected++;
        if (selected >= categories.length)
            selected = 0;
        selectedCategory = categories[selected];
    }

    @Override
    public void onHold() {
        if (player == null)
            player = pathway.getBeyonder().getPlayer();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Обрано: §f" + selectedCategory.name));
    }


    @Override
    public ItemStack getItem() {
        return PriestItems.createItem(Material.IRON_INGOT, "Трансмутація", "300", identifier);
    }


}
