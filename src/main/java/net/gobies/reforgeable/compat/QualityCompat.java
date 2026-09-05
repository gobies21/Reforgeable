package net.gobies.reforgeable.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class QualityCompat {

    public static boolean isPetArmor(ItemStack stack) {
        ResourceLocation materialKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        boolean isPetArmor = false;

        String gearId = materialKey.toString();
        if (gearId.equals("revampedwolf:leather_wolf_armor")) isPetArmor = true;
        if (gearId.equals("revampedwolf:iron_wolf_armor"))  isPetArmor = true;
        if (gearId.equals("revampedwolf:golden_wolf_armor")) isPetArmor = true;
        if (gearId.equals("revampedwolf:diamond_wolf_armor")) isPetArmor = true;
        if (gearId.equals("revampedwolf:netherite_wolf_armor")) isPetArmor = true;

        return isPetArmor;
    }

    public static boolean isCurio(ItemStack stack) {
        ResourceLocation materialKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        boolean isCurio = false;

        String gearId = materialKey.toString();
        if (gearId.equals("toolbelt:belt")) isCurio = true;

        if (gearId.equals("yyzsbackpack:iron_backpack")) isCurio = true;
        if (gearId.equals("yyzsbackpack:gold_backpack")) isCurio = true;
        if (gearId.equals("yyzsbackpack:diamond_backpack")) isCurio = true;
        if (gearId.equals("yyzsbackpack:netherite_backpack")) isCurio = true;

        return isCurio;
    }
}
