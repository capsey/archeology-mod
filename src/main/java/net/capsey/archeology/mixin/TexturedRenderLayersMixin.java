package net.capsey.archeology.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.items.CeramicShardRegistry;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {

    @Inject(method = "addDefaultTextures(Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    private static void addDefaultTextures(Consumer<SpriteIdentifier> adder, CallbackInfo ci) {

        CeramicShardRegistry.getSpriteIds().forEach(adder::accept);
        CeramicShardRegistry.getRawSpriteIds().forEach(adder::accept);
        
	}

}
