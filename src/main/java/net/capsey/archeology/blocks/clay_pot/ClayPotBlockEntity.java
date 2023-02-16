package net.capsey.archeology.blocks.clay_pot;

import net.capsey.archeology.main.BlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class ClayPotBlockEntity extends ShardsContainer implements SidedInventory, Nameable {

    private static final String CUSTOM_NAME_TAG = "CustomName";
    private static final String LOOT_TABLE_TAG = "LootTable";
    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 9).toArray();
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);
    @Nullable
    private Text customName;
    private Identifier lootTableId;
    private long lootTableSeed;

    public ClayPotBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.CLAY_POT_BLOCK_ENTITY, pos, state);
    }

    public DyeColor getColor() {
        return ((ClayPotBlock) getCachedState().getBlock()).getColor();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains(CUSTOM_NAME_TAG, NbtElement.STRING_TYPE)) {
            customName = Text.Serializer.fromJson(nbt.getString(CUSTOM_NAME_TAG));
        }

        if (nbt.contains(LOOT_TABLE_TAG, NbtElement.STRING_TYPE)) {
            lootTableId = new Identifier(nbt.getString(LOOT_TABLE_TAG));
        } else {
            Inventories.readNbt(nbt, items);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if (customName != null) {
            nbt.putString(CUSTOM_NAME_TAG, Text.Serializer.toJson(customName));
        }

        if (lootTableId != null) {
            nbt.putString(LOOT_TABLE_TAG, lootTableId.toString());
        } else {
            Inventories.writeNbt(nbt, items);
        }
    }

    public ItemStack writeStackNbt(ItemStack stack) {
        NbtCompound nbt = writeShards(new NbtCompound());

        if (customName != null) {
            stack.setCustomName(customName);
        }

        if (lootTableId != null) {
            nbt.putString(LOOT_TABLE_TAG, lootTableId.toString());
        } else {
            Inventories.writeNbt(nbt, items);
        }

        BlockItem.setBlockEntityNbt(stack, BlockEntities.CLAY_POT_BLOCK_ENTITY, nbt);
        return stack;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return writeShards(new NbtCompound());
    }

    @Override
    public Text getName() {
        return customName != null ? customName : this.getCachedState().getBlock().getName();
    }

    public void setCustomName(@Nullable Text customName) {
        this.customName = customName;
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

    public DefaultedList<ItemStack> getItems() {
        generateItems();
        return items;
    }

    @Override
    public void clear() {
        lootTableId = null;
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
        // To prevent infinite stacking of Shulker in Pot in Shulker in Pot in Shulker...
        Block block = Block.getBlockFromItem(stack.getItem());
        return !(block instanceof ClayPotBlock || block instanceof ShulkerBoxBlock);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

}
