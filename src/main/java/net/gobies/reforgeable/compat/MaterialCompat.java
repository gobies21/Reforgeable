package net.gobies.reforgeable.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class MaterialCompat {

    public static boolean isCompatMaterial(ItemStack gearStack, ItemStack material) {
        if (gearStack.isEmpty() || material.isEmpty()) {
            return false;
        }
        boolean canReforge = false;
        ResourceLocation gearKey = ForgeRegistries.ITEMS.getKey(gearStack.getItem());
        ResourceLocation materialKey = ForgeRegistries.ITEMS.getKey(material.getItem());

        if (gearKey == null || materialKey == null) {
            return false;
        }

        String gearId = gearKey.toString();
        String materialId = materialKey.toString();

        if (gearStack.is(ItemTags.create(new ResourceLocation("moreartifacts:artifacts"))) && materialId.equals("moreartifacts:shadow_dust")) {
            canReforge = true;
        }

        if (gearId.equals("potionrings2:potion_ring") && materialId.equals("minecraft:gold_ingot")) {
            canReforge = true;
        }

        return canReforge;
    }
}