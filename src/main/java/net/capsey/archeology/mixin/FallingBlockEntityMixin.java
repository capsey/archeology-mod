package net.capsey.archeology.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.blocks.FallingBlockWithBlockEntity;
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

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity implements FallingBlockEntityMixinInterface {

    private static final TrackedData<NbtCompound> CLIENT_BLOCK_ENTITY_DATA;

    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    
    @Inject(method = "initDataTracker()V", at = @At("TAIL"))
    public void initDataTracker(CallbackInfo info) {
		this.dataTracker.startTracking(CLIENT_BLOCK_ENTITY_DATA, new NbtCompound());
	}

    @Override
    public void setClientBlockEntityData(NbtCompound nbt) {
        this.dataTracker.set(CLIENT_BLOCK_ENTITY_DATA, nbt);
    }
    
    @Override
	public NbtCompound getClientBlockEntityData() {
        return this.dataTracker.get(CLIENT_BLOCK_ENTITY_DATA);
    }

    @Override
	public ItemEntity dropItem(ItemConvertible item) {
        if (item instanceof FallingBlockWithBlockEntity) {
            FallingBlockWithBlockEntity block = (FallingBlockWithBlockEntity) item;

            if (block.overrideDroppedItem()) {
                return super.dropItem(block.getStackOnDestroy());
            }
        }

		return super.dropItem(item);
	}

    static {
		CLIENT_BLOCK_ENTITY_DATA = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	}

}
