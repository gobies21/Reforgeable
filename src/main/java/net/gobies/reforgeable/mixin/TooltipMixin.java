package net.gobies.reforgeable.mixin;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(value = ItemStack.class, priority = 900)
public class TooltipMixin {

    @Redirect(
            method = "getTooltipLines",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/util/AttributeUtil;addAttributeTooltips(Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;Lnet/neoforged/neoforge/common/util/AttributeTooltipContext;)V"
            )
    )
    private void reorderAttributes(ItemStack stack, Consumer<Component> consumer, AttributeTooltipContext context) {
        ItemAttributeModifiers originalModifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (originalModifiers == null || originalModifiers.modifiers().isEmpty()) {
            AttributeUtil.addAttributeTooltips(stack, consumer, context);
            return;
        }

        Multimap<Holder<Attribute>, AttributeModifier> original = LinkedHashMultimap.create();
        for (ItemAttributeModifiers.Entry entry : originalModifiers.modifiers()) {
            original.put(entry.attribute(), entry.modifier());
        }

        LinkedHashMultimap<Holder<Attribute>, AttributeModifier> sorted = LinkedHashMultimap.create();

        if (!(stack.getItem() instanceof ArmorItem)) {
            for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : original.entries()) {
                if (entry.getKey().equals(Attributes.ATTACK_DAMAGE)) {
                    sorted.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : original.entries()) {
                if (entry.getKey().equals(Attributes.ATTACK_SPEED)) {
                    sorted.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : original.entries()) {
                if (entry.getKey().equals(Attributes.ENTITY_INTERACTION_RANGE)) {
                    sorted.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : original.entries()) {
                if (!entry.getKey().equals(Attributes.ATTACK_DAMAGE) && !entry.getKey().equals(Attributes.ATTACK_SPEED) && !entry.getKey().equals(Attributes.ENTITY_INTERACTION_RANGE)) {
                    sorted.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : original.entries()) {
                if (entry.getKey().equals(Attributes.ARMOR)) {
                    sorted.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : original.entries()) {
                if (entry.getKey().equals(Attributes.ARMOR_TOUGHNESS)) {
                    sorted.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : original.entries()) {
                if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                    sorted.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : original.entries()) {
                if (!entry.getKey().equals(Attributes.ARMOR) && !entry.getKey().equals(Attributes.ARMOR_TOUGHNESS) && !entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                    sorted.put(entry.getKey(), entry.getValue());
                }
            }
        }

        ItemStack sortedStack = stack.copy();
        List<ItemAttributeModifiers.Entry> sortedEntries = new ArrayList<>();

        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : sorted.entries()) {
            for (ItemAttributeModifiers.Entry originalEntry : originalModifiers.modifiers()) {
                if (originalEntry.attribute().equals(entry.getKey()) && originalEntry.modifier().equals(entry.getValue())) {
                    sortedEntries.add(new ItemAttributeModifiers.Entry(entry.getKey(), entry.getValue(), originalEntry.slot()));
                    break;
                }
            }
        }

        sortedStack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(sortedEntries, originalModifiers.showInTooltip()));
        AttributeUtil.addAttributeTooltips(sortedStack, consumer, context);
    }
}