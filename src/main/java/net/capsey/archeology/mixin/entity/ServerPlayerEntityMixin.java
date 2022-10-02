package net.capsey.archeology.mixin.entity;

import net.capsey.archeology.main.BlockEntities;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.entity.BrushingPlayerEntity;
import net.capsey.archeology.main.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.ref.WeakReference;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements BrushingPlayerEntity, BrushingPlayerEntity.Server {

    @Unique
    private WeakReference<ExcavationBlockEntity> brushingEntity = new WeakReference<>(null);

    @Override
    public void startBrushing(BlockPos pos) {
        World world = ((Entity)(Object) this).world;
        ExcavationBlockEntity entity = world.getBlockEntity(pos, BlockEntities.EXCAVATION_BLOCK_ENTITY).orElse(null);
        brushingEntity = new WeakReference<>(entity);

        if (entity != null) {
            PlayerEntity player = (PlayerEntity)(Object) this;
            entity.generateLoot(player, player.getActiveItem());
        }
    }

    @Inject(method = "onDisconnect()V", at = @At("TAIL"))
    public void onDisconnect(CallbackInfo ctx) {
        onStopBrushing();
    }

    @Inject(method = "dropSelectedItem(Z)Z", at = @At("TAIL"))
    public void dropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        onStopBrushing();
    }

    @Override
    public @Nullable ExcavationBlockEntity getBrushingEntity() {
        ExcavationBlockEntity entity = brushingEntity.get();
        return entity == null || entity.isRemoved() ? null : entity;
    }

    @Override
    public void onStopBrushing() {
        ExcavationBlockEntity entity = brushingEntity.get();
        PlayerEntity player = (PlayerEntity)(Object) this;

        if (entity != null && !entity.isRemoved()) {
            if (entity.hasLoot()) {
                player.world.playSound(null, entity.getPos(), Sounds.SHATTERING_SOUND_EVENT, SoundCategory.BLOCKS, 0.5F, 1.0F);
            }

            player.world.breakBlock(entity.getPos(), true, player);
            player.incrementStat(Stats.MINED.getOrCreateStat(entity.getCachedState().getBlock()));
        }
    }
}
