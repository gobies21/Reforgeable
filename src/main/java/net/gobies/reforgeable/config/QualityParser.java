package net.gobies.reforgeable.config;

import net.gobies.reforgeable.util.Modifier;
import net.gobies.reforgeable.util.Quality;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QualityParser {

    public static List<Quality> parseConfigQualities(List<String> configLines) {
        if (configLines == null || configLines.isEmpty()) {
            return Collections.singletonList(new Quality("none", ChatFormatting.GRAY, new Modifier[0], 0));
        }
        List<Quality> parsedPool = new ArrayList<>();
        for (String row : configLines) {
            if (row == null || row.isEmpty()) continue;
            String[] tokens = row.split(";");
            if (tokens.length < 2) continue;
            String name = tokens[0].toLowerCase();
            if (name.equals("none") || name.equals("normal")) continue;
            ChatFormatting color = ChatFormatting.getByName(tokens[1].toUpperCase());
            if (color == null) color = ChatFormatting.GRAY;

            List<Modifier> modifiersList = new ArrayList<>();
            int weight = 11;

            if (tokens.length > 2 && !tokens[2].isEmpty()) {
                String[] attributePairs = tokens[2].split(",");
                for (String pair : attributePairs) {
                    String[] splitPair = pair.split("=");
                    if (splitPair.length < 2) continue;

                    ResourceLocation key = new ResourceLocation(splitPair[0].trim());
                    if (ForgeRegistries.ATTRIBUTES.containsKey(key)) {
                        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(key);
                        if (attribute != null) {
                            try {
                                modifiersList.add(new Modifier(attribute, Double.parseDouble(splitPair[1])));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
            }

            if (tokens.length > 3 && !tokens[3].isEmpty()) {
                try {
                    weight = Integer.parseInt(tokens[3].trim());
                } catch (NumberFormatException ignored) {
                }
            }
            parsedPool.add(new Quality(name, color, modifiersList.toArray(new Modifier[0]), weight));
        }
        return Collections.unmodifiableList(parsedPool);
    }
}