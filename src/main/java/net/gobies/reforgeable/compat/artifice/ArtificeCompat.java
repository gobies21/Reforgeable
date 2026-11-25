package net.gobies.reforgeable.compat.artifice;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.config.CommonConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public class ArtificeCompat {

    private static Boolean isArtificeLoaded = null;
    private static Class<?> gunClass = null;

    public static boolean isLoaded() {
        if (isArtificeLoaded == null) {
            isArtificeLoaded = ModList.get().isLoaded("irons_artifice");
            if (isArtificeLoaded) {
                try {
                    gunClass = Class.forName("io.redspace.irons_artifice.item.GunItem");
                } catch (ClassNotFoundException e) {
                    Reforgeable.LOGGER.error("Could not find gun class", e);
                }
            }
        }
        return isArtificeLoaded;
    }

    public static boolean isGun(Item item) {
        return isLoaded() && gunClass != null && gunClass.isInstance(item);
    }

    public static boolean isGunItem(ItemStack stack) {
        return CommonConfig.GUN_QUALITIES.get() && isGun(stack.getItem());
    }
}