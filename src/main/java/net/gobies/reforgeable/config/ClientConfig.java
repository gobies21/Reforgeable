package net.gobies.reforgeable.config;

import net.gobies.reforgeable.Reforgeable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Reforgeable.MOD_ID)
public class ClientConfig {
    private static final String FILENAME = "reforgeable/client.toml";

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.ConfigValue<Boolean> MATERIAL_HINTS;
    public static boolean material_hints;
    public static ModConfigSpec.ConfigValue<Boolean> MATERIAL_HINT_NAMES;
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