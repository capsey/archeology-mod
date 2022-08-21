package net.capsey.archeology.blocks.clay_pot;

import net.capsey.archeology.main.BlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class RawClayPotBlockEntity extends ShardsContainer {

    public RawClayPotBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.RAW_CLAY_POT_BLOCK_ENTITY, pos, state);
    }

}
