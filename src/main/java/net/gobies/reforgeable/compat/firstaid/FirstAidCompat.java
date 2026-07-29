package net.gobies.reforgeable.compat.firstaid;

import ichttt.mods.firstaid.FirstAidConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
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
        if (!(gearStack.getItem() instanceof ArmorItem armorItem)) return baseValue;

        EquipmentSlot slot = armorItem.getEquipmentSlot();
        var config = FirstAidConfig.SERVER;

        if (baseValue <= 0.0) {
            return baseValue;
        }

        if (attribute == Attributes.ARMOR) {
            return switch (slot) {
                case HEAD -> (baseValue * config.headArmorMultiplier.get()) + config.headArmorOffset.get();
                case CHEST -> (baseValue * config.chestArmorMultiplier.get()) + config.chestArmorOffset.get();
                case LEGS -> (baseValue * config.legsArmorMultiplier.get()) + config.legsArmorOffset.get();
                case FEET -> (baseValue * config.feetArmorMultiplier.get()) + config.feetArmorOffset.get();
                default -> baseValue;
            };
        } else if (attribute == Attributes.ARMOR_TOUGHNESS) {
            return switch (slot) {
                case HEAD -> (baseValue * config.headThoughnessMultiplier.get()) + config.headThoughnessOffset.get();
                case CHEST -> (baseValue * config.chestThoughnessMultiplier.get()) + config.chestThoughnessOffset.get();
                case LEGS -> (baseValue * config.legsThoughnessMultiplier.get()) + config.legsThoughnessOffset.get();
                case FEET -> (baseValue * config.feetThoughnessMultiplier.get()) + config.feetThoughnessOffset.get();
                default -> baseValue;
            };
        }

        return baseValue;
    }
}