package net.infernal_coding.network;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.function.Supplier;

public class PlayerRespawnPacket {

    BlockPos pos;
    ResourceKey<Level> dimension;
    UUID playerId;

    public PlayerRespawnPacket(BlockPos pos, UUID id, ResourceKey<Level> dimension) {
        this.pos = pos;
        this.playerId = id;
        this.dimension = dimension;
    }

    public static PlayerRespawnPacket decode(FriendlyByteBuf buf) {
       return new PlayerRespawnPacket(buf.readBlockPos(), buf.readUUID(), buf.readResourceKey(Registries.DIMENSION));
    }

    public static void encode(PlayerRespawnPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeUUID(packet.playerId);
        buf.writeResourceKey(packet.dimension);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER;
            ServerPlayer player = server.getPlayerList().getPlayer(playerId);

            ServerLevel sacrificeDimension = server.getLevel(dimension);
            ServerLevel playerDimension = server.getLevel(player.level().dimension());

            if (sacrificeDimension != playerDimension) {
                player.changeDimension(sacrificeDimension);
            }
            player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
            player.setHealth(10);
        });
        ctx.get().setPacketHandled(true);
    }
}
