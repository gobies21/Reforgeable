package net.gobies.reforgeable.events;

import net.gobies.reforgeable.compat.curios.CuriosCompat;
import net.gobies.reforgeable.compat.moreartifacts.MoreArtifactsCompat;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.helper.QualityHelper;
import net.gobies.reforgeable.util.Modifier;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static net.gobies.reforgeable.util.QualityUtil.getQualityForStack;

public class ReforgingEvents {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new ReforgingEvents());
    }

    private static final EquipmentSlot[] SAVE_SLOTS = EquipmentSlot.values();

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        int updateRate = Math.max(1, CommonConfig.QUALITY_UPDATE_RATE.get());
        if (entity.tickCount % updateRate != 0) return;

        if (entity instanceof Player player) {
            List<ItemStack> inventory = player.getInventory().items;
            for (ItemStack stack : inventory) {
                processItemQuality(stack);
            }
        } else {
            for (EquipmentSlot saveSlot : SAVE_SLOTS) {
                processItemQuality(entity.getItemBySlot(saveSlot));
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
        } else if (QualityUtil.isPetArmor(stack)) {
            validSlot = EquipmentSlot.CHEST;
        } else if (stack.getItem() instanceof ArmorItem armor) {
            validSlot = armor.getEquipmentSlot();
        } else {
            validSlot = EquipmentSlot.MAINHAND;
        }

        if (event.getSlotType() != validSlot) return;

        Quality quality = QualityUtil.getQualityForStack(stack, qualityName);

        for (Modifier modifier : quality.modifiers()) {
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

    private void processItemQuality(ItemStack stack) {
        if (stack.isEmpty() || QualityUtil.hasQuality(stack)) return;

        if (QualityUtil.isValidQualityItem(stack)) {
            if (Math.random() < CommonConfig.NO_QUALITY_CHANCE.get()) {
                QualityUtil.setQuality(stack, "none");
            } else {
                Quality rolled = getQualityForStack(stack);
                if (rolled != null) {
                    QualityUtil.setQuality(stack, rolled.name());
                }
            }
        }
    }
}