package net.gobies.reforgeable.network;

import net.gobies.reforgeable.client.ReforgingMenu;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.gobies.reforgeable.util.ReforgeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ReforgeMessage {

    public ReforgeMessage() {}

    public void compile() {}

    public static void encode(ReforgeMessage msg, FriendlyByteBuf buffer) {}

    public static ReforgeMessage decode(FriendlyByteBuf buffer) {
        return new ReforgeMessage();
    }

    public static void handle(ReforgeMessage msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context context = ctxSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof ReforgingMenu menu) {
                long time = player.level().getGameTime();
                if (!menu.setCooldown(time)) {
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

                        Quality rolledQuality = QualityUtil.getQualityForStack(gearStack);
                        QualityUtil.setQuality(gearStack, rolledQuality.name());

                        gearSlot.setChanged();
                        if (CommonConfig.ENABLE_ANTI_SKIP.get() && rolledQuality.weight() <= CommonConfig.MAX_WEIGHT.get()) {
                            menu.setCooldown(time + 10);
                        }
                        player.level().playSound(null, player.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8F, 1.0F);
                        menu.broadcastChanges();
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}