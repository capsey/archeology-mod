package net.capsey.archeology.blocks.excavation_block;

import org.jetbrains.annotations.Nullable;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.items.CopperBrushItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ExcavationBlock extends BlockWithEntity {

    public static final int MAX_BRUSHING_LEVELS = 5;
    public static final IntProperty BRUSHING_LEVEL = IntProperty.of("brushing_level", 0, MAX_BRUSHING_LEVELS);
    
    private static final int[] BRUSH_TICKS = { 48, 42, 36, 30 };

    public static int getBrushTicks(ItemStack stack) {
        if (!(stack.getItem() instanceof CopperBrushItem)) {
            return 0;
        }

        // Only for Debug purposes
        if (stack.getNbt().contains("Debug") && stack.getNbt().getBoolean("Debug")) {
            return 6;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        if (index >= BRUSH_TICKS.length || index < 0) { index = 0; }

        return BRUSH_TICKS[index];
    }

    public ExcavationBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BRUSHING_LEVEL, 0));
    }

    @Override @Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : checkType(type, ArcheologyMod.BlockEntities.EXCAVATION_BLOCK_ENTITY, ExcavationBlockEntity::serverTick);
	}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BRUSHING_LEVEL);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExcavationBlockEntity(pos, state);
    }

    public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
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
    
}
