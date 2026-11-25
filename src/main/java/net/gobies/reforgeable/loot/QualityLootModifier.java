package net.gobies.reforgeable.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.helper.QualityHelper;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
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
        ResourceLocation tableId = context.getQueriedLootTableId();
        boolean allowQuality = isValidLootTable(tableId, CommonConfig.QUALITY_LOOT_TABLES.get());
        boolean allowTreasure = isValidLootTable(tableId, CommonConfig.TREASURE_LOOT_TABLES.get());

        if (!allowQuality && !allowTreasure) {
            return generatedLoot;
        }

        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof FishingHook hook) {
            entity = hook.getPlayerOwner();
        } else if (entity instanceof Projectile projectile && projectile.getOwner() != null) {
            entity = projectile.getOwner();
        }
        if (entity == null) {
            entity = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        }
        if (entity == null) {
            Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
            if (origin != null) {
                entity = context.getLevel().getNearestPlayer(origin.x, origin.y, origin.z, 6.0, false);
            }
        }

        if (entity instanceof Player player) {
            QualityHelper.luckHolder.set(player.getLuck());

            try {
                double treasureChance = CommonConfig.TREASURE_CHANCE.get();
                double luckFactor = player.getLuck() * CommonConfig.TREASURE_LUCK_FACTOR.get();
                double finalChance = treasureChance + luckFactor;

                for (ItemStack stack : generatedLoot) {
                    if (stack == null || stack.isEmpty() || QualityUtil.hasQuality(stack)) continue;
                    if (!QualityUtil.isValidQualityItem(stack) || QualityUtil.isBlacklisted(stack)) continue;
                    List<Quality> qualityList = QualityUtil.getQualities(stack);
                    List<Quality> treasureQualities = qualityList.stream().filter(quality -> quality.weight() == 0).toList();

                    if (allowTreasure && !treasureQualities.isEmpty() && Math.random() < finalChance) {
                        Quality rolledTreasure = treasureQualities.get((int) (Math.random() * treasureQualities.size()));

                        QualityUtil.setQuality(stack, rolledTreasure.name());
                    } else if (allowQuality) {
                        QualityUtil.processItemQuality(stack);
                    }
                }

            } finally {
                QualityHelper.luckHolder.remove();
            }
            return generatedLoot;
        }

        if (allowQuality) {
            for (ItemStack stack : generatedLoot) {
                if (stack != null && !stack.isEmpty() && !stack.is(Items.AIR)) {
                    QualityUtil.processItemQuality(stack);
                }
            }
        }
        return generatedLoot;
    }

    private boolean isValidLootTable(ResourceLocation tableId, List<? extends String> allowedPaths) {
        String fullId = tableId.toString();
        String path = tableId.getPath();

        for (String entry : allowedPaths) {
            if (entry.isEmpty()) continue;
            if (entry.contains(":")) {
                if (fullId.startsWith(entry)) return true;
            } else if (path.startsWith(entry)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}