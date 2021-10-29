package net.capsey.archeology.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
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
			BlockPos pos = context.getBlockPos();
			Block block = world.getBlockState(pos).getBlock();

			if (block instanceof ExcavationBlock) {
				PlayerEntity player = context.getPlayer();
				float cooldown = player.getAttackCooldownProgress(1.0F);
	
				if (cooldown >= 1 && player instanceof ServerPlayerEntity) {
					ItemStack stack = context.getStack();

					if (((ExcavationBlock) block).startBrushing(world, pos, (ServerPlayerEntity) player, stack)) {
						player.setCurrentHand(context.getHand());
						return ActionResult.CONSUME;
					}
				}
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		// TODO: Remove hardcoded player reach value
		HitResult raycast = user.raycast(4.5F, 1, false);

		if (raycast instanceof BlockHitResult) {
			BlockPos pos = ((BlockHitResult) raycast).getBlockPos();

			if (user.getItemUseTime() % (ExcavationBlock.getBrushTicks(user.getActiveItem()) / 6) == 0) {
				BlockState state = world.getBlockState(pos);

				if (!world.isClient) {
					BlockSoundGroup soundGroup = state.getSoundGroup();
					world.playSound(null, pos, soundGroup.getBreakSound(), SoundCategory.BLOCKS, 0.3F * soundGroup.getVolume(), soundGroup.getPitch());
					world.playSound(null, user.getBlockPos(), ArcheologyMod.BRUSHING_SOUND_EVENT, SoundCategory.PLAYERS, 1f, 1f);
				}

				world.addBlockBreakParticles(pos, state);
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
