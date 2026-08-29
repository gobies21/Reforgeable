package net.gobies.reforgeable.compat.curios;

import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosCapability;

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
        if (!CommonConfig.CURIO_QUALITIES.get()) return false;
        if (QualityUtil.getConfigItems(stack, CommonConfig.ADDITIONAL_CURIO_QUALITIES)) return true;
        return CURIO_CACHE.computeIfAbsent(stack.getItem(), item -> stack.getCapability(CuriosCapability.ITEM).isPresent());
    }
}