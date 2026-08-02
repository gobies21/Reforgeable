package net.gobies.reforgeable.mixin.curios;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.client.ClientEventHandler;

import java.util.Map;
import java.util.UUID;

@Mixin(ClientEventHandler.class)
public abstract class ClientEventHandlerMixin {

    @Redirect(
            method = "onTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Ltop/theillusivec4/curios/api/CuriosApi;getAttributeModifiers(Ltop/theillusivec4/curios/api/SlotContext;Ljava/util/UUID;Lnet/minecraft/world/item/ItemStack;)Lcom/google/common/collect/Multimap;"
            ),
            remap = false, require = 0
    )
    private Multimap<Attribute, AttributeModifier> filterAttributes(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> original = CuriosApi.getAttributeModifiers(slotContext, uuid, stack);
        if (original.isEmpty()) return original;

        Multimap<Attribute, AttributeModifier> filtered = LinkedHashMultimap.create();

        for (Map.Entry<Attribute, AttributeModifier> entry : original.entries()) {
            AttributeModifier modifier = entry.getValue();
            if (modifier != null) {
                modifier.getName();
                if (!modifier.getName().startsWith("Reforgeable ")) {
                    filtered.put(entry.getKey(), modifier);
                }
            }
        }

        return filtered;
    }
}
