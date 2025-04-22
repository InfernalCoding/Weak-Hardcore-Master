package dev.infernal_coding.weak_hardcore.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public record GetLorePacket(String key, String playerID, Vector3f playerLastPos, String format) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GetLorePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("weak_hardcore", "get_lore"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, GetLorePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GetLorePacket::key,
            ByteBufCodecs.STRING_UTF8,
            GetLorePacket::playerID,
            ByteBufCodecs.VECTOR3F,
            GetLorePacket::playerLastPos,
            ByteBufCodecs.STRING_UTF8,
            GetLorePacket::format,
            GetLorePacket::new
    );



    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static class ClientPayloadHandler {
        public static void handleDataOnNetwork(final GetLorePacket packet, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                String lore = I18n.get(packet.key, packet.format, packet.format, packet.format);

                PacketDistributor.sendToServer(new AddHeadPacket(lore, packet.playerID, packet.playerLastPos));
            }).exceptionally(e -> {
                // Handle exception
                ctx.disconnect(Component.translatable("weakHardcore.networking.failed", e.getMessage()));
                return null;
            });
        }
    }
}