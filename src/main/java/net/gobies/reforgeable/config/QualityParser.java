package net.gobies.reforgeable.config;

import net.gobies.reforgeable.helper.QualityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QualityParser {

    public static List<QualityHelper.Quality> parseConfigQualities(List<String> configLines) {
        if (configLines == null || configLines.isEmpty()) {
            return Collections.singletonList(new QualityHelper.Quality("none", ChatFormatting.GRAY, new QualityHelper.Modifier[0]));
        }

        List<QualityHelper.Quality> parsedPool = new ArrayList<>();

        for (String row : configLines) {
            if (row == null || row.isEmpty()) continue;

            String[] tokens = row.split(";");
            if (tokens.length < 2) continue;

            String name = tokens[0].toLowerCase();
            if (name.equals("none") || name.equals("normal")) continue;

            ChatFormatting color = ChatFormatting.getByName(tokens[1].toUpperCase());
            if (color == null) color = ChatFormatting.GRAY;

            List<QualityHelper.Modifier> modifiersList = new ArrayList<>();

            if (tokens.length > 2 && !tokens[2].isEmpty()) {
                String[] attributePairs = tokens[2].split(",");

                for (String pair : attributePairs) {
                    String[] splitPair = pair.split("=");
                    if (splitPair.length < 2) continue;

                    String registryPath = splitPair[0].trim();
                    try {
                        double value = Double.parseDouble(splitPair[1]);
                        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(registryPath));

                        if (attribute != null) {
                            modifiersList.add(new QualityHelper.Modifier(attribute, value));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            parsedPool.add(new QualityHelper.Quality(name, color, modifiersList.toArray(new QualityHelper.Modifier[0])));
        }

        return Collections.unmodifiableList(parsedPool);
    }
}