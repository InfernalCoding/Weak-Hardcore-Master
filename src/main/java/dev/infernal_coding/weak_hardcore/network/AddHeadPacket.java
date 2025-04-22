package dev.infernal_coding.weak_hardcore.network;

import dev.infernal_coding.weak_hardcore.EventHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.joml.Vector3f;

import java.util.UUID;

public record AddHeadPacket(String lore, String playerID, Vector3f playerLastPos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AddHeadPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("weak_hardcore", "add_head"));

    public static final StreamCodec<ByteBuf, AddHeadPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            AddHeadPacket::lore,
            ByteBufCodecs.STRING_UTF8,
            AddHeadPacket::playerID,
            ByteBufCodecs.VECTOR3F,
            AddHeadPacket::playerLastPos,
            AddHeadPacket::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ServerPayloadHandler {
        public static void handleDataOnNetwork(final AddHeadPacket packet, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {

                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                ServerPlayer player = server.getPlayerList().getPlayer(UUID.fromString(packet.playerID));
                Level world = player.level();
                ItemStack head = EventHandler.getPlayerHead(player.getGameProfile());
                EventHandler.addLore(head, packet.lore);
                ItemEntity entity = new ItemEntity(world, packet.playerLastPos.x, packet.playerLastPos.y, packet.playerLastPos.z, head);
                world.addFreshEntity(entity);
                world.playLocalSound(new BlockPos((int) packet.playerLastPos.x, (int) packet.playerLastPos.y, (int) packet.playerLastPos.z), SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.value(), SoundSource.MASTER, 1.0F, 1.0F, false);
                EventHandler.ghostPlayer(player);
            }).exceptionally(e -> {
                // Handle exception
                ctx.disconnect(Component.translatable("weakHardcore.networking.failed", e.getMessage()));
                return null;
            });
        }
    }

}
