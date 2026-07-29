package net.gobies.reforgeable.util;

import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.UUID;

public record Modifier(Attribute attribute, double value) {
    public UUID getUuid() {
        return UUID.nameUUIDFromBytes(this.attribute.getDescriptionId().getBytes());
    }
}