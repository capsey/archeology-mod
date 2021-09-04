package net.capsey.archeology.blocks;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ExcavationBlockEntity extends BlockEntity {

    public ItemStack loot;

    private boolean generated = false;

    private Identifier lootTableId;
	private long lootTableSeed;

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(ArcheologyMod.EXCAVATION_BLOCK_ENTITY, pos, state);
    }

    // Serialize the BlockEntity
    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
 
        tag.putString("LootTable", lootTableId.toString());
        tag.putLong("LootTableSeed", lootTableSeed);
 
        return tag;
    }

    // Deserialize the BlockEntity
    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        
        lootTableId = new Identifier(tag.getString("LootTable"));
        lootTableSeed = tag.getLong("LootTableSeed");
    }

    public boolean isLootGenerated() {
        return generated;
    }

    public void generateLoot(PlayerEntity player) {
        // LootTable table = this.world.getServer().getLootManager().getTable(this.lootTableId);
        // LootContext.Builder builder = (new LootContext.Builder((ServerWorld)this.world)).luck(player.getLuck()).random(lootTableSeed);
        // LootContext context = builder.build(LootContextTypes.GIFT);
        // loot = table.generateLoot(context).get(0);

        loot = new ItemStack(Items.DIAMOND_BLOCK, 1);
        generated = true;
    }

    public ItemStack getLoot() {
        return loot;
    }
    
}
