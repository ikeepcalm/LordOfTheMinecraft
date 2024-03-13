package dev.ua.ikeepcalm.entities.beyonders;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Pathway;
import dev.ua.ikeepcalm.mystical.parents.Potion;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.Ability;
import dev.ua.ikeepcalm.mystical.pathways.door.abilities.Record;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolPathway;
import dev.ua.ikeepcalm.mystical.pathways.fool.abilities.Hiding;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_20_R3.boss.CraftBossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Beyonder implements Listener {

    private Pathway pathway;
    protected final UUID uuid;
    private double spirituality;
    private double maxSpirituality;

    private double actingProgress;
    private double actingNeeded;
    private boolean digested;
    private BossBar bossBar;
    private boolean beyonder;
    private boolean loosingControl;
    public boolean online;
    private boolean initializedOnce;

    private Team team;

    private int resurrections;

    private final ArrayList<Record> records;

    private final int[] healthIndex;

    public Beyonder(UUID uuid, Pathway pathway) {
        this.pathway = pathway;
        this.uuid = uuid;

        pathway.setBeyonder(this);

        beyonder = true;
        online = false;
        initializedOnce = false;

        records = new ArrayList<>();

        loosingControl = false;

        resurrections = 0;

        healthIndex = new int[]{
                0, 180, 120, 80, 70, 55, 40, 30, 25, 20
        };

        bossBar = new CraftBossBar("§6Spirituality", BarColor.BLUE, BarStyle.SOLID);
        bossBar.addPlayer(getPlayer());

        pathway.init();

        if (getPlayer() == null || !Bukkit.getOnlinePlayers().contains(getPlayer()))
            return;

        initializedOnce = true;

        //acting initializing
        digested = false;
        actingNeeded = Math.pow((float) (100 / pathway.getSequence().getCurrentSequence()), 2);
        actingProgress = 0;

        pathway.initItems();
        start();
    }

    @EventHandler
    //Restarts everything when Beyonder rejoins
    public void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        if (!beyonder)
            return;

        pathway.setBeyonder(this);

        if (!initializedOnce) {
            pathway.initItems();
            initializedOnce = true;
        }
        start();
    }

    @EventHandler
    //Stops everything when Beyonder leaves
    public void onLeave(PlayerQuitEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        if (!beyonder)
            return;
        online = false;
    }

    @EventHandler
    //Removes Items on Death
    public void onDeath(PlayerDeathEvent e) {
        if (!beyonder)
            return;
        if (e.getEntity() != getPlayer())
            return;
        Player p = e.getEntity();
        Location deathLoc = p.getLocation();

        if (pathway.getSequence() == null)
            return;


        if (pathway instanceof FoolPathway && pathway.getSequence().getCurrentSequence() <= 2 && resurrections < 5 && !loosingControl) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (deathLoc.getWorld() == null)
                        return;

                    for (Entity entity : deathLoc.getWorld().getNearbyEntities(deathLoc, 20, 20, 20)) {
                        if (!(entity instanceof Item))
                            continue;

                        entity.remove();
                    }

                    for (ItemStack item : e.getDrops()) {
                        p.getInventory().addItem(item);
                    }

                    p.teleport(deathLoc);

                    for (Ability ability : pathway.getSequence().getAbilities()) {
                        if (ability instanceof Hiding hiding)
                            hiding.useAbility();
                    }

                    resurrections++;
                }
            }.runTaskLater(LordOfTheMinecraft.instance, 2);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (deathLoc.getWorld() == null)
                    return;

                for (Entity entity : deathLoc.getWorld().getNearbyEntities(deathLoc, 20, 20, 20)) {
                    if (!(entity instanceof Item item))
                        continue;

                    for (ItemStack itemStack : pathway.getItems().returnItemsFromSequence(pathway.getSequence().getCurrentSequence())) {
                        if (itemStack.isSimilar(item.getItemStack()))
                            entity.remove();
                    }
                }
            }
        }.runTaskLater(LordOfTheMinecraft.instance, 2);
    }

    //Gets called everytime the Player rejoins or the Beyonder is newly initialised
    public void start() {
        //Team
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard scoreboard = manager.getNewScoreboard();
        team = scoreboard.registerNewTeam(getPlayer().getName() + " -- " + UUID.randomUUID());

        team.addEntry(getPlayer().getUniqueId().toString());
        team.setDisplayName("display name");
        team.setCanSeeFriendlyInvisibles(true);
        team.setAllowFriendlyFire(false);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);

        if (!initializedOnce) {
            //acting initializing
            digested = false;
            actingNeeded = Math.pow((float) (100 / pathway.getSequence().getCurrentSequence()), 2);
            actingProgress = 0;
        }

        updateSpirituality();

        online = true;
        initializedOnce = true;

        Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(healthIndex[pathway.getSequence().getCurrentSequence()]);

        //onHold
        new BukkitRunnable() {
            @Override
            public void run() {
                //Cancel and return if player, sequence is null or player is not online
                if (!beyonder || !online || getPlayer() == null || pathway.getSequence() == null) {
                    cancel();
                    return;
                }

                if (loosingControl)
                    return;

                Player p = getPlayer();

                //Call onHold() function in Sequence
                pathway.getSequence().onHold(p.getInventory().getItemInMainHand());
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 3);

        //constant loop
        new BukkitRunnable() {
            int counter = 0;
            int actingCounter = 0;

            @Override
            public void run() {
                //Cancel and return if player, sequence is null or player is not online
                if (!beyonder || !online || getPlayer() == null || pathway.getSequence() == null) {
                    cancel();
                    return;
                }

                actingCounter++;
                if (actingCounter >= 20 * 15) {
                    actingCounter = 0;
                    addActing(1);
                }

                //scoreboard
                counter++;

                if (spirituality <= maxSpirituality / 80 && !loosingControl) {
                    looseControl(95, 10);
                }

                pathway.getSequence().run();

                //spirituality handling
                if (spirituality < maxSpirituality && counter >= 8) {
                    counter = 0;
                    spirituality += (maxSpirituality / 200);
                    if (spirituality > maxSpirituality)
                        spirituality = maxSpirituality;
                }

                if (spirituality < maxSpirituality){
                    bossBar.setProgress(spirituality / maxSpirituality);
                    if (!bossBar.isVisible()){
                        bossBar.setVisible(true);
                    }
                } else {
                    bossBar.setVisible(false);
                }

                if (loosingControl)
                    return;

                Player p = getPlayer();

                //passive effects
                if (pathway.getSequence().getSequenceEffects().containsKey(pathway.getSequence().getCurrentSequence())) {
                    for (PotionEffect effect : pathway.getSequence().getSequenceEffects().get(pathway.getSequence().getCurrentSequence())) {
                        p.addPotionEffect(effect);
                    }
                } else {
                    for (int i = pathway.getSequence().getCurrentSequence(); i < 10; i++) {
                        if (pathway.getSequence().getSequenceEffects().containsKey(i)) {
                            for (PotionEffect effect : pathway.getSequence().getSequenceEffects().get(i)) {
                                p.addPotionEffect(effect);
                            }
                            break;
                        }
                    }
                }

                //passive resistances
                if (pathway.getSequence().getSequenceResistances().containsKey(pathway.getSequence().getCurrentSequence())) {
                    for (PotionEffectType effect : pathway.getSequence().getSequenceResistances().get(pathway.getSequence().getCurrentSequence())) {
                        for (PotionEffect potion : p.getActivePotionEffects()) {
                            if (potion.getType() == effect) {
                                p.removePotionEffect(effect);
                            }
                        }
                    }
                } else {
                    for (int i = pathway.getSequence().getCurrentSequence(); i < 9; i++) {
                        if (pathway.getSequence().getSequenceResistances().containsKey(i)) {
                            for (PotionEffectType effect : pathway.getSequence().getSequenceResistances().get(i)) {
                                if (p.getPotionEffect(effect) != null) {
                                    p.removePotionEffect(effect);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 10);
    }

    public void updateSpirituality() {
        if (pathway.getSequence().getCurrentSequence() > 8)
            spirituality = (int) Math.pow((float) (90 / pathway.getSequence().getCurrentSequence()), 2);
        else if (pathway.getSequence().getCurrentSequence() > 4)
            spirituality = (int) Math.pow((double) (90 / pathway.getSequence().getCurrentSequence()) * 2, 2);
        else if (pathway.getSequence().getCurrentSequence() < 5)
            spirituality = (int) Math.pow((float) (90 / pathway.getSequence().getCurrentSequence()), 3);
        maxSpirituality = spirituality;
    }

    public void updateActing() {
        actingNeeded = Math.pow((100f / pathway.getSequence().getCurrentSequence()), 2);
        if (actingProgress >= actingNeeded && !digested) {
            digested = true;
            getPlayer().sendMessage("§6You have digested the potion!");
            getPlayer().spawnParticle(Particle.END_ROD, pathway.getBeyonder().getPlayer().getLocation(), 50, 1, 1, 1, 0);
        }
    }

    public void acting(int sequence) {
        if (!digested) {
            actingProgress += 10f / sequence;
        }

        updateActing();
    }

    public void addActing(int actingAdd) {
        if (!digested) {
            actingProgress += actingAdd;
        }

        updateActing();
    }

    public void setActing(int acting) {
        actingProgress = acting;
        updateActing();
    }


    //lostControl: chance of surviving
    public void looseControl(int lostControl, int timeOfLoosingControl) {
        Random random = new Random();
        boolean survives = ((random.nextInt(100) + 1) <= lostControl);

        loosingControl = true;
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * timeOfLoosingControl, 3, false, false));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * timeOfLoosingControl, 3, false, false));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * timeOfLoosingControl, 3, false, false));

        //Damaging player
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (!online || !beyonder || getPlayer() == null) {
                    loosingControl = false;
                    cancel();
                    return;
                }

                if (random.nextInt(25) + 1 == 5 && getPlayer().getHealth() > 2)
                    getPlayer().damage(2);

                counter++;
                if (counter == timeOfLoosingControl * 20) {
                    //When not survives, summons a Warden
                    if (!survives) {
                        Entity rampager = Objects.requireNonNull(getPlayer().getLocation().getWorld()).spawnEntity(getPlayer().getLocation(), EntityType.WARDEN);
                        rampager.setGlowing(true);
                        rampager.setCustomNameVisible(true);
                        rampager.setCustomName(pathway.getStringColor() + getPlayer().getName());
                        getPlayer().setHealth(0);
                        loosingControl = false;
                        removeBeyonder();
                        cancel();
                        return;
                    }
                    loosingControl = false;
                    cancel();
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    //Called from the PotionListener
    public void consumePotion(int sequence, Potion potion) {
        if (sequence >= pathway.getSequence().getCurrentSequence())
            return;

        if (!getPathway().getNameNormalized().equals(potion.getName())) {
            looseControl(0, 10);
            return;
        }
        if (pathway == null) {
            getPlayer().sendMessage("§cYour advancement has failed! You can call yourself lucky to still be alive...");
            return;
        }

        if (!digested) {
            looseControl(5, 12);
        } else {
            switch (getPathway().getSequence().getCurrentSequence() - 1 - sequence) {
                case 0 -> looseControl(93, 20);
                case 1 -> looseControl(50, 20);
                case 2 -> looseControl(30, 20);
                case 3, 4 -> looseControl(20, 16);
                case 5 -> looseControl(1, 16);
                default -> looseControl(0, 10);
            }
        }

        pathway.getSequence().setCurrentSequence(sequence);
        getPlayer().sendMessage(pathway.getItems().getAbilityInfo().get(sequence));
        digested = false;
        actingProgress = 0;
        updateActing();
        updateSpirituality();

        Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(healthIndex[pathway.getSequence().getCurrentSequence()]);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Pathway getPathway() {
        return pathway;
    }

    public void setPathway(Pathway pathway) {
        this.pathway = pathway;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getSpirituality() {
        return spirituality;
    }

    public void setSpirituality(double spirituality) {
        this.spirituality = spirituality;
    }

    public void removeBeyonder() {
        for (Ability a : pathway.getSequence().getAbilities()) {
            a.removeAbility();
        }
        LordOfTheMinecraft.instance.removeBeyonder(getUuid());
        HandlerList.unregisterAll(this);
        beyonder = false;
        pathway.setSequence(null);
        pathway = null;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isBeyonder() {
        return beyonder;
    }

}



