package net.gobies.reforgeable.compat.curios;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.util.Map;
import java.util.WeakHashMap;

public class CuriosCompat {

    private static Boolean isCuriosLoaded = null;
    private static final Map<Item, Boolean> CURIO_CACHE = new WeakHashMap<>();

    public static boolean isLoaded() {
        if (isCuriosLoaded == null) {
            isCuriosLoaded = ModList.get().isLoaded("curios");
        }
        return isCuriosLoaded;
    }

    public static boolean isCurio(ItemStack stack) {
        if (stack.isEmpty() || !isLoaded()) return false;
        Item item = stack.getItem();
        return CURIO_CACHE.computeIfAbsent(item, i -> stack.getTags().anyMatch(itemTagKey -> itemTagKey.location().getNamespace().equals("curios")));
    }
}