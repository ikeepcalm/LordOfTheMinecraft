package dev.ua.ikeepcalm.mystical.pathways.fool.abilities;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Items;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.NpcAbility;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolItems;
import dev.ua.ikeepcalm.mystical.pathways.fool.abilities.grafting.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Grafting extends NpcAbility implements Listener {

    private final HashMap<Location[], Integer> graftedLocations;
    private final ArrayList<HealthSynchronization> healthSynchros;
    private final ArrayList<EntityToLocation> stuckEntities;
    private final ArrayList<EntityToEntity> entityToEntities;
    private final ArrayList<DamageTransfer> damageTransfers;

    private int radius = 1;

    private final Material[] npcGraftMaterial;

    public Grafting(int identifier, Pathway pathway, int sequence, Items items, boolean npc) {
        super(identifier, pathway, sequence, items);
        if (!npc)
            items.addToSequenceItems(identifier - 1, sequence);

        LordOfTheMinecraft.instance.getServer().getPluginManager().registerEvents(this, LordOfTheMinecraft.instance);

        npcGraftMaterial = new Material[]{
                Material.GRASS_BLOCK,
                Material.DIRT,
                Material.STONE,
                Material.SAND,
                Material.DEEPSLATE,
                Material.GRAVEL
        };

        graftedLocations = new HashMap<>();
        healthSynchros = new ArrayList<>();
        stuckEntities = new ArrayList<>();
        entityToEntities = new ArrayList<>();
        damageTransfers = new ArrayList<>();


        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Location[], Integer> entry : graftedLocations.entrySet()) {
                    if (entry.getKey()[0].getWorld() == null || entry.getKey()[1].getWorld() == null)
                        return;

                    for (Entity entity : entry.getKey()[0].getWorld().getNearbyEntities(entry.getKey()[0], entry.getValue(), entry.getValue(), entry.getValue())) {
                        entity.teleport(entry.getKey()[1]);
                    }
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 0);
    }

    @Override
    public void useNPCAbility(Location loc, Entity caster, double multiplier) {

        World world = loc.getWorld();
        if (world == null)
            return;

        LivingEntity target = null;

        for (Entity entity : world.getNearbyEntities(loc, 1, 1, 1)) {
            if (entity == caster || entity.getType() == EntityType.ARMOR_STAND || !(entity instanceof LivingEntity))
                continue;
            target = (LivingEntity) entity;
            break;
        }

        if (target == null)
            return;

        Random random = new Random();

        switch (random.nextInt(2)) {
            case 0 -> new BlockToEntity(target, npcGraftMaterial[(new Random()).nextInt(npcGraftMaterial.length)]);
            case 1 -> {
                Location graftLoc = target.getLocation().add(random.nextInt(-8, 8), random.nextInt(-3, 3), random.nextInt(-8, 8));
                new EntityToLocation(target, graftLoc);
            }
        }
    }

    enum Category {
        Location("Локація - Локація"),
        Block("Сутність - Блок"),
        Entity("Сутність - Сутність"),
        Stuck("Сутність - Локація"),
        Health("Здоров'я - Здоров'я"),
        Target("Змінити ціль атаки");

        private final String name;

        Category(String name) {
            this.name = name;
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (!e.isSneaking() || e.getPlayer() != p || !p.getInventory().getItemInMainHand().isSimilar(getItem()) || selectedCategory != Category.Location)
            return;

        radius++;
        if (radius > 6)
            radius = 1;

        p.sendMessage("§5Радіус встановлено на " + radius);
    }

    private Category selectedCategory = Category.Location;
    private final Category[] categories = Category.values();
    private boolean grafting = false;
    private int selected = 0;

    private Location loc1;
    private Location loc2;

    private Material graftMaterial;
    private LivingEntity tempEnt;

    @Override
    public void useAbility() {
        p = pathway.getBeyonder().getPlayer();

        Location playerLookEntity = p.getEyeLocation();
        Vector vectorEntity = playerLookEntity.getDirection().normalize().multiply(.5);
        //Get entity player is looking at
        LivingEntity entity = null;

        for (int i = 0; i < 500; i++) {
            playerLookEntity.add(vectorEntity);

            if (p.getWorld().getNearbyEntities(playerLookEntity, 1, 1, 1).isEmpty())
                continue;

            Entity e = p.getWorld().getNearbyEntities(playerLookEntity, 1, 1, 1).iterator().next();

            if (e == p || !(e instanceof LivingEntity))
                continue;

            entity = (LivingEntity) e;
        }

        //Get block player is looking at
        Location playerLook = p.getEyeLocation();
        Vector vector = playerLook.getDirection().normalize().multiply(.5);
        for (int i = 0; i < 300; i++) {
            if (playerLook.getBlock().getType().isSolid())
                break;
            playerLook.add(vector);
        }

        switch (selectedCategory) {

            case Location -> {
                playerLook.add(0, .5, 0);
                if (!grafting) {
                    loc1 = playerLook;
                } else {
                    loc2 = playerLook;

                    graftedLocations.put(new Location[]{loc1, loc2}, radius);
                }

                p.spawnParticle(Particle.SPELL_WITCH, playerLook, 400, radius / 2f, .1525, radius / 2f, 0);

                grafting = !grafting;
            }
            case Block -> {
                if (!grafting) {
                    if (entity == null) {
                        p.sendMessage("§cНе вдалося знайти сутність");
                        return;
                    }

                    p.spawnParticle(Particle.SPELL_WITCH, entity.getLocation(), 50, .5, .5, .5, 0);
                    tempEnt = entity;
                } else {
                    graftMaterial = playerLook.getBlock().getType();
                    playerLook.add(0, .5, 0);
                    p.spawnParticle(Particle.SPELL_WITCH, playerLook, 80, .25, .25, .25, 0);

                    new BlockToEntity(tempEnt, graftMaterial);
                    reset();
                }
                grafting = !grafting;
            }
            case Entity -> {
                if (!grafting) {
                    if (entity == null) {
                        p.sendMessage("§cНе вдалося знайти сутність");
                        return;
                    }

                    for (EntityToEntity entityToEntity : entityToEntities) {
                        if (entityToEntity.getEntity() == entity) {
                            p.sendMessage("§cСкасування Алхімії");
                            entityToEntity.stop();
                            entityToEntities.remove(entityToEntity);
                            return;
                        }
                    }

                    p.spawnParticle(Particle.SPELL_WITCH, entity.getLocation(), 50, .5, .5, .5, 0);
                    tempEnt = entity;
                } else {
                    if (entity == null) {
                        entity = p;
                    }

                    p.spawnParticle(Particle.SPELL_WITCH, entity.getLocation(), 50, .5, .5, .5, 0);
                    entityToEntities.add(new EntityToEntity(tempEnt, entity));
                    reset();
                }

                grafting = !grafting;
            }
            case Stuck -> {
                if (!grafting) {
                    if (entity == null) {
                        p.sendMessage("§cНе вдалося знайти сутність");
                        return;
                    }

                    for (EntityToLocation entityToLocation : stuckEntities) {
                        if (entityToLocation.getEntity() == entity) {
                            p.sendMessage("§cСкасування Алхімії");
                            entityToLocation.stop();
                            stuckEntities.remove(entityToLocation);
                            return;
                        }
                    }

                    p.spawnParticle(Particle.SPELL_WITCH, entity.getLocation(), 50, .5, .5, .5, 0);
                    tempEnt = entity;
                } else {
                    p.spawnParticle(Particle.SPELL_WITCH, playerLook, 80, .25, .25, .25, 0);
                    stuckEntities.add(new EntityToLocation(tempEnt, playerLook.clone()));
                    reset();
                }
                grafting = !grafting;
            }
            case Health -> {

                if (entity == null) {
                    entity = p;
                }

                try {
                    for (HealthSynchronization healthSynchronization : healthSynchros) {
                        if (healthSynchronization.getEntity1() == entity || healthSynchronization.getEntity2() == entity) {
                            healthSynchronization.stop();
                            healthSynchros.remove(healthSynchronization);
                            p.sendMessage("§cСкасування Алхімії");
                        }
                    }
                } catch (ConcurrentModificationException ignored) {
                }

                p.spawnParticle(Particle.SPELL_WITCH, entity.getLocation(), 50, .5, .5, .5, 0);

                if (!grafting)
                    tempEnt = entity;
                else {
                    healthSynchros.add(new HealthSynchronization(tempEnt, entity));
                    reset();
                }
                grafting = !grafting;
            }
            case Target -> {
                if (!grafting) {
                    if (entity == null) {
                        entity = p;
                    }

                    p.spawnParticle(Particle.SPELL_WITCH, entity.getLocation(), 50, .5, .5, .5, 0);
                    tempEnt = entity;
                } else {
                    if (entity == null) {
                        p.sendMessage("§cНе вдалося знайти сутність");
                        return;
                    }

                    p.spawnParticle(Particle.SPELL_WITCH, entity.getLocation(), 50, .5, .5, .5, 0);
                    if (tempEnt == entity) {
                        reset();
                        return;
                    }

                    for (DamageTransfer damageTransfer : damageTransfers) {
                        if (tempEnt == damageTransfer.getReceive()) {
                            reset();
                            return;
                        }
                    }
                    damageTransfers.add(new DamageTransfer(tempEnt, entity, damageTransfers, false));
                    reset();
                }

                grafting = !grafting;
            }
        }
    }

    @Override
    //Display selected category
    public void onHold() {
        if (p == null)
            p = pathway.getBeyonder().getPlayer();
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5Обраний алхімічний рецепт: §f" + selectedCategory.name));

        for (Map.Entry<Location[], Integer> entry : graftedLocations.entrySet()) {
            if (entry.getKey()[0].getWorld() == null || entry.getKey()[1].getWorld() == null)
                return;

            p.spawnParticle(Particle.SPELL_WITCH, entry.getKey()[0], 75, entry.getValue() / 2f, .15, entry.getValue() / 2f, 0);
            p.spawnParticle(Particle.SPELL_WITCH, entry.getKey()[1], 75, entry.getValue() / 2f, .15, entry.getValue() / 2f, 0);
        }
    }

    @Override
    //Cycle through categories on left click
    public void leftClick() {
        grafting = false;
        reset();
        selected++;
        if (selected >= categories.length)
            selected = 0;
        selectedCategory = categories[selected];
    }

    private void reset() {
        loc1 = null;
        loc2 = null;
        radius = 1;

        graftMaterial = null;

        tempEnt = null;
    }


    @Override
    public ItemStack getItem() {
        return FoolItems.createItem(Material.ECHO_SHARD, "Містична Алхімія", "5000", identifier, sequence, pathway.getBeyonder().getPlayer().getName());
    }
}
