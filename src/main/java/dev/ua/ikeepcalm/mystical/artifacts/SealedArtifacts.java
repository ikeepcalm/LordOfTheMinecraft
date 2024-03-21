package dev.ua.ikeepcalm.mystical.artifacts;

import dev.ua.ikeepcalm.utils.AbilityInitHandUtil;
import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.abilitiies.NpcAbility;
import dev.ua.ikeepcalm.mystical.artifacts.negativeEffects.NegativeEffects;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SealedArtifacts implements CommandExecutor {

    private final HashMap<Material, String> artifactMaterials = new HashMap<>();
    private final HashMap<Integer, String[]> pathwayNames = new HashMap<>();

    private final NegativeEffects negativeEffects;

    public SealedArtifacts() {
        Objects.requireNonNull(LordOfTheMinecraft.instance.getCommand("artifact")).setExecutor(this);

        String[][] materials = {
                {"STICK", "Staff"},
                {"CHEST", "Box"},
                {"DIAMOND_SWORD", "Blade"},
                {"STONE_SWORD", "Rusty Blade"},
                {"ENDER_EYE", "Eye"},
                {"SNOWBALL", "Orb"},
                {"BOOK", "Tome"},
                {"BAMBOO", "Cane"},
                {"SPECTRAL_ARROW", "Spear"},
                {"AMETHYST_SHARD", "Crystal"},
                {"GLOWSTONE_DUST", "Dust"},
                {"ECHO_SHARD", "Shard"},
                {"NETHER_STAR", "Star"},
                {"EXPERIENCE_BOTTLE", "Potion"},
                {"BONE", "Remains"}
        };
        for (String[] mapping : materials) {
            artifactMaterials.put(Material.getMaterial(mapping[0]), mapping[1]);
        }

        addToPathwayNames(0,
                "",
                "Gold",
                "Light",
                "Justice",
                "Holiness",
                "Purification",
                "Notarization",
                "Light",
                "Bronze"
        );

        addToPathwayNames(1,
                "",
                "Mystery",
                "Miracles",
                "History",
                "Weirdness",
                "the Nimblewright",
                "the Shapeshifter",
                "Magic",
                "the Clown",
                "Foresight"
        );

        addToPathwayNames(2,
                "",
                "Stars",
                "Stars",
                "Wandering",
                "Secret",
                "Traveling",
                "Recording",
                "Astrology",
                "Tricks",
                "the Free"
        );

        addToPathwayNames(3,
                "",
                "Apocalypse",
                "Catastrophe",
                "Stone",
                "Illness",
                "Illness",
                "Beauty",
                "the Sorceress",
                "Instigation",
                "the Knife"
        );

        addToPathwayNames(4,
                "",
                "the Thunder God",
                "Calamity",
                "the Sea",
                "Storm",
                "Thunder",
                "Wind",
                "Water",
                "Rage",
                "the Pirate"
        );

        negativeEffects = new NegativeEffects();
    }

    private Material getRandomMaterial() {
        List<Material> keysAsArray = new ArrayList<>(artifactMaterials.keySet());
        Random random = new Random();
        return keysAsArray.get(random.nextInt(keysAsArray.size()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(s instanceof Player p))
            return true;

        Random random = new Random();

        int pathway = random.nextInt(pathwayNames.size());

        p.getInventory().addItem(generateArtifact(pathway, random.nextInt(1, 10), true));
        return true;
    }

    public ItemStack generateArtifact(int pathway, int sequence, boolean isRandom) {
        List<NpcAbility> abilities = AbilityInitHandUtil.getSequenceAbilities(pathway, sequence);
        Random random = new Random();

        while (abilities.isEmpty()) {
            if (isRandom) {
                sequence = random.nextInt(AbilityInitHandUtil.getAbilities().get(pathway).size());
                abilities = AbilityInitHandUtil.getSequenceAbilities(pathway, sequence);
            } else {
                sequence++;
                if (sequence > 9)
                    return null;
            }
        }

        NpcAbility ability = abilities.get(random.nextInt(abilities.size()));

        Material material = getRandomMaterial();
        String name = artifactMaterials.get(material) + " of " + pathwayNames.get(pathway)[sequence];

        return new SealedArtifact(material, name, pathway, ability, negativeEffects, true).getItem();
    }

    private void addToPathwayNames(int pathway, String... names) {
        pathwayNames.put(pathway, names);
    }
}
