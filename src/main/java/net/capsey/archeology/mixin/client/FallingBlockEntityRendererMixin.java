package net.capsey.archeology.mixin.client;

import net.capsey.archeology.blocks.clay_pot.ClayPotBlock;
import net.capsey.archeology.main.Blocks;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.entity.FallingBlockEntityMixinInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Comparator;

@Mixin(FallingBlockEntityRenderer.class)
public abstract class FallingBlockEntityRendererMixin extends EntityRenderer<FallingBlockEntity> {

    @Unique
    private static final ClayPotBlockEntity renderClayPot = new ClayPotBlockEntity(BlockPos.ORIGIN, Blocks.CLAY_POT.getDefaultState());
    @Unique
    private static final ClayPotBlockEntity[] renderClayPotDyed = Arrays.stream(DyeColor.values())
            .sorted(Comparator.comparingInt(DyeColor::getId))
            .map(x -> new ClayPotBlockEntity(BlockPos.ORIGIN, Blocks.CLAY_POT_DYED[x.getId()].getDefaultState()))
            .toArray(ClayPotBlockEntity[]::new);

    protected FallingBlockEntityRendererMixin(Context ctx) {
        super(ctx);
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/entity/FallingBlockEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    public void render(FallingBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        BlockState blockState = entity.getBlockState();
        Block block = blockState.getBlock();

        if (block instanceof ClayPotBlock clayPotBlock) {
            World world = entity.getEntityWorld();

            if (blockState != world.getBlockState(entity.getBlockPos())) {
                BlockEntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();
                NbtCompound data = ((FallingBlockEntityMixinInterface) entity).getClientBlockEntityData();
                DyeColor color = clayPotBlock.getColor();
                ClayPotBlockEntity blockEntity = color == null ? renderClayPot : renderClayPotDyed[color.getId()];

                matrices.push();
                matrices.translate(-0.5D, 0.0D, -0.5D);

                blockEntity.readShards(data);
                blockEntity.setCachedState(blockState);
                dispatcher.renderEntity(blockEntity, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);

                matrices.pop();
                super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
            }
        }
    }

}
