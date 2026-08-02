package net.gobies.reforgeable.util;

import net.gobies.reforgeable.compat.QualityCompat;
import net.gobies.reforgeable.compat.curios.CuriosCompat;
import net.gobies.reforgeable.compat.firstaid.FirstAidCompat;
import net.gobies.reforgeable.compat.moreartifacts.MoreArtifactsCompat;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.config.QualityConfig;
import net.gobies.reforgeable.helper.QualityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QualityUtil {

    private static final String QUALITY_KEY = "Quality";
    private static final Map<Item, Boolean> ITEM_LIST = new ConcurrentHashMap<>();

    public static boolean isValidQualityItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return ITEM_LIST.computeIfAbsent(stack.getItem(), item -> isWeapon(stack) || isTool(stack) || isBow(stack) || isFishingRod(stack) || isArmor(stack) | isShield(stack) || isPetArmor(stack) || (CuriosCompat.isLoaded() && CuriosCompat.isCurio(stack)));
    }

    public static boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    public static boolean isHelmet(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getEquipmentSlot().equals(EquipmentSlot.HEAD);
        }
        return getConfigItems(stack, CommonConfig.ADDITIONAL_HELMET_QUALITIES);
    }

    public static boolean isChestplate(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getEquipmentSlot().equals(EquipmentSlot.CHEST);
        }
        return getConfigItems(stack, CommonConfig.ADDITIONAL_CHESTPLATE_QUALITIES);
    }

    public static boolean isLeggings(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getEquipmentSlot().equals(EquipmentSlot.LEGS);
        }
        return getConfigItems(stack, CommonConfig.ADDITIONAL_LEGGINGS_QUALITIES);
    }

    public static boolean isFeet(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getEquipmentSlot().equals(EquipmentSlot.FEET);
        }
        return getConfigItems(stack, CommonConfig.ADDITIONAL_BOOTS_QUALITIES);
    }

    public static boolean isShield(ItemStack stack) {
        if (stack.getItem() instanceof ShieldItem) {
            return true;
        }
        return getConfigItems(stack, CommonConfig.ADDITIONAL_SHIELD_QUALITIES);
    }

    public static boolean isPetArmor(ItemStack stack) {
        if (stack.getItem() instanceof HorseArmorItem) {
            return true;
        }
        if (QualityCompat.isPetArmor(stack)) return true;
        return getConfigItems(stack, CommonConfig.ADDITIONAL_PET_QUALITIES);
    }

    public static boolean isWeapon(ItemStack stack) {
        Item item = stack.getItem();
        if ((item instanceof TieredItem && !(item instanceof DiggerItem)) || item instanceof TridentItem) {
            return true;
        }
        return getConfigItems(stack, CommonConfig.ADDITIONAL_WEAPON_QUALITIES);
    }

    public static boolean isTool(ItemStack stack) {
        if (stack.getItem() instanceof DiggerItem) {
            return true;
        }
        return getConfigItems(stack, CommonConfig.ADDITIONAL_TOOL_QUALITIES);
    }

    public static boolean isBow(ItemStack stack) {
        if (stack.getItem() instanceof ProjectileWeaponItem) {
            return true;
        }
        return getConfigItems(stack, CommonConfig.ADDITIONAL_BOW_QUALITIES);
    }

    public static boolean isFishingRod(ItemStack stack) {
        if (stack.getItem() instanceof FishingRodItem) {
            return true;
        }
        return getConfigItems(stack, CommonConfig.ADDITIONAL_ROD_QUALITIES);
    }

    public static boolean isBlacklisted(ItemStack stack) {
        if (stack.isEmpty() || QualityHelper.BLACKLISTED_ITEMS.isEmpty()) return false;

        ResourceLocation gearKey = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (gearKey != null && QualityHelper.BLACKLISTED_ITEMS.contains(gearKey.toString())) {
            return true;
        }

        for (TagKey<Item> tagKey : stack.getTags().toList()) {
            if (QualityHelper.BLACKLISTED_ITEMS.contains("#" + tagKey.location())) {
                return true;
            }
        }

        return false;
    }

    public static String getQuality(ItemStack stack) {
        if (stack.hasTag()) {
            assert stack.getTag() != null;
            if (stack.getTag().contains(QUALITY_KEY)) {
                return stack.getTag().getString(QUALITY_KEY);
            }
        }
        return "";
    }

    public static boolean hasQuality(ItemStack stack) {
        if (!stack.hasTag()) return false;
        assert stack.getTag() != null;
        return stack.getTag().contains(QUALITY_KEY);
    }

    public static void setQuality(ItemStack stack, String qualityType) {
        if (stack.isEmpty()) {
            return;
        }
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putString(QUALITY_KEY, qualityType);
    }

    public static Quality getQualityForStack(ItemStack stack, String... qualityName) {
        String category = "none";

        boolean isBlacklisted = !stack.isEmpty() && isBlacklisted(stack);

        if (!isBlacklisted) {
            if (MoreArtifactsCompat.isMAShield(stack)) category = "curio";
            else if (isWeapon(stack)) category = "weapon";
            else if (isTool(stack)) category = "tool";
            else if (isBow(stack)) category = "bow";
            else if (isShield(stack)) category = "shield";
            else if (isFishingRod(stack)) category = "rod";
            else if (isHelmet(stack)) category = "helmet";
            else if (isChestplate(stack)) category = "chestplate";
            else if (isLeggings(stack)) category = "leggings";
            else if (isFeet(stack)) category = "boots";
            else if (isPetArmor(stack)) category = "pet";
            else if (CuriosCompat.isLoaded() && CuriosCompat.isCurio(stack)) category = "curio";
        }

        List<Quality> list = QualityConfig.CACHED_QUALITIES.getOrDefault(category, Collections.emptyList());
        return QualityHelper.resolve(list, qualityName);
    }

    public static List<Component> getQualityTooltips(Quality quality, ItemStack gearStack) {
        List<Component> lines = new ArrayList<>();
        if (quality == null) return lines;
        String qualityKey = "item.quality." + quality.name().toLowerCase();
        lines.add(Component.translatable("reforgeable.quality").withStyle(ChatFormatting.GRAY).append(Component.translatable(qualityKey).withStyle(quality.color())));
        if (quality.modifiers() != null) {
            for (Modifier modifier : quality.modifiers()) {
                double value = modifier.value();

                String prefix = "";
                if (ModList.get().isLoaded("firstaid") && FirstAidCompat.isTooltipReplaced(modifier.attribute(), gearStack)) {
                    prefix = Component.translatable("reforgeable.locational").getString();
                    value = FirstAidCompat.scaleValue(modifier.attribute(), gearStack, value);
                }

                AttributeModifier.Operation operation = QualityHelper.ATTRIBUTE_OPERATION.getOrDefault(modifier.attribute(), AttributeModifier.Operation.MULTIPLY_BASE);
                boolean isPercentage = operation == AttributeModifier.Operation.MULTIPLY_BASE;
                if (isPercentage) {
                    value *= 100.0;
                }
                String displayNumber = BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
                String sign = value > 0 ? "+" : "";
                String suffix = isPercentage ? "% " : " ";
                String attributeName = Component.translatable(modifier.attribute().getDescriptionId()).getString();
                ChatFormatting statColor = value > 0 ? ChatFormatting.BLUE : ChatFormatting.RED;
                lines.add(Component.literal(sign + displayNumber + suffix + prefix + attributeName).withStyle(statColor));
            }
        }
        return lines;
    }

    public static boolean getConfigItems(ItemStack stack, ForgeConfigSpec.ConfigValue<List<? extends String>> configList) {
        if (stack.isEmpty() || configList == null) return false;
        List<? extends String> strings = configList.get();
        if (strings == null || strings.isEmpty()) return false;
        if (!QualityHelper.isInitialized) QualityHelper.initializeConfig();

        Item item = stack.getItem();
        if (QualityHelper.ADDITIONAL_ITEMS.contains(item)) {
            if (strings.contains(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString())) return true;
        }
        for (TagKey<Item> tagKey : QualityHelper.ADDITIONAL_TAGS) {
            if (stack.is(tagKey) && strings.contains("#" + tagKey.location())) {
                return true;
            }
        }
        return false;
    }
}