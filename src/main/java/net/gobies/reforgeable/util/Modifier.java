package net.gobies.reforgeable.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

public record Modifier(Attribute attribute, double value) {
    public ResourceLocation getId() {
        return BuiltInRegistries.ATTRIBUTE.getKey(this.attribute);
    }
}