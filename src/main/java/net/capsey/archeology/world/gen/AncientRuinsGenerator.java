package net.capsey.archeology.world.gen;

import java.util.Iterator;
import java.util.Random;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class AncientRuinsGenerator {

	static final Identifier STRUCTURE_TOP_ID = new Identifier(ArcheologyMod.MODID, "ancient_ruins/ancient_ruins_top");
	static final Identifier STRUCTURE_ID = new Identifier(ArcheologyMod.MODID, "ancient_ruins/ancient_ruins_1");

	static final int UP_SCAN_LIMIT = 16;
	static final BlockPos STRUCTURE_TOP_OFFSET = new BlockPos(2, 0, 1);
	static final BlockPos STRUCTURE_OFFSET = new BlockPos(0, -12, 0);

	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, StructurePiecesHolder structurePiecesHolder, Random random) {
		structurePiecesHolder.addPiece(new AncientRuinsGenerator.Piece(manager, STRUCTURE_ID, pos.add(STRUCTURE_OFFSET), rotation));
		structurePiecesHolder.addPiece(new AncientRuinsGenerator.Piece(manager, STRUCTURE_TOP_ID, pos.add(STRUCTURE_TOP_OFFSET.rotate(rotation)), rotation));
	}

	public static class Piece extends SimpleStructurePiece {

		public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation) {
			super(ArcheologyMod.Features.ANCIENT_RUINS_PIECE, 0, manager, identifier, identifier.toString(), createPlacementData(rotation), pos);
		}

		public Piece(ServerWorld world, NbtCompound nbt) {
			super(ArcheologyMod.Features.ANCIENT_RUINS_PIECE, nbt, world, identifier ->
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
			if ("loot_pot".equals(metadata) && (new Random(pos.asLong())).nextFloat() < 0.3F) {
				world.setBlockState(pos, ArcheologyMod.Blocks.CLAY_POT.getDefaultState(), Block.NOTIFY_LISTENERS);
				BlockEntity blockEntity = world.getBlockEntity(pos);

				if (blockEntity instanceof ClayPotBlockEntity potEntity) {
					potEntity.setLootTable(new Identifier(ArcheologyMod.MODID, "loot_pot/ancient_ruins"), random.nextLong());
				}
			}
		}

		@Override
		public boolean generate(StructureWorldAccess world, StructureAccessor accessor, ChunkGenerator generator, Random random, BlockBox box, ChunkPos chunkPos, BlockPos pos) {
			if (!STRUCTURE_TOP_ID.toString().equals(this.identifier)) {
				// Moving underground (here because if at adding a piece, terrain will adjust)
				this.placementData.setBoundingBox(boundingBox);
				this.boundingBox = this.structure.calculateBoundingBox(this.placementData, this.pos);

				// Replacing some blocks to Excavation Block
				BlockPos start = new BlockPos(this.boundingBox.getMinX(), this.boundingBox.getMinY() - 1, this.boundingBox.getMinZ());
				BlockPos end = new BlockPos(this.boundingBox.getMaxX(), this.boundingBox.getMinY() - 1, this.boundingBox.getMaxZ());
				Iterator<BlockPos> iterator = BlockPos.iterate(start, end).iterator();

				while (iterator.hasNext()) {
					BlockPos current = iterator.next();
					this.upScan(world, current, random);
				}
			}

			// Spawning structure
			return super.generate(world, accessor, generator, random, box, chunkPos, this.pos);
		}

		private void upScan(StructureWorldAccess world, BlockPos pos, Random random) {
			// Find surface level
			int n = UP_SCAN_LIMIT;

			for (int i = 1; i <= UP_SCAN_LIMIT; i++) {
				BlockPos p = pos.add(0, i, 0);
				BlockState state = world.getBlockState(p);
				
				if (state.isAir()) {
					n = i;
					break;
				}
			}

			float hn = n / (2.0F - 0.5F);

			// Replace until surface
			for (int i = 0; i <= n; i++) {
				BlockPos p = pos.add(0, i, 0);
				BlockState state = world.getBlockState(p);
				float chance = (i == n) ? 1 : Math.abs((i - hn) / hn);

				if (canReplaceWithExcavationBlock(state) && random.nextFloat() < chance * 0.6F) {
					// Placing block and configuring its loot table
					BlockState newState = (state.isOf(Blocks.STONE) ? ArcheologyMod.Blocks.EXCAVATION_GRAVEL : ArcheologyMod.Blocks.EXCAVATION_DIRT).getDefaultState();
					world.setBlockState(p, newState, Block.NOTIFY_LISTENERS);
					BlockEntity blockEntity = world.getBlockEntity(p);
					
					if (blockEntity instanceof ExcavationBlockEntity excEntity) {
						excEntity.setLootTable(ExcavationBlockEntity.DEFAULT_LOOT_TABLE);
					}
				}
			}
		}

		private static boolean canReplaceWithExcavationBlock(BlockState state) {
			return state.isOpaque() && !state.isOf(Blocks.OBSIDIAN) && !state.isIn(BlockTags.FEATURES_CANNOT_REPLACE);
		}

	}

}
