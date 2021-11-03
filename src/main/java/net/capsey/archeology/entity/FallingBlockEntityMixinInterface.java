package net.capsey.archeology.entity;

import net.minecraft.nbt.NbtCompound;

public interface FallingBlockEntityMixinInterface {
    
    public void setClientBlockEntityData(NbtCompound nbt);
    
	public NbtCompound getClientBlockEntityData();

}
