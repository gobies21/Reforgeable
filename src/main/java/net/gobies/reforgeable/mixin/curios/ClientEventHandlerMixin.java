package net.gobies.reforgeable.mixin.curios;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.theillusivec4.curios.client.ClientEventHandler;

@SuppressWarnings("all")
@Mixin(ClientEventHandler.class)
public class ClientEventHandlerMixin {

    @ModifyVariable(
            method = "onAttributeTooltip",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Ltop/theillusivec4/curios/api/CuriosApi;getAttributeModifiers(Ltop/theillusivec4/curios/api/SlotContext;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/ItemStack;)Lcom/google/common/collect/Multimap;",
                    remap = false
            ),
            name = "attributes"
    )
    private Multimap<Holder<Attribute>, AttributeModifier> filterAttributes(Multimap<Holder<Attribute>, AttributeModifier> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return attributes;
        }

        attributes.values().removeIf(modifier -> modifier != null && modifier.id().getNamespace().equals("reforgeable"));

        return attributes;
    }
}