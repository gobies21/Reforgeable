package net.gobies.reforgeable.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.compat.curios.CuriosCompat;
import net.gobies.reforgeable.helper.QualityHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static final Map<String, List<QualityHelper.Quality>> CACHED_QUALITIES = new HashMap<>();

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

                List<String> rawOps = getList(json, "attribute_operations");
                for (String opLine : rawOps) {
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
        List<String> rawLines = getList(json, category + "_qualities");

        List<QualityHelper.Quality> parsedQualities = QualityParser.parseConfigQualities(rawLines);
        CACHED_QUALITIES.put(category, parsedQualities);
    }

    private static void createDefaultJson() {
        JsonObject json = new JsonObject();
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

        String[] defaultAttributeOps = {
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
                "forge:entity_reach;ADDITION",
                "forge:block_reach;ADDITION",
                "apothecary:damage_resistance;MULTIPLY_BASE",
                "apothecary:damage_multiplier;MULTIPLY_BASE",
                "apothecary:dig_speed;MULTIPLY_BASE",
                "apothecary:jump_height;ADDITION",
                "apothecary:magic_shielding;ADDITION",
                "apothecary:projectile_damage;MULTIPLY_BASE",
                "apothecary:magic_damage;MULTIPLY_BASE"
        };
        json.add("attribute_operations", toJsonArray(defaultAttributeOps));

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
    private static List<String> getList(JsonObject json, String key) {
        return json.has(key) ? jsonArrayToList(json.getAsJsonArray(key)) : new ArrayList<>();
    }

    private static JsonArray toJsonArray(String[] array) {
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
            "worthless;DARK_RED;minecraft:generic.attack_damage=-0.1,minecraft:generic.attack_speed=-0.1,forge:entity_reach=-1.0",
            "bulky;DARK_GRAY;minecraft:generic.attack_damage=-0.05,minecraft:generic.attack_speed=-0.05",
            "rusted;RED;minecraft:generic.attack_damage=-0.1",
            "clumsy;RED;minecraft:generic.attack_speed=-0.1",
            "short;RED;forge:entity_reach=-1.0",
            "broad;YELLOW;minecraft:generic.attack_damage=-0.1,forge:entity_reach=0.5",
            "thin;YELLOW;minecraft:generic.attack_damage=0.1,forge:entity_reach=-0.5",
            "pokey;YELLOW;minecraft:generic.attack_damage=0.1,minecraft:generic.attack_speed=-0.1",
            "vicious;YELLOW;minecraft:generic.attack_damage=0.15,forge:entity_reach=-0.5",
            "long;BLUE;forge:entity_reach=1.0",
            "sharp;BLUE;minecraft:generic.attack_damage=0.15",
            "keen;BLUE;minecraft:generic.attack_speed=0.1",
            "energetic;BLUE;apothecary:damage_multiplier=0.03",
            "sweeping;AQUA;minecraft:generic.attack_speed=0.2,forge:entity_reach=0.5",
            "strong;AQUA;minecraft:generic.attack_damage=0.15,minecraft:generic.attack_knockback=0.5",
            "agile;AQUA;minecraft:generic.attack_speed=0.15,minecraft:generic.attack_knockback=0.5",
            "legendary;LIGHT_PURPLE;minecraft:generic.attack_damage=0.15,minecraft:generic.attack_speed=0.1,forge:entity_reach=0.5"
    };

    private static final String[] DEFAULT_TOOL_QUALITIES = {
            "terrible;DARK_RED;apothecary:dig_speed=-0.1,forge:block_reach=-1.0",
            "broken;DARK_GRAY;apothecary:dig_speed=-0.05,forge:block_reach=-0.5",
            "chipped;RED;apothecary:dig_speed=-0.1",
            "small;RED;forge:block_reach=-1.0",
            "massive;BLUE;forge:block_reach=1.5",
            "graceful;AQUA;minecraft:generic.attack_speed=0.1,apothecary:dig_speed=0.1,forge:block_reach=0.5",
            "legendary;LIGHT_PURPLE;apothecary:dig_speed=0.15,forge:block_reach=1.0",
    };

    private static final String[] DEFAULT_BOW_QUALITIES = {
            "awful;DARK_RED;apothecary:projectile_damage=-0.15",
            "awkward;RED;apothecary:projectile_damage=-0.05",
            "deadly;BLUE;apothecary:projectile_damage=0.05",
            "powerful;AQUA;apothecary:projectile_damage=0.1",
            "unreal;GOLD;apothecary:projectile_damage=0.15"
    };

    private static final String[] DEFAULT_SHIELD_QUALITIES = {
            "worthless;DARK_RED;minecraft:generic.armor=-1.0,minecraft:generic.movement_speed=-0.1",
            "heavy;RED;minecraft:generic.movement_speed=-0.1",
            "damaged;RED;minecraft:generic.armor=-1.5",
            "thick;YELLOW;minecraft:generic.armor=0.5,minecraft:generic.movement_speed=-0.05",
            "protective;BLUE;minecraft:generic.armor=1.5",
            "arcane;BLUE;apothecary:magic_shielding=1.0",
            "solid;BLUE;minecraft:generic.knockback_resistance=1.0",
            "light;AQUA;minecraft:generic.movement_speed=0.1",
            "legendary;LIGHT_PURPLE;minecraft:generic.armor=1.5,minecraft:knockback_resistance=0.5,apothecary:magic_shielding=0.5"
    };

    private static final String[] DEFAULT_ROD_QUALITIES = {
            "unlucky;RED;minecraft:generic.luck=-0.5",
            "lucky;AQUA;minecraft:generic.luck=0.5"
    };

    private static final String[] DEFAULT_HELMET_QUALITIES = {
            "crumbled;DARK_RED;minecraft:generic.armor=-1.0,apothecary:magic_shielding=-1.5,minecraft:generic.armor_toughness=-1.0",
            "dented;DARK_GRAY;minecraft:generic.armor=-1.0",
            "heavy;RED;minecraft:generic.movement_speed=-0.1",
            "thick;YELLOW;minecraft:generic.armor=1.0,minecraft:generic.movement_speed=-0.05",
            "tough;BLUE;minecraft:generic.armor_toughness=1.0",
            "protective;BLUE;minecraft:generic.armor=1.0",
            "arcane;BLUE;apothecary:magic_shielding=1.0",
            "lucky;AQUA;minecraft:generic.luck=0.5",
            "masterful;LIGHT_PURPLE;minecraft:generic.armor=1.0,minecraft:generic.armor_toughness=1.0,minecraft:generic.luck=0.5"
    };
    private static final String[] DEFAULT_CHESTPLATE_QUALITIES = {
            "crumbled;DARK_RED;minecraft:generic.armor=-2.0,minecraft:generic.armor_toughness=-1.0,apothecary:magic_shielding=-1.0",
            "cumbersome;DARK_GRAY;apothecary:dig_speed=-0.1,minecraft:generic.attack_speed=-0.1",
            "dented;DARK_GRAY;minecraft:generic.armor=-2.0",
            "heavy;RED;minecraft:generic.movement_speed=-0.1",
            "thick;YELLOW;minecraft:generic.armor=1.0,minecraft:generic.movement_speed=-0.05",
            "tough;BLUE;minecraft:generic.armor_toughness=1.0",
            "protective;BLUE;minecraft:generic.armor=1.0",
            "arcane;BLUE;apothecary:magic_shielding=1.0",
            "solid;BLUE;minecraft:generic.knockback_resistance=0.5",
            "masterful;LIGHT_PURPLE;minecraft:generic.armor=2.0,minecraft:generic.armor_toughness=1.0,minecraft:generic.knockback_resistance=0.5"
    };

    private static final String[] DEFAULT_LEGGINGS_QUALITIES = {
            "crumbled;DARK_RED;minecraft:generic.armor=-1.5,minecraft:generic.armor_toughness=-1.0,apothecary:magic_shielding=-1.0",
            "dented;DARK_GRAY;minecraft:generic.armor=-1.5",
            "heavy;RED;minecraft:generic.movement_speed=-0.1",
            "thick;YELLOW;minecraft:generic.armor=1.0,minecraft:generic.movement_speed=-0.05",
            "tough;BLUE;minecraft:generic.armor_toughness=1.0",
            "protective;BLUE;minecraft:generic.armor=1.0",
            "arcane;BLUE;apothecary:magic_shielding=1.0",
            "springy;BLUE;apothecary:jump_height=0.5",
            "masterful;LIGHT_PURPLE;minecraft:generic.armor=1.5,minecraft:generic.armor_toughness=1.0,apothecary:jump_height=0.5"
    };

    private static final String[] DEFAULT_BOOTS_QUALITIES = {
            "crumbled;DARK_RED;minecraft:generic.armor=-1.0,minecraft:generic.armor_toughness=-0.5,apothecary:magic_shielding=-1",
            "dented;DARK_GRAY;minecraft:generic.armor=-1.0",
            "heavy;RED;minecraft:generic.movement_speed=-0.1",
            "thick;YELLOW;minecraft:generic.armor=1.0,minecraft:generic.movement_speed=-0.05",
            "tough;BLUE;minecraft:generic.armor_toughness=-1.0",
            "protective;BLUE;minecraft:generic.armor=1.0",
            "arcane;BLUE;apothecary:magic_shielding=1.0",
            "tall;BLUE;forge:step_height_addition=0.5",
            "speedy;BLUE;minecraft:generic.movement_speed=0.1",
            "masterful;LIGHT_PURPLE;minecraft:generic.armor=1.0,minecraft:generic.armor_toughness=1.0,minecraft:generic.movement_speed=0.1,forge:step_height_addition=0.5"
    };

    private static final String[] DEFAULT_PET_QUALITIES = {
            "crumbled;DARK_RED;minecraft:generic.armor=-2.0,apothecary:magic_shielding=-1.0",
            "dented;DARK_GRAY;minecraft:generic.armor=-1.0",
            "heavy;RED;minecraft:generic.movement_speed=-0.1",
            "thick;YELLOW;minecraft:generic.armor=1.0,minecraft:generic.movement_speed=-0.1",
            "protective;BLUE;minecraft:generic.armor=1.5",
            "arcane;BLUE;apothecary:magic_shielding=1.0",
            "speedy;BLUE;minecraft:generic.movement_speed=0.3",
            "masterful;LIGHT_PURPLE;minecraft:generic.armor=2.0,minecraft:generic.movement_speed=0.15"
    };

    private static final String[] DEFAULT_CURIO_QUALITIES = {
            "horrible;DARK_RED;minecraft:generic.attack_damage=-0.05,apothecary:magic_damage=-0.05,apothecary:projectile_damage=-0.05",
            "defective;DARK_RED;minecraft:generic.attack_speed=-0.05,apothecary:dig_speed=-0.05,minecraft:generic.movement_speed=-0.05",
            "unlucky;RED;minecraft:generic.luck=-0.2",
            "healthy;BLUE;minecraft:generic.max_health=2.0",
            "armored;BLUE;apothecary:damage_resistance=0.03",
            "speedy;BLUE;minecraft:generic.movement_speed=0.03",
            "springy;BLUE;apothecary:jump_height=0.5",
            "prospecting;BLUE;apothecary:dig_speed=0.03",
            "flailing;BLUE;minecraft:generic.attack_speed=0.03",
            "arcane;BLUE;apothecary:magic_shielding=1.0",
            "aiming;BLUE;apothecary:projectile_damage=0.03",
            "strengthening;BLUE;minecraft:generic.attack_damage=0.03",
            "precise;BLUE;apothecary:projectile_damage=0.03",
            "lucky;AQUA;minecraft:generic.luck=0.2",
            "graceful;AQUA;minecraft:generic.attack_speed=0.03,apothecary:dig_speed=0.03",
            "athletic;AQUA;minecraft:generic.movement_speed=0.05,apothecary:jump_height=0.5",
            "versatile;LIGHT_PURPLE;minecraft:generic.attack_speed=0.03,apothecary:dig_speed=0.03,minecraft:generic.movement_speed=0.03",
            "punishing;LIGHT_PURPLE;minecraft:generic.attack_damage=0.03,apothecary:magic_damage=0.03,apothecary:projectile_damage=0.03",
            "undying;LIGHT_PURPLE;minecraft:generic.max_health=2.0,apothecary:damage_resistance=0.03,apothecary:magic_shielding=1.0"
    };
}
