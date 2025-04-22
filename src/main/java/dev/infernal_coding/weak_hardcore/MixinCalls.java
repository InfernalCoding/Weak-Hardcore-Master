package dev.infernal_coding.weak_hardcore;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.function.Predicate;

import static dev.infernal_coding.weak_hardcore.Config.RESURRECTION_STARTERS;

public class MixinCalls {

    public static void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (!level.isClientSide) {

            if (entity instanceof ItemEntity itemEntity && isItemContained(itemEntity)) {
                BlockPos lodestonePos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
                if (level.getBlockState(lodestonePos).getBlock() == Blocks.LODESTONE) {
                    AABB bounds = new AABB(lodestonePos.getX() - 1, lodestonePos.getY() - 1, lodestonePos.getZ() - 1,
                            lodestonePos.getX() + 1, lodestonePos.getY() - 1, lodestonePos.getZ() + 1);
                    List<BlockState> gold = getBlocks(level, bounds, block -> block.is(Blocks.GOLD_BLOCK));
                    if (gold.size() == 9) {
                        AABB headBounds = new AABB(lodestonePos.getX() - 1, lodestonePos.getY(), lodestonePos.getZ() - 1, lodestonePos.getX() + 1, lodestonePos.getY(), lodestonePos.getZ() + 1);
                        Map<BlockPos, BlockState> headMap = getBlockMap(level, headBounds, block -> block.is(Blocks.PLAYER_HEAD) || block.is(Blocks.PLAYER_WALL_HEAD));

                        headMap.forEach((pos1, head) -> {
                            SkullBlockEntity skull = (SkullBlockEntity) level.getBlockEntity(pos1);

                            if (skull != null && skull.getOwnerProfile() != null) {
                                UUID playerID = skull.getOwnerProfile().gameProfile().getId();
                                Optional<ServerPlayer> player1 = Optional.ofNullable(server.getPlayerList().getPlayer(playerID));
                                player1.ifPresent(player -> {
                                    if (player.isSpectator()) {
                                        level.destroyBlock(pos1, false);
                                        player.setGameMode(GameType.SURVIVAL);
                                        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Config.INITIAL_PLAYER_HEALTH.get());

                                        ServerLevel playerDimension = server.getLevel(player.level().dimension());

                                        if (server.getLevel(level.dimension()) != playerDimension) {
                                            player.changeDimension(new DimensionTransition(Objects.requireNonNull(server.getLevel(level.dimension())), player, DimensionTransition.DO_NOTHING));
                                        }
                                        player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
                                        player.setHealth(Config.INITIAL_PLAYER_HEALTH.get().floatValue() / 2);
                                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100));
                                        level.broadcastEntityEvent(player, (byte) 35);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        }
    }

    public static boolean isItemContained(ItemEntity entity) {
        Item item = entity.getItem().getItem();
        return RESURRECTION_STARTERS.get().stream().map(ResourceLocation::parse).map(BuiltInRegistries.ITEM::get).anyMatch(item1 -> item1 == item);
    }


    private static List<BlockState> getBlocks(Level world, AABB aabb, Predicate<BlockState> predicate) {
        return world.getBlockStates(aabb).filter(predicate).toList();
    }


    private static Map<BlockPos, BlockState> getBlockMap(Level world, AABB aabb, Predicate<BlockState> predicate) {
        Map<BlockPos, BlockState> blockMap = new HashMap<>();
        for (double x = aabb.minX; x <= aabb.maxX; x++) {
            for (double y = aabb.minY; y <= aabb.maxY; y++) {
                for (double z = aabb.minZ; z <= aabb.maxZ; z++) {
                    BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
                    BlockState state = world.getBlockState(pos);
                    if (predicate.test(state)) blockMap.put(pos, state);
                }
            }
        }
        return blockMap;
    }

}