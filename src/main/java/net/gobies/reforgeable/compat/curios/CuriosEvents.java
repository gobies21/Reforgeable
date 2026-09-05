package net.gobies.reforgeable.compat.curios;

import net.gobies.reforgeable.config.QualityConfig;
import net.gobies.reforgeable.util.Modifier;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

public class CuriosEvents {

    public static void loadCompat() {
        NeoForge.EVENT_BUS.register(new CuriosEvents());
    }

    @SubscribeEvent
    public void onCurioAttributeModifier(CurioAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        String qualityName = QualityUtil.getQuality(stack);

        if (qualityName.isEmpty() || !QualityUtil.isValidQualityItem(stack)) return;
        String curioSlotId = event.getSlotContext().identifier();
        int slotIndex = event.getSlotContext().index();

        Quality quality = QualityUtil.getQualityForStack(stack, qualityName);

        for (Modifier modifier : quality.modifiers()) {
            Attribute attribute = modifier.attribute();
            Holder<Attribute> attributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);

            String path = (modifier.getId().getPath() + "_" + quality.name() + "_" + curioSlotId + "_" + slotIndex).toLowerCase();
            ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath("reforgeable", path);

            AttributeModifier attributeModifier = new AttributeModifier(
                    modifierId,
                    modifier.value(),
                    QualityConfig.ATTRIBUTE_OPERATION.getOrDefault(attribute, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            );
            event.addModifier(attributeHolder, attributeModifier);
        }
    }
}
