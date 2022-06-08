package net.capsey.archeology.mixin.render;

import net.capsey.archeology.blocks.clay_pot.client.ClayPotBlockEntityRenderer;
import net.capsey.archeology.blocks.clay_pot.client.ShardsContainerRenderer;
import net.capsey.archeology.items.ceramic_shard.CeramicShardRegistry;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {

    private static final Identifier SHARDS_ATLAS_TEXTURE = new Identifier("textures/atlas/shards.png");
    private static final Identifier RAW_SHARDS_ATLAS_TEXTURE = new Identifier("textures/atlas/raw_shards.png");

    @Inject(method = "addDefaultTextures(Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    private static void addDefaultTextures(Consumer<SpriteIdentifier> adder, CallbackInfo ci) {
        // Adding Clay Pots model textures
        adder.accept(ClayPotBlockEntityRenderer.MODEL_TEXTURE);
        adder.accept(ClayPotBlockEntityRenderer.RAW_MODEL_TEXTURE);

        // Making SpriteIds map for all shards
        Map<Identifier, SpriteIdentifier> shardIds = getSpriteIds(SHARDS_ATLAS_TEXTURE, "entity/shard/");
        Map<Identifier, SpriteIdentifier> rawShardIds = getSpriteIds(RAW_SHARDS_ATLAS_TEXTURE, "entity/raw_shard/");

        // Adding shards textures
        shardIds.values().forEach(adder::accept);
        rawShardIds.values().forEach(adder::accept);

        // Putting them into map for renderer to use
        ShardsContainerRenderer.SHARD_SPRITE_IDS.putAll(shardIds);
        ShardsContainerRenderer.RAW_SHARD_SPRITE_IDS.putAll(rawShardIds);
    }

    private static Map<Identifier, SpriteIdentifier> getSpriteIds(Identifier atlas, String directory) {
        Map<Identifier, SpriteIdentifier> result = new HashMap<>();

        CeramicShardRegistry.getShardIds().forEach(spriteId -> {
            Identifier id = new Identifier(spriteId.getNamespace(), directory + spriteId.getPath());
            result.put(spriteId, new SpriteIdentifier(atlas, id));
        });

        return result;
    }

}
