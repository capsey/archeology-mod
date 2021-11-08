package net.capsey.archeology.items;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.capsey.archeology.items.ceramic_shard.CeramicShard;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class CeramicShardItem extends Item {

	private final CeramicShard shard;

    public CeramicShardItem(CeramicShard shard, Settings settings) {
        super(settings);
		this.shard = shard;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(this.getDescription().formatted(Formatting.GRAY));
	}

	public MutableText getDescription() {
		return new TranslatableText(this.getTranslationKey() + ".desc");
	}

	public CeramicShard getShard() {
		return shard;
	}
    
}
