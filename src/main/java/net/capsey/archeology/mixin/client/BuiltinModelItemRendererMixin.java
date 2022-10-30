package net.capsey.archeology.mixin.client;

import net.capsey.archeology.blocks.clay_pot.*;
import net.capsey.archeology.main.Blocks;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin {

    @Unique
    private static final RawClayPotBlockEntity renderRawClayPot = new RawClayPotBlockEntity(BlockPos.ORIGIN, Blocks.RAW_CLAY_POT.getDefaultState());
    @Unique
    private static final ClayPotBlockEntity renderClayPot = new ClayPotBlockEntity(BlockPos.ORIGIN, Blocks.CLAY_POT.getDefaultState());
    @Unique
    private static final ClayPotBlockEntity[] renderClayPotDyed = Arrays.stream(DyeColor.values())
            .sorted(Comparator.comparingInt(DyeColor::getId))
            .map(x -> new ClayPotBlockEntity(BlockPos.ORIGIN, Blocks.CLAY_POT_DYED[x.getId()].getDefaultState()))
            .toArray(ClayPotBlockEntity[]::new);

    @Final
    @Shadow
    private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    @Inject(at = @At("HEAD"), cancellable = true, method = "render(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V")
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo info) {
        Block block = Block.getBlockFromItem(stack.getItem());
        ShardsContainer blockEntity;

        if (block instanceof ClayPotBlock clayPotBlock) {
            DyeColor color = clayPotBlock.getColor();
            blockEntity = color == null ? Objects.requireNonNull(renderClayPot) : renderClayPotDyed[color.getId()];
        } else if (block instanceof RawClayPotBlock) {
            blockEntity = renderRawClayPot;
        } else {
            return;
        }

        blockEntity.readShards(BlockItem.getBlockEntityNbt(stack));
        blockEntityRenderDispatcher.renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);
        info.cancel();
    }

}
