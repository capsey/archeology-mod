package net.capsey.archeology.client;

import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public interface ExcavationManagerContainer {

    public void addExcavationManager(ExcavationBlockEntity entity, ClientWorld world);
    
}
