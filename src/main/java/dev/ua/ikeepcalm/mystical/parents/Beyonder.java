package dev.ua.ikeepcalm.mystical.parents;

import cz.foresttech.api.ColorAPI;
import de.tr7zw.nbtapi.NBT;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.listeners.RampagerListener;
import dev.ua.ikeepcalm.mystical.parents.abilities.Ability;
import dev.ua.ikeepcalm.mystical.pathways.fool.FoolPathway;
import dev.ua.ikeepcalm.mystical.pathways.fool.abilities.Hiding;
import dev.ua.ikeepcalm.utils.LoggerUtil;
import lombok.Getter;
import lombok.Setter;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.lapismc.afkplus.playerdata.AFKPlusPlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.awt.Color;
import java.util.List;
import java.util.*;

import static dev.ua.ikeepcalm.LordOfTheMinecraft.bossBarUtil;

public class Beyonder implements Listener {

    @Getter
    protected final UUID uuid;
    private final int[] healthIndex;
    public boolean online;
    @Setter
    @Getter
    private Pathway pathway;
    @Setter
    @Getter
    private double spirituality;
    @Getter
    private double maxSpirituality;
    private double lastSpirituality;
    @Getter
    @Setter
    private double actingProgress;
    @Getter
    private double lastActing;
    @Getter
    private double actingNeeded;
    private boolean digested;
    @Getter
    private boolean beyonder;
    private boolean loosingControl;
    private boolean initializedOnce;
    @Getter
    private Team team;
    private int resurrections;

    public Beyonder(UUID uuid, Pathway pathway, int acting, int spirituality) {
        this.pathway = pathway;
        this.uuid = uuid;

        pathway.setBeyonder(this);

        this.beyonder = true;
        this.online = false;
        this.initializedOnce = false;

        this.loosingControl = false;

        this.lastActing = acting;
        this.lastSpirituality = spirituality;

        this.resurrections = 0;

        this.healthIndex = new int[]{0, 100, 80, 60, 50, 40, 35, 30, 25, 20};

        pathway.init();

        if (getPlayer() == null || !Bukkit.getOnlinePlayers().contains(getPlayer())) return;

        this.initializedOnce = true;

        //acting initializing
        this.digested = false;
        this.actingNeeded = Math.pow((float) (250 / pathway.getSequence().getCurrentSequence()), 2);

        pathway.initItems();
        start();
    }

