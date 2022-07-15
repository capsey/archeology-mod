package net.capsey.archeology.mixin.entity;

import net.capsey.archeology.blocks.FallingBlockWithEntity;
import net.capsey.archeology.entity.FallingBlockEntityMixinInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity implements FallingBlockEntityMixinInterface {

    private static final TrackedData<NbtCompound> CLIENT_BLOCK_ENTITY_DATA;

    static {
        CLIENT_BLOCK_ENTITY_DATA = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    }

    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker()V", at = @At("TAIL"))
    public void initDataTracker(CallbackInfo info) {
        this.dataTracker.startTracking(CLIENT_BLOCK_ENTITY_DATA, new NbtCompound());
    }

    @Override
    public NbtCompound getClientBlockEntityData() {
        return this.dataTracker.get(CLIENT_BLOCK_ENTITY_DATA);
    }

    @Override
    public void setClientBlockEntityData(NbtCompound nbt) {
        this.dataTracker.set(CLIENT_BLOCK_ENTITY_DATA, nbt);
    }

    @Override
    public ItemEntity dropItem(ItemConvertible item) {
        if (item instanceof FallingBlockWithEntity block) {
            return block.dropItem((FallingBlockEntity) (Object) this, item);
        }

        return super.dropItem(item);
    }

}
