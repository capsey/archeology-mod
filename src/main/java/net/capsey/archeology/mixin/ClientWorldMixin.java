package net.capsey.archeology.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.capsey.archeology.ClientExcavationManager;
import net.capsey.archeology.ClientWorldMixinInterface;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements ClientWorldMixinInterface {
    
    public final ClientExcavationManager excavationManager = new ClientExcavationManager((ClientWorld)(Object) this);

    @Override
    public ClientExcavationManager getExcavationManager() {
        return excavationManager;
    }

}
