package net.capsey.archeology.mixin.render;

import net.capsey.archeology.Blocks;
import net.capsey.archeology.blocks.clay_pot.AbstractClayPotBlock;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin {

    private final ClayPotBlockEntity renderClayPot = new ClayPotBlockEntity(BlockPos.ORIGIN, Blocks.CLAY_POT.getDefaultState());
    private final RawClayPotBlockEntity renderRawClayPot = new RawClayPotBlockEntity(BlockPos.ORIGIN, Blocks.RAW_CLAY_POT.getDefaultState());

    @Inject(at = @At("HEAD"), cancellable = true, method = "render(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V")
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo info) {
        if (stack.getItem() instanceof BlockItem item) {
            Block block = item.getBlock();

            if (block instanceof AbstractClayPotBlock) {
                BlockState blockState = block.getDefaultState();
                BlockEntity blockEntity;

                if (blockState.isOf(Blocks.CLAY_POT)) {
                    this.renderClayPot.readFrom(stack);
                    blockEntity = this.renderClayPot;
                } else if (blockState.isOf(Blocks.RAW_CLAY_POT)) {
                    this.renderRawClayPot.readFrom(stack);
                    blockEntity = this.renderRawClayPot;
                } else {
                    return;
                }

                BlockEntityRenderDispatcher dispatcher = this.getBlockEntityRenderDispatcher();
                dispatcher.renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);
                info.cancel();
            }
        }
    }

    @Accessor("blockEntityRenderDispatcher")
    abstract BlockEntityRenderDispatcher getBlockEntityRenderDispatcher();

}
