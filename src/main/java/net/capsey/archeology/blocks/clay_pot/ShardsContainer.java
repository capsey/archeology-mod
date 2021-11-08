package net.capsey.archeology.blocks.clay_pot;

import java.security.InvalidParameterException;
import java.util.EnumMap;

import net.capsey.archeology.items.ceramic_shard.CeramicShard;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class ShardsContainer extends BlockEntity implements BlockEntityClientSerializable {

	private final EnumMap<Side, CeramicShard> ceramicShards = new EnumMap<>(Side.class);

	protected ShardsContainer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected void replaceShards(EnumMap<Side, CeramicShard> shards) {
		ceramicShards.clear();
		ceramicShards.putAll(shards);
    }

	public EnumMap<Side, CeramicShard> getShards() {
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
		this.markDirty();
		this.sync();
	}

	public void removeShard(Side direction) {
		ceramicShards.remove(direction);
		this.markDirty();
		this.sync();
	}

	public void clearShards() {
		ceramicShards.clear();
		this.markDirty();
		this.sync();
	}

    public boolean hasShards() {
		return !ceramicShards.isEmpty();
	}

	public static NbtList getPatternListTag(ItemStack stack) {
		NbtList nbtList = null;
		NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns", 9)) {
			nbtList = nbtCompound.getList("Patterns", 10).copy();
		}

		return nbtList;
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
		
		if (tag != null && tag.contains("Shards")) {
			NbtList nbtList = tag.getList("Shards", NbtElement.COMPOUND_TYPE);

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
    
            tag.put("Shards", nbtList);
        }

		return tag;
	}

	@Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        readShards(tag);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        return writeShards(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        readShards(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
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
