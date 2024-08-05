package dev.ua.ikeepcalm.cmds;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import dev.ua.ikeepcalm.mystical.parents.Beyonder;
import dev.ua.ikeepcalm.utils.GeneralItemsUtil;
import dev.ua.ikeepcalm.utils.GeneralPurposeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class TestCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (args.length == 0) return true;

        if (args[0].equalsIgnoreCase("stats")) {
            int averageSequence = 0;
            List<Beyonder> beyonders = LordOfTheMinecraft.beyonders.values().stream().toList();

            if (beyonders.isEmpty()) {
                sender.sendMessage("No beyonders found.");
                return true;
            }

            for (Beyonder beyonder : beyonders) {
                averageSequence += beyonder.getPathway().getSequence().getCurrentSequence();
            }
            averageSequence /= beyonders.size();

            int sun = 0, fool = 0, door = 0, demoness = 0, tyrant = 0, priest = 0;
            for (Beyonder beyonder : beyonders) {
                switch (beyonder.getPathway().getNameNormalized()) {
                    case "sun" -> sun++;
                    case "fool" -> fool++;
                    case "door" -> door++;
                    case "demoness" -> demoness++;
                    case "tyrant" -> tyrant++;
                    case "priest" -> priest++;
                }
            }

            int totalBeyonders = beyonders.size();

            double sunPercentage = ((double) sun / totalBeyonders) * 100;
            double foolPercentage = ((double) fool / totalBeyonders) * 100;
            double doorPercentage = ((double) door / totalBeyonders) * 100;
            double demonessPercentage = ((double) demoness / totalBeyonders) * 100;
            double tyrantPercentage = ((double) tyrant / totalBeyonders) * 100;
            double priestPercentage = ((double) priest / totalBeyonders) * 100;

            sender.sendMessage("Beyonder Stats:");
            sender.sendMessage("Average Sequence: " + averageSequence);
            sender.sendMessage("Sun Pathway: " + String.format("%.2f", sunPercentage) + "%");
            sender.sendMessage("Fool Pathway: " + String.format("%.2f", foolPercentage) + "%");
            sender.sendMessage("Door Pathway: " + String.format("%.2f", doorPercentage) + "%");
            sender.sendMessage("Demoness Pathway: " + String.format("%.2f", demonessPercentage) + "%");
            sender.sendMessage("Tyrant Pathway: " + String.format("%.2f", tyrantPercentage) + "%");
            sender.sendMessage("Priest Pathway: " + String.format("%.2f", priestPercentage) + "%");
            sender.sendMessage("Total Beyonders: " + totalBeyonders);
        }

        if (!(sender instanceof Player p)) return true;

        if (args[0].equalsIgnoreCase("char")) {
            p.getInventory().addItem(LordOfTheMinecraft.instance.getCharacteristic().getCharacteristic(GeneralPurposeUtil.parseInt(args[2]), args[1], "§" + args[3]));
        }

        if (args[0].equalsIgnoreCase("recipe")) {
            Beyonder beyonder = LordOfTheMinecraft.beyonders.get(p.getUniqueId());
            p.getInventory().addItem(LordOfTheMinecraft.instance.getRecipe().getRecipeForSequence(LordOfTheMinecraft.instance.getPotions().get(beyonder.getPathway().getPathwayInt()), GeneralPurposeUtil.parseInt(args[1])));
        }

        if (args[0].equalsIgnoreCase("potion")) {
            p.getInventory().addItem(LordOfTheMinecraft.instance.getPotions().get(LordOfTheMinecraft.beyonders.get(p.getUniqueId()).getPathway().getPathwayInt()).returnPotionForSequence(GeneralPurposeUtil.parseInt(args[1])));
        }

        if (args[0].equalsIgnoreCase("acting")) {
            Beyonder beyonder = LordOfTheMinecraft.beyonders.get(p.getUniqueId());
            int amount = GeneralPurposeUtil.parseInt(args[1]);
            beyonder.setActingProgress(amount);
        }

        if (args[0].equalsIgnoreCase("vacting")) {
            Beyonder beyonder = LordOfTheMinecraft.beyonders.get(p.getUniqueId());
            p.sendMessage("Your acting progress: " + beyonder.getActingProgress());
        }

        if (args[0].equalsIgnoreCase("moon")) {
            LordOfTheMinecraft.instance.getBeyonderMobsHandler().toggleRedMoon();
        }

        if (args[0].equalsIgnoreCase("ingredients")) {
            int pathway = parsePathway(args[1]);
            if (pathway == -1) {
                p.sendMessage("Invalid pathway");
                return true;
            }
            int sequence = GeneralPurposeUtil.parseInt(args[2]);
            ItemStack[] ingredients = LordOfTheMinecraft.instance.getPotions().get(pathway).getMainIngredients(sequence);
            ItemStack[] supplementaryIngredients = LordOfTheMinecraft.instance.getPotions().get(pathway).getSupplIngredients(sequence);
            for (ItemStack ingredient : ingredients) {
                p.getInventory().addItem(ingredient);
            }

            for (ItemStack ingredient : supplementaryIngredients) {
                p.getInventory().addItem(ingredient);
            }
        }

        if (args[0].equalsIgnoreCase("tome")) {
            p.getInventory().addItem(GeneralItemsUtil.getRandomGrimoire());
        }

        return true;
    }

    private int parsePathway(String pathway) {
        switch (pathway) {
            case "sun" -> {
                return 0;
            }
            case "fool" -> {
                return 1;
            }
            case "door" -> {
                return 2;
            }
            case "demoness" -> {
                return 3;
            }
            case "tyrant" -> {
                return 4;
            }
            case "priest" -> {
                return 5;
            }
            default -> {
                return -1;
            }
        }
    }

}
