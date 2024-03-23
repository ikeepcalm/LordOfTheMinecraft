package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.entities.disasters.Disaster;
import dev.ua.ikeepcalm.entities.disasters.Lightning;
import dev.ua.ikeepcalm.entities.disasters.Meteor;
import dev.ua.ikeepcalm.entities.disasters.Tornado;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Miracles extends Ability implements Listener {

    private int selected;
    private Category selectedCategory;
    private final Category[] categories;

    private Chat chat;

    private final Inventory[] inventories;

    private final ArrayList<Disaster> disasters;

    private final ArrayList<Entity> summonedMobs;

    enum Chat {
        MOB,
        BIOME,
        TELEPORT,
        NOTHING,
    }

    public Miracles(int identifier, Pathway pathway, int sequence, Items items) {
        super(identifier, pathway, sequence, items);

        items.addToSequenceItems(identifier - 1, sequence);
        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);

        p = pathway.getBeyonder().getPlayer();

        selected = 0;
        categories = Category.values();
        selectedCategory = categories[selected];

        chat = Chat.NOTHING;

        summonedMobs = new ArrayList<>();

        inventories = new Inventory[categories.length];

        initializeInvs();

        disasters = new ArrayList<>();
        initializeDisasters();
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (!Arrays.asList(inventories).contains(e.getInventory()))
            return;
        e.setCancelled(true);

        //Check if clicked on disaster and spawn corresponding disaster where player is looking at
        for (Disaster disaster : disasters) {
            if (!disaster.getItem().isSimilar(e.getCurrentItem()))
                continue;

            //Get block player is looking at
            BlockIterator iter = new BlockIterator(p, 100);
            Block lastBlock = iter.next();
            while (iter.hasNext()) {
                lastBlock = iter.next();
                if (lastBlock.getType() == Material.AIR) {
                    continue;
                }
                break;
            }
            Location loc = lastBlock.getLocation();
            disaster.spawnDisaster(p, loc);
            p.closeInventory();
        }

        World world = p.getWorld();
        if (GeneralItemsUtil.getSunnyWeather().isSimilar(e.getCurrentItem())) {
            p.sendMessage("§6Погода прояснюється!");
            world.setClearWeatherDuration(120 * 60 * 20);
            p.closeInventory();
        } else if (GeneralItemsUtil.getRainyWeather().isSimilar(e.getCurrentItem())) {
            p.sendMessage("§3Починається дощ!");
            world.setClearWeatherDuration(0);
            world.setStorm(true);
            world.setThunderDuration(120 * 60 * 20);
            p.closeInventory();
        } else if (GeneralItemsUtil.getStormyWeather().isSimilar(e.getCurrentItem())) {
            p.sendMessage("§9Наближається шторм!");
            world.setClearWeatherDuration(0);
            world.setStorm(true);
            world.setThundering(true);
            world.setThunderDuration(120 * 60 * 20);
            p.closeInventory();
        }

    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (!summonedMobs.contains(e.getEntity()))
            return;

        if (e.getTarget() == pathway.getBeyonder().getPlayer())
            e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer() != pathway.getBeyonder().getPlayer())
            return;

        if (chat == Chat.NOTHING)
            return;

        e.setCancelled(true);

        //Summoning Mob
        if (chat == Chat.MOB) {
            chat = Chat.NOTHING;
            String chatMsg = e.getMessage();
            EntityType entityType = null;

            for (EntityType type : EntityType.values()) {
                if (type.name().replace("_", " ").equalsIgnoreCase(chatMsg)) {
                    entityType = type;
                    break;
                }
            }

            final EntityType type = entityType;

            if (entityType == null) {
                p.sendMessage("§c" + chatMsg + " не є дійсною сутністю!");
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    //Get block player is looking at
                    Location loc = getLocation(p);
                    World world = loc.getWorld();
                    if (world == null)
                        return;
                    Entity entity = world.spawnEntity(loc, type);
                    world.spawnParticle(Particle.SPELL_WITCH, loc, 2000, 1, 2, 1, 2);

                    Team team = pathway.getBeyonder().getTeam();
                    team.addEntry(entity.getUniqueId().toString());

                    summonedMobs.add(entity);
                }

                @NotNull
                private Location getLocation(Player p) {
                    BlockIterator iter = new BlockIterator(p, 100);
                    Block lastBlock = iter.next();
                    while (iter.hasNext()) {
                        Block prevBlock = lastBlock;
                        lastBlock = iter.next();
                        if (!lastBlock.getType().isSolid()) {
                            continue;
                        }
                        lastBlock = prevBlock;
                        break;
                    }

                    return lastBlock.getLocation();
                }
            }.runTaskLater(LordOfTheMinecraft.instance, 0);
        }
        //Teleporting
        else if (chat == Chat.TELEPORT) {
            chat = Chat.NOTHING;
            if (e.getMessage().split(" ").length != 1 && e.getMessage().split(" ").length != 3) {
                p.sendMessage("§cВведіть координати або ім'я гравця");
                chat = Chat.NOTHING;
                return;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (e.getMessage().split(" ").length == 1) {
                        if (Bukkit.getPlayer(e.getMessage()) == null) {
                            p.sendMessage("§c" + e.getMessage() + " не є дійсним гравцем!");
                            chat = Chat.NOTHING;
                            return;
                        }

                        p.teleport(Objects.requireNonNull(Bukkit.getPlayer(e.getMessage())));
                    } else {
                        for (String msg : e.getMessage().split(" ")) {
                            if (!GeneralPurposeUtil.isInteger(msg)) {
                                p.sendMessage("§cВведіть координати або ім'я гравця");
                                chat = Chat.NOTHING;
                                return;
                            }

                            Location loc = new Location(p.getWorld(), GeneralPurposeUtil.parseInt(e.getMessage().split(" ")[0]), GeneralPurposeUtil.parseInt(e.getMessage().split(" ")[1]), GeneralPurposeUtil.parseInt(e.getMessage().split(" ")[2]));
                            p.teleport(loc);
                        }
                    }
                    chat = Chat.NOTHING;
                }
            }.runTaskLater(LordOfTheMinecraft.instance, 0);
        }
        //Change the Biome
        else if (chat == Chat.BIOME) {
            chat = Chat.NOTHING;
            if (e.getMessage().split(" ").length != 1) {
                p.sendMessage("§cВведіть назву біома");
                chat = Chat.NOTHING;
                return;
            }

            String chatMsg = e.getMessage();
            Biome biome = null;

            for (Biome b : Biome.values()) {
                if (b.name().replace("_", " ").equalsIgnoreCase(chatMsg)) {
                    biome = b;
                    break;
                }
            }

            if (biome == null) {
                p.sendMessage("§c" + chatMsg + " не є дійсним біомом!");
                return;
            }

            final Biome biomeChange = biome;
            final Location loc = p.getLocation();
            final World world = p.getWorld();

            new BukkitRunnable() {
                @Override
                public void run() {
                    final int radius = 64;
                    for (int i = radius / 2; i > -(radius / 2); i--) {
                        for (int x = -radius; x <= radius; x++) {
                            for (int z = -radius; z <= radius; z++) {
                                if ((x * x) + (z * z) <= Math.pow(radius, 2)) {
                                    Location tempLoc = new Location(world, (int) loc.getX() + x, (int) loc.getY() + i, (int) loc.getZ() + z);
                                    world.setBiome(tempLoc, biomeChange);
                                    if (!tempLoc.getBlock().getType().isSolid()) {
                                        tempLoc.subtract(0, 1, 0);
                                        if (tempLoc.getBlock().getType().isSolid()) {
                                            tempLoc.add(0, 1, 0);
                                            world.spawnParticle(Particle.SPELL_WITCH, tempLoc, 5, .075, .075, .075, 2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    String biomeName = String.join(" ", (biomeChange.name().substring(0, 1).toUpperCase() + biomeChange.name().substring(1).toLowerCase()).split("_"));
                    p.sendMessage("§5Біом змінено на " + biomeName);
                }
            }.runTaskLater(LordOfTheMinecraft.instance, 0);
        }
    }

    private void initializeDisasters() {
        Meteor meteor = new Meteor(p);
        Tornado tornado = new Tornado(p);
        Lightning lightning = new Lightning(p);

        disasters.add(meteor);
        disasters.add(tornado);
        disasters.add(lightning);
    }

    //Create all the Inventories and put them into the array
    private void initializeInvs() {
        final ItemStack pane = GeneralItemsUtil.getMagentaPane();
        final ItemStack meteor = GeneralItemsUtil.getMeteor();
        final ItemStack tornado = GeneralItemsUtil.getTornado();
        final ItemStack lightning = GeneralItemsUtil.getLightning();

        final ItemStack sun = GeneralItemsUtil.getSunnyWeather();
        final ItemStack rain = GeneralItemsUtil.getRainyWeather();
        final ItemStack storm = GeneralItemsUtil.getStormyWeather();

        //Natural Disasters Inventory
        Inventory inventoryDisaster = Bukkit.createInventory(p, 27, "§5§lСтихійні лиха");
        for (int i = 0; i < inventoryDisaster.getSize(); i++) {
            inventoryDisaster.setItem(i, pane);
        }
        inventoryDisaster.setItem(10, meteor);
        inventoryDisaster.setItem(13, tornado);
        inventoryDisaster.setItem(17, lightning);

        inventories[0] = inventoryDisaster;

        //Weather Inventory
        Inventory inventoryWeather = Bukkit.createInventory(p, 27, "§5§lКерування погодою");
        for (int i = 0; i < inventoryWeather.getSize(); i++) {
            inventoryWeather.setItem(i, pane);
        }
        inventoryWeather.setItem(10, sun);
        inventoryWeather.setItem(13, rain);
        inventoryWeather.setItem(17, storm);

        inventories[4] = inventoryWeather;
    }

    enum Category {
        Natural_Disaster("Стихійні лиха", 950),
        Summoning("Покликати сутність", 250),
        Teleportation("Телепортація", 500),
        Change_Biome("Зміна біому", 400),
        Change_Weather("Керування погодою", 400);
        //Structure("Spawn Structure", 600);

        private final String name;
        private final int spirituality;

        Category(String name, int spirituality) {
            this.spirituality = spirituality;
            this.name = name;
        }
    }

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();
        pathway.getSequence().removeSpirituality(selectedCategory.spirituality);

        chat = Chat.NOTHING;

        initializeInvs();

        //open corresponding Inventory
        if (inventories[selected] != null)
            p.openInventory(inventories[selected]);

        switch (selected) {
            case 1 -> {
                chat = Chat.MOB;
                p.sendMessage("§5Яку сутність ви бажаєте покликати?");
            }
            case 2 -> {
                chat = Chat.TELEPORT;
                p.sendMessage("§5Введіть координати або ім'я гравця, до якого ви хочете телепортуватися");
            }
            case 3 -> {
                chat = Chat.BIOME;
                p.sendMessage("§5Введіть назву нового біому");
            }
            default -> chat = Chat.NOTHING;
        }
    }

    @Override
    //Display selected category
    public void onHold() {
        if (p == null)
            p = pathway.getBeyonder().getPlayer();
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Обрана Вища Сила: §f" + selectedCategory.name));
    }

    @Override
    //Cycle through categories on left click
    public void leftClick() {
        selected++;
        if (selected >= categories.length)
            selected = 0;
        selectedCategory = categories[selected];
    }

    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.NETHER_STAR, "Вища Сила", "різниться", identifier);
    }
}
