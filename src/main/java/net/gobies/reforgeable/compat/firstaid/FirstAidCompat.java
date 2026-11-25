package net.gobies.reforgeable.compat.firstaid;

import ichttt.mods.firstaid.FirstAidConfig;
import net.gobies.reforgeable.config.QualityConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class FirstAidCompat {

    public static boolean isTooltipReplaced(Attribute attribute, ItemStack gearStack) {
        if (!(gearStack.getItem() instanceof ArmorItem)) return false;
        if (FirstAidConfig.CLIENT.armorTooltipMode.get() != FirstAidConfig.Client.TooltipMode.REPLACE) return false;
        return attribute == Attributes.ARMOR || attribute == Attributes.ARMOR_TOUGHNESS;
    }

    public static double scaleValue(Attribute attribute, ItemStack gearStack, double baseValue) {
        if (!(gearStack.getItem() instanceof ArmorItem armorItem) || baseValue == 0.0) return baseValue;

        AttributeModifier.Operation operation = QualityConfig.ATTRIBUTE_OPERATION.getOrDefault(attribute, AttributeModifier.Operation.MULTIPLY_BASE);
        if (operation != AttributeModifier.Operation.ADDITION) {
            return baseValue;
        }

        EquipmentSlot slot = armorItem.getEquipmentSlot();
        var config = FirstAidConfig.SERVER;

        if (attribute == Attributes.ARMOR) {
            return baseValue * switch (slot) {
                case HEAD -> config.headArmorMultiplier.get();
                case CHEST -> config.chestArmorMultiplier.get();
                case LEGS -> config.legsArmorMultiplier.get();
                case FEET -> config.feetArmorMultiplier.get();
                default -> 1.0;
            };
        } else if (attribute == Attributes.ARMOR_TOUGHNESS) {
            return baseValue * switch (slot) {
                case HEAD -> config.headThoughnessMultiplier.get();
                case CHEST -> config.chestThoughnessMultiplier.get();
                case LEGS -> config.legsThoughnessMultiplier.get();
                case FEET -> config.feetThoughnessMultiplier.get();
                default -> 1.0;
            };
        }

        return baseValue;
    }
}