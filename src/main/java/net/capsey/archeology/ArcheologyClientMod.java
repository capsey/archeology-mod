package net.capsey.archeology;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ArcheologyClientMod implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        FabricModelPredicateProviderRegistry.register(ArcheologyMod.COPPER_BRUSH, new Identifier("damage"), (itemStack, clientWorld, livingEntity, i) -> {
            return itemStack.getDamage() / itemStack.getMaxDamage();
        });
    }

}
