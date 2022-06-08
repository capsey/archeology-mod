package net.capsey.archeology;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = ArcheologyMod.MOD_ID)
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean disableBrushingAnimation = false;

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public BrushingOptions brushing = new BrushingOptions();

    public float getBreakDeltaCoef(boolean moved) {
        // XOR operator, gives True only if one of them True, otherwise False
        // Because on Mojang breaking behavior is reversed
        boolean bl = brushing.mojangExcavationBreaking ^ moved;
        return (bl ? brushing.repairingSpeed : brushing.breakingSpeed) / 100.0F;
    }

    public float getThresholdCoef() {
        return brushing.thresholdCoef / 100.0F;
    }

    public static class BrushingOptions {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean mojangExcavationBreaking = false;

        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.BoundedDiscrete(min = 50, max = 200)
        public int thresholdCoef = 100;

        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.BoundedDiscrete(min = 50, max = 200)
        public int breakingSpeed = 100;

        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.BoundedDiscrete(min = 50, max = 200)
        public int repairingSpeed = 100;
    }

}
