package net.capsey.archeology.blocks.clay_pot;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RawClayPotBlockEntity extends BlockEntity implements ShardsContainer {

    private final DefaultedList<ItemStack> ceramicShards = DefaultedList.ofSize(8, ItemStack.EMPTY);

    public short hardeningProgress = 0;

    public static void tick(World world, BlockPos pos, BlockState state, RawClayPotBlockEntity be) {
        BlockState block = world.getBlockState(pos.down());

		if (block.isIn(BlockTags.FIRE) || (block.isIn(BlockTags.CAMPFIRES) && block.get(CampfireBlock.LIT))) {
			be.hardeningProgress += world.getRandom().nextInt(2);
            if (be.hardeningProgress > 64) {
                world.setBlockState(pos, ArcheologyMod.CLAY_POT.getDefaultState());
            }
		} else {
            be.hardeningProgress = 0;
        }
    }

    public RawClayPotBlockEntity(BlockPos pos, BlockState state) {
        super(ArcheologyMod.RAW_CLAY_POT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        readShardsNbt(tag, ceramicShards);
        hardeningProgress = tag.getShort("HardeningProgress");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        writeShardsNbt(tag, ceramicShards);
        tag.putShort("HardeningProgress", hardeningProgress);
 
        return tag;
    }

    @Override
    public ItemStack[] getShards() {
        return (ItemStack[]) ceramicShards.toArray();
    }

}
