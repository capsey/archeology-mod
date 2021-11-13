package net.capsey.archeology.client;

import java.util.Objects;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.items.CopperBrushItem;
import net.capsey.archeology.network.ExcavationBreakingC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ClientExcavationManager {
    
    private final ClientWorld world;
    private final ExcavationBlockEntity entity;
    private final BlockPos pos;
    private int stage = 0;

    public ClientExcavationManager(ExcavationBlockEntity entity, ClientWorld world) {
        this.world = world;
        this.entity = entity;
        this.pos = entity.getPos();
    }

    public boolean tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity user = client.player;

        if (user.isUsingItem() && user.getItemUseTimeLeft() > 0) {
            HitResult result = user.raycast(client.interactionManager.getReachDistance(), 1, false);
    
            if (result instanceof BlockHitResult blockResult) {
                int ticks = user.getItemUseTime();

                aestheticTick(user.getActiveItem(), ticks);
                return excavationTick(blockResult, ticks);
            }
        }
        
        return true;
    }

    public boolean excavationTick(BlockHitResult result, int usageTick) {
        BlockEntity blockEntity = this.world.getBlockEntity(result.getBlockPos());

        if (Objects.equals(blockEntity, this.entity)) {
            if (usageTick % 10 == 0) {
                if (this.stage++ < 9) {
                    this.world.setBlockBreakingInfo(0, this.pos, this.stage);
                    return false;
                }
                
                return true;
            }

            return false;
        }
        
        return true;
    }

    public void aestheticTick(ItemStack stack, int usageTick) {
        int brushTicks = CopperBrushItem.getBrushTicks(CopperBrushItem.getOxidizationLevel(stack));
        
        if (usageTick % brushTicks == 0) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.particleManager.addBlockBreakingParticles(this.pos, Direction.UP);
        }
    }

    public void onRemoved() {
        this.world.sendPacket(new ExcavationBreakingC2SPacket(this.pos));
        this.world.setBlockBreakingInfo(0, this.pos, -1);

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player.isUsingItem() && player.getActiveItem().isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
            player.stopUsingItem();
        }
    }

}
