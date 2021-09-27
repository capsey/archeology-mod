package net.capsey.archeology.mixin;

import java.util.ArrayList;
import java.util.Arrays;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.capsey.archeology.items.CustomUseAction;
import net.minecraft.util.UseAction;

@Mixin(UseAction.class)
public abstract class UseActionMixin {

    @SuppressWarnings("InvokerTarget")
    @Invoker("<init>")
    private static UseAction newVariant(String name, int ordinal) {
        throw new AssertionError();
    }

    @SuppressWarnings("ShadowTarget")
    @Shadow
    private static @Final
    @Mutable
    UseAction[] field_8948;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<clinit>", at = @At(value = "FIELD", 
        opcode = Opcodes.PUTSTATIC, 
        target = "Lnet/minecraft/util/UseAction;field_8948:[Lnet/minecraft/util/UseAction;", 
        shift = At.Shift.AFTER))
    private static void addCustomVariant(CallbackInfo ci) {
        var variants = new ArrayList<>(Arrays.asList(field_8948));
        var last = variants.get(variants.size() - 1);
        
        // This means our code will still work if other mods or Mojang add more variants!
        var brush = newVariant("BRUSH", last.ordinal() + 1);
        CustomUseAction.BRUSH = brush;

        variants.add(brush);
        field_8948 = variants.toArray(new UseAction[0]);
    }
    
}
