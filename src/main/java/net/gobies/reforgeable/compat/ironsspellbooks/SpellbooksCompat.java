package net.gobies.reforgeable.compat.ironsspellbooks;

import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import net.gobies.reforgeable.config.CommonConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public class SpellbooksCompat {

    private static Boolean isSpellbooksLoaded = null;

    public static boolean isLoaded() {
        if (isSpellbooksLoaded == null) {
            isSpellbooksLoaded = ModList.get().isLoaded("irons_spellbooks");
        }
        return isSpellbooksLoaded;
    }

    public static boolean isStaff(Item item) {
        if (isSpellbooksLoaded) {
            return item instanceof StaffItem;
        }
        return false;
    }

    public static boolean isSpellbook(Item item) {
        if (isSpellbooksLoaded) {
            return item instanceof SpellBook;
        }
        return false;
    }

    public static boolean isMagicItem(ItemStack stack) {
        return (CommonConfig.STAFF_QUALITIES.get() && isStaff(stack.getItem())) || (CommonConfig.SPELLBOOK_QUALITIES.get() && isSpellbook(stack.getItem()));
    }
}