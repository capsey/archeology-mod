package net.capsey.archeology;

import net.minecraft.util.UseAction;

public class CustomUseAction {

    static {
        UseAction.values(); // Ensure class is loaded before the variant is accessed
    }

    public static UseAction BRUSH;

}
