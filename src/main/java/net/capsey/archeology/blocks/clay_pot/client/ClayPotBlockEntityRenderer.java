package net.capsey.archeology.blocks.clay_pot.client;

import net.capsey.archeology.ArcheologyClientMod;
import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Comparator;

@Environment(EnvType.CLIENT)
public class ClayPotBlockEntityRenderer extends AbstractClayPotBlockEntityRenderer<ClayPotBlockEntity> {

    public static final SpriteIdentifier MODEL_TEXTURE = new SpriteIdentifier(CLAY_POTS_ATLAS_TEXTURE, new Identifier(ArcheologyMod.MOD_ID, "entity/clay_pot"));
    public static final SpriteIdentifier[] MODEL_TEXTURE_DYED = Arrays.stream(DyeColor.values())
            .sorted(Comparator.comparingInt(DyeColor::getId))
            .map(x -> new SpriteIdentifier(CLAY_POTS_ATLAS_TEXTURE, new Identifier(ArcheologyMod.MOD_ID, "entity/clay_pot_" + x.getName())))
            .toArray(SpriteIdentifier[]::new);

    public ClayPotBlockEntityRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    protected SpriteIdentifier getTextureId(ClayPotBlockEntity entity) {
        DyeColor color = entity.getColor();
        return color == null ? MODEL_TEXTURE : MODEL_TEXTURE_DYED[color.getId()];
    }
}
