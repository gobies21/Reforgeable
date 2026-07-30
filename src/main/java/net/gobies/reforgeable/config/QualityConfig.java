package net.gobies.reforgeable.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.compat.curios.CuriosCompat;
import net.gobies.reforgeable.helper.QualityHelper;
import net.gobies.reforgeable.util.Quality;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QualityConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final File JSON_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "reforgeable/qualities.json");

    public static List<String> WEAPON_QUALITIES = new ArrayList<>();
    public static List<String> TOOL_QUALITIES = new ArrayList<>();
    public static List<String> BOW_QUALITIES = new ArrayList<>();
    public static List<String> SHIELD_QUALITIES = new ArrayList<>();
    public static List<String> ROD_QUALITIES = new ArrayList<>();
    public static List<String> HELMET_QUALITIES = new ArrayList<>();
    public static List<String> CHESTPLATE_QUALITIES = new ArrayList<>();
    public static List<String> LEGGINGS_QUALITIES = new ArrayList<>();
    public static List<String> BOOTS_QUALITIES = new ArrayList<>();
    public static List<String> PET_QUALITIES = new ArrayList<>();
    public static List<String> CURIO_QUALITIES = new ArrayList<>();

    public static final Map<String, List<Quality>> CACHED_QUALITIES = new HashMap<>();

    public static void loadJsonConfig() {
        if (!JSON_FILE.getParentFile().exists()) {
            boolean directoryCreated = JSON_FILE.getParentFile().mkdirs();
            if (!directoryCreated && !JSON_FILE.getParentFile().exists()) {
                Reforgeable.LOGGER.error("Failed to create configuration file");
                return;
            }
        }
        if (!JSON_FILE.exists()) {
            createDefaultJson();
        }
        try (FileReader reader = new FileReader(JSON_FILE)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            if (json != null) {
                CACHED_QUALITIES.clear();
                QualityHelper.ATTRIBUTE_OPERATION.clear();

                List<String> operations = getList(json, "attribute_operations");
                for (String opLine : operations) {
                    if (opLine == null || !opLine.contains(";")) continue;

                    String[] tokens = opLine.split(";");
                    if (tokens.length < 2) continue;

                    String registryPath = tokens[0].trim();
                    Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(registryPath));

                    if (attribute != null) {
                        try {
                            AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(tokens[1].toUpperCase());
                            QualityHelper.ATTRIBUTE_OPERATION.put(attribute, operation);
                        } catch (IllegalArgumentException e) {
                            Reforgeable.LOGGER.error("Invalid attribute operation configured: {}", tokens[1]);
                        }
                    }
                }

                registerCategory(json, "weapon");
                registerCategory(json, "tool");
                registerCategory(json, "bow");
                registerCategory(json, "shield");
                registerCategory(json, "rod");
                registerCategory(json, "helmet");
                registerCategory(json, "chestplate");
                registerCategory(json, "leggings");
                registerCategory(json, "boots");
                registerCategory(json, "pet");
                if (CuriosCompat.isLoaded()) {
                    registerCategory(json, "curio");
                }
            }
        } catch (IOException e) {
            Reforgeable.LOGGER.error("Error parsing JSON file");
        }
    }

    private static void registerCategory(JsonObject json, String category) {
        List<String> lines = getList(json, category + "_qualities");

        List<Quality> parsedQualities = QualityParser.parseConfigQualities(lines);
        CACHED_QUALITIES.put(category, parsedQualities);
    }

    private static void createDefaultJson() {
        JsonObject json = new JsonObject();
        json.add("quality_config", getQualityInstructions());

        json.add("weapon_qualities", toJsonArray(DEFAULT_WEAPON_QUALITIES));
        json.add("tool_qualities", toJsonArray(DEFAULT_TOOL_QUALITIES));
        json.add("bow_qualities", toJsonArray(DEFAULT_BOW_QUALITIES));
        json.add("shield_qualities", toJsonArray(DEFAULT_SHIELD_QUALITIES));
        json.add("rod_qualities", toJsonArray(DEFAULT_ROD_QUALITIES));
        json.add("helmet_qualities", toJsonArray(DEFAULT_HELMET_QUALITIES));
        json.add("chestplate_qualities", toJsonArray(DEFAULT_CHESTPLATE_QUALITIES));
        json.add("leggings_qualities", toJsonArray(DEFAULT_LEGGINGS_QUALITIES));
        json.add("boots_qualities", toJsonArray(DEFAULT_BOOTS_QUALITIES));
        json.add("pet_qualities", toJsonArray(DEFAULT_PET_QUALITIES));
        if (CuriosCompat.isLoaded()) {
            json.add("curio_qualities", toJsonArray(DEFAULT_CURIO_QUALITIES));
        }

        json.add("operation_config", getOperationInstructions());
        json.add("attribute_operations", toJsonArray(DEFAULT_ATTRIBUTE_OPERATIONS));

        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            Reforgeable.LOGGER.error("Failed to write JSON file");
        }

        WEAPON_QUALITIES = jsonArrayToList(json.getAsJsonArray("weapon_qualities"));
        TOOL_QUALITIES = jsonArrayToList(json.getAsJsonArray("tool_qualities"));
        BOW_QUALITIES = jsonArrayToList(json.getAsJsonArray("bow_qualities"));
        SHIELD_QUALITIES = jsonArrayToList(json.getAsJsonArray("shield_qualities"));
        ROD_QUALITIES = jsonArrayToList(json.getAsJsonArray("rod_qualities"));
        HELMET_QUALITIES = jsonArrayToList(json.getAsJsonArray("helmet_qualities"));
        CHESTPLATE_QUALITIES = jsonArrayToList(json.getAsJsonArray("chestplate_qualities"));
        LEGGINGS_QUALITIES = jsonArrayToList(json.getAsJsonArray("leggings_qualities"));
        BOOTS_QUALITIES = jsonArrayToList(json.getAsJsonArray("boots_qualities"));
        PET_QUALITIES = jsonArrayToList(json.getAsJsonArray("pet_qualities"));
        CURIO_QUALITIES = jsonArrayToList(json.getAsJsonArray("curio_qualities"));
    }

    private static @NotNull JsonArray getQualityInstructions() {
        JsonArray instructions = new JsonArray();
        instructions.add("#================================================================================================#");
        instructions.add("#                                   QUALITY CONFIGURATION                                        #");
        instructions.add("#================================================================================================#");
        instructions.add("#            How the format works exactly for adding or modifying item qualities:                #");
        instructions.add("#          Each quality MUST follow this format, each category must be separated by ';'          #");
        instructions.add("#              \"name ; color ; attribute_modifiers ; weight (optional)\"                        #");
        instructions.add("#                                                                                                #");
        instructions.add("# 1. NAME:        The name of the quality added                                                  #");
        instructions.add("#                                                                                                #");
        instructions.add("# 2. COLOR:       A valid color for the quality, an easy way to find these is here:              #");
        instructions.add("#                 https://htmlcolorcodes.com/minecraft-color-codes/                              #");
        instructions.add("#                                                                                                #");
        instructions.add("# 3. ATTRIBUTES:  Attributes applied to the qualities, format is 'attribute=value'               #");
        instructions.add("#                 Multiple attributes for the same quality must be separated by commas           #");
        instructions.add("#                 Supports positive and negative stats eg... (0.15) | (-0.1)                     #");
        instructions.add("#                                                                                                #");
        instructions.add("# 4. WEIGHT:      The weight of the quality, weight is a single number, eg... (1) | (5)          #");
        instructions.add("#                 higher weight values mean more common, while lower weight values are more rare #");
        instructions.add("#                 Weight is completely optional, if no weight value is filled it defaults to 10  #");
        instructions.add("#================================================================================================#");
        return instructions;
    }

    private static @NotNull JsonArray getOperationInstructions() {
        JsonArray instructions = new JsonArray();
        instructions.add("#================================================================================================#");
        instructions.add("#                                   OPERATION CONFIGURATION                                      #");
        instructions.add("#================================================================================================#");
        instructions.add("#     How the format works for changing attribute operations for specific attributes:            #");
        instructions.add("#                               \"attribute ; operation\"                                        #");
        instructions.add("#                                                                                                #");
        instructions.add("# 1. ATTRIBUTE:   The name of the attribute, eg... 'minecraft:generic.attack_damage'             #");
        instructions.add("#                                                                                                #");
        instructions.add("# 1. OPERATION:   The attribute operation to apply the the attribute, this will apply across     #");
        instructions.add("#                 the attribute globally for all qualities using that attribute                  #");
        instructions.add("#                 Valid operations: 'ADDITION', 'MULTIPLY_BASE', 'MULTIPLY_TOTAL'                #");
        instructions.add("#================================================================================================#");
        return instructions;
    }

    private static List<String> getList(JsonObject json, String key) {
        return json.has(key) ? jsonArrayToList(json.getAsJsonArray(key)) : new ArrayList<>();
    }

    public static JsonArray toJsonArray(String[] array) {
        JsonArray jsonArray = new JsonArray();
        for (String element : array) jsonArray.add(element);
        return jsonArray;
    }

    private static List<String> jsonArrayToList(JsonArray array) {
        List<String> list = new ArrayList<>();
        if (array != null) array.forEach(element -> list.add(element.getAsString()));
        return list;
    }

    private static final String[] DEFAULT_WEAPON_QUALITIES= {
            "worthless;DARK_RED;minecraft:generic.attack_damage=-0.1,minecraft:generic.attack_speed=-0.1,forge:entity_reach=-1.0;8",
            "bulky;DARK_GRAY;minecraft:generic.attack_damage=-0.15,minecraft:generic.attack_speed=-0.15;10",
            "rusted;RED;minecraft:generic.attack_damage=-0.1;10",
            "clumsy;RED;minecraft:generic.attack_speed=-0.1;10",
            "short;RED;forge:entity_reach=-1.0;10",
            "broad;YELLOW;minecraft:generic.attack_damage=0.1,minecraft:generic.attack_speed=-0.1,forge:entity_reach=-0.5;10",
            "thin;YELLOW;minecraft:generic.attack_damage=-0.05,minecraft:generic.attack_speed=0.10;10",
            "pokey;YELLOW;minecraft:generic.attack_damage=0.1,minecraft:generic.attack_speed=-0.1;10",
            "vicious;YELLOW;minecraft:generic.attack_damage=0.15,forge:entity_reach=-0.5;10",
            "long;BLUE;forge:entity_reach=0.5;10",
            "sharp;BLUE;minecraft:generic.attack_damage=0.15;10",
            "keen;BLUE;minecraft:generic.attack_damage=0.1,minecraft:generic.attack_speed=0.1;10",
            "sweeping;AQUA;minecraft:generic.attack_speed=0.2,forge:entity_reach=0.5;10",
            "strong;AQUA;minecraft:generic.attack_damage=0.15,minecraft:generic.attack_knockback=0.5;10",
            "agile;AQUA;minecraft:generic.attack_speed=0.15,forge:entity_reach=0.5;10",
            "legendary;LIGHT_PURPLE;minecraft:generic.attack_damage=0.15,minecraft:generic.attack_speed=0.1,forge:entity_reach=0.5;5"
    };

    private static final String[] DEFAULT_TOOL_QUALITIES = {
            "terrible;DARK_RED;minecraft:generic.attack_speed=0.1,apothecary:dig_speed=-0.1,forge:block_reach=-1.0;8",
            "broken;DARK_GRAY;apothecary:dig_speed=-0.15,forge:block_reach=-0.5;10",
            "bulky;DARK_GRAY;apothecary:dig_speed=-0.15,minecraft:generic.attack_speed=-0.15;10",
            "rusted;RED;apothecary:dig_speed=0.05,minecraft:generic.attack_damage=-0.05;10",
            "clumsy;RED;apothecary:dig_speed=0.1,minecraft:generic.attack_speed=-0.1;10",
            "chipped;RED;minecraft:generic.attack_damage=-0.1,minecraft:generic.attack_speed=-0.1;10",
            "small;RED;forge:block_reach=-1.0;10",
            "massive;BLUE;forge:block_reach=1.0;10",
            "nimble;BLUE;apothecary:dig_speed=0.05",
            "quick;BLUE;minecraft:generic.attack_speed=0.1,apothecary:dig_speed=0.1",
            "graceful;AQUA;minecraft:generic.attack_speed=0.1,apothecary:dig_speed=0.1;10",
            "light;AQUA;minecraft:generic.attack_speed=0.10,apothecary:dig_speed=0.10;10",
            "legendary;LIGHT_PURPLE;minecraft:generic.attack_speed=0.1,apothecary:dig_speed=0.15,forge:entity_reach=0.5;5"
    };

    private static final String[] DEFAULT_BOW_QUALITIES = {
            "awful;DARK_RED;apothecary:projectile_damage=-0.15;8",
            "shoddy;DARK_GRAY;apothecary:projectile_damage=-0.1;10",
            "awkward;RED;apothecary:projectile_damage=-0.05;10",
            "deadly;BLUE;apothecary:projectile_damage=0.05;10",
            "powerful;AQUA;apothecary:projectile_damage=0.1;10",
            "unreal;GOLD;apothecary:projectile_damage=0.15;5"
    };

    private static final String[] DEFAULT_SHIELD_QUALITIES = {
            "worthless;DARK_RED;minecraft:generic.armor=-1.0,minecraft:generic.movement_speed=-0.1;8",
            "heavy;RED;minecraft:generic.movement_speed=-0.1;10",
            "thick;YELLOW;minecraft:generic.armor=0.5,minecraft:generic.movement_speed=-0.05;10",
            "protective;BLUE;minecraft:generic.armor=0.5;10",
            "arcane;BLUE;apothecary:magic_shielding=1.0;10",
            "solid;BLUE;minecraft:generic.knockback_resistance=0.5;10",
            "light;AQUA;minecraft:generic.movement_speed=0.1;10",
            "legendary;LIGHT_PURPLE;minecraft:generic.armor=0.5,minecraft:knockback_resistance=0.5,apothecary:magic_shielding=1.0;5"
    };

    private static final String[] DEFAULT_ROD_QUALITIES = {
            "unlucky;RED;minecraft:generic.luck=-0.5;10",
            "lucky;AQUA;minecraft:generic.luck=0.5;10"
    };

    private static final String[] DEFAULT_HELMET_QUALITIES = {
            "crumbling;DARK_RED;minecraft:generic.armor=-1.0,minecraft:generic.armor_toughness=-1.0,apothecary:magic_shielding=-1.0;8",
            "dented;DARK_GRAY;minecraft:generic.armor=-1.0;10",
            "heavy;RED;minecraft:generic.movement_speed=-0.1",
            "thick;YELLOW;minecraft:generic.armor=0.5,minecraft:generic.movement_speed=-0.05",
            "tough;BLUE;minecraft:generic.armor_toughness=1.0",
            "protective;BLUE;minecraft:generic.armor=0.5",
            "arcane;BLUE;apothecary:magic_shielding=1.0",
            "lucky;AQUA;minecraft:generic.luck=0.5",
            "masterful;LIGHT_PURPLE;minecraft:generic.armor=0.5,minecraft:generic.armor_toughness=1.0,minecraft:generic.luck=0.5"
    };
    private static final String[] DEFAULT_CHESTPLATE_QUALITIES = {
            "crumbling;DARK_RED;minecraft:generic.armor=-1.5,minecraft:generic.armor_toughness=-1.0,apothecary:magic_shielding=-1.0;8",
            "cumbersome;DARK_GRAY;minecraft:generic.attack_speed=-0.1,apothecary:dig_speed=-0.1;10",
            "dented;DARK_GRAY;minecraft:generic.armor=-1.0;10",
            "heavy;RED;minecraft:generic.movement_speed=-0.1;10",
            "thick;YELLOW;minecraft:generic.armor=0.5,minecraft:generic.movement_speed=-0.05;10",
            "tough;BLUE;minecraft:generic.armor_toughness=1.0;10",
            "protective;BLUE;minecraft:generic.armor=0.5;10",
            "arcane;BLUE;apothecary:magic_shielding=1.0;10",
            "solid;BLUE;minecraft:generic.knockback_resistance=0.5;10",
            "masterful;LIGHT_PURPLE;minecraft:generic.armor=0.5,minecraft:generic.armor_toughness=1.0,minecraft:generic.knockback_resistance=0.5;5"
    };

    private static final String[] DEFAULT_LEGGINGS_QUALITIES = {
            "crumbling;DARK_RED;minecraft:generic.armor=-1.5,minecraft:generic.armor_toughness=-1.0,apothecary:magic_shielding=-1.0;8",
            "dented;DARK_GRAY;minecraft:generic.armor=-1.5;10",
            "heavy;RED;minecraft:generic.movement_speed=-0.1;10",
            "thick;YELLOW;minecraft:generic.armor=0.5,minecraft:generic.movement_speed=-0.05;10",
            "tough;BLUE;minecraft:generic.armor_toughness=1.0;10",
            "protective;BLUE;minecraft:generic.armor=0.5;10",
            "arcane;BLUE;apothecary:magic_shielding=1.0;10",
            "springy;BLUE;apothecary:jump_height=0.5;10",
            "masterful;LIGHT_PURPLE;minecraft:generic.armor=0.5,minecraft:generic.armor_toughness=1.0,apothecary:jump_height=0.5;5"
    };

    private static final String[] DEFAULT_BOOTS_QUALITIES = {
            "crumbling;DARK_RED;minecraft:generic.armor=-1.0,minecraft:generic.armor_toughness=-0.5,apothecary:magic_shielding=-1;8",
            "dented;DARK_GRAY;minecraft:generic.armor=-1.0;10",
            "heavy;RED;minecraft:generic.movement_speed=-0.1;10",
            "thick;YELLOW;minecraft:generic.armor=0.5,minecraft:generic.movement_speed=-0.05;10",
            "tough;BLUE;minecraft:generic.armor_toughness=1.0;10",
            "protective;BLUE;minecraft:generic.armor=0.5;10",
            "arcane;BLUE;apothecary:magic_shielding=1.0;10",
            "tall;BLUE;forge:step_height_addition=0.5;10",
            "speedy;BLUE;minecraft:generic.movement_speed=0.1;10",
            "masterful;LIGHT_PURPLE;minecraft:generic.armor=0.5,minecraft:generic.armor_toughness=1.0,minecraft:generic.movement_speed=0.1,forge:step_height_addition=0.5;10"
    };

    private static final String[] DEFAULT_PET_QUALITIES = {
            "crumbled;DARK_RED;minecraft:generic.armor=-2.0,apothecary:magic_shielding=-1.0;8",
            "dented;DARK_GRAY;minecraft:generic.armor=-1.0;10",
            "heavy;RED;minecraft:generic.movement_speed=-0.1;10",
            "thick;YELLOW;minecraft:generic.armor=2.0,minecraft:generic.movement_speed=-0.1;10",
            "protective;BLUE;minecraft:generic.armor=2.0;10",
            "arcane;BLUE;apothecary:magic_shielding=1.0;10",
            "speedy;BLUE;minecraft:generic.movement_speed=0.1;10",
            "masterful;LIGHT_PURPLE;minecraft:generic.armor=2.0,minecraft:generic.movement_speed=0.1;5"
    };

    private static final String[] DEFAULT_CURIO_QUALITIES = {
            "horrible;DARK_GRAY;minecraft:generic.attack_damage=-0.05,apothecary:magic_damage=-0.05,apothecary:projectile_damage=-0.05;8",
            "clunky;DARK_GRAY;minecraft:generic.movement_speed=-0.05,minecraft:generic.attack_speed=-0.05,apothecary:dig_speed=-0.05;8",
            "unlucky;RED;minecraft:generic.luck=-0.2;10",
            "lucky;BLUE;minecraft:generic.luck=0.2;10",
            "healthy;BLUE;minecraft:generic.max_health=2.0;10",
            "armored;BLUE;apothecary:damage_resistance=0.03;10",
            "speedy;BLUE;minecraft:generic.movement_speed=0.05;10",
            "springy;BLUE;apothecary:jump_height=0.5;10",
            "prospecting;BLUE;apothecary:dig_speed=0.03;10",
            "flailing;BLUE;minecraft:generic.attack_speed=0.03;10",
            "arcane;BLUE;apothecary:magic_shielding=1.0;10",
            "aiming;BLUE;apothecary:projectile_damage=0.03;10",
            "focusing;BLUE;apothecary:magic_damage=0.03;10",
            "strengthening;BLUE;minecraft:generic.attack_damage=0.03;10",
            "aiming;BLUE;apothecary:projectile_damage=0.03;10",
            "superior;BLUE;apothecary:damage_multiplier=0.03;10",
            "mystic;AQUA;apothecary:magic_damage=0.03,apothecary:magic_shielding=1.0;10",
            "graceful;AQUA;minecraft:generic.attack_speed=0.03,apothecary:dig_speed=0.03;10",
            "athletic;AQUA;minecraft:generic.movement_speed=0.05,apothecary:jump_height=0.5;10",
            "punishing;LIGHT_PURPLE;minecraft:generic.attack_damage=0.03,apothecary:magic_damage=0.03,apothecary:projectile_damage=0.03;5",
            "undying;LIGHT_PURPLE;minecraft:generic.max_health=2.0,apothecary:damage_resistance=0.03,apothecary:magic_shielding=1.0;5"
    };

    private static final String[] DEFAULT_ATTRIBUTE_OPERATIONS = {
            "minecraft:generic.movement_speed;MULTIPLY_BASE",
            "minecraft:generic.attack_damage;MULTIPLY_BASE",
            "minecraft:generic.attack_speed;MULTIPLY_BASE",
            "minecraft:generic.armor;ADDITION",
            "minecraft:generic.armor_toughness;ADDITION",
            "minecraft:generic.luck;ADDITION",
            "minecraft:generic.max_health;ADDITION",
            "minecraft:generic.attack_knockback;ADDITION",
            "minecraft:generic.knockback_resistance;ADDITION",
            "forge:step_height_addition;ADDITION",
            "forge:entity_reach;ADDITION", "forge:block_reach;ADDITION",
            "apothecary:damage_resistance;MULTIPLY_BASE",
            "apothecary:damage_multiplier;MULTIPLY_BASE",
            "apothecary:dig_speed;MULTIPLY_BASE",
            "apothecary:jump_height;ADDITION",
            "apothecary:magic_shielding;ADDITION",
            "apothecary:projectile_damage;MULTIPLY_BASE",
            "apothecary:magic_damage;MULTIPLY_BASE"
    };
}
