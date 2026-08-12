package net.gobies.reforgeable.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class QualityCompat {

    public static boolean isPetArmor(ItemStack stack) {
        ResourceLocation materialKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        boolean isPetArmor = false;

        String stackId = Objects.requireNonNull(materialKey).toString();
        if (stackId.equals("revampedwolf:leather_wolf_armor")) isPetArmor = true;
        if (stackId.equals("revampedwolf:iron_wolf_armor"))  isPetArmor = true;
        if (stackId.equals("revampedwolf:golf_wolf_armor")) isPetArmor = true;
        if (stackId.equals("revampedwolf:diamond_wolf_armor")) isPetArmor = true;
        if (stackId.equals("revampedwolf:netherite_wolf_armor")) isPetArmor = true;

        return isPetArmor;
    }
}
