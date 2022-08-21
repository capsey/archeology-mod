package net.capsey.archeology.advancement;

import com.google.gson.JsonObject;
import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.main.Blocks;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ExcavatedCriterion extends AbstractCriterion<ExcavatedCriterion.Conditions> {

    private static final Identifier ID = new Identifier(ArcheologyMod.MOD_ID, "excavated");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new ExcavatedCriterion.Conditions(playerPredicate);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, BlockState state) {
        this.trigger(player, conditions -> conditions.test(state));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public Conditions(EntityPredicate.Extended entity) {
            super(ExcavatedCriterion.ID, entity);
        }

        public boolean test(BlockState state) {
            return state.isIn(Blocks.EXCAVATION_BLOCKS_TAG);
        }

    }

}
