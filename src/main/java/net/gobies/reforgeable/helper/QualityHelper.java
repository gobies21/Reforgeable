package net.gobies.reforgeable.helper;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.*;

public class QualityHelper {

    public static final Map<Attribute, AttributeModifier.Operation> ATTRIBUTE_OPERATION = new HashMap<>();

    public static Quality resolve(List<Quality> list, String... selectedQuality) {
        if (list == null || list.isEmpty()) {
            return new Quality("none", ChatFormatting.GRAY, new Modifier[0]);
        }

        if (selectedQuality.length > 0) {
            String target = selectedQuality[0].toLowerCase();
            for (Quality quality : list) {
                if (quality.name().equals(target)) return quality;
            }
            return new Quality(selectedQuality[0], ChatFormatting.GRAY, new Modifier[0]);
        }

        int randomIndex = (int) (Math.random() * list.size());
        return list.get(randomIndex);
    }

    public record Modifier(Attribute attribute, double value) {
        public UUID getUuid() {
            return UUID.nameUUIDFromBytes(this.attribute.getDescriptionId().getBytes());
        }
    }

    public record Quality(String name, ChatFormatting color, Modifier[] modifiers) {}
}
