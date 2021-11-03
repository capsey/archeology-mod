package net.capsey.archeology.blocks.clay_pot;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

import net.capsey.archeology.items.ceramic_shard.CeramicShard;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class ShardsContainer extends BlockEntity implements BlockEntityClientSerializable {

	public EnumMap<Side, CeramicShard> ceramic_shards = new EnumMap<Side, CeramicShard>(Side.class);

	public ShardsContainer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected void replaceShards(EnumMap<Side, CeramicShard> shards) {
		ceramic_shards.clear();
		ceramic_shards.putAll(shards);
    }

	public EnumMap<Side, CeramicShard> getShards() {
		return ceramic_shards.clone();
	}

	public CeramicShard getShard(Side direction) {
		return ceramic_shards.get(direction);
	}

	public boolean hasShard(Side direction) {
		return ceramic_shards.containsKey(direction);
	}

	public void setShard(Side direction, CeramicShard shard) {
		ceramic_shards.put(direction, shard);
		this.markDirty();
		this.sync();
	}

	public void removeShard(Side direction) {
		ceramic_shards.remove(direction);
		this.markDirty();
		this.sync();
	}

	public void clearShards() {
		ceramic_shards.clear();
		this.markDirty();
		this.sync();
	}

    public boolean hasShards() {
		return !ceramic_shards.isEmpty();
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
		ceramic_shards.clear();
		
		NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag");
		if (nbtCompound != null) {
			readShards(nbtCompound);
		}
	}

	public void readShards(NbtCompound tag) {
		ceramic_shards.clear();
		
		if (tag != null) {
			if (tag.contains("Shards")) {
				NbtList nbtList = tag.getList("Shards", 10);
	
				if (nbtList.size() <= Side.values().length) {
					for (int i = 0; i < nbtList.size(); i++) {
						NbtCompound nbtCompound = nbtList.getCompound(i);
						int j = nbtCompound.getByte("Side");
	
						if (j < Side.values().length) {
							ceramic_shards.put(Side.values()[j], CeramicShard.fromNbt(nbtCompound));
						}
					}
				}
			}
		}
	}

	public NbtCompound writeShards(NbtCompound tag) {
		if (hasShards()) {
			List<Side> values = Arrays.asList(Side.values());
            NbtList nbtList = new NbtList();

            for (Entry<Side, CeramicShard> entry : ceramic_shards.entrySet()) {
				if (entry.getValue() == null) {
					continue;
				}

                NbtCompound nbtCompound = new NbtCompound();

				nbtCompound.putByte("Side", (byte) values.indexOf(entry.getKey()));
                entry.getValue().writeNbt(nbtCompound);

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
		NorthWest(false),
		North(true),
		NorthEast(false),
		East(true),
		SouthEast(false),
		South(true),
		SouthWest(false),
		West(true);

		public final boolean straight;

		private Side(boolean straight) {
			this.straight = straight;
		}

		public static boolean validHit(BlockHitResult hit) {
			Vec3d blockPos = Vec3d.ofBottomCenter(hit.getBlockPos());
			Vec3d relativePos = blockPos.relativize(hit.getPos());

			boolean correctMin = relativePos.getY() > 0;
			boolean correctMax = relativePos.getY() < ClayPotBlock.BASE_SHAPE.getBoundingBox().getYLength();

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
				case 0: return East;
				case 1: return NorthEast;
				case 2: return North;
				case 3: return NorthWest;
				case 4: return West;
				case 5: return SouthWest;
				case 6: return South;
				case 7: return SouthEast;

				default: throw new IllegalStateException("WTF... How?");
			}
		}
	}

}
