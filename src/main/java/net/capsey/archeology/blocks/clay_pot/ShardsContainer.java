package net.capsey.archeology.blocks.clay_pot;

import net.capsey.archeology.items.ceramic_shard.CeramicShard;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.security.InvalidParameterException;
import java.util.*;

public abstract class ShardsContainer extends BlockEntity {

    private static final String SHARDS_TAG = "Shards";

    private final EnumMap<Side, CeramicShard> ceramicShards = new EnumMap<>(Side.class);

    protected ShardsContainer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void replaceShards(Map<Side, CeramicShard> shards) {
        ceramicShards.clear();
        ceramicShards.putAll(shards);
    }

    public Map<Side, CeramicShard> getShards() {
        return ceramicShards.clone();
    }

    public CeramicShard getShard(Side direction) {
        return ceramicShards.get(direction);
    }

    public boolean hasShard(Side direction) {
        return ceramicShards.containsKey(direction);
    }

    public void setShard(Side direction, CeramicShard shard) {
        ceramicShards.put(direction, shard);
        markForUpdate();
    }

    public void removeShard(Side direction) {
        ceramicShards.remove(direction);
        markForUpdate();
    }

    public void clearShards() {
        ceramicShards.clear();
        markForUpdate();
    }

    protected void markForUpdate() {
        markDirty();
        if (world != null && !world.isClient) {
            ((ServerWorld) world).getChunkManager().markForUpdate(pos);
        }
    }

    public void rotateShards(BlockRotation rotation) {
        if (rotation == BlockRotation.NONE) {
            return;
        }

        EnumMap<Side, CeramicShard> newShards = new EnumMap<>(Side.class);
        ceramicShards.forEach((key, value) -> newShards.put(key.rotate(rotation), value));
        replaceShards(newShards);
    }

    public boolean hasShards() {
        return !ceramicShards.isEmpty();
    }

    public void readFrom(ItemStack stack) {
        ceramicShards.clear();

        NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag");
        if (nbtCompound != null) {
            readShards(nbtCompound);
        }
    }

    public void readShards(NbtCompound tag) {
        ceramicShards.clear();

        if (tag != null && tag.contains(SHARDS_TAG)) {
            NbtList nbtList = tag.getList(SHARDS_TAG, NbtElement.COMPOUND_TYPE);

            if (nbtList.size() <= Side.values().length) {
                for (int i = 0; i < nbtList.size(); i++) {
                    NbtCompound nbtCompound = nbtList.getCompound(i);
                    CeramicShard shard = CeramicShard.fromNbt(nbtCompound);

                    if (shard != null) {
                        ceramicShards.put(Side.values()[i], shard);
                    }
                }
            }
        }
    }

    public void writeShards(NbtCompound tag) {
        if (hasShards()) {
            NbtList nbtList = new NbtList();

            for (Side side : Side.values()) {
                NbtCompound nbtCompound = new NbtCompound();
                CeramicShard shard = ceramicShards.get(side);

                if (shard != null) {
                    shard.writeNbt(nbtCompound);
                }

                nbtList.add(nbtCompound);
            }

            tag.put(SHARDS_TAG, nbtList);
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        readShards(tag);
    }

    @Override
    protected void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        writeShards(tag);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public enum Side {
        NORTH(true, 0),
        NORTH_WEST(false, 1),
        WEST(true, 2),
        SOUTH_WEST(false, 3),
        SOUTH(true, 4),
        SOUTH_EAST(false, 5),
        EAST(true, 6),
        NORTH_EAST(false, 7);

        public final boolean straight;
        public final int id;

        Side(boolean straight, int id) {
            this.straight = straight;
            this.id = id;
        }

        public static Side fromId(int id) {
            return switch (id % 8) {
                case 0 -> NORTH;
                case 1 -> NORTH_WEST;
                case 2 -> WEST;
                case 3 -> SOUTH_WEST;
                case 4 -> SOUTH;
                case 5 -> SOUTH_EAST;
                case 6 -> EAST;
                case 7 -> NORTH_EAST;
                default -> throw new IllegalStateException();
            };
        }

        public static boolean isValidHit(BlockHitResult hit) {
            Vec3d blockPos = Vec3d.ofBottomCenter(hit.getBlockPos());
            Vec3d relativePos = blockPos.relativize(hit.getPos());

            boolean correctMin = relativePos.getY() > 0;
            boolean correctMax = relativePos.getY() < AbstractClayPotBlock.BASE_SHAPE.getBoundingBox().getYLength();

            return correctMin && correctMax;
        }

        public static Side fromHit(BlockHitResult hit) {
            if (!isValidHit(hit)) {
                throw new InvalidParameterException();
            }

            Vec3d blockPos = Vec3d.ofBottomCenter(hit.getBlockPos());
            Vec3d relativePos = blockPos.relativize(hit.getPos());
            double angle = MathHelper.atan2(-relativePos.getX(), -relativePos.getZ());

            return fromId((int) Math.round(8 * angle / MathHelper.TAU) + 8);
        }

        public Side rotate(BlockRotation rotation) {
            return switch (rotation) {
                case NONE -> this;
                case CLOCKWISE_90 -> fromId(id + EAST.id);
                case CLOCKWISE_180 -> fromId(id + SOUTH.id);
                case COUNTERCLOCKWISE_90 -> fromId(id + WEST.id);
            };
        }
    }

}
