package net.capsey.archeology.main;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Sounds {

    public static final Identifier BRUSHING_SOUND_ID = new Identifier(ArcheologyMod.MOD_ID, "item.copper_brush.brushing");
    public static final SoundEvent BRUSHING_SOUND_EVENT = new SoundEvent(BRUSHING_SOUND_ID);

    public static void onInitialize() {
        Registry.register(Registry.SOUND_EVENT, BRUSHING_SOUND_ID, BRUSHING_SOUND_EVENT);
    }

}
