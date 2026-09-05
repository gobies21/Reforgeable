package net.gobies.reforgeable.helper;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.client.ReforgingMenu;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.util.Modifier;
import net.gobies.reforgeable.util.Quality;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;

public class QualityHelper {

    public static final Set<String> REFORGE_MATERIALS = new HashSet<>();
    public static boolean isInitialized = false;

    public static void initializeConfig() {
        for (QualityType category : QualityType.values()) {
            category.clear();
        }
        REFORGE_MATERIALS.clear();

        addConfigLists(CommonConfig.ADDITIONAL_SHIELD_QUALITIES, QualityType.SHIELD);
        addConfigLists(CommonConfig.ADDITIONAL_PET_QUALITIES, QualityType.PET);
        addConfigLists(CommonConfig.ADDITIONAL_WEAPON_QUALITIES, QualityType.WEAPON);
        addConfigLists(CommonConfig.ADDITIONAL_TOOL_QUALITIES, QualityType.TOOL);
        addConfigLists(CommonConfig.ADDITIONAL_BOW_QUALITIES, QualityType.BOW);
        addConfigLists(CommonConfig.ADDITIONAL_ROD_QUALITIES, QualityType.ROD);
        addConfigLists(CommonConfig.ADDITIONAL_CURIO_QUALITIES, QualityType.CURIO);
        addConfigLists(CommonConfig.BLACKLIST_QUALITIES, QualityType.BLACKLIST);

        List<? extends String> materials = CommonConfig.REFORGE_MATERIALS.get();
        REFORGE_MATERIALS.addAll(materials);
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

        int totalWeight = 0; // Total sum of all quality weights
        int maxRareWeight = CommonConfig.MAX_WEIGHT.get(); // Max weight for rare qualities
        int rareWeightTotal = 0; // Total sum of all rare quality weights

        for (Quality quality : list) {
            int weight = Math.max(1, quality.weight());
            totalWeight += weight;
            if (weight <= maxRareWeight) {
                rareWeightTotal += weight;
            }
        }

        double luckFactor = getLuckFactor();
        double rolledValue = (Math.random() * totalWeight) * luckFactor;

        if (rolledValue >= totalWeight) {
            if (rareWeightTotal > 0) {
                rolledValue = totalWeight - (Math.random() * rareWeightTotal);
            } else {
                rolledValue = totalWeight - 0.01;
            }
        }

        int cumulativeWeight = 0;
        for (Quality quality : list) {
            cumulativeWeight += Math.max(1, quality.weight());
            if (rolledValue < cumulativeWeight) {
                return quality;
            }
        }
        return list.getLast();
    }

    public static final ThreadLocal<Float> luckHolder = ThreadLocal.withInitial(() -> null);

    private static double getLuckFactor() {
        float luck = 0.0F;
        Float playerLuck = luckHolder.get();
        if (playerLuck != null) {
            luck = luckHolder.get();
        } else {
            try {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null) {
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        if (player.containerMenu instanceof ReforgingMenu menu && menu.contextPlayer == player) {
                            luck = player.getLuck();
                            break;
                        }
                    }
                }
            } catch (Throwable t) {
                Reforgeable.LOGGER.error("Failed to fetch player luck values");
            }
        }

        if (luck == 0.0F) {
            return 1.0;
        }

        double luckScale = CommonConfig.LUCK_SCALE.get();
        double luckFactor;

        if (luck > 0) {
            luckFactor = Math.max(0.99, 1.0 + (luck * luckScale));
        } else {
            luckFactor = Math.max(0.01, 1.0 - (Math.abs(luck) * luckScale));
        }

        return luckFactor;
    }

    private static void addConfigLists(ModConfigSpec.ConfigValue<List<? extends String>> config, QualityType type) {
        if (config == null) {
            return;
        } else {
            config.get();
        }
        for (String entry : config.get()) {
            if (entry == null || entry.isEmpty()) continue;
            String trimmed = entry.trim();

            if (trimmed.startsWith("#")) {
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, ResourceLocation.parse(trimmed.substring(1)));
                type.tags.add(tagKey);
            } else {
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(trimmed));
                if (item != Items.AIR) {
                    type.items.add(item);
                }
            }
        }
    }
}
