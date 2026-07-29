package net.gobies.reforgeable.mixin.firstaid;

import ichttt.mods.firstaid.common.util.ArmorUtils;
import net.gobies.reforgeable.util.Modifier;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ichttt.mods.firstaid.client.ClientEventHandler.class)
public class ClientEventHandler {
    @Redirect(
            method = "tooltipItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lichttt/mods/firstaid/common/util/ArmorUtils;getArmor(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;)D"
            ),
            remap = false
    )
    private static double redirectArmor(ItemStack stack, EquipmentSlot slot) {
        return ArmorUtils.getArmor(stack, slot) - reforgeable$getQualityModifier(stack, Attributes.ARMOR);
    }

    @Redirect(
            method = "tooltipItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lichttt/mods/firstaid/common/util/ArmorUtils;getArmorToughness(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;)D"
            ),
            remap = false
    )
    private static double redirectArmorToughness(ItemStack stack, EquipmentSlot slot) {
        return ArmorUtils.getArmorToughness(stack, slot) - reforgeable$getQualityModifier(stack, Attributes.ARMOR_TOUGHNESS);
    }

    @Unique
    private static double reforgeable$getQualityModifier(ItemStack stack, Attribute attribute) {
        String qualityName = QualityUtil.getQuality(stack);
        if (!qualityName.isEmpty() && !qualityName.equalsIgnoreCase("none")) {
            Quality quality = QualityUtil.getQualityForStack(stack, qualityName);
            if (quality != null && quality.modifiers() != null) {
                for (Modifier modifier : quality.modifiers()) {
                    if (modifier.attribute() == attribute) {
                        return modifier.value();
                    }
                }
            }
        }
        return 0.0;
    }

    @Redirect(
            method = "makeArmorMsg",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    private static MutableComponent redirectArmorLang(String pKey, Object[] pArgs) {
        return Component.translatable("reforgeable.firstaid.specificarmor", pArgs);
    }

    @Redirect(
            method = "makeToughnessMsg",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    private static MutableComponent redirectToughnessLang(String pKey, Object[] pArgs) {
        return Component.translatable("reforgeable.firstaid.specifictoughness", pArgs);
    }
}