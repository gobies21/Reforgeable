package net.gobies.reforgeable.config;

import net.gobies.reforgeable.Reforgeable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

@EventBusSubscriber(modid = Reforgeable.MOD_ID)
public class CommonConfig {
    private static final String FILENAME = "reforgeable/common.toml";

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.ConfigValue<Double> NO_QUALITY_CHANCE;
    public static float no_quality_chance;
    public static ModConfigSpec.ConfigValue<String> GLOBAL_REFORGE_MATERIAL;
    public static String global_reforge_material;
    public static ModConfigSpec.ConfigValue<Integer> QUALITY_UPDATE_RATE;
    public static int quality_update_rate;
    public static ModConfigSpec.ConfigValue<List<? extends String>> REFORGE_MATERIALS;
    public static List<? extends String> reforge_materials;
    public static ModConfigSpec.ConfigValue<Boolean> ENABLE_ANTI_SKIP;
    public static boolean enable_anti_skip;
    public static ModConfigSpec.ConfigValue<Integer> ANTI_SKIP_DURATION;
    public static int anti_skip_duration;
    public static ModConfigSpec.ConfigValue<Integer> MAX_WEIGHT;
    public static int max_weight;
    public static ModConfigSpec.ConfigValue<Double> LUCK_SCALE;
    public static float luck_scale;

    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_HELMET_QUALITIES;
    public static List<? extends String> additional_helmet_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_CHESTPLATE_QUALITIES;
    public static List<? extends String> additional_chestplate_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_LEGGINGS_QUALITIES;
    public static List<? extends String> additional_leggings_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_BOOTS_QUALITIES;
    public static List<? extends String> additional_boots_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_SHIELD_QUALITIES;
    public static List<? extends String> additional_shield_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_PET_QUALITIES;
    public static List<? extends String> additional_pet_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_WEAPON_QUALITIES;
    public static List<? extends String> additional_weapon_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_TOOL_QUALITIES;
    public static List<? extends String> additional_tool_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_BOW_QUALITIES;
    public static List<? extends String> additional_bow_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_ROD_QUALITIES;
    public static List<? extends String> additional_rod_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_CURIO_QUALITIES;
    public static List<? extends String> additional_curio_qualities;
    public static ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST_QUALITIES;
    public static List<? extends String> blacklist_qualities;

    public static ModConfigSpec.ConfigValue<Boolean> CURIO_QUALITIES;
    public static boolean curio_qualities;
    public static ModConfigSpec.ConfigValue<Boolean> STAFF_QUALITIES;
    public static boolean staff_qualities;
    public static ModConfigSpec.ConfigValue<Boolean> SPELLBOOK_QUALITIES;
    public static boolean spellbook_qualities;



    @SubscribeEvent
    static void onLoad(ModConfigEvent.Loading configEvent) {
        if (configEvent.getConfig().getFileName().equals(FILENAME)) {
            no_quality_chance = NO_QUALITY_CHANCE.get().floatValue();
            global_reforge_material = GLOBAL_REFORGE_MATERIAL.get();
            quality_update_rate = QUALITY_UPDATE_RATE.get();
            reforge_materials = REFORGE_MATERIALS.get();
            enable_anti_skip = ENABLE_ANTI_SKIP.get();
            anti_skip_duration = ANTI_SKIP_DURATION.get();
            max_weight = MAX_WEIGHT.get();
            luck_scale = LUCK_SCALE.get().floatValue();
            additional_helmet_qualities = ADDITIONAL_HELMET_QUALITIES.get();
            additional_chestplate_qualities = ADDITIONAL_CHESTPLATE_QUALITIES.get();
            additional_leggings_qualities = ADDITIONAL_LEGGINGS_QUALITIES.get();
            additional_boots_qualities = ADDITIONAL_BOOTS_QUALITIES.get();
            additional_shield_qualities = ADDITIONAL_SHIELD_QUALITIES.get();
            additional_pet_qualities = ADDITIONAL_PET_QUALITIES.get();
            additional_weapon_qualities = ADDITIONAL_WEAPON_QUALITIES.get();
            additional_tool_qualities = ADDITIONAL_TOOL_QUALITIES.get();
            additional_bow_qualities = ADDITIONAL_BOW_QUALITIES.get();
            additional_rod_qualities = ADDITIONAL_ROD_QUALITIES.get();
            additional_curio_qualities = ADDITIONAL_CURIO_QUALITIES.get();
            blacklist_qualities = BLACKLIST_QUALITIES.get();
            curio_qualities = CURIO_QUALITIES.get();
            staff_qualities = STAFF_QUALITIES.get();
            spellbook_qualities = SPELLBOOK_QUALITIES.get();
        }
    }

