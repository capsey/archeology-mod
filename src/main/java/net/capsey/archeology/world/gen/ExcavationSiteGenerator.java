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
import net.minecraft.structure.StructurePieceType;
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

public class ExcavationSiteGenerator {

	static final Identifier STRUCTURE_ID = new Identifier(ArcheologyMod.MODID, "ancient_ruins/ancient_ruins_1");
	static final BlockPos GENERATION_OFFSET = new BlockPos(0, -9, 0);
	static final int UP_SCAN_LIMIT = 12;

	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, StructurePiecesHolder structurePiecesHolder, Random random) {
		structurePiecesHolder.addPiece(new ExcavationSiteGenerator.Piece(manager, STRUCTURE_ID, pos, rotation));
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
			// Moving underground (here because if at adding a piece, terrain will adjust)
			this.pos = this.pos.add(GENERATION_OFFSET);
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

			// Spawning structure
			return super.generate(world, accessor, generator, random, box, chunkPos, this.pos);
		}

		private void upScan(StructureWorldAccess world, BlockPos pos, Random random) {
			// Iterate over 20 blocks upwards
			for (int i = 0; i <= UP_SCAN_LIMIT; i++) {
				BlockPos p = pos.add(0, i, 0);
				BlockState state = world.getBlockState(p);
				
				// Stop at surface
				if (state.isAir()) {
					break;
				}

				// Closer to the limit, greater chance to replace
				float chance = 1.0F / (1 + UP_SCAN_LIMIT - i);

				if (canReplaceWithExcavationBlock(state) && random.nextFloat() < chance) {
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
			return !state.isOf(Blocks.OBSIDIAN) && !state.isIn(BlockTags.FEATURES_CANNOT_REPLACE);
		}

	}

}
