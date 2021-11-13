package net.capsey.archeology.mixin.client;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.client.ClientExcavationManager;
import net.capsey.archeology.client.ExcavationManagerContainer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin implements ExcavationManagerContainer {

    private ClientExcavationManager excavationManager;

    @Override
    public void addExcavationManager(ExcavationBlockEntity entity, ClientWorld world) {
        excavationManager = new ClientExcavationManager(entity, world);
    }

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("HEAD"))
    public void tick(BooleanSupplier supplier, CallbackInfo info) {
        if (excavationManager != null && excavationManager.tick()) {
            excavationManager.onRemoved();
            excavationManager = null;
        }
	}
    
}
