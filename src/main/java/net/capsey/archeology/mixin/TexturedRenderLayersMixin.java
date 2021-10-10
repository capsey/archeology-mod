package net.capsey.archeology.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntityRenderer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {

    @Inject(method = "addDefaultTextures(Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    private static void addDefaultTextures(Consumer<SpriteIdentifier> adder, CallbackInfo ci) {
		adder.accept(new SpriteIdentifier(RawClayPotBlockEntityRenderer.ATLAS_TEXTURE, new Identifier("archeology", "shard/ender_dragon")));
	}

}
