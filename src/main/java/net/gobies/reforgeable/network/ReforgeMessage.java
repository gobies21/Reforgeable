package net.gobies.reforgeable.network;

import net.gobies.reforgeable.client.ReforgingMenu;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.gobies.reforgeable.util.ReforgeUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
public record ReforgeMessage() implements CustomPacketPayload {

    public static final Type<ReforgeMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("reforgeable", "reforge_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReforgeMessage> CODEC = StreamCodec.of((buf, msg) -> {}, buf -> new ReforgeMessage());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(final ReforgeMessage msg, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player.containerMenu instanceof ReforgingMenu menu) {
                if (menu.antiSkipCooldown > 0 || !menu.setCooldown(player.level().getGameTime())) {
                    return;
                }

                Slot gearSlot = menu.getSlot(0);
                Slot materialSlot = menu.getSlot(1);

                if (gearSlot.hasItem() && materialSlot.hasItem()) {
                    ItemStack gearStack = gearSlot.getItem();
                    ItemStack materialStack = materialSlot.getItem();

                    if (ReforgeUtil.getReforgeMaterial(gearStack, materialStack)) {
                        materialStack.shrink(1);
                        materialSlot.setChanged();

                        menu.contextPlayer = player;
                        Quality rolledQuality = QualityUtil.getQualityForStack(gearStack);
                        menu.contextPlayer = null;
                        QualityUtil.setQuality(gearStack, rolledQuality);

                        gearSlot.setChanged();
                        if (CommonConfig.ENABLE_ANTI_SKIP.get() && rolledQuality.weight() <= CommonConfig.MAX_WEIGHT.get()) {
                            menu.antiSkipCooldown = CommonConfig.ANTI_SKIP_DURATION.get();
                        }
                        player.level().playSound(null, player.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8F, 1.0F);
                        menu.broadcastChanges();
                    }
                }
            }
        });
    }
}