package net.infernal_coding.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import static net.infernal_coding.Config.HEALTH_DECREASE;
import static net.infernal_coding.EventHandler.getColoredComponent;

public class PlayerHealthChangePacket {

    UUID playerID;

    float health;

    boolean isAdd;

    public PlayerHealthChangePacket(UUID playerID, float health, boolean isAdd) {
        this.playerID = playerID;
        this.health = health;
        this.isAdd = isAdd;
    }

    public static PlayerHealthChangePacket decode(FriendlyByteBuf buf) {
        return new PlayerHealthChangePacket(buf.readUUID(), buf.readFloat(), buf.readBoolean());
    }

    public static void encode(PlayerHealthChangePacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.playerID);
        buf.writeFloat(packet.health);
        buf.writeBoolean(packet.isAdd);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT;
            Player player = Minecraft.getInstance().player;
            Level level = player.level();

            if (isAdd) {
                //player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
                int num = new Random().nextInt(1, 16);
                String key = I18n.get("weakHardcore.gainHeart" + num);
                Component message = getColoredComponent(key);
                player.displayClientMessage(message, false);
                level.playLocalSound(player.getOnPos(), SoundEvents.CHORUS_FLOWER_GROW, SoundSource.MASTER,1, 1.0F, false);
            } else {
                //player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);

                if (health <= 0.0) {
                    Component message = getColoredComponent(I18n.get("weakHardcore.finalDeathReminder"));
                    player.displayClientMessage(message, false);
                } else {
                    int num = new Random().nextInt(1, 16);
                    String key = I18n.get("weakHardcore.respawnText" + num);
                    Component message = getColoredComponent(key);
                    player.displayClientMessage(message, false);
                    level.playLocalSound(player.getOnPos(), SoundEvents.SOUL_ESCAPE, SoundSource.MASTER, 1, 1.0F, false);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
