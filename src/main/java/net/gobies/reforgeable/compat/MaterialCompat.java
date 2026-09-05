package net.gobies.reforgeable.compat;

import net.gobies.reforgeable.compat.ironsspellbooks.SpellbooksCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class MaterialCompat {

    public static boolean isAdditionalMaterial(ItemStack gearStack, ItemStack material) {
        if (gearStack.isEmpty() || material.isEmpty()) {
            return false;
        }
        boolean canReforge = false;
        ResourceLocation gearKey = ForgeRegistries.ITEMS.getKey(gearStack.getItem());
        ResourceLocation materialKey = ForgeRegistries.ITEMS.getKey(material.getItem());

        if (gearKey == null || materialKey == null) {
            return false;
        }

        // TODO: Ice&Fire2

        String gearId = gearKey.toString();
        String materialId = materialKey.toString();

        // Horse Armors
        if (gearId.equals("minecraft:leather_horse_armor") && materialId.equals("minecraft:leather")) canReforge = true;
        if (gearId.equals("minecraft:iron_horse_armor") && materialId.equals("minecraft:iron_ingot")) canReforge = true;
        if (gearId.equals("minecraft:golden_horse_armor") && materialId.equals("minecraft:gold_ingot")) canReforge = true;
        if (gearId.equals("minecraft:diamond_horse_armor") && materialId.equals("minecraft:diamond")) canReforge = true;

        // Compat

        // Misc
        if (gearId.equals("potionrings2:potion_ring") && materialId.equals("minecraft:gold_ingot")) canReforge = true;

        if (gearId.equals("toolbelt:belt") && materialId.equals("minecraft:leather")) canReforge = true;

        // Backpacks
        if (gearId.equals("yyzsbackpack:iron_backpack") && materialId.equals("minecraft:leather")) canReforge = true;
        if (gearId.equals("yyzsbackpack:gold_backpack") && materialId.equals("minecraft:leather")) canReforge = true;
        if (gearId.equals("yyzsbackpack:diamond_backpack") && materialId.equals("minecraft:leather")) canReforge = true;
        if (gearId.equals("yyzsbackpack:netherite_backpack") && materialId.equals("minecraft:leather")) canReforge = true;

        if (gearStack.is(ItemTags.create(new ResourceLocation("moreartifacts:artifacts"))) && materialId.equals("moreartifacts:shadow_dust")) canReforge = true;

        // Irons Spellbooks
        if (SpellbooksCompat.isSpellbook(gearStack.getItem()) && materialId.equals("irons_spellbooks:magic_cloth")) canReforge = true;

        if (gearId.equals("irons_spellbooks:graybeard_staff") && materialId.equals("irons_spellbooks:arcane_essence")) canReforge = true;
        if (gearId.equals("irons_spellbooks:artificers_cane") && materialId.equals("irons_spellbooks:amethyst_shard")) canReforge = true;
        if (gearId.equals("irons_spellbooks:lightning_rod") && materialId.equals("minecraft:copper_ingot")) canReforge = true;
        if (gearId.equals("irons_spellbooks:ice_staff") && materialId.equals("irons_spellbooks:frozen_bone")) canReforge = true;
        if (gearId.equals("irons_spellbooks:blood_staff") && materialId.equals("irons_spellbooks:blood_vial")) canReforge = true;
        if (gearId.equals("irons_spellbooks:pyrium_staff") && materialId.equals("irons_spellbooks:pyrium_ingot")) canReforge = true;

        // Revamped Wolf
        if (gearId.equals("revampedwolf:leather_wolf_armor") && materialId.equals("minecraft:leather")) canReforge = true;
        if (gearId.equals("revampedwolf:iron_wolf_armor") && materialId.equals("minecraft:iron_ingot")) canReforge = true;
        if (gearId.equals("revampedwolf:golf_wolf_armor") && materialId.equals("minecraft:gold_ingot")) canReforge = true;
        if (gearId.equals("revampedwolf:diamond_wolf_armor") && materialId.equals("minecraft:diamond")) canReforge = true;
        if (gearId.equals("revampedwolf:netherite_wolf_armor") && materialId.equals("minecraft:netherite_scrap")) canReforge = true;

        return canReforge;
    }
}