package net.capsey.archeology.items;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.chiseled.ChiseledBlock;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

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
        return 8;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());

        if (ChiseledBlock.isChiselable(state.getBlock())) {
            context.getPlayer().setCurrentHand(context.getHand());
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

                // Adding breaking particles
                BlockPos pos = raycast.getBlockPos();
                segment.shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                    double d = Math.min(1.0, maxX - minX);
                    double e = Math.min(1.0, maxY - minY);
                    double f = Math.min(1.0, maxZ - minZ);
                    int i = Math.max(2, MathHelper.ceil(d / 0.25));
                    int j = Math.max(2, MathHelper.ceil(e / 0.25));
                    int k = Math.max(2, MathHelper.ceil(f / 0.25));
                    for (int l = 0; l < i; ++l) {
                        for (int m = 0; m < j; ++m) {
                            for (int n = 0; n < k; ++n) {
                                double g = ((double) l + 0.5) / (double) i;
                                double h = ((double) m + 0.5) / (double) j;
                                double o = ((double) n + 0.5) / (double) k;
                                double p = g * d + minX;
                                double q = h * e + minY;
                                double r = o * f + minZ;
                                client.particleManager.addParticle(new BlockDustParticle(
                                        (ClientWorld) world,
                                        (double) pos.getX() + p,
                                        (double) pos.getY() + q,
                                        (double) pos.getZ() + r,
                                        g - 0.5,
                                        h - 0.5,
                                        o - 0.5,
                                        state,
                                        pos
                                ));
                            }
                        }
                    }
                });

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
