package net.gobies.reforgeable.helper;

import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.util.Modifier;
import net.gobies.reforgeable.util.Quality;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class QualityHelper {

    public static final Map<Attribute, AttributeModifier.Operation> ATTRIBUTE_OPERATION = new HashMap<>();
    public static final Set<Item> ADDITIONAL_ITEMS = new HashSet<>();
    public static final Set<TagKey<Item>> ADDITIONAL_TAGS = new HashSet<>();
    public static final Set<String> BLACKLISTED_ITEMS = new HashSet<>();
    public static boolean isInitialized = false;

    public static void initializeConfig() {
        ADDITIONAL_ITEMS.clear();
        ADDITIONAL_TAGS.clear();
        BLACKLISTED_ITEMS.clear();

        addConfigLists(CommonConfig.ADDITIONAL_HELMET_QUALITIES);
        addConfigLists(CommonConfig.ADDITIONAL_CHESTPLATE_QUALITIES);
        addConfigLists(CommonConfig.ADDITIONAL_LEGGINGS_QUALITIES);
        addConfigLists(CommonConfig.ADDITIONAL_BOOTS_QUALITIES);
        addConfigLists(CommonConfig.ADDITIONAL_SHIELD_QUALITIES);
        addConfigLists(CommonConfig.ADDITIONAL_PET_QUALITIES);
        addConfigLists(CommonConfig.ADDITIONAL_WEAPON_QUALITIES);
        addConfigLists(CommonConfig.ADDITIONAL_TOOL_QUALITIES);
        addConfigLists(CommonConfig.ADDITIONAL_BOW_QUALITIES);
        addConfigLists(CommonConfig.ADDITIONAL_ROD_QUALITIES);
        addConfigLists(CommonConfig.ADDITIONAL_CURIO_QUALITIES);
        addConfigLists(CommonConfig.BLACKLIST_QUALITIES);
        List<? extends String> blacklist = CommonConfig.BLACKLIST_QUALITIES.get();
        if (blacklist != null) {
            BLACKLISTED_ITEMS.addAll(blacklist);
        }

        isInitialized = true;
    }

    public static Quality resolve(List<Quality> list, String... selectedQuality) {
        if (list == null || list.isEmpty()) {
            return new Quality("none", ChatFormatting.GRAY, new Modifier[0], 0);
        }
        if (selectedQuality != null && selectedQuality.length > 0 && selectedQuality[0] != null) {
            String target = selectedQuality[0].toLowerCase();
            for (Quality quality : list) {
                if (quality.name().equals(target)) return quality;
            }
            return new Quality(selectedQuality[0], ChatFormatting.GRAY, new Modifier[0], 1);
        }

        int totalWeight = 0;
        for (Quality quality : list) {
            totalWeight += Math.max(1, quality.weight());
        }

        double randomValue = Math.random() * totalWeight;
        int cumulativeWeight = 0;

        for (Quality quality : list) {
            cumulativeWeight += Math.max(1, quality.weight());
            if (randomValue < cumulativeWeight) {
                return quality;
            }
        }

        return list.get(list.size() - 1);
    }

    private static void addConfigLists(ForgeConfigSpec.ConfigValue<List<? extends String>> config) {
        if (config == null || config.get() == null) return;
        for (String entry : config.get()) {
            if (entry == null || entry.isEmpty()) continue;
            String trimmed = entry.trim();

            if (trimmed.startsWith("#")) {
                ADDITIONAL_TAGS.add(TagKey.create(Registries.ITEM, new ResourceLocation(trimmed.substring(1))));
            } else {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(trimmed));
                if (item != null && item != net.minecraft.world.item.Items.AIR) {
                    ADDITIONAL_ITEMS.add(item);
                }
            }
        }
    }
}
