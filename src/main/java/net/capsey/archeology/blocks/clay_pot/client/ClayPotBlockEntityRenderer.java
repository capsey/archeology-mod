package net.capsey.archeology.blocks.clay_pot.client;

import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.DyeColor;

import java.util.Arrays;
import java.util.Comparator;

@Environment(EnvType.CLIENT)
public class ClayPotBlockEntityRenderer extends AbstractClayPotBlockEntityRenderer<ClayPotBlockEntity> {

    public static final SpriteIdentifier MODEL_TEXTURE = spriteId("entity/clay_pot/clay_pot");
    public static final SpriteIdentifier[] MODEL_TEXTURE_DYED = Arrays.stream(DyeColor.values())
            .sorted(Comparator.comparingInt(DyeColor::getId))
            .map(x -> spriteId("entity/clay_pot/clay_pot_" + x.getName()))
            .toArray(SpriteIdentifier[]::new);

    public ClayPotBlockEntityRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    protected SpriteIdentifier getSpriteId(ClayPotBlockEntity entity) {
        DyeColor color = entity.getColor();
        return color == null ? MODEL_TEXTURE : MODEL_TEXTURE_DYED[color.getId()];
    }

}
