package net.gobies.reforgeable.network;

import net.gobies.reforgeable.Reforgeable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Reforgeable.MOD_ID)
public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static PayloadRegistrar INSTANCE;
    
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        INSTANCE = event.registrar(PROTOCOL_VERSION);
        INSTANCE.playToServer(ReforgeMessage.TYPE, ReforgeMessage.CODEC, ReforgeMessage::handleOnServer);
    }
}
