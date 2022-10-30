package net.capsey.archeology.blocks.clay_pot;

import net.capsey.archeology.main.BlockEntities;
import net.capsey.archeology.main.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ClayPotBlock extends AbstractClayPotBlock implements BlockEntityProvider {

    public static final BlockSoundGroup SOUND_GROUP = new BlockSoundGroup(1.0F, 1.0F, SoundEvents.BLOCK_GLASS_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL);

    @Nullable private final DyeColor color;

    public ClayPotBlock(@Nullable DyeColor color, Settings settings) {
        super(settings);
        this.color = color;
    }

    public @Nullable DyeColor getColor() {
        return color;
    }

    public static void setColor(@Nullable DyeColor color, World world, BlockPos pos, BlockState state) {
        Block block = color == null ? Blocks.CLAY_POT : Blocks.CLAY_POT_DYED[color.getId()];
        BlockState newState = block.getDefaultState().with(FACING, state.get(FACING));
        NbtCompound data = new NbtCompound();

        world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY).ifPresent(x -> x.writeNbt(data));
        world.removeBlockEntity(pos);
        world.setBlockState(pos, newState);
        world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY).ifPresent(x -> x.readNbt(data));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Optional<ClayPotBlockEntity> entity = world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY);

        if (entity.isPresent()) {
            ItemStack stack = player.getStackInHand(hand);

            if (color == null && stack.getItem() instanceof DyeItem dyeStack) {
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                setColor(dyeStack.getColor(), world, pos, state);

                if (!player.isCreative()) {
                    stack.decrement(1);
                }

                return ActionResult.success(world.isClient);
            } else if (color != null && stack.isOf(Items.POTION) && PotionUtil.getPotion(stack) == Potions.WATER) {
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
                setColor(null, world, pos, state);

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
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY).ifPresent(entity -> {
                ItemScatterer.spawn(world, pos, entity);
                world.updateComparators(pos, this);
            });
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        final ItemPredicate predicate = ItemPredicate.Builder.create().enchantment(
                new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.ANY)
        ).build();

        if (!world.isClient && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY).ifPresent(entity -> {
                if (player.isCreative() || predicate.test(player.getMainHandStack())) {
                    if (!player.isCreative() || entity.hasShards() || !entity.isEmpty()) {
                        ItemStack stack = entity.writeStackNbt(new ItemStack(this));
                        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                        itemEntity.setToDefaultPickupDelay();
                        world.spawnEntity(itemEntity);
                    }
                    entity.clear();
                }
            });
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY).ifPresent(entity -> {
            if (stack.hasCustomName()) entity.setCustomName(stack.getName());
        });
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        world.getBlockEntity(pos, BlockEntities.CLAY_POT_BLOCK_ENTITY).ifPresent(entity -> {
            NbtCompound nbt = entity.writeShards(new NbtCompound());
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
