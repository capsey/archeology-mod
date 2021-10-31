package net.capsey.archeology.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.capsey.archeology.blocks.FallingBlockWithBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.world.World;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
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

}
