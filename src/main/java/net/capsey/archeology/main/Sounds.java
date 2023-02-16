package net.capsey.archeology.main;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Sounds {

    // Sound IDs
    public static final Identifier BRUSHING_SOUND_ID = new Identifier(ArcheologyMod.MOD_ID, "item.copper_brush.brushing");
    public static final Identifier SHATTERING_SOUND_ID = new Identifier(ArcheologyMod.MOD_ID, "block.excavation.shattering");

    // Sound Events
    public static final SoundEvent BRUSHING_SOUND_EVENT = SoundEvent.of(BRUSHING_SOUND_ID);
    public static final SoundEvent SHATTERING_SOUND_EVENT = SoundEvent.of(SHATTERING_SOUND_ID);

    public static void onInitialize() {
        Registry.register(Registries.SOUND_EVENT, BRUSHING_SOUND_ID, BRUSHING_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, SHATTERING_SOUND_ID, SHATTERING_SOUND_EVENT);
    }

}
