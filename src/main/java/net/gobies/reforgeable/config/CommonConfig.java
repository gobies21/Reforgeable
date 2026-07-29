package net.gobies.reforgeable.config;

import net.gobies.reforgeable.Reforgeable;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reforgeable.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    private static final String FILENAME = "reforgeable/common.toml";

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<Double> NO_QUALITY_CHANCE;
    public static float no_quality_chance;
    public static ForgeConfigSpec.ConfigValue<String> GLOBAL_REFORGE_MATERIAL;
    public static String global_reforge_material;
    public static ForgeConfigSpec.ConfigValue<Integer> QUALITY_UPDATE_RATE;
    public static int quality_update_rate;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> REFORGE_MATERIALS;
    public static List<? extends String> reforge_materials;
    public static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_ANTI_SKIP;
    public static boolean enable_anti_skip;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_SHIELD_QUALITIES;
    public static List<? extends String> additional_shield_qualities;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_PET_QUALITIES;
    public static List<? extends String> additional_pet_qualities;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_WEAPON_QUALITIES;
    public static List<? extends String> additional_weapon_qualities;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_TOOL_QUALITIES;
    public static List<? extends String> additional_tool_qualities;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_BOW_QUALITIES;
    public static List<? extends String> additional_bow_qualities;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_ROD_QUALITIES;
    public static List<? extends String> additional_rod_qualities;


    @SubscribeEvent
    static void onLoad(ModConfigEvent.Loading configEvent) {
        if (configEvent.getConfig().getFileName().equals(FILENAME)) {
            no_quality_chance = NO_QUALITY_CHANCE.get().floatValue();
            global_reforge_material = GLOBAL_REFORGE_MATERIAL.get();
            quality_update_rate = QUALITY_UPDATE_RATE.get();
            reforge_materials = REFORGE_MATERIALS.get();
            enable_anti_skip = ENABLE_ANTI_SKIP.get();
            additional_shield_qualities = ADDITIONAL_SHIELD_QUALITIES.get();
            additional_pet_qualities = ADDITIONAL_PET_QUALITIES.get();
            additional_weapon_qualities = ADDITIONAL_WEAPON_QUALITIES.get();
            additional_tool_qualities = ADDITIONAL_TOOL_QUALITIES.get();
            additional_bow_qualities = ADDITIONAL_BOW_QUALITIES.get();
            additional_rod_qualities = ADDITIONAL_ROD_QUALITIES.get();
        }
    }

    static {
        BUILDER.push("General");
        NO_QUALITY_CHANCE = BUILDER.comment("Chance that items do not receive a quality, higher values makes qualities rarer").defineInRange("Normal_Chance", 0.5, 0.0, 1.0);
        GLOBAL_REFORGE_MATERIAL = BUILDER.comment("Item used to globally reforge any items").define("Material", "minecraft:nether_star");
        QUALITY_UPDATE_RATE = BUILDER.comment("The rate at which items are checked for qualities in ticks, lower values may cause performance issues").define("Update_Rate", 5);
        REFORGE_MATERIALS = BUILDER.comment("List of materials that are used to reforge specific items, supports tags (e.g., minecraft:trident=minecraft:iron_ingot, #forge:tools/shields=minecraft:iron_ingot etc...)").defineList("Reforge_Materials", List.of(), s -> s instanceof String);
        ENABLE_ANTI_SKIP = BUILDER.comment("Enable anti skip, makes the reforging button not work for a very short duration after getting the lowest weighted quality").define("Enable_Anti_Skip", true);
        BUILDER.pop();

        BUILDER.comment("Supports item ids or item tags eg... #forge:shields, 'minecraft:trident'").push("Item_Lists");
        ADDITIONAL_SHIELD_QUALITIES = BUILDER.comment("List of items that should be considered as shields").defineList("Additional_Shields", List.of(), s -> s instanceof String);
        ADDITIONAL_PET_QUALITIES = BUILDER.comment("List of items that should be considered as pet armor").defineList("Additional_Pet_Armor", List.of(), s -> s instanceof String);
        ADDITIONAL_WEAPON_QUALITIES = BUILDER.comment("List of items that should be considered as weapons").defineList("Additional_Weapons", List.of(), s -> s instanceof String);
        ADDITIONAL_TOOL_QUALITIES = BUILDER.comment("List of items that should be considered as tools").defineList("Additional_Tool", List.of(), s -> s instanceof String);
        ADDITIONAL_BOW_QUALITIES = BUILDER.comment("List of items that should be considered as bows").defineList("Additional_Bow", List.of(), s -> s instanceof String);
        ADDITIONAL_ROD_QUALITIES = BUILDER.comment("List of items that should be considered as fishing rods").defineList("Additional_Rods", List.of(), s -> s instanceof String);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}