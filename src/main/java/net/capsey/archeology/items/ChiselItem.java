package net.capsey.archeology.items;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.chiseled.ChiseledBlock;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class ChiselItem extends Item {

    public ChiselItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 12;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        PlayerEntity player = Objects.requireNonNull(context.getPlayer());

        if (ChiseledBlock.isChiselable(state.getBlock())) {
            player.setCurrentHand(context.getHand());
            return ActionResult.CONSUME;
        }

        return super.useOnBlock(context);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.crosshairTarget instanceof BlockHitResult raycast) {
                BlockPos pos = raycast.getBlockPos();
                BlockState state = world.getBlockState(pos);

                if (ChiseledBlock.isChiselable(state.getBlock())) {
                    return;
                }
            }

            user.stopUsingItem();
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (world.isClient) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.crosshairTarget instanceof BlockHitResult raycast) {
                // Calculating segment to chisel
                BlockState state = world.getBlockState(raycast.getBlockPos());
                ChiseledBlock.Segment segment = ChiseledBlock.Segment.fromRaycast(raycast, state);

                // Sending packet to the server
                PacketByteBuf buf = PacketByteBufs.create();

                buf.writeBlockPos(raycast.getBlockPos());
                buf.writeString(segment.name());

                ClientPlayNetworking.send(ArcheologyMod.CHISEL_BLOCK_SEGMENT, buf);
            }
        }

        return super.finishUsing(stack, world, user);
    }
}
