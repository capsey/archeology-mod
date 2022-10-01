package net.capsey.archeology.mixin.client;

import net.capsey.archeology.blocks.clay_pot.client.ClayPotBlockEntityRenderer;
import net.capsey.archeology.blocks.clay_pot.client.RawClayPotBlockEntityRenderer;
import net.capsey.archeology.blocks.clay_pot.client.ShardsContainerRenderer;
import net.capsey.archeology.items.CeramicShardRegistry;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {

    @Inject(method = "addDefaultTextures(Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    private static void addDefaultTextures(Consumer<SpriteIdentifier> adder, CallbackInfo ci) {
        // Adding Clay Pots model textures
        adder.accept(ClayPotBlockEntityRenderer.MODEL_TEXTURE);
        Arrays.stream(ClayPotBlockEntityRenderer.MODEL_TEXTURE_DYED).forEach(adder);
        adder.accept(RawClayPotBlockEntityRenderer.MODEL_TEXTURE);

        // Adding raw shard model texture
        adder.accept(ShardsContainerRenderer.RAW_SHARD);

        // Making SpriteIds map for all shards
        Map<Identifier, SpriteIdentifier> shardIds = new HashMap<>();

        CeramicShardRegistry.getShardIds().forEach(shardId -> {
            Identifier id = new Identifier(shardId.getNamespace(), "entity/shard/" + shardId.getPath());
            shardIds.put(shardId, new SpriteIdentifier(ClayPotBlockEntityRenderer.SHARDS_ATLAS_TEXTURE, id));
        });

        // Adding shards textures
        shardIds.values().forEach(adder);

        // Putting them into map for renderer to use
        ShardsContainerRenderer.SHARD_SPRITE_IDS.putAll(shardIds);
    }

}
