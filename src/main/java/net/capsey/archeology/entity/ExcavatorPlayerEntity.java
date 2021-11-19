package net.capsey.archeology.entity;

import org.jetbrains.annotations.Nullable;

import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;

public interface ExcavatorPlayerEntity {
    
    public void startBrushing(ExcavationBlockEntity entity);

    @Nullable
    public ExcavationBlockEntity getExcavatingBlock();

    public void resetLastBrushedTicks();

    public float getBrushCooldownProgress();

}
