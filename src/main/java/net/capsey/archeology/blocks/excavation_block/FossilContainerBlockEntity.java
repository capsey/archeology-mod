package net.capsey.archeology.blocks.excavation_block;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.items.CopperBrushItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class FossilContainerBlockEntity extends BlockEntity {

    private static final String LOOT_TABLE_TAG = "LootTable";
    private static final String LOOT_TAG = "Loot";
    @Nullable protected Identifier lootTableId;
    protected final ArrayList<ItemStack> loot = new ArrayList<>();

    protected FossilContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable Identifier lootTable) {
        super(type, pos, state);
        lootTableId = lootTable;
    }

    private static float getLuckPoints(@NotNull ItemStack stack) {
        return stack.getItem() instanceof CopperBrushItem brushItem ? brushItem.getLuckPoints() : 0;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        loot.clear();

        if (tag.contains(LOOT_TABLE_TAG, NbtElement.STRING_TYPE)) {
            lootTableId = new Identifier(tag.getString(LOOT_TABLE_TAG));
        }

        if (tag.contains(LOOT_TAG)) {
            NbtList itemsList = tag.getList(LOOT_TAG, NbtElement.COMPOUND_TYPE);

            for (int i = 0; i < itemsList.size(); i++) {
                NbtCompound stackTag = itemsList.getCompound(i);
                loot.add(ItemStack.fromNbt(stackTag));
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        if (lootTableId != null) {
            tag.putString(LOOT_TABLE_TAG, lootTableId.toString());
        }

        if (hasLoot()) {
            NbtList itemsList = new NbtList();

            for (ItemStack stack : loot) {
                itemsList.add(stack.writeNbt(new NbtCompound()));
            }

            tag.put(LOOT_TAG, itemsList);
        }
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = new NbtCompound();
        NbtList itemsList = new NbtList();

        if (hasLoot()) {
            itemsList.add(getDisplayLootItem().writeNbt(new NbtCompound()));
        }

        tag.put(LOOT_TAG, itemsList);
        return tag;
    }

    /***
     * Should only be called on server side, otherwise will cause crash!
     * Check {@link net.minecraft.world.World#isClient world.isClient} before calling!
     */
    public void generateLoot(PlayerEntity player, ItemStack stack) {
        if (lootTableId != null) {
            LootContext.Builder builder = (new LootContext.Builder((ServerWorld) world))
                    .parameter(LootContextParameters.TOOL, stack)
                    .parameter(LootContextParameters.THIS_ENTITY, player)
                    .parameter(LootContextParameters.BLOCK_ENTITY, this)
                    .random(world.getRandom()).luck(player.getLuck() + getLuckPoints(stack));

            LootTable lootTable = world.getServer().getLootManager().getTable(lootTableId);
            List<ItemStack> list = lootTable.generateLoot(builder.build(ArcheologyMod.EXCAVATION_LOOT_CONTEXT_TYPE));

            lootTableId = null;
            loot.addAll(list);
            markDirty();
        }
    }

    public void dropLoot(ServerPlayerEntity player) {
        if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            for (ItemStack stack : loot) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }

            loot.clear();
        }
    }

    public boolean hasLoot() {
        return !loot.isEmpty();
    }

    public ItemStack getDisplayLootItem() {
        return loot.get(0);
    }
}
