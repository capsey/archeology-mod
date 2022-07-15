package net.capsey.archeology;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {

    @Client
    @Entry
    public static boolean disableFirstPersonItemAnimations = false;
    @Client
    @Entry
    public static boolean mojangExcavationBreaking = false;
    @Client
    @Entry(min = 0.5F, max = 2.0F)
    public static float thresholdCoef = 1.0F;
    @Client
    @Entry(min = 0.5F, max = 2.0F)
    public static float breakingSpeed = 1.0F;
    @Client
    @Entry(min = 0.5F, max = 2.0F)
    public static float repairingSpeed = 1.0F;

    public static float getBreakDeltaCoef(boolean moved) {
        // XOR operator, gives True only if one of them True, otherwise False
        // Because on Mojang breaking behavior is reversed
        boolean bl = mojangExcavationBreaking ^ moved;
        return bl ? repairingSpeed : breakingSpeed;
    }

}
