package net.gobies.reforgeable.util;

import net.minecraft.ChatFormatting;

public record Quality(String name, ChatFormatting color, Modifier[] modifiers, int weight) {}

