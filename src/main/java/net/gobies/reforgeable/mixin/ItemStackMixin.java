package net.gobies.reforgeable.mixin;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.Map;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @ModifyVariable(
            method = "getTooltipLines",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/item/ItemStack;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"
            ),
            ordinal = 0
    )
    private Multimap<Attribute, AttributeModifier> filterAttributes(Multimap<Attribute, AttributeModifier> original) {
        if (original == null || original.isEmpty()) {
            return original;
        }

        Multimap<Attribute, AttributeModifier> filtered = LinkedHashMultimap.create();
        for (Map.Entry<Attribute, AttributeModifier> entry : original.entries()) {
            AttributeModifier modifier = entry.getValue();

            if (modifier != null) {
                if (modifier.getName().startsWith("Reforgeable ")) {
                    continue;
                }
            }

            filtered.put(entry.getKey(), entry.getValue());
        }

        return filtered;
    }
}