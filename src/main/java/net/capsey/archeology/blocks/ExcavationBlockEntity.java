package net.capsey.archeology.blocks;

import java.util.List;

import net.capsey.archeology.ArcheologyMod;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ExcavationBlockEntity extends BlockEntity implements BlockEntityClientSerializable  {

    public static final String LOOT_TABLE_KEY = "LootTable";

    private static final float[] LUCK_POINTS = { 1.0F, 2.0F, 3.0F, 4.0F };

    private static float getLuckPoints(ItemStack stack) {
        if (stack.getItem() != ArcheologyMod.COPPER_BRUSH) {
            return -1;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        return LUCK_POINTS[index];
    }

    private static float getBreakingDelta(ItemStack stack, double magnitude) {
        if (stack.getItem() != ArcheologyMod.COPPER_BRUSH) {
            return -1;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        switch (index) {
            case 0: return (float) ((-0.75F * Math.sqrt(magnitude)) + 0.04F);
            case 1: return (float) ((-0.70F * Math.sqrt(magnitude)) + 0.05F);
            case 2: return (float) ((-0.65F * Math.sqrt(magnitude)) + 0.06F);
            case 3: return (float) ((-0.50F * Math.sqrt(magnitude)) + 0.07F);
            default: return 1.0F;
        }
    }

    private Identifier lootTableId;

    private ItemStack loot = ItemStack.EMPTY;
    private PlayerEntity brushingPlayer;
    private float breakingProgress = -1.0F;
    private Vec3d prevLookPoint;

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(ArcheologyMod.EXCAVATION_BLOCK_ENTITY, pos, state);
        lootTableId = new Identifier("archeology", "excavation/excavation_site");
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        
        if (tag.contains("Loot")){
            loot = ItemStack.fromNbt(tag.getCompound("Loot"));
        }

        if (tag.contains("LootTable")) {
            String id = tag.getString("LootTable");
            if (!id.isBlank()) {
                lootTableId = new Identifier(id);
            }
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        if (!loot.isEmpty()) {
            NbtCompound nbtCompound = new NbtCompound();
            loot.writeNbt(nbtCompound);
            tag.put("Loot", nbtCompound);
        }

        tag.putString("LootTable", lootTableId.toString());
 
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }

    // Brushing
    public boolean isBrushingPlayer(LivingEntity player) {
        return brushingPlayer == null || brushingPlayer.equals(player);
    }

    public boolean startBrushing(PlayerEntity player, ItemStack stack) {
        if (isBrushingPlayer(player)) {
            BlockState state = world.getBlockState(pos);
            brushingPlayer = player;
    
            if (state.getBlock() instanceof ExcavationBlock) {
                if (state.get(ExcavationBlock.BRUSHING_LEVEL) == 0) {
                    ((ExcavationBlockEntity) world.getBlockEntity(pos)).generateLoot(player, stack);
                    return true;
                }

                breakBlock();
            }
        }

        return false;
    }

    public void brushingTick(float progress, int remainingUseTicks, ItemStack stack, Vec3d lookPoint) {
        BlockState state = world.getBlockState(pos);
        
        // Aestetics
        if (remainingUseTicks % (ExcavationBlock.getBrushTicks(stack) / 4) == 0) {
            BlockSoundGroup soundGroup = world.getBlockState(pos).getSoundGroup();
            world.playSound(null, pos, soundGroup.getBreakSound(), SoundCategory.BLOCKS, soundGroup.getVolume(), soundGroup.getPitch());
            world.addBlockBreakParticles(pos, world.getBlockState(pos));
        }

        // Brushing
        if (remainingUseTicks % ExcavationBlock.getBrushTicks(stack) == 0) {
            int num = (int) Math.floor(progress * ExcavationBlock.MAX_BRUSHING_LEVELS) + 1;

            if (num < ExcavationBlock.MAX_BRUSHING_LEVELS + 1) {
                world.setBlockState(pos, state.with(ExcavationBlock.BRUSHING_LEVEL, num));
            }
        }

        // Breaking
        if (prevLookPoint != null) {
            double magnitude = Math.pow(lookPoint.getX() - prevLookPoint.getX(), 2)
                             + Math.pow(lookPoint.getY() - prevLookPoint.getY(), 2)
                             + Math.pow(lookPoint.getZ() - prevLookPoint.getZ(), 2);
            
            float delta = getBreakingDelta(stack, magnitude);
            updateBlockBreakingProgress(Math.max(-0.05F, delta));
        }

        prevLookPoint = lookPoint;
    }

    public void finishedBrushing() {
        ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), loot);
        world.spawnEntity(item);
        breakBlock();
    }

    // Breaking
    public void updateBlockBreakingProgress(float delta) {
        BlockState blockState = world.getBlockState(pos);

        if (breakingProgress < 0.0F) {
            blockState.onBlockBreakStart(world, pos, brushingPlayer);
            world.setBlockBreakingInfo(brushingPlayer.getId(), pos, (int) (breakingProgress * 10.0F) - 1);
            breakingProgress = 0.0F;
            return;
        }

        breakingProgress += delta;

        if (breakingProgress >= 1.0F) {
            breakBlock();
            return;
        }

        world.setBlockBreakingInfo(brushingPlayer.getId(), pos, (int) (breakingProgress * 10.0F) - 1);
	}

    public void breakBlock() {
        if (world.getBlockState(pos).getBlock() instanceof ExcavationBlock) {
            world.setBlockBreakingInfo(brushingPlayer.getId(), pos, -1);
            breakingProgress = -1.0F;
    
            world.breakBlock(pos, true);
        }
    }

    // Loot
    public void generateLoot(PlayerEntity player, ItemStack stack) {
        if (this.world.isClient) {
            return;
        }
        
        LootContext.Builder builder = (new LootContext.Builder((ServerWorld)this.world))
            .parameter(LootContextParameters.TOOL, stack)
            .parameter(LootContextParameters.THIS_ENTITY, player)
            .parameter(LootContextParameters.BLOCK_ENTITY, this)
            .random(this.world.getRandom()).luck(player.getLuck() + getLuckPoints(stack));
        
        LootTable lootTable = this.world.getServer().getLootManager().getTable(lootTableId);
        List<ItemStack> list = lootTable.generateLoot(builder.build(ArcheologyMod.EXCAVATION));
        
        if (list.size() > 0) {
            loot = list.get(0);
            this.markDirty();
        }
    }
    
    public boolean isLootGenerated() {
        return !loot.isEmpty();
    }

    public ItemStack getLoot() {
        return loot;
    }
    
}
