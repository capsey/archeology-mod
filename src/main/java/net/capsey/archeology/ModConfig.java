package net.capsey.archeology;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "archeology")
public class ModConfig implements ConfigData {
    
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean stopUsingAfterBrushing = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean mojangExcavationBreaking = false;

}
