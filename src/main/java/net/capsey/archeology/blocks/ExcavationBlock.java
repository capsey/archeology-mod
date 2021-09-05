package net.capsey.archeology.blocks;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ExcavationBlock extends Block implements BlockEntityProvider {

    public static final int MAX_BRUSHING_LEVELS = 7;
    public static final IntProperty BRUSHING_LEVEL = IntProperty.of("brushing_level", 0, MAX_BRUSHING_LEVELS);
    
    private static final int[] CHECK_TICKS = { 20, 18, 16, 14 };

    public static int getCheckTicks(ItemStack stack) {
        if (stack.getItem() != ArcheologyMod.COPPER_BRUSH) {
            return -1;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        return CHECK_TICKS[index];
    }

    public ExcavationBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BRUSHING_LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BRUSHING_LEVEL);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExcavationBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        float num = 8 - state.get(BRUSHING_LEVEL);
        return VoxelShapes.cuboid(0.0F, 0.0F, 0.0F, 1.0F, (num / 8), 1.0F);
    }

    @Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

    public void startBrushing(World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof ExcavationBlock) {
            if (state.get(BRUSHING_LEVEL) != 0) {
                stoppedBrushing(world, pos);
                return;
            }

            ((ExcavationBlockEntity) world.getBlockEntity(pos)).generateLoot(player, stack);
        }
    }

    public void brushingTick(World world, BlockPos pos, float progress, int remainingUseTicks, ItemStack stack) {
        if (remainingUseTicks % getCheckTicks(stack) == 0) {
            BlockState state = world.getBlockState(pos);
    
            if (state.getBlock() instanceof ExcavationBlock) {
                int num = (int) Math.floor(progress * MAX_BRUSHING_LEVELS) + 1;

                if (num < 8) {
                    world.setBlockState(pos, state.with(BRUSHING_LEVEL, num));
                    world.playSound(null, pos, soundGroup.getBreakSound(), SoundCategory.BLOCKS, soundGroup.getVolume(), soundGroup.getPitch());
                    world.addBlockBreakParticles(pos, state);
                }
            }
        }
    }

    public void finishedBrushing(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() instanceof ExcavationBlock) {
            ((ExcavationBlockEntity) world.getBlockEntity(pos)).spawnLootItems();
            world.breakBlock(pos, true);
        }
    }

    public void stoppedBrushing(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() instanceof ExcavationBlock) {
            world.breakBlock(pos, false);
        }
    }
    
}
