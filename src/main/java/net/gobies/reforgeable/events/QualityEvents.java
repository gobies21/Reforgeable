package net.gobies.reforgeable.events;

import net.gobies.reforgeable.compat.curios.CuriosCompat;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.config.QualityConfig;
import net.gobies.reforgeable.helper.QualityHelper;
import net.gobies.reforgeable.util.Modifier;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.*;

public class QualityEvents {

    public static void register() {
        NeoForge.EVENT_BUS.register(new QualityEvents());
    }

    private static final EquipmentSlot[] SAVE_SLOTS = EquipmentSlot.values();
    public static final ThreadLocal<Float> playerLuck = ThreadLocal.withInitial(() -> null);

    @SubscribeEvent
    public void onLivingTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.level().isClientSide()) return;

            int updateRate = Math.max(1, CommonConfig.QUALITY_UPDATE_RATE.get());
            if (livingEntity.tickCount % updateRate != 0) return;

            if (livingEntity instanceof Player player) {
                playerLuck.set(player.getLuck());
                List<ItemStack> inventory = player.getInventory().items;
                for (ItemStack stack : inventory) {
                    processItemQuality(stack);
                }
                playerLuck.remove();
            } else {
                for (EquipmentSlot saveSlot : SAVE_SLOTS) {
                    processItemQuality(livingEntity.getItemBySlot(saveSlot));
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        String qualityName = QualityUtil.getQuality(stack);

        if (qualityName.isEmpty() || !QualityUtil.isValidQualityItem(stack)) return;

        if (CuriosCompat.isCurio(stack)) return;

        EquipmentSlot slot;

        if (QualityUtil.isShield(stack)) {
            slot = EquipmentSlot.OFFHAND;
        } else if (QualityUtil.isPetArmor(stack)) {
            slot = EquipmentSlot.BODY;
        } else if (stack.getItem() instanceof ArmorItem armor) {
            slot = armor.getEquipmentSlot();
        } else {
            slot = EquipmentSlot.MAINHAND;
        }

        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(slot);

        Quality quality = QualityUtil.getQualityForStack(stack, qualityName);

        for (Modifier modifier : quality.modifiers()) {
            Attribute attribute = modifier.attribute();
            Holder<Attribute> attributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);

            String path = (modifier.getId().getPath() + "_" + quality.name() + "_" + slot.getName()).toLowerCase();
            ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath("reforgeable", path);

            AttributeModifier attributeModifier = new AttributeModifier(
                    modifierId,
                    modifier.value(),
                    QualityHelper.ATTRIBUTE_OPERATION.getOrDefault(attribute, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            );
            event.addModifier(attributeHolder, attributeModifier, slotGroup);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerAboutToStartEvent event) {
        QualityConfig.loadJsonConfig();
        QualityHelper.initializeConfig();
    }

    private void processItemQuality(ItemStack stack) {
        if (stack.isEmpty() || QualityUtil.hasQuality(stack)) return;
        if (QualityUtil.isBlacklisted(stack)) return;

        if (QualityUtil.isValidQualityItem(stack)) {
            if (Math.random() < CommonConfig.NO_QUALITY_CHANCE.get()) {
                Quality none = new Quality("none", ChatFormatting.GRAY, new Modifier[0], 0);
                QualityUtil.setQuality(stack, none);
            } else {
                Quality rolled = QualityUtil.getQualityForStack(stack);
                if (rolled != null) {
                    QualityUtil.setQuality(stack, rolled);
                }
            }
        }
    }
}