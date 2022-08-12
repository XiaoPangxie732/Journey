package cn.maxpixel.mods.journey.coremod;

import cn.maxpixel.mods.journey.entity.StructureEntity;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class CoremodEntityGetter {
    public static boolean addStructureEntityCollisions(@Nullable Entity getterEntity, AABB box,
                                                       ImmutableList.Builder<VoxelShape> builder, Entity entity) {
        if (entity instanceof StructureEntity structureEntity) {
            builder.addAll(new BlockCollisions(structureEntity.getStructureLevel(), getterEntity, box.move(
                    structureEntity.getOriginPos().reverse()), structureEntity.getOriginPos()));
            return true;
        }
        return false;
    }

    private static class BlockCollisions extends AbstractIterator<VoxelShape> {
        private final AABB box;
        private final CollisionContext context;
        private final Cursor3D cursor;
        private final BlockPos.MutableBlockPos pos;
        private final VoxelShape entityShape;
        private final CollisionGetter collisionGetter;
        @Nullable
        private BlockGetter cachedBlockGetter;
        private long cachedBlockGetterPos;
        private final Vec3 originPos;

        public BlockCollisions(CollisionGetter collisionGetter, @Nullable Entity entity, AABB box, Vec3 originPos) {
            this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
            this.originPos = originPos;
            this.pos = new BlockPos.MutableBlockPos();
            this.entityShape = Shapes.create(box);
            this.collisionGetter = collisionGetter;
            this.box = box;
            int i = Mth.floor(box.minX - 1.0E-7D) - 1;
            int j = Mth.floor(box.maxX + 1.0E-7D) + 1;
            int k = Mth.floor(box.minY - 1.0E-7D) - 1;
            int l = Mth.floor(box.maxY + 1.0E-7D) + 1;
            int i1 = Mth.floor(box.minZ - 1.0E-7D) - 1;
            int j1 = Mth.floor(box.maxZ + 1.0E-7D) + 1;
            this.cursor = new Cursor3D(i, k, i1, j, l, j1);
        }

        @Nullable
        private BlockGetter getChunk(int blockX, int blockZ) {
            int chunkX = SectionPos.blockToSectionCoord(blockX);
            int chunkZ = SectionPos.blockToSectionCoord(blockZ);
            long chunkPos = ChunkPos.asLong(chunkX, chunkZ);
            if (this.cachedBlockGetter != null && this.cachedBlockGetterPos == chunkPos) {
                return this.cachedBlockGetter;
            } else {
                BlockGetter blockgetter = this.collisionGetter.getChunkForCollisions(chunkX, chunkZ);
                this.cachedBlockGetter = blockgetter;
                this.cachedBlockGetterPos = chunkPos;
                return blockgetter;
            }
        }

        protected VoxelShape computeNext() {
            while(true) {
                if (cursor.advance()) {
                    int x = cursor.nextX();
                    int y = cursor.nextY();
                    int z = cursor.nextZ();
                    int type = cursor.getNextType();
                    if (type == Cursor3D.TYPE_CORNER) {
                        continue;
                    }

                    BlockGetter blockGetter = getChunk(x, z);
                    if (blockGetter == null) {
                        continue;
                    }

                    pos.set(x, y, z);
                    BlockState state = blockGetter.getBlockState(pos);
                    if (type == Cursor3D.TYPE_FACE && !state.hasLargeCollisionShape() ||
                            type == Cursor3D.TYPE_EDGE && !state.is(Blocks.MOVING_PISTON)) {
                        continue;
                    }

                    VoxelShape collisionShape = state.getCollisionShape(collisionGetter, pos, context);
                    if (collisionShape == Shapes.block()) {
                        if (!box.intersects(x, y, z, x + 1, y + 1, z + 1)) {
                            continue;
                        }

                        return collisionShape.move(x + originPos.x, y + originPos.y, z + originPos.z);
                    }

                    VoxelShape collisionShape1 = collisionShape.move(x, y, z);
                    if (!Shapes.joinIsNotEmpty(collisionShape1, entityShape, BooleanOp.AND)) {
                        continue;
                    }

                    return collisionShape1.move(originPos.x, originPos.y, originPos.z);
                }

                return endOfData();
            }
        }
    }
}