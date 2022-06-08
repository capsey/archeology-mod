package net.capsey.archeology.entity;

import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import org.jetbrains.annotations.Nullable;

public interface ExcavatorPlayerEntity {

    void startBrushing(ExcavationBlockEntity entity);

    @Nullable ExcavationBlockEntity getExcavatingBlock();

}
