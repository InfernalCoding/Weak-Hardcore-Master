package net.infernal_coding;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.function.Predicate;

import static net.infernal_coding.Config.RESURRECTION_STARTERS;

public class MixinCalls {

    public static void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        Level world = entity.level();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (!level.isClientSide) {

            if (entity instanceof ItemEntity itemEntity && isItemContained(itemEntity)) {
                BlockPos lodestonePos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
                if (world.getBlockState(lodestonePos).getBlock() == Blocks.LODESTONE) {
                    AABB bounds = new AABB(lodestonePos.getX() - 1, lodestonePos.getY() - 1, lodestonePos.getZ() - 1,
                            lodestonePos.getX() + 1, lodestonePos.getY() - 1, lodestonePos.getZ() + 1);
                    List<BlockState> gold = getBlocks(world, bounds, block -> block.is(Blocks.GOLD_BLOCK));
                    if (gold.size() == 9) {
                        AABB headBounds = new AABB(lodestonePos.getX() - 1, lodestonePos.getY(), lodestonePos.getZ() - 1, lodestonePos.getX() + 1, lodestonePos.getY(), lodestonePos.getZ() + 1);
                        Map<BlockPos, BlockState> headMap = getBlockMap(world, headBounds, block -> block.is(Blocks.PLAYER_HEAD) || block.is(Blocks.PLAYER_WALL_HEAD));

                        headMap.forEach((pos1, head) -> {
                            SkullBlockEntity skull = (SkullBlockEntity) world.getBlockEntity(pos1);

                            if (skull != null && skull.getOwnerProfile() != null) {
                                UUID playerID = skull.getOwnerProfile().getId();
                                Optional<ServerPlayer> player1 = Optional.ofNullable(server.getPlayerList().getPlayer(playerID));
                                player1.ifPresent(player -> {
                                    if (player.isSpectator()) {
                                        world.destroyBlock(pos1, false);
                                        player.setGameMode(GameType.SURVIVAL);
                                        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Config.INITIAL_PLAYER_HEALTH.get());

                                        ServerLevel playerDimension = server.getLevel(player.level().dimension());

                                        if (server.getLevel(level.dimension()) != playerDimension) {
                                            player.changeDimension(server.getLevel(level.dimension()));
                                        }
                                        player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
                                        player.setHealth(Config.INITIAL_PLAYER_HEALTH.get().floatValue() / 2);
                                        world.broadcastEntityEvent(player, (byte) 35);
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
        return RESURRECTION_STARTERS.get().stream().map(ResourceLocation::new).map(ForgeRegistries.ITEMS::getValue).anyMatch(item1 -> item1 == item);
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
