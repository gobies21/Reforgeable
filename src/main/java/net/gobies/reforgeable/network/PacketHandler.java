package net.gobies.reforgeable.network;

import net.gobies.reforgeable.Reforgeable;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Reforgeable.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private static int packetId = 0;
    private static int nextId() {
        return packetId++;
    }

    public static void registerMessages() {
        INSTANCE.messageBuilder(ReforgeMessage.class, nextId(), NetworkDirection.PLAY_TO_SERVER).encoder(ReforgeMessage::encode).decoder(ReforgeMessage::decode).consumerNetworkThread(ReforgeMessage::handle).add();
    }
}
