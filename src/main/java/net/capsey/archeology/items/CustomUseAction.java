package net.capsey.archeology.items;

import net.minecraft.util.UseAction;

public class CustomUseAction {

    public static UseAction BRUSH;

    static {
        UseAction.values(); // Ensure class is loaded before the variant is accessed
    }

}
