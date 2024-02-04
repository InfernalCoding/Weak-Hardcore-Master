package net.infernal_coding.network;

import net.infernal_coding.WeakHardcore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public class Network {

    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;
    public static void init() {

        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(WeakHardcore.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        INSTANCE.registerMessage(0, PlayerResurrectPacket.class, PlayerResurrectPacket::encode, PlayerResurrectPacket::decode, PlayerResurrectPacket::handle);
        INSTANCE.registerMessage(1, PlayerHealthChangePacket.class, PlayerHealthChangePacket::encode, PlayerHealthChangePacket::decode, PlayerHealthChangePacket::handle);
        INSTANCE.registerMessage(2, GetLorePacket.class, GetLorePacket::encode, GetLorePacket::decode, GetLorePacket::handle);

    }

    public static <MSG> void sendToPlayerClient(Player player, MSG msg) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerPlayer p = server.getPlayerList().getPlayer(player.getUUID());
        INSTANCE.sendTo(msg, p.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
