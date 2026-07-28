package net.gobies.reforgeable.compat.curios;

import net.gobies.reforgeable.helper.QualityHelper;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.UUID;

public class CuriosEvents {

    public static void loadCompat() {
        MinecraftForge.EVENT_BUS.register(new CuriosEvents());
    }

    @SubscribeEvent
    public void onCurioAttributeModifier(CurioAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        String qualityName = QualityUtil.getQuality(stack);

        if (qualityName.isEmpty() || !QualityUtil.isValidQualityItem(stack)) return;

        QualityHelper.Quality quality = QualityUtil.getQualityForStack(stack, qualityName);

        String curioSlotId = event.getSlotContext().identifier();

        for (QualityHelper.Modifier modifier : quality.modifiers()) {
            UUID baseAttributeUuid = modifier.getUuid();
            UUID uuidFromBytes = UUID.nameUUIDFromBytes((quality.name() + baseAttributeUuid + curioSlotId).getBytes());

            AttributeModifier attributeModifier = new AttributeModifier(
                    uuidFromBytes,
                    "Reforgeable " + quality.name(),
                    modifier.value(),
                    QualityHelper.ATTRIBUTE_OPERATION.getOrDefault(modifier.attribute(), AttributeModifier.Operation.MULTIPLY_BASE)
            );

            event.addModifier(modifier.attribute(), attributeModifier);
        }
    }
}
