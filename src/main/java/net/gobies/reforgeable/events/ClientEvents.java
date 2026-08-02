package net.gobies.reforgeable.events;

import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reforgeable.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        String qualityName = QualityUtil.getQuality(stack);

        if (qualityName.isEmpty() || qualityName.equalsIgnoreCase("none") || !QualityUtil.isValidQualityItem(stack)) {
            return;
        }

        Quality quality = QualityUtil.getQualityForStack(stack, qualityName);
        if (quality == null || quality.modifiers() == null || quality.modifiers().length == 0) {
            return;
        }

        List<Component> qualityLines = QualityUtil.getQualityTooltips(quality, stack);

        if (!qualityLines.isEmpty()) {
            List<Component> tooltip = event.getToolTip();
            tooltip.add(Component.empty());
            tooltip.addAll(qualityLines);
        }
    }
}