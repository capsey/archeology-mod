package net.capsey.archeology;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlacedBlock {

    public PlacedBlock(Block b, BlockPos p) {
        block = b;
        position = p;
    }

    private Block block;
    private BlockPos position;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlacedBlock) {
            PlacedBlock pBlock = (PlacedBlock) obj;
            return (block == pBlock.getBlock()) && (position.equals(pBlock.getPosition()));
        }

        return false;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getPosition() {
        return position;
    }

    public static PlacedBlock getBlockEntityLookingAt(Entity entity, World world) {
        // TODO: Remove hardcoded player reach value
        HitResult result = entity.raycast(4.5, 1, false);

        if (result.getClass() == BlockHitResult.class) {
            BlockHitResult blockHitResult = (BlockHitResult) result;
            
            return new PlacedBlock(world.getBlockState(blockHitResult.getBlockPos()).getBlock(), blockHitResult.getBlockPos());
        }

        return null;
    }

}
