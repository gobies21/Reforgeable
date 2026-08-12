package net.gobies.reforgeable.mixin;

import com.google.common.collect.Multimap;
import net.gobies.reforgeable.init.RFDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeUtil;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = AttributeUtil.class, remap = false)
public class AttributeUtilMixin {

    @Inject(
            method = "applyTextFor",
            at = @At("HEAD")
    )
    private static void filterAttributes(ItemStack stack, Consumer<Component> tooltip, Multimap<Holder<Attribute>, AttributeModifier> modifierMap, AttributeTooltipContext ctx, CallbackInfo ci) {
        if (modifierMap == null || modifierMap.isEmpty()) return;
        if (!stack.has(RFDataComponents.QUALITY.get())) return;

        modifierMap.values().removeIf(modifier -> modifier != null && modifier.id().getNamespace().equals("reforgeable"));
    }
}