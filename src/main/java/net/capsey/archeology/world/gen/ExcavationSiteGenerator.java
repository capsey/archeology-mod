package net.capsey.archeology.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
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
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ExcavationSiteGenerator {

	static final Identifier TOP_TEMPLATE = new Identifier("igloo/top");
	static final BlockPos OFFSET = new BlockPos(3, 5, 5);
	static final BlockPos OFFSET_FROM_TOP = BlockPos.ORIGIN;

	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, StructurePiecesHolder structurePiecesHolder, Random random) {
		structurePiecesHolder.addPiece(new ExcavationSiteGenerator.Piece(manager, TOP_TEMPLATE, pos, rotation, 0));
	}

	public static class Piece extends SimpleStructurePiece {

		public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, int yOffset) {
			super(StructurePieceType.IGLOO, 0, manager, identifier, identifier.toString(), createPlacementData(rotation), getPosOffset(pos, yOffset));
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
				.setPosition(ExcavationSiteGenerator.OFFSET)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		}

		private static BlockPos getPosOffset(BlockPos pos, int yOffset) {
			return pos.add(ExcavationSiteGenerator.OFFSET_FROM_TOP).down(yOffset);
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putString("Rot", this.placementData.getRotation().name());
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
			if ("chest".equals(metadata)) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
				BlockEntity blockEntity = world.getBlockEntity(pos.down());

				if (blockEntity instanceof ChestBlockEntity chestEntity) {
					chestEntity.setLootTable(LootTables.IGLOO_CHEST_CHEST, random.nextLong());
				}
			}
		}

		@Override
		public boolean generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
			// StructurePlacementData data = createPlacementData(this.placementData.getRotation());
			
			// BlockPos pos0 = new BlockPos(3 - OFFSET_FROM_TOP.getX(), 0, -OFFSET_FROM_TOP.getZ());
			// BlockPos pos1 = this.pos.add(Structure.transform(data, pos0));
			// int y = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, pos1.getX(), pos1.getZ());

			// BlockPos temp = this.pos;
			// this.pos = this.pos.add(0, y - 90 - 1, 0);
			
			boolean result = super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
			// Identifier identifier = new Identifier(this.identifier);

			// if (identifier.equals(ExcavationSiteGenerator.TOP_TEMPLATE)) {
				// BlockPos pos2 = this.pos.add(Structure.transform(data, new BlockPos(3, 0, 5)));
				// BlockState blockState = world.getBlockState(pos2.down());

				// if (!blockState.isAir() && !blockState.isOf(Blocks.LADDER)) {
				// 	world.setBlockState(pos2, Blocks.SNOW_BLOCK.getDefaultState(), Block.NOTIFY_ALL);
				// }
			// }

			// this.pos = temp;
			return result;
		}

	}

}
