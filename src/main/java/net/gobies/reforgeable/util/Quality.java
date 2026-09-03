package net.gobies.reforgeable.util;

import com.mojang.serialization.Codec;
import net.gobies.reforgeable.config.QualityConfig;
import net.gobies.reforgeable.helper.QualityType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record Quality(String name, ChatFormatting color, Modifier[] modifiers, int weight) {
    public static final Codec<Quality> CODEC = Codec.STRING.xmap(Quality::locateQuality, Quality::name);
    public static final StreamCodec<RegistryFriendlyByteBuf, Quality> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    public static final Quality NONE_QUALITY = new Quality("none", ChatFormatting.GRAY, new Modifier[0], 0);

    private static Quality locateQuality(String qualityName) {
        if (QualityType.NONE.key.equalsIgnoreCase(qualityName)) {
            return NONE_QUALITY;
        }

        for (List<Quality> list : QualityConfig.CACHED_QUALITIES.values()) {
            for (Quality quality : list) {
                if (quality.name().equalsIgnoreCase(qualityName)) {
                    return quality;
                }
            }
        }
        return new Quality(qualityName, ChatFormatting.GRAY, new Modifier[0], 0);
    }
}