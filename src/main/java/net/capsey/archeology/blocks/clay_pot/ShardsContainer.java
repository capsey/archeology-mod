package net.capsey.archeology.blocks.clay_pot;

import java.security.InvalidParameterException;
import java.util.EnumMap;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class ShardsContainer extends BlockEntity {

	public EnumMap<Side, ItemStack> ceramic_shards = new EnumMap<Side, ItemStack>(Side.class);

	public ShardsContainer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public ItemStack getShard(Side direction) {
		return ceramic_shards.get(direction);
	}

	public boolean addShard(Side direction, ItemStack shard) {
		return ceramic_shards.putIfAbsent(direction, shard) == null;
	}

    public boolean isEmpty() {
		return ceramic_shards.isEmpty();
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

		private boolean straight;

		private Side(boolean straight) {
			this.straight = straight;
		}

		public boolean isStraight() {
			return this.straight;
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
				case 1: return NorthWest;
				case 2: return North;
				case 3: return SouthWest;
				case 4: return West;
				case 5: return SouthEast;
				case 6: return South;
				case 7: return NorthEast;

				default: return North;
			}
		}
	}

}
