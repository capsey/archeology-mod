package net.capsey.archeology;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {

    @Client @Entry public static boolean disableBrushingAnimation = false;
    @Client @Entry public static boolean releaseUseKeyAfterBrushing = false;
    @Client @Entry(min=0.0F, max=1.0F) public static float brushingLowerThreshold = 0.03F;
    @Client @Entry public static float brushingBreakingSpeed = 3.0F;
    @Client @Entry public static float brushingRepairSpeed = 1.0F;

}
