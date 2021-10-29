package net.capsey.archeology.blocks.excavation_block;

import java.util.ArrayList;
import java.util.List;

import net.capsey.archeology.ArcheologyMod;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;

public class FossilContainerBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    
    private static final float[] LUCK_POINTS = { 1.0F, 2.0F, 3.0F, 4.0F };

    private static float getLuckPoints(ItemStack stack) {
        if (!stack.isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
            return 0.0F;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        index = Math.max(Math.min(index, LUCK_POINTS.length - 1), 0);

        return LUCK_POINTS[index];
    }

    protected Identifier lootTableId;
    protected ArrayList<ItemStack> loot = new ArrayList<ItemStack>();

    public FossilContainerBlockEntity(BlockPos pos, BlockState state) {
        super(ArcheologyMod.BlockEntities.EXCAVATION_BLOCK_ENTITY, pos, state);
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

    public void generateLoot(PlayerEntity player, ItemStack stack) {
        if (!world.isClient) {
            LootContext.Builder builder = (new LootContext.Builder((ServerWorld)this.world))
                .parameter(LootContextParameters.TOOL, stack)
                .parameter(LootContextParameters.THIS_ENTITY, player)
                .parameter(LootContextParameters.BLOCK_ENTITY, this)
                .random(this.world.getRandom()).luck(player.getLuck() + getLuckPoints(stack));
            
            LootTable lootTable = this.world.getServer().getLootManager().getTable(lootTableId);
            List<ItemStack> list = lootTable.generateLoot(builder.build(ArcheologyMod.EXCAVATION_LOOT_CONTEXT_TYPE));
            
            loot.addAll(list);
            this.markDirty();
            this.sync();
        }
    }

    public void dropLoot() {
        for (ItemStack stack : loot) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
    }
    
    public boolean isLootGenerated() {
        return !loot.isEmpty();
    }

    public ItemStack getOneLootItem() {
        return loot.get(0);
    }

}
