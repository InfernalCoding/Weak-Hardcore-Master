package net.infernal_coding.network;

import net.infernal_coding.WeakHardcore;
import net.infernal_coding.network.PlayerRespawnPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;
    public static void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(WeakHardcore.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        INSTANCE.registerMessage(0, PlayerRespawnPacket.class, PlayerRespawnPacket::encode, PlayerRespawnPacket::decode, PlayerRespawnPacket::handle);
    }
}
