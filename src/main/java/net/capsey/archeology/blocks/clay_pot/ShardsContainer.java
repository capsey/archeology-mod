package net.capsey.archeology.blocks.clay_pot;

import java.security.InvalidParameterException;
import java.util.EnumMap;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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
		NorthWest,
		North,
		NorthEast,
		East,
		SouthEast,
		South,
		SouthWest,
		West;

		public static boolean validHit(BlockHitResult hit) {
			// TODO: Add more cases
			return hit.getSide() != Direction.UP && hit.getSide() != Direction.DOWN;
		}

		public static Side fromHit(BlockHitResult hit) {
			// TODO: Add more returns
			switch(hit.getSide()) {
                case NORTH:
                    return North;
                case SOUTH:
                    return South;
                case WEST:
                    return West;
                case EAST:
                    return East;
                default:
					throw new InvalidParameterException();
            }
		}

		private static final Side[] straights = { Side.North, Side.East, Side.South, Side.West };

		public static Side[] straightValues() {
			return straights;
		}

		private static final Side[] corners = { Side.NorthWest, Side.NorthEast, Side.SouthEast, Side.SouthWest };

		public static Side[] cornerValues() {
			return corners;
		}

	}

}
