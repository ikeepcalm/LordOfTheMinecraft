package dev.ua.ikeepcalm.optional.crafts;

import dev.ua.ikeepcalm.LordOfTheMinecraft;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecuttingRecipe;

public class StonecutterCrafts {

    public StonecutterCrafts() {
        addQuartzCraft();
    }

    private void addQuartzCraft() {
        ItemStack quartz = new ItemStack(Material.QUARTZ);
        quartz.setAmount(2);
        StonecuttingRecipe quartzRecipe = new StonecuttingRecipe(
                new NamespacedKey(LordOfTheMinecraft.instance, "quartz"),
                quartz,
                Material.QUARTZ_BLOCK
        );
        LordOfTheMinecraft.instance.getServer().addRecipe(quartzRecipe);
    }

}
