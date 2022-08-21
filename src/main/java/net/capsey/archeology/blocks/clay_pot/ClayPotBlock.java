package net.capsey.archeology.blocks.clay_pot;

import net.capsey.archeology.main.BlockEntities;
import net.capsey.archeology.blocks.FallingBlockWithEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.LandingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ClayPotBlock extends AbstractClayPotBlock implements BlockEntityProvider, FallingBlockWithEntity, LandingBlock {

    public static final BlockSoundGroup SOUND_GROUP = new BlockSoundGroup(1.0F, 1.0F, SoundEvents.BLOCK_GLASS_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL);

    public ClayPotBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Optional<ClayPotBlockEntity> entity = world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY);

        if (entity.isPresent()) {
            ItemStack stack = player.getStackInHand(hand);

            if (entity.get().getColor() == null && stack.getItem() instanceof DyeItem dyeStack) {
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                entity.get().setColor(dyeStack.getColor());

                if (!player.isCreative()) {
                    stack.decrement(1);
                }

                return ActionResult.success(world.isClient);
            } else if (entity.get().getColor() != null && stack.isOf(Items.POTION) && PotionUtil.getPotion(stack) == Potions.WATER) {
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.PLAYERS, 1.0f, 1.0f);
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));

                if (!world.isClient) {
                    ServerWorld serverWorld = (ServerWorld) world;

                    for (int i = 0; i < 5; ++i) {
                        serverWorld.spawnParticles(ParticleTypes.SPLASH, pos.getX() + world.random.nextDouble(), pos.getY() + 1, pos.getZ() + world.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                    }
                }

                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
                entity.get().setColor(null);

                return ActionResult.success(world.isClient);
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ClayPotBlockEntity(pos, state);
    }

    @Override
    public NbtCompound writeFallingBlockNbt(BlockEntity entity) {
        NbtCompound nbt = entity.createNbt();

        if (entity instanceof ClayPotBlockEntity potEntity) {
            potEntity.clear();
        }

        return nbt;
    }

    @Override
    public NbtCompound writeClientData(NbtCompound nbt, BlockEntity entity) {
        if (entity instanceof ClayPotBlockEntity potEntity) {
            potEntity.writeClientData(nbt);
        }

        return nbt;
    }

    @Override
    public ItemEntity dropItem(FallingBlockEntity entity, ItemConvertible item) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

        if (entity.blockEntityData != null) {
            Inventories.readNbt(entity.blockEntityData, items);
            items.forEach(entity::dropStack);
        }

        return null;
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity entity) {
        world.playSound(null, pos, getSoundGroup(entity.getBlockState()).getBreakSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        tryScheduleTick(world, pos, this);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        tryScheduleTick(world, pos, this);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        trySpawnFallingBlock(state, world, pos, true);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY).ifPresent(entity -> {
                entity.onBreak();
                world.updateComparators(pos, this);
            });

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY).ifPresent(blockEntity -> blockEntity.readFrom(stack));
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY).ifPresent(entity -> {
            NbtCompound nbt = entity.writeClientData(new NbtCompound());
            BlockItem.setBlockEntityNbt(stack, BlockEntities.CLAY_POT_BLOCK_ENTITY, nbt);
        });
        return stack;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

}
