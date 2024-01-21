package net.infernal_coding.mixins;

import net.infernal_coding.MixinCalls;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {


    @Inject(at = @At("HEAD"), method = "entityInside")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo ci) {
        MixinCalls.entityInside(state, world, pos, entity);
    }
}
