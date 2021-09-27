package net.capsey.archeology.items;

import net.minecraft.util.UseAction;

public class CustomUseAction {

    static {
        UseAction.values(); // Ensure class is loaded before the variant is accessed
    }

    public static UseAction BRUSH;

}
