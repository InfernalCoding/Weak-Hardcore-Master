package net.infernal_coding.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import static net.infernal_coding.EventHandler.getColoredComponent;

public class GetLorePacket {

    String key;
    Object[] formats;
    UUID playerID;
    BlockPos playerLastPos;



    public GetLorePacket(String key, UUID playerID, BlockPos playerLastPos, String... formats) {
        this.key = key;
        this.playerID = playerID;
        this.playerLastPos = playerLastPos;
        this.formats = formats;
    }

    public static GetLorePacket decode(FriendlyByteBuf buf) {
        String key = buf.readUtf();
        UUID id = buf.readUUID();
        BlockPos pos = buf.readBlockPos();
        int length = buf.readVarInt();
        String[] formats = new String[length];

        for (int i = 0; i < length; i++) {
            formats[i] = buf.readUtf();
        }
        return new GetLorePacket(key, id, pos, formats);
    }

    public static void encode(GetLorePacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.key);
        buf.writeUUID(packet.playerID);
        buf.writeBlockPos(packet.playerLastPos);
        buf.writeVarInt(packet.formats.length);

        for (int i = 0; i < packet.formats.length; i++) {
            buf.writeUtf((String) packet.formats[i]);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;
            String lore = I18n.get(key, formats);

            Network.INSTANCE.sendToServer(new AddHeadPacket(lore, playerID, playerLastPos));
        });
        ctx.get().setPacketHandled(true);
    }
}
