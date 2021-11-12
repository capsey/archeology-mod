package net.capsey.archeology.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;

@Mixin(BuiltinModelItemRenderer.class)
public interface BuiltinModelItemRendererAccessor {

    @Accessor("blockEntityRenderDispatcher")
    BlockEntityRenderDispatcher getBlockEntityRenderDispatcher();
    
}
