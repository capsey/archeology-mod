package net.capsey.archeology.entity;

import net.minecraft.nbt.NbtCompound;

public interface FallingBlockEntityMixinInterface {

    NbtCompound getClientBlockEntityData();

    void setClientBlockEntityData(NbtCompound nbt);

}
