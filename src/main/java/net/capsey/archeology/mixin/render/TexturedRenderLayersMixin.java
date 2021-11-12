package net.capsey.archeology.mixin.render;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.blocks.clay_pot.client.ClayPotBlockEntityRenderer;
import net.capsey.archeology.items.ceramic_shard.CeramicShardRegistry;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {

    @Inject(method = "addDefaultTextures(Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    private static void addDefaultTextures(Consumer<SpriteIdentifier> adder, CallbackInfo ci) {

        adder.accept(ClayPotBlockEntityRenderer.MODEL_TEXTURES[0]);
        adder.accept(ClayPotBlockEntityRenderer.MODEL_TEXTURES[1]);

        CeramicShardRegistry.getSpriteIds().forEach(adder::accept);
        CeramicShardRegistry.getRawSpriteIds().forEach(adder::accept);
        
	}

}
