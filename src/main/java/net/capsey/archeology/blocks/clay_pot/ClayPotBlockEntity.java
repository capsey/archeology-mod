package net.capsey.archeology.blocks.clay_pot;

import net.capsey.archeology.main.BlockEntities;
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
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.IntStream;

public class ClayPotBlockEntity extends ShardsContainer implements SidedInventory {

    private static final String LOOT_TABLE_TAG = "LootTable";
    private static final String COLOR_TAG = "Color";
    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 9).toArray();
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);
    @Nullable private DyeColor color;
    private Identifier lootTableId;
    private long lootTableSeed;

    public ClayPotBlockEntity(BlockPos pos, BlockState state, Map<Side, CeramicShard> shards, @Nullable DyeColor color) {
        super(BlockEntities.CLAY_POT_BLOCK_ENTITY, pos, state);
        this.color = color;
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

    public @Nullable DyeColor getColor() {
        return color;
    }

    public void setColor(@Nullable DyeColor color) {
        this.color = color;
        markForUpdate();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains(LOOT_TABLE_TAG, NbtElement.STRING_TYPE)) {
            lootTableId = new Identifier(nbt.getString(LOOT_TABLE_TAG));
        } else {
            Inventories.readNbt(nbt, items);
        }

        if (nbt.contains(COLOR_TAG, NbtElement.INT_TYPE)) {
            color = DyeColor.byId(nbt.getInt(COLOR_TAG));
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

        if (color != null) {
            nbt.putInt(COLOR_TAG, color.getId());
        }
    }

    public void readClientData(NbtCompound tag) {
        readShards(tag);
        if (tag.contains(COLOR_TAG, NbtElement.INT_TYPE)) {
            color = DyeColor.byId(tag.getInt(COLOR_TAG));
        } else {
            color = null;
        }
    }

    public NbtCompound writeClientData(NbtCompound tag) {
        writeShards(tag);
        if (color != null) {
            tag.putInt(COLOR_TAG, color.getId());
        }
        return tag;
    }

    @Override
    public void readFrom(ItemStack stack) {
        super.readFrom(stack);

        NbtCompound nbt = stack.getSubNbt("BlockEntityTag");
        if (nbt != null && nbt.contains(COLOR_TAG, NbtElement.INT_TYPE)) {
            color = DyeColor.byId(nbt.getInt(COLOR_TAG));
        } else {
            color = null;
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = new NbtCompound();
        return writeClientData(tag);
    }

    @Override
    public int size() {
        generateItems();
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
        generateItems();
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
        generateItems();
        items.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    private void generateItems() {
        if (!world.isClient && lootTableId != null) {
            LootTable lootTable = world.getServer().getLootManager().getTable(lootTableId);
            LootContext.Builder builder = (new LootContext.Builder((ServerWorld) world))
                    .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                    .random(lootTableSeed);

            lootTableId = null;
            lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST));
        }
    }

    public void onBreak() {
        generateItems();
        ItemScatterer.spawn(world, pos, this);
    }

    @Override
    public void clear() {
        generateItems();
        items.clear();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
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
