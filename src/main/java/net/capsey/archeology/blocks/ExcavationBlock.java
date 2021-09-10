package net.capsey.archeology.blocks;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ExcavationBlock extends Block implements BlockEntityProvider {

    public static final int MAX_BRUSHING_LEVELS = 5;
    public static final IntProperty BRUSHING_LEVEL = IntProperty.of("brushing_level", 0, MAX_BRUSHING_LEVELS);
    
    private static final int[] BRUSH_TICKS = { 40, 36, 32, 28 };

    public static int getBrushTicks(ItemStack stack) {
        if (stack.getItem() != ArcheologyMod.COPPER_BRUSH) {
            return -1;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        return BRUSH_TICKS[index];
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

    public void visualsTick(World world, BlockPos pos, int remainingTicks, ItemStack stack) {
        if (remainingTicks % (getBrushTicks(stack) / 4) == 0) {
            BlockSoundGroup soundGroup = world.getBlockState(pos).getSoundGroup();
            world.playSound(null, pos, soundGroup.getBreakSound(), SoundCategory.BLOCKS, soundGroup.getVolume(), soundGroup.getPitch());
            world.addBlockBreakParticles(pos, world.getBlockState(pos));
        }
    }
    
}
