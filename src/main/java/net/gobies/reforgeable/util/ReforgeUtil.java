package net.gobies.reforgeable.util;

import net.gobies.reforgeable.compat.MaterialCompat;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.helper.QualityHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ReforgeUtil {

    private static final Map<Item, List<Item>> HINT_ITEMS = new ConcurrentHashMap<>();

    public static boolean getReforgeMaterial(ItemStack gearStack, ItemStack materialStack) {
        if (gearStack.isEmpty() || materialStack.isEmpty()) {
            return false;
        }

        boolean canReforge = false;

        Item item = gearStack.getItem();

        if (item.isValidRepairItem(gearStack, materialStack)) {
            canReforge = true;
        }

        if (item instanceof TieredItem tieredItem && tieredItem.getTier() == Tiers.NETHERITE) {
            if (materialStack.is(Items.NETHERITE_SCRAP)) {
                canReforge = true;
            }
        }

        if (item instanceof ArmorItem armor && armor.getMaterial() == ArmorMaterials.NETHERITE) {
            if (materialStack.is(Items.NETHERITE_SCRAP)) {
                canReforge = true;
            }
        }

        if (item instanceof BowItem || item instanceof CrossbowItem || item instanceof FishingRodItem) {
            if (materialStack.is(Items.STRING)) {
                canReforge = true;
            }
        }

        if (item instanceof TridentItem) {
            if (materialStack.is(Items.IRON_INGOT)) {
                canReforge = true;
            }
        }

        if (getConfiguredMaterials(gearStack).contains(materialStack.getItem())) {
            canReforge = true;
        }

        String globalMaterialString = CommonConfig.GLOBAL_REFORGE_MATERIAL.get();
        if (!globalMaterialString.isEmpty()) {
            ResourceLocation globalKey = ResourceLocation.tryParse(globalMaterialString);
            if (globalKey != null) {
                Item globalItem = BuiltInRegistries.ITEM.get(globalKey);
                if (Items.AIR != globalItem && materialStack.is(globalItem)) {
                    canReforge = true;
                }
            }
        }

        if (MaterialCompat.isAdditionalMaterial(gearStack, materialStack)) {
            canReforge = true;
        }

        return canReforge;
    }

    public static List<Item> getHintItems(ItemStack materialStack) {
        if (materialStack.isEmpty()) {
            return new ArrayList<>();
        }
        Item gearItem = materialStack.getItem();

        return HINT_ITEMS.computeIfAbsent(gearItem, item -> {
            List<Item> hints = new ArrayList<>();
            ItemStack gearStack = new ItemStack(item);

            for (Item configMaterial : getConfiguredMaterials(gearStack)) {
                if (!hints.contains(configMaterial)) {
                    hints.add(configMaterial);
                }
            }

            if (item instanceof TieredItem tieredItem && tieredItem.getTier() == Tiers.NETHERITE) {
                if (!hints.contains(Items.NETHERITE_SCRAP)) {
                    hints.add(Items.NETHERITE_SCRAP);
                }
            }

            if (item instanceof ArmorItem armor && armor.getMaterial() == ArmorMaterials.NETHERITE) {
                if (!hints.contains(Items.NETHERITE_SCRAP)) {
                    hints.add(Items.NETHERITE_SCRAP);
                }
            }

            for (Item material : BuiltInRegistries.ITEM) {
                if (material != Items.AIR) {
                    ItemStack testMaterialStack = new ItemStack(material);
                    if (item.isValidRepairItem(gearStack, testMaterialStack) || MaterialCompat.isAdditionalMaterial(gearStack, testMaterialStack)) {
                        if (!hints.contains(material)) {
                            hints.add(material);
                        }
                    }
                }
            }

            if (item instanceof TridentItem) {
                if (!hints.contains(Items.IRON_INGOT)) {
                    hints.add(Items.IRON_INGOT);
                }
            }

            if (item.asItem().equals(Items.BOW) || item.asItem().equals(Items.CROSSBOW) || item.asItem().equals(Items.FISHING_ROD)) {
                if (!hints.contains(Items.STRING)) {
                    hints.add(Items.STRING);
                }
            }

            return hints;
        });
    }

    private static List<Item> getConfiguredMaterials(ItemStack gearStack) {
        List<Item> resolved = new ArrayList<>();
        if (gearStack.isEmpty()) {
            return resolved;
        }

        var itemRegistry = BuiltInRegistries.ITEM;
        String gearId = Objects.requireNonNull(itemRegistry.getKey(gearStack.getItem())).toString();

        for (String entry : QualityHelper.REFORGE_MATERIALS) {
            if (entry == null || !entry.contains("=")) {
                continue;
            }

            String[] parts = entry.split("=", 2);
            String targetGear = parts[0].trim();
            String targetMaterial = parts[1].trim();

            boolean gearMatches = false;

            if (targetGear.startsWith("#")) {
                var gearTagKey = TagKey.create(Registries.ITEM, ResourceLocation.parse(targetGear.substring(1)));
                if (gearStack.is(gearTagKey)) {
                    gearMatches = true;
                }
            } else {
                if (targetGear.equals(gearId)) {
                    gearMatches = true;
                }
            }

            if (gearMatches) {
                if (targetMaterial.startsWith("#")) {
                    TagKey<Item> tagKey = TagKey.create(Registries.ITEM, ResourceLocation.parse(targetMaterial.substring(1)));
                    BuiltInRegistries.ITEM.getOrCreateTag(tagKey).forEach(holder -> resolved.add(holder.value()));
                } else {
                    Item materialItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(targetMaterial));
                    if (materialItem != Items.AIR) {
                        resolved.add(materialItem);
                    }
                }
            }
        }
        return resolved;
    }
}