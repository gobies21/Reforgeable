package net.gobies.reforgeable.compat.moreartifacts;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.compat.curios.CuriosCompat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public class MoreArtifactsCompat {

    private static Boolean isLoaded = null;
    private static Class<?> cobaltShieldClass = null;
    private static Class<?> obsidianShieldClass = null;
    private static Class<?> ankhShieldClass = null;
    private static Class<?> frostedShieldClass = null;

    public static boolean isLoaded() {
        if (isLoaded == null) {
            isLoaded = CuriosCompat.isLoaded() && ModList.get().isLoaded("moreartifacts");
            if (isLoaded) {
                try {
                    cobaltShieldClass = Class.forName("net.gobies.moreartifacts.item.artifacts.CobaltShieldItem");
                    obsidianShieldClass = Class.forName("net.gobies.moreartifacts.item.artifacts.ObsidianShieldItem");
                    ankhShieldClass = Class.forName("net.gobies.moreartifacts.item.artifacts.AnkhShieldItem");
                    frostedShieldClass = Class.forName("net.gobies.moreartifacts.item.artifacts.FrostedShieldItem");
                } catch (ClassNotFoundException e) {
                    Reforgeable.LOGGER.error("Could not find More Artifacts shield classes");
                }
            }
        }
        return isLoaded;
    }

    public static boolean isMAShield(ItemStack stack) {
        if (!isLoaded()) return false;
        Item item = stack.getItem();
        return isInstance(item, cobaltShieldClass) || isInstance(item, obsidianShieldClass) || isInstance(item, ankhShieldClass) || isInstance(item, frostedShieldClass);
    }

    private static boolean isInstance(Item item, Class<?> itemClass) {
        return itemClass != null && itemClass.isInstance(item);
    }
}