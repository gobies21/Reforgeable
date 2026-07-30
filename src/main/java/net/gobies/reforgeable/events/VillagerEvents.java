package net.gobies.reforgeable.events;

import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Mod.EventBusSubscriber
public class VillagerEvents {

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        for (int level = 1; level <= 5; level++) {
            List<VillagerTrades.ItemListing> trades = event.getTrades().get(level);
            if (trades != null) {
                trades.replaceAll(QualityTrades::new);
            }
        }
    }

    private record QualityTrades(VillagerTrades.ItemListing original) implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource random) {
            MerchantOffer offer = this.original.getOffer(trader, random);
            if (offer == null) return null;

            ItemStack result = offer.getResult();
            if (result.isEmpty() || QualityUtil.hasQuality(result) || !QualityUtil.isValidQualityItem(result)) {
                return offer;
            }

            if (Math.random() >= CommonConfig.NO_QUALITY_CHANCE.get()) {
                for (int i = 0; i < 32; i++) {
                    Quality rolled = QualityUtil.getQualityForStack(result);
                    if (rolled == null) break;

                    ChatFormatting color = rolled.color();
                    if (color != ChatFormatting.DARK_RED && color != ChatFormatting.DARK_GRAY) { // Very bad quality filter
                        QualityUtil.setQuality(result, rolled.name());
                        break;
                    }
                }
            } else {
                QualityUtil.setQuality(result, "none");
            }
            return offer;
        }
    }
}