package net.infernal_coding.mixins;

import java.util.List;
import java.util.Set;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends LivingEntity {
    @Shadow @Final public ServerPlayerGameMode gameMode;

    @Shadow public abstract void setCamera(@Nullable Entity p_9214_);

    @Shadow public abstract void teleportTo(double p_8969_, double p_8970_, double p_8971_);

    @Shadow public abstract ServerLevel serverLevel();

    @Shadow public ServerGamePacketListenerImpl connection;

    protected ServerPlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(
            method = {"tick"},
            at = {@At("HEAD")}
    )
    public void tick(CallbackInfo ci) {

        if (this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR && this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() < 2.0D) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            List<ServerPlayer> players = server.getPlayerList().getPlayers();
            if (players.size() <= 1) {
                this.teleportTo(this.serverLevel(), 0.0D, 100.0D, 0.0D, Set.of(), this.getYRot(), this.getXRot());
                this.connection.resetPosition();
            } else {
                ServerPlayer closest = players.get(0);

                for (ServerPlayer target: players) {
                    if (this.distanceTo(target) < this.distanceTo(closest)) {
                        closest = target;
                    }
                }

                if (this.serverLevel().dimension() != closest.serverLevel().dimension()) {
                    this.setLevel(closest.serverLevel());
                }
                this.setCamera(closest);
            }
        }

    }
}
