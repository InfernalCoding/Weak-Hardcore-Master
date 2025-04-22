package dev.infernal_coding.weak_hardcore.network;

import dev.infernal_coding.weak_hardcore.WeakHardcore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = WeakHardcore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Network {
    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(GetLorePacket.TYPE, GetLorePacket.STREAM_CODEC, GetLorePacket.ClientPayloadHandler::handleDataOnNetwork);
            registrar.playToServer(AddHeadPacket.TYPE, AddHeadPacket.STREAM_CODEC, AddHeadPacket.ServerPayloadHandler::handleDataOnNetwork);
    }
}
