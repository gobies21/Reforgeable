package net.gobies.reforgeable.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.ConfigValue<Boolean> MATERIAL_HINTS;
    public static ModConfigSpec.ConfigValue<Boolean> MATERIAL_HINT_NAMES;

    static {
        MATERIAL_HINTS = BUILDER.comment("Show hints on the reforging stations material slot").define("Material_Hints", true);
        MATERIAL_HINT_NAMES = BUILDER.comment("Display the name of hint items when hovering over them").define("Hint_Names", false);
        SPEC = BUILDER.build();
    }
}