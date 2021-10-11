package net.capsey.archeology.blocks.excavation_block;

import java.util.ArrayList;
import java.util.List;

import net.capsey.archeology.ArcheologyMod;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ExcavationBlockEntity extends BlockEntity implements BlockEntityClientSerializable  {

    private static final float[] LUCK_POINTS = { 1.0F, 2.0F, 3.0F, 4.0F };

    private static float getLuckPoints(ItemStack stack) {
        if (stack.getItem() != ArcheologyMod.COPPER_BRUSH) {
            return 0.0F;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        if (index >= LUCK_POINTS.length || index < 0) { index = 0; }

        return LUCK_POINTS[index];
    }

    private static float getBreakingDelta(ItemStack stack, double magnitude) {
        if (stack.getItem() != ArcheologyMod.COPPER_BRUSH) {
            return 1.0F;
        }

        // Only for Debug purposes
        if (stack.getNbt().contains("Debug") && stack.getNbt().getBoolean("Debug")) {
            return 0;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        
        switch (index) {
            case 0: return (float) (-0.75F * Math.sqrt(magnitude)) + 0.04F;
            case 1: return (float) (-0.72F * Math.sqrt(magnitude)) + 0.04F;
            case 2: return (float) (-0.67F * Math.sqrt(magnitude)) + 0.05F;
            case 3: return (float) (-0.65F * Math.sqrt(magnitude)) + 0.06F;
            default: return 1.0F;
        }
    }

    private Identifier lootTableId;

    private ArrayList<ItemStack> loot = new ArrayList<ItemStack>();
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
        
        if (tag.contains("Loot")) {
            loot.clear();
            NbtList nbtList = tag.getList("Loot", 10);

            for (int i = 0; i < nbtList.size(); i++) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                loot.add(ItemStack.fromNbt(nbtCompound));
            }
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
            NbtList nbtList = new NbtList();

            for (ItemStack stack : loot) {
                NbtCompound nbtCompound = new NbtCompound();
                stack.writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
    
            tag.put("Loot", nbtList);
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
                    generateLoot(player, stack);
                    return true;
                }

                breakBlock();
            }
        }

        return false;
    }

    public boolean brushingTick(LivingEntity user, ItemStack stack, float progress, int remainingUseTicks) {
        BlockState state = world.getBlockState(pos);

        // Check
        if (!isBrushingPlayer(user)) {
            return false;
        }
        
        // TODO: Remove hardcoded player reach value
        HitResult result = user.raycast(4.5F, 1, false);

        if (!(result instanceof BlockHitResult)) {
            return false;
        }

        BlockEntity blockEntity = world.getBlockEntity(((BlockHitResult) result).getBlockPos());
        
        if (!(blockEntity instanceof ExcavationBlockEntity && blockEntity.equals(this))) {
            return false;
        }
        
        // Aestetics
        if (remainingUseTicks % (ExcavationBlock.getBrushTicks(stack) / 6) == 0) {
            BlockSoundGroup soundGroup = world.getBlockState(pos).getSoundGroup();
            world.playSound(null, pos, soundGroup.getBreakSound(), SoundCategory.BLOCKS, 0.5F * soundGroup.getVolume(), soundGroup.getPitch());
            world.addBlockBreakParticles(pos, world.getBlockState(pos));

            world.playSound(null, user.getBlockPos(), ArcheologyMod.BRUSHING_SOUND_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        }

        // Brushing
        if (!world.isClient && remainingUseTicks % ExcavationBlock.getBrushTicks(stack) == 0) {
            int num = (int) Math.floor(progress * ExcavationBlock.MAX_BRUSHING_LEVELS) + 1;

            if (num < ExcavationBlock.MAX_BRUSHING_LEVELS + 1) {
                world.setBlockState(pos, state.with(ExcavationBlock.BRUSHING_LEVEL, num));
            }
        }

        // Breaking
        if (prevLookPoint != null) {
            double magnitude = Math.pow(result.getPos().getX() - prevLookPoint.getX(), 2)
                             + Math.pow(result.getPos().getY() - prevLookPoint.getY(), 2)
                             + Math.pow(result.getPos().getZ() - prevLookPoint.getZ(), 2);
            
            float delta = getBreakingDelta(stack, magnitude);
            updateBlockBreakingProgress(Math.max(-0.05F, delta));
        }

        prevLookPoint = result.getPos();
        return true;
    }

    public void finishedBrushing() {
        for (ItemStack stack : loot) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }

        breakBlock();
    }

    // Breaking
    public void updateBlockBreakingProgress(float delta) {
        if (world.isClient) {
            return;
        }

        BlockState blockState = world.getBlockState(pos);

        if (breakingProgress < 0.0F) {
            blockState.onBlockBreakStart(world, pos, brushingPlayer);
            world.setBlockBreakingInfo(0, pos, (int) (breakingProgress * 10.0F) - 1);
            breakingProgress = 0.0F;
            return;
        }

        breakingProgress += delta;

        if (breakingProgress >= 1.0F) {
            breakBlock();
            return;
        }

        world.setBlockBreakingInfo(0, pos, (int) (breakingProgress * 10.0F) - 1);
	}

    public void breakBlock() {
        if (world.isClient) {
            return;
        }

        world.setBlockBreakingInfo(0, pos, -1);
        breakingProgress = -1.0F;

        if (!world.isClient && world.getBlockState(pos).getBlock() instanceof ExcavationBlock) {
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
        
        loot.addAll(list);
        this.markDirty();
        this.sync();
    }
    
    public boolean isLootGenerated() {
        return !loot.isEmpty();
    }

    public ItemStack getOneLootItem() {
        return loot.get(0);
    }
    
}
