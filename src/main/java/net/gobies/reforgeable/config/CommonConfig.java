package net.gobies.reforgeable.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class CommonConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.ConfigValue<Boolean> ENABLE_DEBUG;
    public static ModConfigSpec.ConfigValue<Double> NO_QUALITY_CHANCE;
    public static ModConfigSpec.ConfigValue<String> GLOBAL_REFORGE_MATERIAL;
    public static ModConfigSpec.ConfigValue<Integer> QUALITY_UPDATE_RATE;
    public static ModConfigSpec.ConfigValue<List<? extends String>> REFORGE_MATERIALS;
    public static ModConfigSpec.ConfigValue<Boolean> ENABLE_ANTI_SKIP;
    public static ModConfigSpec.ConfigValue<Integer> ANTI_SKIP_DURATION;
    public static ModConfigSpec.ConfigValue<Integer> MAX_WEIGHT;
    public static ModConfigSpec.ConfigValue<Double> LUCK_SCALE;
    public static ModConfigSpec.ConfigValue<List<? extends String>> QUALITY_LOOT_TABLES;

    public static ModConfigSpec.ConfigValue<Double> TREASURE_CHANCE;
    public static ModConfigSpec.ConfigValue<Double> TREASURE_LUCK_FACTOR;
    public static ModConfigSpec.ConfigValue<List<? extends String>> TREASURE_LOOT_TABLES;

    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_SHIELD_QUALITIES;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_PET_QUALITIES;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_WEAPON_QUALITIES;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_TOOL_QUALITIES;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_BOW_QUALITIES;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_ROD_QUALITIES;
    public static ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_CURIO_QUALITIES;
    public static ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST_QUALITIES;

    public static ModConfigSpec.ConfigValue<Boolean> JEI_COMPAT;
    public static ModConfigSpec.ConfigValue<Boolean> CURIO_QUALITIES;
    public static ModConfigSpec.ConfigValue<Boolean> STAFF_QUALITIES;
    public static ModConfigSpec.ConfigValue<Boolean> SPELLBOOK_QUALITIES;
    public static ModConfigSpec.ConfigValue<Boolean> GUN_QUALITIES;

    static {
        BUILDER.push("General");
        ENABLE_DEBUG = BUILDER.comment("Enable debugging").define("Debug", false);
        NO_QUALITY_CHANCE = BUILDER.comment("Chance that items do not receive a quality, higher values makes qualities rarer").defineInRange("Normal_Chance", 0.5, 0.0, 1.0);
        GLOBAL_REFORGE_MATERIAL = BUILDER.comment("Item used to globally reforge any items").define("Material", "minecraft:nether_star");
        QUALITY_UPDATE_RATE = BUILDER.comment("The rate at which items are checked for qualities in ticks, lower values may cause performance issues").define("Update_Rate", 10);
        REFORGE_MATERIALS = BUILDER.comment("List of materials that are used to reforge specific items, supports tags (e.g., minecraft:trident=minecraft:iron_ingot, #forge:tools/shields=minecraft:iron_ingot etc...)").defineList("Reforge_Materials", List.of(), () -> "", s -> s instanceof String);
        ENABLE_ANTI_SKIP = BUILDER.comment("Enable anti skip, makes the reforging button not work for a very short duration after getting the lowest weighted quality").define("Enable_Anti_Skip", true);
        ANTI_SKIP_DURATION = BUILDER.comment("The time that the anti skip lasts for to prevent accidentally skipping desired qualities").define("Anti_Skip_Duration", 10);
        MAX_WEIGHT = BUILDER.comment("Max weight at which a quality is considered one of the 'best' qualities, used in anti skip and luck factor, any value less than or equal to this weight is qualified").define("Max_Weight", 5);
        LUCK_SCALE = BUILDER.comment("The luck factor of qualities in percentage, the chance increase per luck changes dynamically based on how many qualities the item has,  set to 0 to disable").defineInRange("Luck_Scale", 0.02, 0.0, 0.25);
        QUALITY_LOOT_TABLES = BUILDER.comment("Loot table paths allowed to roll qualities (e.g., modid:loot_path) or this can be more specific (e.g., modid:loot_path:loot_table)").defineListAllowEmpty(
                List.of("Loot_Table_Paths"),
                () -> List.of("chests/", "chest/", "entities/", "entity/", "gameplay/fishing", "fishing/", "archaeology/"),
                entry -> entry instanceof String);
        BUILDER.pop();

        BUILDER.push("Treasure");
        TREASURE_CHANCE = BUILDER.comment("Chance to find a treasure quality in loot (weight 0 qualities)").defineInRange("Treasure_Chance", 0.01, 0.0, 1.0);
        TREASURE_LUCK_FACTOR = BUILDER.comment("Chance to find a treasure quality in loot").defineInRange("Treasure_Luck_Factor", 0.005, 0.0, 1.0);
        TREASURE_LOOT_TABLES = BUILDER.comment("Loot table paths allowed to roll treasure qualities (e.g., modid:loot_path) or this can be more specific (e.g., modid:loot_path:loot_table)").defineListAllowEmpty(
                List.of("Loot_Table_Paths"),
                () -> List.of("chests/", "chest/", "gameplay/fishing", "fishing/", "archaeology/"),
                entry -> entry instanceof String);
        BUILDER.pop();

        BUILDER.comment("Supports item ids or item tags eg... #forge:shields, 'minecraft:trident'").push("Item_Lists");
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
        JEI_COMPAT = BUILDER.comment("Enable jei compat").define("Jei_Compat", true);
        CURIO_QUALITIES = BUILDER.comment("Enable curios having their own custom qualities").define("Curio_Qualities", true);
        STAFF_QUALITIES = BUILDER.comment("Enable staffs from irons spellbooks having their own custom qualities").define("Staff_Qualities", true);
        SPELLBOOK_QUALITIES = BUILDER.comment("Enable spellbooks from irons spellbooks having their own custom qualities").define("Spellbook_Qualities", true);
        GUN_QUALITIES = BUILDER.comment("Enable guns from irons artifice having their own custom qualities").define("Gun_Qualities", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}