    static {
        BUILDER.push("General");
        NO_QUALITY_CHANCE = BUILDER.comment("Chance that items do not receive a quality, higher values makes qualities rarer").defineInRange("Normal_Chance", 0.5, 0.0, 1.0);
        GLOBAL_REFORGE_MATERIAL = BUILDER.comment("Item used to globally reforge any items").define("Material", "minecraft:nether_star");
        QUALITY_UPDATE_RATE = BUILDER.comment("The rate at which items are checked for qualities in ticks, lower values may cause performance issues").define("Update_Rate", 5);
        REFORGE_MATERIALS = BUILDER.comment("List of materials that are used to reforge specific items, supports tags (e.g., minecraft:trident=minecraft:iron_ingot, #forge:tools/shields=minecraft:iron_ingot etc...)").defineList("Reforge_Materials", List.of(), () -> "", s -> s instanceof String);
        ENABLE_ANTI_SKIP = BUILDER.comment("Enable anti skip, makes the reforging button not work for a very short duration after getting the lowest weighted quality").define("Enable_Anti_Skip", true);
        ANTI_SKIP_DURATION = BUILDER.comment("The time that the anti skip lasts for to prevent accidentally skipping desired qualities").define("Anti_Skip_Duration", 10);
        MAX_WEIGHT = BUILDER.comment("Max weight at which a quality is considered one of the 'best' qualities, used in anti skip and luck factor, any value less than or equal to this weight is qualified").define("Max_Weight", 5);
        LUCK_SCALE = BUILDER.comment("The luck factor of qualities in percentage, e.g., 0.03 = 3% higher chance for better qualities per luck, set to 0 to disable").defineInRange("Luck_Scale", 0.03, 0.0, 0.25);
        BUILDER.pop();

        BUILDER.comment("Supports item ids or item tags eg... #forge:shields, 'minecraft:trident'").push("Item_Lists");
        ADDITIONAL_HELMET_QUALITIES = BUILDER.comment("List of items that should be considered as helmets").defineList("Additional_Helmets", List.of(), () -> "", s -> s instanceof String);
        ADDITIONAL_CHESTPLATE_QUALITIES = BUILDER.comment("List of items that should be considered as chestplates").defineList("Additional_Chestplates", List.of(), () -> "", s -> s instanceof String);
        ADDITIONAL_LEGGINGS_QUALITIES = BUILDER.comment("List of items that should be considered as leggings").defineList("Additional_Leggings", List.of(), () -> "", s -> s instanceof String);
        ADDITIONAL_BOOTS_QUALITIES = BUILDER.comment("List of items that should be considered as boots").defineList("Additional_Boots", List.of(), () -> "", s -> s instanceof String);
        ADDITIONAL_SHIELD_QUALITIES = BUILDER.comment("List of items that should be considered as shields").defineList("Additional_Shields", List.of(), () -> "", s -> s instanceof String);
        ADDITIONAL_PET_QUALITIES = BUILDER.comment("List of items that should be considered as pet armor").defineList("Additional_Pet_Armors", List.of(), () -> "", s -> s instanceof String);
        ADDITIONAL_WEAPON_QUALITIES = BUILDER.comment("List of items that should be considered as weapons").defineList("Additional_Weapons", List.of(), () -> "", s -> s instanceof String);
        ADDITIONAL_TOOL_QUALITIES = BUILDER.comment("List of items that should be considered as tools").defineList("Additional_Tools", List.of(), () -> "", s -> s instanceof String);
        ADDITIONAL_BOW_QUALITIES = BUILDER.comment("List of items that should be considered as bows").defineList("Additional_Bows", List.of(), () -> "", s -> s instanceof String);
        ADDITIONAL_ROD_QUALITIES = BUILDER.comment("List of items that should be considered as fishing rods").defineList("Additional_Rods", List.of(), () -> "", s -> s instanceof String);
        ADDITIONAL_CURIO_QUALITIES = BUILDER.comment("List of items that should be considered as curios").defineList("Additional_Curios", List.of(), () -> "", s -> s instanceof String);
        BLACKLIST_QUALITIES = BUILDER.comment("List of items that are never able to receive qualities").defineList("Blacklist_Qualities", List.of(), () -> "", s -> s instanceof String);
        BUILDER.pop();

        BUILDER.push("Compat");
        CURIO_QUALITIES = BUILDER.comment("Enable curios having their own custom qualities").define("Curio_Qualities", true);
        STAFF_QUALITIES = BUILDER.comment("Enable staffs from irons spellbooks having their own custom qualities").define("Staff_Qualities", true);
        SPELLBOOK_QUALITIES = BUILDER.comment("Enable spellbooks from irons spellbooks having their own custom qualities").define("Spellbook_Qualities", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}