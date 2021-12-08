package net.capsey.archeology.world.gen;

import java.util.Random;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.ArcheologyMod.Blocks;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;

public class ExcavationSiteGenerator {

	static final Identifier TOP_TEMPLATE = new Identifier(ArcheologyMod.MODID, "ancient_ruins/ancient_ruins_1");

	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, StructurePiecesHolder structurePiecesHolder, Random random) {
		structurePiecesHolder.addPiece(new ExcavationSiteGenerator.Piece(manager, TOP_TEMPLATE, pos, rotation));
	}

	public static class Piece extends SimpleStructurePiece {

		public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation) {
			super(StructurePieceType.IGLOO, 0, manager, identifier, identifier.toString(), createPlacementData(rotation), pos);
		}

		public Piece(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.IGLOO, nbt, world, identifier ->
				createPlacementData(BlockRotation.valueOf(nbt.getString("Rot")))
			);
		}

		private static StructurePlacementData createPlacementData(BlockRotation rotation) {
			return (new StructurePlacementData())
					.setRotation(rotation)
					.setMirror(BlockMirror.NONE)
					.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putString("Rot", this.placementData.getRotation().name());
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
			// Loot Pot has 30% chance to spawn from Data Structure Block (to provide variation in position)
			if ("loot_pot".equals(metadata) && (new Random(pos.asLong())).nextFloat() < 0.3) {
				world.setBlockState(pos, Blocks.CLAY_POT.getDefaultState(), Block.NOTIFY_LISTENERS);
				BlockEntity blockEntity = world.getBlockEntity(pos);

				if (blockEntity instanceof ClayPotBlockEntity potEntity) {
					potEntity.setLootTable(new Identifier(ArcheologyMod.MODID + ":loot_pot/ancient_ruins"), random.nextLong());
				}
			}
		}

	}

}
