package net.capsey.archeology;

import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.util.math.MathHelper;

public class ModConfig extends MidnightConfig {

    @Comment
    public static Comment brushingSection;
    @Entry(min = 0.0F, max = 1.0F, isSlider = true)
    public static float brushingLayerChance = 0.35F;
    @Client
    @Entry(min = 0.0F, max = 1.0F, isSlider = true)
    public static float brushingLowerThreshold = 0.02F;
    @Client
    @Entry(min = 0.0F, max = 20.0F, isSlider = true)
    public static float brushingBreakingSpeed = 3.0F;
    @Client
    @Entry
    public static boolean enableMojangBrushingStyle = false;
    @Client
    @Entry
    public static boolean releaseUseKeyAfterBrushing = false;
    @Comment
    public static Comment displayingSection;
    @Client
    @Entry
    public static boolean disableBrushingAnimation = false;

    public static float getBrushingDelta(float mouseMovement) {
        mouseMovement -= brushingLowerThreshold * (!enableMojangBrushingStyle ? 1.0F : 0.1F);
        float delta = mouseMovement * brushingBreakingSpeed * (!enableMojangBrushingStyle ? 1.0F : 1.5F);

        // XOR operator inverts the boolean expression
        // in case `enableMojangBrushingStyle` is true
        return (mouseMovement < 0) ^ enableMojangBrushingStyle ? MathHelper.abs(delta) : 0;
    }

}
