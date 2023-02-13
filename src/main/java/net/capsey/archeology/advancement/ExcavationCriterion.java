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

public class ExcavationCriterion extends AbstractCriterion<ExcavationCriterion.Conditions> {

    private final Identifier id;

    public ExcavationCriterion(String id) {
        this.id = new Identifier(ArcheologyMod.MOD_ID, id);
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new ExcavationCriterion.Conditions(id, playerPredicate);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public void trigger(ServerPlayerEntity player, BlockState state) {
        this.trigger(player, conditions -> conditions.test(state));
    }

    public static class Conditions extends AbstractCriterionConditions {

        public Conditions(Identifier id, EntityPredicate.Extended entity) {
            super(id, entity);
        }

        public boolean test(BlockState state) {
            return state.isIn(Blocks.EXCAVATION_BLOCKS_TAG);
        }

    }

}
