package net.capsey.archeology.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(FlowerBlock.class)
public abstract class FlowerBlockMixin extends PlantBlock {

    protected FlowerBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos) || floor.isOf(ArcheologyMod.CLAY_POT);
    }

}
