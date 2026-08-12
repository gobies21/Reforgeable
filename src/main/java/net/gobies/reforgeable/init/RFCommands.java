package net.gobies.reforgeable.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.config.QualityConfig;
import net.gobies.reforgeable.util.Modifier;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Reforgeable.MOD_ID)
public class RFCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("setquality").requires(source -> source.hasPermission(2))
                .then(Commands.argument("quality", StringArgumentType.word())
                        .suggests(RFCommands::suggestQualities)
                        .executes(RFCommands::setQuality)
                )
        );
        dispatcher.register(Commands.literal("reloadQualityConfig")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    QualityConfig.loadJsonConfig();
                    context.getSource().sendSuccess(() -> Component.literal("Successfully reloaded all qualities from config").withStyle(ChatFormatting.GREEN), true);
                    return 1;
                })
        );
    }

    private static CompletableFuture<Suggestions> suggestQualities(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        Set<String> suggestions = new HashSet<>();
        suggestions.add("none");

        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!heldItem.isEmpty() && QualityUtil.isValidQualityItem(heldItem)) {
                for (List<Quality> pool : QualityConfig.CACHED_QUALITIES.values()) {
                    for (Quality quality : pool) {
                        Quality matching = QualityUtil.getQualityForStack(heldItem, quality.name());
                        if (matching != null && matching.name().equals(quality.name()) && matching.weight() != 1) {
                            suggestions.add(quality.name());
                        }
                    }
                }
            }
        }
        return SharedSuggestionProvider.suggest(suggestions, builder);
    }

    private static int setQuality(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return 0;
        }

        String targetQuality = StringArgumentType.getString(context, "quality").toLowerCase();
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("You need an item in hand to change quality"));
            return 0;
        }

        if (!QualityUtil.isValidQualityItem(heldItem)) {
            source.sendFailure(Component.literal("This item cannot receive qualities"));
            return 0;
        }

        if (targetQuality.equals("none")) {
            Quality none = new Quality("none", ChatFormatting.GRAY, new Modifier[0], 0);
            QualityUtil.setQuality(heldItem, none);
            source.sendSuccess(() -> Component.literal("Removed quality from item"), true);
            return 1;
        }

        Quality qualityStack = QualityUtil.getQualityForStack(heldItem, targetQuality);
        if (qualityStack == null || !qualityStack.name().equals(targetQuality) || qualityStack.weight() == 1) {
            source.sendFailure(Component.literal(targetQuality + " does not exist for this item type"));
            return 0;
        }

        QualityUtil.setQuality(heldItem, qualityStack);

        String formattedName = targetQuality.substring(0, 1).toUpperCase() + targetQuality.substring(1);
        source.sendSuccess(() -> Component.literal("Set item quality to ").append(Component.literal(formattedName).withStyle(qualityStack.color())), true);

        return 1;
    }
}