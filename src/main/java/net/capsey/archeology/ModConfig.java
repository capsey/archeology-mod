package net.capsey.archeology;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {

    @Client @Entry public static boolean disableBrushingAnimation = false;
    @Client @Entry public static boolean releaseUseKeyAfterBrushing = false;

}
