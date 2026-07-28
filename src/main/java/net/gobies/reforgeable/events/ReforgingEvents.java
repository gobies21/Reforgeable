package net.gobies.reforgeable.events;

import net.gobies.reforgeable.compat.curios.CuriosCompat;
import net.gobies.reforgeable.compat.moreartifacts.MoreArtifactsCompat;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.helper.QualityHelper;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static net.gobies.reforgeable.util.QualityUtil.getQualityForStack;

public class ReforgingEvents {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new ReforgingEvents());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Player player = event.player;
        if (player.level().isClientSide) return;
        if (player.tickCount % CommonConfig.QUALITY_UPDATE_RATE.get() != 0) return;
        List<ItemStack> inventory = player.getInventory().items;
        for (ItemStack stack : inventory) {
            if (stack.isEmpty() || QualityUtil.hasQuality(stack)) continue;

            if (QualityUtil.isValidQualityItem(stack)) {
                if (Math.random() < CommonConfig.NO_QUALITY_CHANCE.get()) {
                    QualityUtil.setQuality(stack, "none");
                } else {
                    QualityHelper.Quality rolled = getQualityForStack(stack);
                    QualityUtil.setQuality(stack, rolled.name());
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        String qualityName = QualityUtil.getQuality(stack);

        if (qualityName.isEmpty() || !QualityUtil.isValidQualityItem(stack)) return;

        boolean maShield = MoreArtifactsCompat.isMAShield(stack);

        if (CuriosCompat.isCurio(stack) && !MoreArtifactsCompat.isMAShield(stack)) return;

        EquipmentSlot validSlot;

        if (maShield) {
            validSlot = EquipmentSlot.OFFHAND;
        } else if (QualityUtil.isShield(stack)) {
            validSlot = EquipmentSlot.OFFHAND;
        } else if (QualityUtil.isPet(stack)) {
            validSlot = EquipmentSlot.CHEST;
        } else if (stack.getItem() instanceof ArmorItem armor) {
            validSlot = armor.getEquipmentSlot();
        } else {
            validSlot = EquipmentSlot.MAINHAND;
        }

        if (event.getSlotType() != validSlot) return;

        QualityHelper.Quality quality = QualityUtil.getQualityForStack(stack, qualityName);

        for (QualityHelper.Modifier modifier : quality.modifiers()) {
            UUID baseAttributeUuid = modifier.getUuid();
            UUID uuidFromBytes = UUID.nameUUIDFromBytes((quality.name() + baseAttributeUuid + validSlot.getName()).getBytes());

            AttributeModifier attributeModifier = new AttributeModifier(
                    uuidFromBytes,
                    "Reforgeable " + qualityName,
                    modifier.value(),
                    QualityHelper.ATTRIBUTE_OPERATION.getOrDefault(modifier.attribute(), AttributeModifier.Operation.MULTIPLY_BASE)
            );
            event.addModifier(modifier.attribute(), attributeModifier);
        }
    }
}