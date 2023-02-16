package net.capsey.archeology.mixin.client;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.clay_pot.client.AbstractClayPotBlockEntityRenderer;
import net.capsey.archeology.items.client.ShardsContainerRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {

    @Mutable
    @Final
    @Shadow
    private static Map<Identifier, Identifier> LAYERS_TO_LOADERS;

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void archeology$static(CallbackInfo info) {
        Map<Identifier, Identifier> temp = new HashMap<>(LAYERS_TO_LOADERS);

        temp.put(AbstractClayPotBlockEntityRenderer.ATLAS_TEXTURE_ID, new Identifier(ArcheologyMod.MOD_ID, "clay_pots"));
        temp.put(ShardsContainerRenderer.ATLAS_TEXTURE_ID, new Identifier(ArcheologyMod.MOD_ID, "shards"));

        LAYERS_TO_LOADERS = temp;
    }

}
