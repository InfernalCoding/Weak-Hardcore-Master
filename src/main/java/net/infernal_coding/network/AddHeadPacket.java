package net.infernal_coding.network;

import net.infernal_coding.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.function.Supplier;

public class AddHeadPacket {
    String lore;
    UUID playerID;
    BlockPos playerLastPos;

    public AddHeadPacket(String lore, UUID playerID, BlockPos playerLastPos) {
        this.lore = lore;
        this.playerID = playerID;
        this.playerLastPos = playerLastPos;
    }

    public static void encode(AddHeadPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.lore);
        buf.writeUUID(packet.playerID);
        buf.writeBlockPos(packet.playerLastPos);
    }

    public static AddHeadPacket decode(FriendlyByteBuf buf) {
        String lore = buf.readUtf();
        UUID id = buf.readUUID();
        BlockPos pos = buf.readBlockPos();
        return new AddHeadPacket(lore, id, pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER;

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            ServerPlayer player = server.getPlayerList().getPlayer(playerID);
            Level world = player.level();
            ItemStack head = EventHandler.getPlayerHead(player.getGameProfile());
            EventHandler.addLore(head, this.lore);
            ItemEntity entity = new ItemEntity(world, this.playerLastPos.getX(), this.playerLastPos.getY(), this.playerLastPos.getZ(), head);
            world.addFreshEntity(entity);
            world.playLocalSound(this.playerLastPos, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.get(), SoundSource.MASTER, 1.0F, 1.0F, false);
            EventHandler.ghostPlayer(player);
        });
        ctx.get().setPacketHandled(true);
    }
}
