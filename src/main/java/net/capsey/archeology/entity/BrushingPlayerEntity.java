package net.capsey.archeology.entity;

import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface BrushingPlayerEntity {

    void startBrushing(BlockPos pos);

    @Nullable ExcavationBlockEntity getBrushingEntity();

    interface Server {
        void onStopBrushing();
    }

    interface Client {
        boolean tick();
    }

}
