package net.capsey.archeology.blocks.clay_pot;

import java.security.InvalidParameterException;
import java.util.EnumMap;
import java.util.Map;

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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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
		if (!this.world.isClient && this.world instanceof ServerWorld server) {
			this.markDirty();
			server.getChunkManager().markForUpdate(this.pos);
		}
	}

	public void removeShard(Side direction) {
		ceramicShards.remove(direction);
		if (!this.world.isClient && this.world instanceof ServerWorld server) {
			this.markDirty();
			server.getChunkManager().markForUpdate(this.pos);
		}
	}

	public void clearShards() {
		ceramicShards.clear();
		if (!this.world.isClient && this.world instanceof ServerWorld server) {
			this.markDirty();
			server.getChunkManager().markForUpdate(this.pos);
		}
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

	public NbtCompound writeShards(NbtCompound tag) {
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

		return tag;
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
        NbtCompound tag = this.createNbt();
        return writeShards(tag);
    }

	public enum Side {
		NORTH_WEST(false),
		NORTH(true),
		NORTH_EAST(false),
		EAST(true),
		SOUTH_EAST(false),
		SOUTH(true),
		SOUTH_WEST(false),
		WEST(true);

		public final boolean straight;

		private Side(boolean straight) {
			this.straight = straight;
		}

		public static boolean validHit(BlockHitResult hit) {
			Vec3d blockPos = Vec3d.ofBottomCenter(hit.getBlockPos());
			Vec3d relativePos = blockPos.relativize(hit.getPos());

			boolean correctMin = relativePos.getY() > 0;
			boolean correctMax = relativePos.getY() < AbstractClayPotBlock.BASE_SHAPE.getBoundingBox().getYLength();

			return correctMin && correctMax;
		}

		public static Side fromHit(BlockHitResult hit) {
			if (!validHit(hit)) {
				throw new InvalidParameterException();
			}
			
			Vec3d blockPos = Vec3d.ofBottomCenter(hit.getBlockPos());
			Vec3d relativePos = blockPos.relativize(hit.getPos());
			
			int compass = ((int) Math.round(MathHelper.atan2(-relativePos.getZ(), relativePos.getX()) / (2 * MathHelper.PI / 8)) + 8) % 8;

			switch (compass) {
				case 0: return EAST;
				case 1: return NORTH_EAST;
				case 2: return NORTH;
				case 3: return NORTH_WEST;
				case 4: return WEST;
				case 5: return SOUTH_WEST;
				case 6: return SOUTH;
				case 7: return SOUTH_EAST;

				default: throw new IllegalStateException("WTF... How?");
			}
		}
	}

}
