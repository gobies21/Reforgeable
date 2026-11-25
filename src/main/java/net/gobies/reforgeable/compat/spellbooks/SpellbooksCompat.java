package net.gobies.reforgeable.compat.spellbooks;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.config.CommonConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public class SpellbooksCompat {

    private static Boolean isSpellbooksLoaded = null;
    private static Class<?> staffClass = null;
    private static Class<?> spellBookClass = null;

    public static boolean isLoaded() {
        if (isSpellbooksLoaded == null) {
            isSpellbooksLoaded = ModList.get().isLoaded("irons_spellbooks");
            if (isSpellbooksLoaded) {
                try {
                    staffClass = Class.forName("io.redspace.ironsspellbooks.item.weapons.StaffItem");
                    spellBookClass = Class.forName("io.redspace.ironsspellbooks.item.SpellBook");
                } catch (ClassNotFoundException e) {
                    Reforgeable.LOGGER.error("Could not find spellbook or staff classes", e);
                }
            }
        }
        return isSpellbooksLoaded;
    }

    public static boolean isStaff(Item item) {
        return isLoaded() && staffClass != null && staffClass.isInstance(item);
    }

    public static boolean isSpellbook(Item item) {
        return isLoaded() && spellBookClass != null && spellBookClass.isInstance(item);
    }

    public static boolean isMagicItem(ItemStack stack) {
        return (CommonConfig.STAFF_QUALITIES.get() && isStaff(stack.getItem())) || (CommonConfig.SPELLBOOK_QUALITIES.get() && isSpellbook(stack.getItem()));
    }
}