    @EventHandler
    //Restarts everything when Beyonder rejoins
    public void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid)) return;
        if (!beyonder) return;

        pathway.setBeyonder(this);

        if (!initializedOnce) {
            pathway.initItems();
            initializedOnce = true;
        }

        setAbilitiesShortcut(e.getPlayer());

        start();
    }

    @EventHandler
    //Stops everything when Beyonder leaves
    public void onLeave(PlayerQuitEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid)) return;
        if (!beyonder) return;

        if (!e.getPlayer().getUniqueId().equals(uuid)) return;
        if (!beyonder) return;

        Disguise disguise = DisguiseAPI.getDisguise(e.getPlayer());
        if (disguise != null) {
            disguise.removeDisguise();
            LordOfTheMinecraft.disguises.remove(e.getPlayer().getUniqueId());
        }

        if (loosingControl) {
            if (pathway.getSequence().getCurrentSequence() <= 4) {
                getPlayer().getWorld().showTitle(Title.title(Component.text("Смерть напівбога").color(NamedTextColor.GOLD), Component.text("Цей світ здригається...").color(NamedTextColor.GOLD)));
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                for (Player player : players) {
                    player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
                }
            }

            LivingEntity rampager = (LivingEntity) Objects.requireNonNull(getPlayer().getLocation().getWorld()).spawnEntity(getPlayer().getLocation(), EntityType.WARDEN);
            rampager.setGlowing(true);
            rampager.setCustomNameVisible(true);
            rampager.setCustomName(pathway.getStringColor() + getPlayer().getName());
            rampager.setMetadata("pathway", new FixedMetadataValue(LordOfTheMinecraft.instance, pathway.getNameNormalized()));
            rampager.setMetadata("sequence", new FixedMetadataValue(LordOfTheMinecraft.instance, pathway.getSequence().getCurrentSequence()));
            Objects.requireNonNull(rampager.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(calculateScaledHealth(pathway.getSequence().getCurrentSequence(), 250));
            rampager.setHealth(calculateScaledHealth(pathway.getSequence().getCurrentSequence(), 250));
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
            getPlayer().setHealth(0);
            loosingControl = false;
            removeBeyonder();
            bossBarUtil.removePlayer(getPlayer());
            return;
        }

        online = false;
        lastSpirituality = spirituality;
        LordOfTheMinecraft.instance.saveBeyonder(this);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid)) return;
        if (!beyonder) return;

        setAbilitiesShortcut(e.getPlayer());
    }

    @EventHandler
    //Removes Items on Death
    public void onDeath(PlayerDeathEvent e) {
        if (!beyonder) return;
        if (e.getEntity() != getPlayer()) return;
        Player p = e.getEntity();
        Location deathLoc = p.getLocation();

        if (pathway.getSequence() == null) return;

        actingProgress -= actingProgress * 0.2;
        if (actingProgress < 0) actingProgress = 0;

        if (pathway instanceof FoolPathway && pathway.getSequence().getCurrentSequence() <= 2 && resurrections < 5 && !loosingControl) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (deathLoc.getWorld() == null) return;

                    for (Entity entity : deathLoc.getWorld().getNearbyEntities(deathLoc, 20, 20, 20)) {
                        if (!(entity instanceof Item)) continue;

                        entity.remove();
                    }

                    for (ItemStack item : e.getDrops()) {
                        p.getInventory().addItem(item);
                    }

                    p.teleport(deathLoc);

                    for (Ability ability : pathway.getSequence().getAbilities()) {
                        if (ability instanceof Hiding hiding) hiding.useAbility();
                    }

                    resurrections++;
                }
            }.runTaskLater(LordOfTheMinecraft.instance, 2);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (deathLoc.getWorld() == null) return;

                for (Entity entity : deathLoc.getWorld().getNearbyEntities(deathLoc, 20, 20, 20)) {
                    if (!(entity instanceof Item item)) continue;

                    for (ItemStack itemStack : pathway.getItems().returnItemsFromSequence(pathway.getSequence().getCurrentSequence())) {
                        if (itemStack.isSimilar(item.getItemStack())) entity.remove();
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
            actingNeeded = Math.pow((float) (250 / pathway.getSequence().getCurrentSequence()), 2);
        }

        updateSpirituality();

        if (lastSpirituality != 0) {
            spirituality = lastSpirituality;
            lastSpirituality = 0;
        }

        if (lastActing != 0) {
            actingProgress = lastActing;
            lastActing = 0;
        }

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

                if (loosingControl) return;

                Player p = getPlayer();

                //Call onHold() function in Sequence
                pathway.getSequence().onHold(p.getInventory().getItemInMainHand());
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 3);

        //constant loop
        new BukkitRunnable() {
            int counter = 0;
            int actingCounter = 0;
            boolean bossBar = false;

            @Override
            public void run() {
                //Cancel and return if player, sequence is null or player is not online
                if (!beyonder || !online || getPlayer() == null || pathway.getSequence() == null) {
                    cancel();
                    return;
                }

                actingCounter++;
                if (actingCounter >= 50 * 15) {
                    AFKPlusPlayer player = LordOfTheMinecraft.afkPlus.getPlayer(uuid);
                    if (!player.isAFK()) {
                        addActing(3);
                    } else {
                        LordOfTheMinecraft.instance.log("Player " + getPlayer().getName() + " is marked as AFK and is ignored for acting.");
                    }
                    actingCounter = 0;
                }

                //scoreboard
                counter++;

                if (spirituality <= maxSpirituality * 0.1 && !loosingControl) {
                    looseControl(20, 10);
                }

                pathway.getSequence().run();

                //spirituality handling
                if (spirituality < maxSpirituality && counter >= 8) {
                    counter = 0;
                    spirituality += (maxSpirituality / 200);
                    if (spirituality > maxSpirituality) spirituality = maxSpirituality;
                }

                if (spirituality < maxSpirituality) {
                    if (!bossBar) {
                        bossBar = true;
                        bossBarUtil.addPlayer(getPlayer(), "§6Духовність: " + (int) spirituality + "§6/§e" + (int) maxSpirituality, BarColor.BLUE, BarStyle.SOLID, (float) (spirituality / maxSpirituality));
                    } else {
                        bossBarUtil.setProgress(getPlayer(), (float) (spirituality / maxSpirituality));
                        bossBarUtil.setTitle(getPlayer(), "§6Духовність: " + (int) spirituality + "§6/§e" + (int) maxSpirituality);
                    }
                } else {
                    bossBarUtil.removePlayer(getPlayer());
                    bossBar = false;
                }

                if (loosingControl) return;

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
        if (pathway.getSequence().getCurrentSequence() > 8) {
            spirituality = (int) Math.pow((float) (90 / pathway.getSequence().getCurrentSequence()), 2);
        } else if (pathway.getSequence().getCurrentSequence() > 6) {
            spirituality = (int) Math.pow((float) (90 / pathway.getSequence().getCurrentSequence()) * 1.5, 2);
        } else if (pathway.getSequence().getCurrentSequence() > 4) {
            spirituality = (int) Math.pow((float) (90 / pathway.getSequence().getCurrentSequence()) * 2.0, 2);
        } else if (pathway.getSequence().getCurrentSequence() > 2) {
            spirituality = (int) Math.pow((double) (90 / pathway.getSequence().getCurrentSequence()) * 2.5, 2);
        } else {
            spirituality = (int) Math.pow((float) (90 / pathway.getSequence().getCurrentSequence()) * 3, 2);
        }

        maxSpirituality = spirituality;
    }

    public void verifyActing() {
        if (actingNeeded == 0) {
            actingNeeded = Math.pow((250f / pathway.getSequence().getCurrentSequence()), 2);
        }

        int percentage = (int) ((actingProgress / actingNeeded) * 100);

        if (actingProgress >= actingNeeded && !digested) {
            digested = true;
            getPlayer().sendMessage("§6Ви засвоїли магічне зілля! Повністю...");
            getPlayer().spawnParticle(Particle.END_ROD, pathway.getBeyonder().getPlayer().getLocation(), 50, 1, 1, 1, 0);
        } else {
            int chance = new Random().nextInt(5);
            if (chance == 1) {
                getPlayer().sendActionBar(Component.text("Засвоєння: " + percentage + "%").color(TextColor.color(2, 255, 131)));
            }
        }
    }


    public void updateActing(int sequence) {
        if (!digested) {
            actingProgress += 10f / sequence;
        }

        verifyActing();
    }

    public void addActing(int actingAdd) {
        if (!digested) {
            actingProgress += actingAdd;
        }

        verifyActing();
    }

    private void setAbilitiesShortcut(Player player) {
        ItemStack item = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorAPI.colorize(LordOfTheMinecraft.beyonders.get(player.getUniqueId()).getPathway().getStringColor()) + "Знання");
        meta.setCustomModelData(pathway.getPathwayInt() + 1);
        item.setItemMeta(meta);
        NBT.modify(item, (nbt) -> {
            nbt.setBoolean("openAbilities", true);
        });

        ItemStack shortcut = player.getInventory().getItem(9);
        if (shortcut != null) {
            if (shortcut.getType() != Material.AIR && shortcut.getType() != Material.GLOWSTONE_DUST) {
                player.getWorld().dropItemNaturally(player.getLocation(), shortcut);
            }
        }

        player.getInventory().setItem(9, item);
    }

    //lostControl: chance of surviving
    public void looseControl(int lostControl, int timeOfLoosingControl) {
        Random random = new Random();
        int randomInt = random.nextInt(100) + 1;
        boolean survives = (randomInt <= lostControl);
        LoggerUtil.logPlayerLooseControl(getPlayer(), this, survives);
        loosingControl = true;
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * timeOfLoosingControl, 3, false, false));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * timeOfLoosingControl, 3, false, false));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.OOZING, 20 * timeOfLoosingControl, 3, false, false));
        getPlayer().showTitle(Title.title(Component.text("Що?...."), Component.text("Мені... погано....")));

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

                if (random.nextInt(25) + 1 == 5 && getPlayer().getHealth() > 2) {
                    if (random.nextBoolean()) {
                        getPlayer().sendMessage(Component.text("Ні... Ні, ні, НІ, НІ, НІ! ТІЛЬКИ НЕ ЦЕ, НЕ ЗАЛИШАЙ МЕНЕ! Я БЛАГАЮ!!!").color(TextColor.color(Color.RED.getRGB())));
                    } else {
                        getPlayer().sendMessage(Component.text("Я... я... Це неможливо! Я ВІДМОВЛЯЮСЯ У ЦЕ ВІРИТИ!").color(TextColor.color(Color.RED.getRGB())));
                    }
                    getPlayer().damage(2);
                }

                counter++;
                if (counter >= timeOfLoosingControl * 20) {
                    //When not survives, summons a Warden
                    if (!survives) {
                        if (pathway.getSequence().getCurrentSequence() <= 4) {
                            getPlayer().getWorld().showTitle(Title.title(Component.text("Смерть напівбога").color(NamedTextColor.GOLD), Component.text("Цей світ здригається...").color(NamedTextColor.GOLD)));
                            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                            for (Player player : players) {
                                player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
                            }
                        }

                        LivingEntity rampager = (LivingEntity) Objects.requireNonNull(getPlayer().getLocation().getWorld()).spawnEntity(getPlayer().getLocation(), EntityType.WARDEN);
                        rampager.setGlowing(true);
                        rampager.setCustomNameVisible(true);
                        rampager.setCustomName(pathway.getStringColor() + getPlayer().getName());
                        rampager.setMetadata("pathway", new FixedMetadataValue(LordOfTheMinecraft.instance, pathway.getNameNormalized()));
                        rampager.setMetadata("sequence", new FixedMetadataValue(LordOfTheMinecraft.instance, pathway.getSequence().getCurrentSequence()));
                        Objects.requireNonNull(rampager.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(calculateScaledHealth(pathway.getSequence().getCurrentSequence(), 200));
                        rampager.setHealth(calculateScaledHealth(pathway.getSequence().getCurrentSequence(), 200));

                        if (pathway.getSequence().getCurrentSequence() < 7) {
                            rampager.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999999, 1, false, false));
                        }

                        if (pathway.getSequence().getCurrentSequence() < 5) {
                            rampager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, false, false));
                        }

                        if (pathway.getSequence().getCurrentSequence() < 3) {
                            rampager.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999999, 1, false, false));
                        }

                        if (pathway.getSequence().getCurrentSequence() < 2) {
                            rampager.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 999999, 1, false, false));
                        }

                        Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
                        LordOfTheMinecraft.instance.registerEvents(new RampagerListener((Warden) rampager));

                        getPlayer().setHealth(0);
                        loosingControl = false;
                        removeBeyonder();
                        bossBarUtil.removePlayer(getPlayer());
                        cancel();
                        return;
                    }
                    loosingControl = false;
                    cancel();
                    spirituality = maxSpirituality * 0.2;
                    getPlayer().sendMessage(Component.text("Вам вдається заспокоїтися...").color(TextColor.color(Color.GREEN.getRGB())));
                }
            }
        }.runTaskTimer(LordOfTheMinecraft.instance, 0, 1);
    }

    public static int calculateScaledHealth(int value, int baseHealth) {
        double multiplier = switch (value) {
            case 9 -> 1.0;
            case 8 -> 1.2;
            case 7 -> 1.5;
            case 6 -> 1.8;
            case 5 -> 2.1;
            case 4 -> 2.5;
            case 3 -> 3.0;
            case 2 -> 3.5;
            case 1 -> 4.0;
            default -> 1.0;
        };
        int health = (int) (baseHealth * multiplier);
        return Math.min(health, 375);
    }

    //Called from the PotionListener
    public void consumePotion(int sequence, Potion potion) {
        if (sequence > pathway.getSequence().getCurrentSequence()) return;

        if (!getPathway().getNameNormalized().equals(potion.getName())) {
            looseControl(0, 10);
            LoggerUtil.logPlayerPotion(getPlayer(), this, sequence, potion.getName(), false);
            return;
        }

        if (pathway == null) {
            getPlayer().sendMessage("§cНевдача! Можете вважати, що вам пощастило, що ви залишилися живі...");
            return;
        }

        if (sequence == pathway.getSequence().getCurrentSequence()) {
            if (digested) {
                LoggerUtil.logPlayerPotion(getPlayer(), this, sequence, potion.getName(), false);
                getPlayer().sendMessage("§cВи вже засвоїли це зілля!");
                looseControl(90, 20);
                return;
            }
            if (actingNeeded == 0) {
                verifyActing();
            }
            LoggerUtil.logPlayerPotion(getPlayer(), this, sequence, potion.getName(), true);
            actingProgress += actingNeeded * 0.2;
            getPlayer().sendMessage("§aВи відчуваєте, як зілля покращило ваше розуміння теперішнього стану речей.");
            return;
        }

        if (!digested) {
            LoggerUtil.logPlayerPotion(getPlayer(), this, sequence, potion.getName(), false);
            looseControl(10, 12);
        } else {
            switch (getPathway().getSequence().getCurrentSequence() - 1 - sequence) {
                case 0 -> looseControl(98, 20);
                case 1, 2 -> looseControl(1, 20);
                case 3, 4, 5 -> looseControl(1, 16);
                default -> looseControl(0, 10);
            }
        }

        pathway.getSequence().setCurrentSequence(sequence);
        LoggerUtil.logPlayerPotion(getPlayer(), this, sequence, potion.getName(), true);
        digested = false;
        actingProgress = 0;
        verifyActing();
        updateSpirituality();
        sendNotification(sequence);

        Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(healthIndex[pathway.getSequence().getCurrentSequence()]);
    }

    private void sendNotification(int sequence) {
        Collection<? extends Player> playerList = Bukkit.getOnlinePlayers();
        switch (sequence) {
            case 4 -> {
                Component mainTitle = Component.text("Народження напів-бога").color(NamedTextColor.DARK_PURPLE);
                Component subTitle = Component.text("що готує для нього доля?").color(NamedTextColor.DARK_PURPLE);
                Title title = Title.title(mainTitle, subTitle);
                Sound sound = Sound.ENTITY_ENDER_DRAGON_GROWL;
                for (Player player : playerList) {
                    player.showTitle(title);
                    player.playSound(player.getLocation(), sound, 1, 1);
                }
            }
            case 3 -> {
                Component mainTitle = Component.text("Поява святого").color(NamedTextColor.DARK_AQUA);
                Component subTitle = Component.text("що готує для нього доля?").color(NamedTextColor.DARK_AQUA);
                Title title = Title.title(mainTitle, subTitle);
                Sound sound = Sound.ENTITY_WARDEN_ROAR;
                for (Player player : playerList) {
                    player.showTitle(title);
                    player.playSound(player.getLocation(), sound, 1, 1);
                }
            }
            case 2 -> {
                Component mainTitle = Component.text("Піднесення до Янгола").color(NamedTextColor.DARK_GREEN);
                Component subTitle = Component.text("шлях до божественності?").color(NamedTextColor.DARK_GREEN);
                Title title = Title.title(mainTitle, subTitle);
                Sound sound = Sound.ENTITY_RAVAGER_ROAR;
                for (Player player : playerList) {
                    player.showTitle(title);
                    player.playSound(player.getLocation(), sound, 1, 1);
                }
            }
            case 1 -> {
                Component mainTitle = Component.text("Король Янголів").color(NamedTextColor.DARK_RED);
                Component subTitle = Component.text("світ встає на коліна").color(NamedTextColor.DARK_RED);
                Title title = Title.title(mainTitle, subTitle);
                Sound sound = Sound.EVENT_RAID_HORN;
                for (Player player : playerList) {
                    player.showTitle(title);
                    player.playSound(player.getLocation(), sound, 1, 1);
                }
            }
            default -> {
                return;
            }
        }
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
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

}



