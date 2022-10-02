package net.capsey.archeology;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {

    // Brushing
    @Comment public static Comment brushingSection;
    @Client @Entry public static boolean releaseUseKeyAfterBrushing = false;
    @Entry(min=0.0F, max=1.0F) public static float brushingLayerChance = 0.35F;

    @Client @Entry(min=0.0F, max=1.0F) public static float brushingLowerThreshold = 0.03F;
    @Client @Entry public static float brushingBreakingSpeed = 3.0F;
    @Client @Entry public static float brushingRepairSpeed = 1.0F;

    // Displaying
    @Comment public static Comment displayingSection;
    @Client @Entry public static boolean disableBrushingAnimation = false;
    @Client @Entry public static boolean removeShardsItemGroup = false;

}
