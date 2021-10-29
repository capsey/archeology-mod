package net.capsey.archeology.blocks.excavation_block;

import java.util.Optional;
import java.util.Random;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.hit.BlockHitResult;
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
        if (!stack.isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
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

    public boolean startBrushing(World world, BlockPos pos, ServerPlayerEntity player, ItemStack stack) {
        BlockState state = world.getBlockState(pos);

        if (state.isOf(this) && state.get(BRUSHING_LEVEL) == 0) {
            Optional<ExcavationBlockEntity> entity = world.getBlockEntity(pos, ArcheologyMod.BlockEntities.EXCAVATION_BLOCK_ENTITY);
    
            if (entity.isPresent() && !world.getBlockTickScheduler().isScheduled(pos, this)) {
                entity.get().startBrushing(player, stack);

                world.setBlockState(pos, state.with(BRUSHING_LEVEL, 1));
                world.getBlockTickScheduler().schedule(pos, this, 1); // getBrushTicks(stack)

                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(BRUSHING_LEVEL);

        if (i != 0) {
            Optional<ExcavationBlockEntity> entity = world.getBlockEntity(pos, ArcheologyMod.BlockEntities.EXCAVATION_BLOCK_ENTITY);
    
            if (entity.isPresent() && entity.get().brushingCheck()) {
                Optional<BlockHitResult> raycast = entity.get().getRaycast();
    
                if (raycast.isPresent() && pos.equals(raycast.get().getBlockPos())) {
        
                    if (i < MAX_BRUSHING_LEVELS) {
                        if (entity.get().isTime()) {
                            world.setBlockState(pos, state.with(ExcavationBlock.BRUSHING_LEVEL, i + 1), NOTIFY_LISTENERS);
                            entity.get().brushingTick();
                        }

                        entity.get().breakingTick(raycast.get());
                        world.getBlockTickScheduler().schedule(pos, this, 1); // getBrushTicks(entity.get().getStack())
                        return;
                    } else {
                        entity.get().dropLoot();
                    }
                }
            }
    
            world.breakBlock(pos, true);
        }
	}

    @Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
            if (!world.isClient) {
                Optional<ExcavationBlockEntity> entity = world.getBlockEntity(pos, ArcheologyMod.BlockEntities.EXCAVATION_BLOCK_ENTITY);

                if (entity.isPresent()) {
                    entity.get().onBlockBreak();
                }
            }

            super.onStateReplaced(state, world, pos, newState, moved);
		}
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
