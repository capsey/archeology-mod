package net.capsey.archeology.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.client.ClientExcavationManager;
import net.capsey.archeology.client.ExcavationManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements ExcavationManagerAccessor {
    
    public final ClientExcavationManager excavationManager = new ClientExcavationManager();

    @Override
    public ClientExcavationManager getExcavationManager() {
        return excavationManager;
    }

    @Inject(at = @At("HEAD"), method = "setWorld(Lnet/minecraft/client/world/ClientWorld;)V")
    private void setWorld(@Nullable ClientWorld world, CallbackInfo info) {
		excavationManager.setWorld(world);
	}

}
