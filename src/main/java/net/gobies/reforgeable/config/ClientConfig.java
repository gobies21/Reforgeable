package net.gobies.reforgeable.config;

import net.gobies.reforgeable.Reforgeable;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Reforgeable.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    private static final String FILENAME = "reforgeable/client.toml";

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<Boolean> MATERIAL_HINTS;
    public static boolean material_hints;
    public static ForgeConfigSpec.ConfigValue<Boolean> MATERIAL_HINT_NAMES;
    public static boolean material_hint_names;


    @SubscribeEvent
    static void onLoad(ModConfigEvent.Loading configEvent) {
        if (configEvent.getConfig().getFileName().equals(FILENAME)) {
            material_hints = MATERIAL_HINTS.get();
            material_hint_names = MATERIAL_HINT_NAMES.get();
        }
    }

    static {
        MATERIAL_HINTS = BUILDER.comment("Show hints on the reforging stations material slot").define("Material_Hints", true);
        MATERIAL_HINT_NAMES = BUILDER.comment("Display the name of hint items when hovering over them").define("Hint_Names", false);
        SPEC = BUILDER.build();
    }
}