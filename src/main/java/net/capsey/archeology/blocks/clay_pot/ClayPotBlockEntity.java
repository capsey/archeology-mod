package net.capsey.archeology.blocks.clay_pot;

import net.capsey.archeology.BlockEntities;
import net.capsey.archeology.items.ceramic_shard.CeramicShard;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.stream.IntStream;

public class ClayPotBlockEntity extends ShardsContainer implements SidedInventory {

    private static final String LOOT_TABLE_TAG = "LootTable";
    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 9).toArray();
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private Identifier lootTableId;
    private long lootTableSeed;

    public ClayPotBlockEntity(BlockPos pos, BlockState state, Map<Side, CeramicShard> shards) {
        super(BlockEntities.CLAY_POT_BLOCK_ENTITY, pos, state);
        replaceShards(shards);
    }

    public ClayPotBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.CLAY_POT_BLOCK_ENTITY, pos, state);
    }

    public void setLootTable(Identifier id, long seed) {
        items.clear();
        this.lootTableId = id;
        this.lootTableSeed = seed;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains(LOOT_TABLE_TAG, NbtElement.STRING_TYPE)) {
            lootTableId = new Identifier(nbt.getString(LOOT_TABLE_TAG));
        } else {
            Inventories.readNbt(nbt, items);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if (lootTableId != null) {
            nbt.putString(LOOT_TABLE_TAG, lootTableId.toString());
        } else {
            Inventories.writeNbt(nbt, items);
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = new NbtCompound();
        super.writeNbt(tag);

        return writeShards(tag);
    }

    @Override
    public int size() {
        this.generateItems();
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            if (!getStack(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        this.generateItems();
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.generateItems();
        items.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    private void generateItems() {
        if (!this.world.isClient && this.lootTableId != null) {
            LootTable lootTable = this.world.getServer().getLootManager().getTable(this.lootTableId);
            LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world))
                    .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(this.pos))
                    .random(this.lootTableSeed);

            this.lootTableId = null;
            lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST));
        }
    }

    public void onBreak() {
        this.generateItems();
        ItemScatterer.spawn(world, pos, this);
    }

    @Override
    public void clear() {
        this.generateItems();
        items.clear();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

}
