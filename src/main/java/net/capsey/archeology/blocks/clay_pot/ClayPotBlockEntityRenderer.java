package net.capsey.archeology.blocks.clay_pot;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;

@Environment(EnvType.CLIENT)
public class ClayPotBlockEntityRenderer extends ShardsContainerRenderer<ClayPotBlockEntity> {

	public ClayPotBlockEntityRenderer(Context ctx) {
		super(ctx, ClayPotBlockEntity.class);
	}
    
}
