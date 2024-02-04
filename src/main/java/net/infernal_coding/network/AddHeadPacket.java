package net.infernal_coding.network;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AddHeadPacket {

    String lore;


    public AddHeadPacket(String lore) {
        this.lore = lore;
    }

    public static AddHeadPacket decode(FriendlyByteBuf buf) {
        return new AddHeadPacket(buf.readUtf());
    }

    public static void encode(AddHeadPacket packet, FriendlyByteBuf buf) {
       buf.writeUtf(packet.lore);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER;
            addLore(head, packet.lore);
            ItemEntity entity = new ItemEntity(world, x, y, z, head);
            world.addFreshEntity(entity);
            world.playLocalSound(playerLastPos, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.get(), SoundSource.MASTER,1, 1.0F, false);
            ghostPlayer(player);
        });
        ctx.get().setPacketHandled(true);
    }

}
