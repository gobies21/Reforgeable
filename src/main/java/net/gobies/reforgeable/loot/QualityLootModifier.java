package net.gobies.reforgeable.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.helper.QualityHelper;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QualityLootModifier extends LootModifier {

    public static final Codec<QualityLootModifier> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance).apply(instance, QualityLootModifier::new));
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Reforgeable.MOD_ID);

    public QualityLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIERS.register(eventBus);
        LOOT_MODIFIERS.register("add_quality", () -> CODEC);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!context.getLevel().getServer().isSameThread()) {
            return generatedLoot;
        }
        String path = context.getQueriedLootTableId().getPath();
        boolean isChest = path.startsWith("chests/") && CommonConfig.CHEST_LOOT_TABLES.get();
        boolean isEntity = path.startsWith("entities/") && CommonConfig.ENTITY_LOOT_TABLES.get();

        if (!isChest && !isEntity) {
            return generatedLoot;
        }

        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof Player player) {

            QualityHelper.luckHolder.set(player.getLuck());

            double treasureChance = CommonConfig.TREASURE_CHANCE.get();
            double luckFactor = player.getLuck() * CommonConfig.TREASURE_LUCK_FACTOR.get();
            double finalChance = treasureChance + luckFactor;

            for (ItemStack stack : generatedLoot) {
                if (!stack.isEmpty()) {
                    List<Quality> qualityList = QualityUtil.getQualities(stack);
                    List<Quality> treasureQualities = qualityList.stream().filter(quality -> quality.weight() == 0).toList();

                    if (!treasureQualities.isEmpty() && Math.random() < finalChance) {
                        Quality rolledTreasure = treasureQualities.get((int) (Math.random() * treasureQualities.size()));

                        QualityUtil.setQuality(stack, rolledTreasure.name());
                    } else {
                        QualityUtil.processItemQuality(stack);
                    }
                }
            }

            QualityHelper.luckHolder.remove();

            return generatedLoot;
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}