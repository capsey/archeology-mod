package net.capsey.archeology.blocks;

import java.util.List;

import net.capsey.archeology.ArcheologyMod;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ExcavationBlockEntity extends BlockEntity implements BlockEntityClientSerializable  {

    private static final float[] LUCK_MULTIPLIERS = { 1.0F, 2.0F, 3.0F, 4.0F };

    private static float getLuckMultiplier(ItemStack stack) {
        if (stack.getItem() != ArcheologyMod.COPPER_BRUSH) {
            return -1;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        return LUCK_MULTIPLIERS[index];
    }

    private ItemStack loot = ItemStack.EMPTY;

    // private Identifier lootTableId;
	// private long lootTableSeed;

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(ArcheologyMod.EXCAVATION_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        
        loot = ItemStack.fromNbt(tag.getCompound("Loot"));

        // lootTableId = new Identifier(tag.getString("LootTable"));
        // lootTableSeed = tag.getLong("LootTableSeed");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        if (!loot.isEmpty()) {
            NbtCompound nbtCompound = new NbtCompound();
            loot.writeNbt(nbtCompound);
            tag.put("Loot", nbtCompound);
        }

        // tag.putString("LootTable", lootTableId.toString());
        // tag.putLong("LootTableSeed", lootTableSeed);
 
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

    public void generateLoot(PlayerEntity player, ItemStack stack) {
        if (this.world.isClient) {
            loot = new ItemStack(Items.DIAMOND_BLOCK, 1);
            return;
        }
        
        // TODO: Change to custom Loot Table
        Vec3d origin = new Vec3d(this.pos.getX(), this.pos.getY(), this.pos.getZ());
        LootContext.Builder builder = (new LootContext.Builder((ServerWorld)this.world)).parameter(LootContextParameters.ORIGIN, origin).parameter(LootContextParameters.TOOL, stack).parameter(LootContextParameters.BLOCK_ENTITY, this).parameter(LootContextParameters.BLOCK_STATE, this.world.getBlockState(this.getPos())).random(this.world.getRandom()).luck(getLuckMultiplier(stack) * player.getLuck());
        LootTable lootTable = this.world.getServer().getLootManager().getTable(LootTables.SIMPLE_DUNGEON_CHEST);
        List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.BLOCK));
        
        loot = list.get(0);
        this.markDirty();
    }
    
    public boolean isLootGenerated() {
        return !loot.isEmpty();
    }

    public ItemStack getLoot() {
        return loot;
    }

    public void spawnLootItems() {
        ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), loot);
        world.spawnEntity(item);
    }
    
}
