package net.gobies.reforgeable.util;

import net.gobies.reforgeable.compat.QualityCompat;
import net.gobies.reforgeable.compat.curios.CuriosCompat;
import net.gobies.reforgeable.compat.firstaid.FirstAidCompat;
import net.gobies.reforgeable.compat.ironsspellbooks.SpellbooksCompat;
import net.gobies.reforgeable.compat.moreartifacts.MoreArtifactsCompat;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.config.QualityConfig;
import net.gobies.reforgeable.helper.QualityType;
import net.gobies.reforgeable.helper.QualityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.ModList;

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

        return ITEM_LIST.computeIfAbsent(stack.getItem(), item -> isWeapon(stack) || isTool(stack) || isBow(stack) || isFishingRod(stack) || isArmor(stack) | isShield(stack) || isPetArmor(stack) || (CuriosCompat.isLoaded() && CuriosCompat.isCurio(stack)) || (SpellbooksCompat.isLoaded() && SpellbooksCompat.isMagicItem(stack)));
    }

    public static boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    public static boolean isHelmet(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getEquipmentSlot().equals(EquipmentSlot.HEAD);
        }
        return false;
    }

    public static boolean isChestplate(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getEquipmentSlot().equals(EquipmentSlot.CHEST);
        }
        return false;
    }

    public static boolean isLeggings(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getEquipmentSlot().equals(EquipmentSlot.LEGS);
        }
        return false;
    }

    public static boolean isFeet(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getEquipmentSlot().equals(EquipmentSlot.FEET);
        }
        return false;
    }

    public static boolean isShield(ItemStack stack) {
        if (stack.getItem() instanceof ShieldItem) {
            return true;
        }
        return getConfigItems(stack, QualityType.SHIELD);
    }

    public static boolean isPetArmor(ItemStack stack) {
        if (stack.getItem() instanceof HorseArmorItem) {
            return true;
        }
        if (QualityCompat.isPetArmor(stack)) return true;
        return getConfigItems(stack, QualityType.PET);
    }

    public static boolean isWeapon(ItemStack stack) {
        Item item = stack.getItem();
        if ((item instanceof TieredItem && !(item instanceof DiggerItem)) || item instanceof TridentItem) {
            return true;
        }
        return getConfigItems(stack, QualityType.WEAPON);
    }

    public static boolean isTool(ItemStack stack) {
        if (stack.getItem() instanceof DiggerItem) {
            return true;
        }
        return getConfigItems(stack, QualityType.TOOL);
    }

    public static boolean isBow(ItemStack stack) {
        if (stack.getItem() instanceof ProjectileWeaponItem) {
            return true;
        }
        return getConfigItems(stack,  QualityType.BOW);
    }

    public static boolean isFishingRod(ItemStack stack) {
        if (stack.getItem() instanceof FishingRodItem) {
            return true;
        }
        return getConfigItems(stack, QualityType.ROD);
    }

    public static boolean isBlacklisted(ItemStack stack) {
        return getConfigItems(stack, QualityType.BLACKLIST);
    }

    public static String getQuality(ItemStack stack) {
        if (hasQuality(stack) && stack.getTag() != null) {
            return stack.getTag().getString(QUALITY_KEY);
        }
        return "";
    }

    public static boolean hasQuality(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains(QUALITY_KEY);
    }

    public static void setQuality(ItemStack stack, String qualityType) {
        if (stack.isEmpty()) {
            return;
        }
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putString(QUALITY_KEY, qualityType);
    }

    public static void removeQuality(ItemStack stack) {
        if (stack.isEmpty()) return;

        CompoundTag nbt = stack.getTag();
        if (nbt != null) {
            nbt.remove(QUALITY_KEY);
        }
    }

    public static Quality getQualityForStack(ItemStack stack, String... qualityName) {
        QualityType type = QualityType.NONE;

        if (MoreArtifactsCompat.isMAShield(stack)) type = QualityType.CURIO;
        else if (isWeapon(stack)) type = QualityType.WEAPON;
        else if (isTool(stack)) type = QualityType.TOOL;
        else if (isBow(stack)) type = QualityType.BOW;
        else if (isShield(stack)) type = QualityType.SHIELD;
        else if (isFishingRod(stack)) type = QualityType.ROD;
        else if (isHelmet(stack)) type = QualityType.HELMET;
        else if (isChestplate(stack)) type = QualityType.CHESTPLATE;
        else if (isLeggings(stack)) type = QualityType.LEGGINGS;
        else if (isFeet(stack)) type = QualityType.BOOTS;
        else if (isPetArmor(stack)) type = QualityType.PET;
        else if (SpellbooksCompat.isMagicItem(stack)) type = QualityType.MAGIC;
        else if (CuriosCompat.isLoaded() && CuriosCompat.isCurio(stack)) type = QualityType.CURIO;

        List<Quality> list = QualityConfig.CACHED_QUALITIES.getOrDefault(type.key, Collections.emptyList());
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

                AttributeModifier.Operation operation = QualityConfig.ATTRIBUTE_OPERATION.getOrDefault(modifier.attribute(), AttributeModifier.Operation.MULTIPLY_BASE);
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


    public static void processItemQuality(ItemStack stack) {
        if (stack.isEmpty()) return;
        if (QualityUtil.hasQuality(stack)) {
            if (QualityUtil.isBlacklisted(stack)) QualityUtil.removeQuality(stack);
            return;
        }

        if (QualityUtil.isBlacklisted(stack)) return;

        if (QualityUtil.isValidQualityItem(stack)) {
            if (Math.random() < CommonConfig.NO_QUALITY_CHANCE.get()) {
                QualityUtil.setQuality(stack, "none");
            } else {
                Quality rolled = QualityUtil.getQualityForStack(stack);
                if (rolled != null) {
                    QualityUtil.setQuality(stack, rolled.name());
                }
            }
        }
    }

    public static boolean getConfigItems(ItemStack stack, QualityType type) {
        if (stack.isEmpty()) return false;
        if (!QualityHelper.isInitialized) QualityHelper.initializeConfig();

        if (type.items.contains(stack.getItem())) {
            return true;
        }

        if (type.tags.isEmpty()) return false;

        for (TagKey<Item> tagKey : type.tags) {
            if (stack.is(tagKey)) {
                return true;
            }
        }
        return false;
    }
}