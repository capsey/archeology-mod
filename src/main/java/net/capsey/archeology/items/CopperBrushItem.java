package net.capsey.archeology.items;

import java.util.Optional;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperBrushItem extends Item {

	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public CopperBrushItem(Settings settings, double cooldownSpeed) {
        super(settings);
		
		Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", cooldownSpeed, EntityAttributeModifier.Operation.ADDITION));
		attributeModifiers = builder.build();
    }

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();

		if (!world.isClient && context.getHand() == Hand.MAIN_HAND) {
			PlayerEntity player = context.getPlayer();
			float cooldown = player.getAttackCooldownProgress(1.0F);

			if (cooldown >= 1) {
				ItemStack stack = player.getStackInHand(context.getHand());
				
				if (stack.isOf(ArcheologyMod.COPPER_BRUSH)) {
					BlockEntity be = world.getBlockEntity(context.getBlockPos());
		
					if (be instanceof ExcavationBlockEntity) {
						ExcavationBlockEntity entity = (ExcavationBlockEntity) be;
						if (player instanceof ServerPlayerEntity && entity.startBrushing((ServerPlayerEntity) player, stack)) {
							player.setCurrentHand(context.getHand());
							return ActionResult.CONSUME;
						}
					}
				}
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		Optional<BlockHitResult> raycast = ExcavationBlockEntity.getRaycast(user);

		if (raycast.isPresent()) {
			BlockPos pos = raycast.get().getBlockPos();

			if (user.getItemUseTime() % (ExcavationBlock.getBrushTicks(user.getActiveItem()) / 6) == 0) {
				world.addBlockBreakParticles(pos, world.getBlockState(pos));
			}
		}
	}

	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 6000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return CustomUseAction.BRUSH;
	}

	@Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == Items.COPPER_INGOT;
	}

}
