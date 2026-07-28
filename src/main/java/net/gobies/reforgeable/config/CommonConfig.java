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


    @SubscribeEvent
    static void onLoad(ModConfigEvent.Loading configEvent) {
        if (configEvent.getConfig().getFileName().equals(FILENAME)) {
            no_quality_chance = NO_QUALITY_CHANCE.get().floatValue();
            global_reforge_material = GLOBAL_REFORGE_MATERIAL.get();
            quality_update_rate = QUALITY_UPDATE_RATE.get();
            reforge_materials = REFORGE_MATERIALS.get();
        }
    }

    static {
        NO_QUALITY_CHANCE = BUILDER.comment("Chance that items do not receive a quality, higher values makes qualities rarer").defineInRange("Normal_Chance", 0.5, 0.0, 1.0);
        GLOBAL_REFORGE_MATERIAL = BUILDER.comment("Item used to globally reforge any items").define("Material", "minecraft:nether_star");
        QUALITY_UPDATE_RATE = BUILDER.comment("The rate at which items are checked for qualities in ticks, lower values may cause performance issues").define("Update_Rate", 10);
        REFORGE_MATERIALS = BUILDER.comment("List of materials that are used to reforge specific items, supports tags (e.g., minecraft:trident=minecraft:iron_ingot, #forge:tools/shields=minecraft:iron_ingot etc...)").defineList("Reforge_Materials", List.of(), s -> s instanceof String);
        SPEC = BUILDER.build();
    }
}