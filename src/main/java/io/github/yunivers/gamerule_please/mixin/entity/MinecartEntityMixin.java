package io.github.yunivers.gamerule_please.mixin.entity;

import com.mojang.datafixers.util.Pair;
import io.github.yunivers.gamerule_please.config.Config;
import io.github.yunivers.gamerule_please.utils.MinecartMoveIteration;
import io.github.yunivers.gamerule_please.utils.MinecartStep;
import io.github.yunivers.gamerule_please.utils.QueuedCollisionCheck;
import io.github.yunivers.gamerule_please.utils.RailShape;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Mixin(MinecartEntity.class)
public class MinecartEntityMixin
{
    @Unique
    private boolean firstUpdate;

    @Unique
    private boolean onRail;

    @Unique
    private Vec3d lastPosition;

    @Unique
    public final List<MinecartStep> stagingLerpSteps = new LinkedList();

    public MinecartEntityMixin()
    {
        this.queuedCollisionChecks = new ObjectArrayList<>();
        this.currentlyCheckedCollisions = new ObjectArrayList<>();
    }

    @Unique
    public double getRemainingSpeed()
    {
        return 0.4d * ((Config.Gamerules.misc.minecartMaxSpeed % 8d) / 8d);
    }

    /**
     * @author GamerulePlease by Yunivers
     * @reason Sorry other mods, movement needs to be ran through steps to fix flying off rails so I needed to rewrite the entire Minecart
     */
    /*@Overwrite
    public void tick()
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        lastPosition = Vec3d.create(minecart.x, minecart.y, minecart.z);
        World world = minecart.world;
        *//*if (world instanceof ServerWorld serverWorld) {
            BlockPos pos = this.getRailOrMinecartPos();
            BlockState blockState = minecart.world.getBlockState(pos);
            if (firstUpdate) {
                onRail = RailBlock.isRail(blockState.getBlock().id);
                this.adjustToRail(world, pos, blockState, true);
            }

            this.applyGravity();
            this.moveOnRail(serverWorld);
        } else {
            //this.tickClient();
            //boolean bl = AbstractRailBlock.isRail(this.getWorld().getBlockState(this.minecart.getRailOrMinecartPos()));
            //this.minecart.setOnRail(bl);
        }*//*
    }*/

    @Unique
    private BlockPos getRailOrMinecartPos()
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        Vec3d vec3 = minecart.snapPositionToRail(minecart.x, minecart.y, minecart.z);
        return new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z);
    }

    @Unique
    private void adjustToRail(World world, BlockPos pos, BlockState blockState, boolean ignoreWeight)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        if (RailBlock.isRail(blockState.getBlock().id)) {
            RailShape railShape = getRailShape(world, pos);
            int[][] pair = MinecartEntity.ADJACENT_RAIL_POSITIONS_BY_SHAPE[world.getBlockMeta(pos.x, pos.y, pos.z)];
            Vec3d head = Vec3d.createCached(pair[0][0] * 0.5d, pair[0][1] * 0.5d, pair[0][2] * 0.5d);
            Vec3d tail = Vec3d.createCached(pair[1][0] * 0.5d, pair[1][1] * 0.5d, pair[1][2] * 0.5d);
            Vec3d horiHead = Vec3d.createCached(head.x, 0, head.z);
            Vec3d horiTail = Vec3d.createCached(tail.x, 0, tail.z);
            Vec3d velocity = Vec3d.create(minecart.velocityX, minecart.velocityY, minecart.velocityZ);
            if (velocity.length() > (double)1.0E-5F && dotProduct(velocity, horiHead) < dotProduct(velocity, horiTail) || this.ascends(horiTail, railShape)) {
                Vec3d vec3d5 = horiHead;
                horiHead = horiTail;
                horiTail = vec3d5;
            }

            float yaw = 180.0F - (float)(Math.atan2(horiHead.z, horiHead.x) * (double)180.0F / Math.PI);
            yaw += minecart.yawFlipped ? 180.0F : 0.0F;
            Vec3d position = Vec3d.create(minecart.x, minecart.y, minecart.z);
            boolean curve = head.x != tail.x && head.z != tail.z;
            Vec3d center;
            if (curve) {
                Vec3d diff = Vec3d.create(tail.x - head.x, tail.y - head.y, tail.z - head.z);
                Vec3d btm = toBottomCenterPos(pos);
                Vec3d rem = Vec3d.create(position.x - btm.x - head.x, position.y - btm.y - head.y, position.z - btm.z - head.z);
                double dot1 = dotProduct(diff, rem);
                double dot2 = dotProduct(diff, diff);
                double dotDivi = dot1 / dot2;
                Vec3d dot = Vec3d.create(diff.x * dotDivi, diff.y * dotDivi, diff.z * dotDivi);
                center = Vec3d.create(btm.x + head.x + dot.x, btm.z + head.z + dot.z, btm.z + head.z + dot.z);
                yaw = 180.0F - (float)(Math.atan2(dot.z, dot.x) * (double)180.0F / Math.PI);
                yaw += minecart.yawFlipped ? 180.0F : 0.0F;
            } else {
                boolean headTailDiffX = (head.x - tail.x) != (double)0.0F;
                boolean headTailDiffZ = (head.z - tail.z) != (double)0.0F;
                center = Vec3d.create(headTailDiffZ ? toCenterPos(pos).x : position.x, pos.getY(), headTailDiffX ? toCenterPos(pos).z : position.z);
            }

            Vec3d worldPos = Vec3d.create(center.x - position.x, center.y - position.y, center.z - position.z);
            minecart.x = position.x + worldPos.x;
            minecart.y = position.y + worldPos.y;
            minecart.z = position.z + worldPos.z;
            Vec3d newPosition = Vec3d.create(minecart.x, minecart.y, minecart.z);
            float pitch = 0.0F;
            boolean headTailDiffY = head.y != tail.y;
            if (headTailDiffY) {
                Vec3d btmTail = toBottomCenterPos(pos).add(horiTail.x, horiTail.y, horiTail.z);
                double dist = btmTail.distanceTo(position);
                newPosition.y = minecart.y = position.y + dist + 0.1d;
                pitch = minecart.yawFlipped ? 45.0F : -45.0F;
            } else {
                newPosition.y = minecart.y = position.y + 0.1d;
            }

            minecart.setRotation(yaw, pitch);
            double distMoved = position.distanceTo(newPosition);
            if (distMoved > (double)0.0F) {
                this.stagingLerpSteps.add(new MinecartStep(newPosition, velocity, minecart.yaw, minecart.pitch, ignoreWeight ? 0.0F : (float)distMoved));
            }
        }
    }

    @Unique
    private RailShape getRailShape(World world, BlockPos pos)
    {
        int meta = world.getBlockMeta(pos.x, pos.y, pos.z);
        return switch (meta)
        {
            case 0 -> RailShape.NORTH_SOUTH;
            case 1 -> RailShape.EAST_WEST;
            case 2 -> RailShape.ASCENDING_EAST;
            case 3 -> RailShape.ASCENDING_WEST;
            case 4 -> RailShape.ASCENDING_NORTH;
            case 5 -> RailShape.ASCENDING_SOUTH;
            case 6 -> RailShape.NORTH_EAST;
            case 7 -> RailShape.NORTH_WEST;
            case 8 -> RailShape.SOUTH_WEST;
            default -> RailShape.SOUTH_EAST;
        };
    }

    @Unique
    private double dotProduct(Vec3d vec3d1, Vec3d vec3d2)
    {
        return vec3d1.x * vec3d2.x + vec3d1.y * vec3d2.y + vec3d1.z * vec3d2.z;
    }

    @Unique
    private boolean ascends(Vec3d velocity, RailShape railShape) {
        boolean doesAscend;
        switch (railShape) {
            case ASCENDING_EAST -> doesAscend = velocity.x < (double)0.0F;
            case ASCENDING_WEST -> doesAscend = velocity.x > (double)0.0F;
            case ASCENDING_NORTH -> doesAscend = velocity.z > (double)0.0F;
            case ASCENDING_SOUTH -> doesAscend = velocity.z < (double)0.0F;
            default -> doesAscend = false;
        }

        return doesAscend;
    }

    @Unique
    private Vec3d toBottomCenterPos(BlockPos pos)
    {
        return Vec3d.create(pos.x + 0.5d, pos.y, pos.z + 0.5d);
    }

    @Unique
    private Vec3d toCenterPos(BlockPos pos)
    {
        return Vec3d.create(pos.x + 0.5d, pos.y + 0.5d, pos.z + 0.5d);
    }

    @Unique
    private void applyGravity()
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        minecart.velocityY -= minecart.isWet() ? 0.005 : 0.04;
    }

    @Unique
    private Vec3d getHorizontal(Vec3d vec)
    {
        return Vec3d.create(vec.x, 0.0, vec.z);
    }

    @Unique
    @Environment(EnvType.SERVER)
    private void moveOnRail(ServerWorld world)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        for (MinecartMoveIteration moveIteration = new MinecartMoveIteration(); moveIteration.shouldContinue() && minecart.isAlive(); moveIteration.initial = false)
        {
            Vec3d velocity = Vec3d.create(minecart.velocityX, minecart.velocityY, minecart.velocityZ);
            BlockPos blockPos = this.getRailOrMinecartPos();
            BlockState blockState = minecart.world.getBlockState(blockPos);
            boolean onRail = RailBlock.isRail(blockState.getBlock().id);
            if (this.onRail != onRail)
            {
                this.onRail = onRail;
                this.adjustToRail(world, blockPos, blockState, false);
            }


            if (onRail)
            {
                minecart.onLanding(0f);
                lastPosition = Vec3d.create(minecart.x, minecart.y, minecart.z);

                RailShape railShape = getRailShape(world, blockPos);
                Vec3d vec3d2 = this.calcNewHorizontalVelocity(world, getHorizontal(velocity), moveIteration, blockPos, blockState, railShape);
                if (moveIteration.initial)
                    moveIteration.remainingMovement = horizontalLength(vec3d2);
                else
                    moveIteration.remainingMovement += horizontalLength(vec3d2) - horizontalLength(velocity);

                minecart.velocityX = vec3d2.x;
                minecart.velocityY = vec3d2.y;
                minecart.velocityZ = vec3d2.z;
                moveIteration.remainingMovement = moveAlongTrack(blockPos, railShape, moveIteration.remainingMovement);
            }
            else
            {
                moveOffRail();
                moveIteration.remainingMovement = (double)0.0F;
            }

            Vec3d vec3d3 = Vec3d.create(minecart.x, minecart.y, minecart.z);
            Vec3d vec3d2 = vec3d3.add(-lastPosition.x, -lastPosition.y, -lastPosition.z);
            double speed = vec3d2.length();
            Vec3d velocity2 = Vec3d.create(minecart.velocityX, minecart.velocityY, minecart.velocityZ);
            if (speed > (double)1.0E-5F) {
                if (!(horizontalLengthSquared(vec3d2) > (double)1.0E-5F))
                {
                    if (!this.onRail)
                        minecart.pitch = minecart.onGround ? 0.0F : lerpAngleDegrees(0.2F, minecart.pitch, 0.0F);
                }
                else
                {
                    float yaw = 180.0F - (float)(Math.atan2(vec3d2.z, vec3d2.x) * (double)180.0F / Math.PI);
                    float pitch = minecart.onGround && !this.onRail ? 0.0F : 90.0F - (float)(Math.atan2(horizontalLength(vec3d2), vec3d2.y) * (double)180.0F / Math.PI);
                    yaw += minecart.yawFlipped ? 180.0F : 0.0F;
                    pitch *= minecart.yawFlipped ? -1.0F : 1.0F;
                    minecart.setRotation(yaw, pitch);
                }

                this.stagingLerpSteps.add(new MinecartStep(vec3d3, velocity2, minecart.yaw, minecart.pitch, (float)Math.min(speed, this.getMaxSpeed())));
            }
            else if (horizontalLengthSquared(velocity) > (double)0.0F)
                this.stagingLerpSteps.add(new MinecartStep(vec3d3, velocity2, minecart.yaw, minecart.pitch, 1.0F));

            if (speed > (double)1.0E-5F || moveIteration.initial) {
                tickBlockCollision();
                tickBlockCollision();
            }
        }
    }

    private final List<List<QueuedCollisionCheck>> queuedCollisionChecks;
    private final List<QueuedCollisionCheck> currentlyCheckedCollisions;

    @Unique
    private void tickBlockCollision()
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        this.currentlyCheckedCollisions.clear();
        for (List<QueuedCollisionCheck> checks : queuedCollisionChecks)
            this.currentlyCheckedCollisions.addAll(checks);
        this.queuedCollisionChecks.clear();
        Vec3d pos = Vec3d.create(minecart.x, minecart.y, minecart.z);
        if (this.currentlyCheckedCollisions.isEmpty()) {
            this.currentlyCheckedCollisions.add(new QueuedCollisionCheck(lastPosition, pos));
        } else if (this.currentlyCheckedCollisions.get(currentlyCheckedCollisions.size() - 1).to.squaredDistanceTo(pos) > 0.0000000001) {
            this.currentlyCheckedCollisions.add(new QueuedCollisionCheck((this.currentlyCheckedCollisions.get(currentlyCheckedCollisions.size() - 1)).to, pos));
        }

        this.tickBlockCollisions(this.currentlyCheckedCollisions);
    }

    @Unique
    private void tickBlockCollisions(List<QueuedCollisionCheck> checks)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        if (this.shouldTickBlockCollision())
        {
            if (minecart.onGround)
            {
                BlockPos blockPos = new BlockPos((int)minecart.x, (int)(minecart.y - 0.2), (int)minecart.z);
                BlockState blockState = minecart.world.getBlockState(blockPos);
                blockState.getBlock().onSteppedOn(minecart.world, blockPos.x, blockPos.y, blockPos.z, minecart);
            }

            boolean bl = minecart.isOnFire();
            //minecart.checkBlockCollision(checks, minecart.collisionHandler);
            //minecart.collisionHandler.runCallbacks(this);
            if (minecart.world.isRaining((int)minecart.x, (int)minecart.y, (int)minecart.z))
                minecart.fireTicks = 0;

            //if (bl && !minecart.isOnFire())
            //    minecart.playExtinguishSound();

            if (bl && !minecart.isOnFire() && minecart.fireTicks <= 0)
                minecart.fireTicks = -1;
        }
    }

    @Unique
    private boolean shouldTickBlockCollision()
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        return minecart.isAlive() && !minecart.noClip;
    }

    @Unique
    private static float lerpAngleDegrees(float delta, float start, float end)
    {
        return start + delta * wrapDegrees(end - start);
    }

    @Unique
    private static float wrapDegrees(float degrees) {
        float f = degrees % 360.0F;
        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    @Unique
    private void moveOffRail()
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        double maxSpeed = this.getMaxSpeed();
        minecart.velocityX = clamp(minecart.velocityX, -maxSpeed, maxSpeed);
        minecart.velocityZ = clamp(minecart.velocityZ, -maxSpeed, maxSpeed);
        if (minecart.onGround)
        {
            minecart.velocityX *= 0.5;
            minecart.velocityY *= 0.5;
            minecart.velocityZ *= 0.5;
        }

        this.move(Vec3d.create(minecart.velocityX, minecart.velocityY, minecart.velocityZ));
        if (!minecart.onGround)
        {
            minecart.velocityX *= 0.95;
            minecart.velocityY *= 0.95;
            minecart.velocityZ *= 0.95;
        }
    }

    @Unique
    private static double clamp(double value, double min, double max)
    {
        return value < min ? min : Math.min(value, max);
    }

    @Unique
    private double moveAlongTrack(BlockPos blockPos, RailShape railShape, double remainingMovement)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        if (remainingMovement < (double)1.0E-5F)
            return (double)0.0F;
        else
        {
            Vec3d vec3d = Vec3d.create(minecart.x, minecart.y, minecart.z);
            int[][] pair = MinecartEntity.ADJACENT_RAIL_POSITIONS_BY_SHAPE[minecart.world.getBlockMeta(blockPos.x, blockPos.y, blockPos.z)];
            Vec3i vec3i = new Vec3i(pair[0][0], pair[0][1], pair[0][2]);
            Vec3i vec3i2 = new Vec3i(pair[1][0], pair[1][1], pair[1][2]);
            Vec3d vec3d2 = getHorizontal(Vec3d.create(minecart.velocityX, minecart.velocityY, minecart.velocityZ));
            if (vec3d2.length() < (double)1.0E-5F) {
                minecart.velocityX = 0;
                minecart.velocityY = 0;
                minecart.velocityZ = 0;
                return (double)0.0F;
            } else {
                boolean bl = vec3i.y != vec3i2.y;
                Vec3d vec3d3 = Vec3d.create(vec3i2.x, vec3i2.y, vec3i2.z);
                vec3d3.x *= 0.5;
                vec3d3.y *= 0.5;
                vec3d3.z *= 0.5;
                vec3d3 = getHorizontal(vec3d3);
                Vec3d vec3d4 = Vec3d.create(vec3i.x, vec3i.y, vec3i.z);
                vec3d4.x *= 0.5;
                vec3d4.y *= 0.5;
                vec3d4.z *= 0.5;
                vec3d4 = getHorizontal(vec3d4);
                if (dotProduct(vec3d2, vec3d4) < dotProduct(vec3d2, vec3d3)) {
                    vec3d4 = vec3d3;
                }

                Vec3d vec3d5mult = vec3d4.normalize();
                vec3d5mult.x *= 0.00001;
                vec3d5mult.y *= 0.00001;
                vec3d5mult.z *= 0.00001;
                Vec3d vec3d5 = toBottomCenterPos(blockPos).add(vec3d4.x, vec3d4.y + 0.1, vec3d4.z).add(vec3d5mult.x, vec3d5mult.y, vec3d5mult.z);
                if (bl && !this.ascends(vec3d2, railShape)) {
                    vec3d5 = vec3d5.add((double)0.0F, (double)1.0F, (double)0.0F);
                }

                Vec3d vec3d6 = vec3d5.add(-minecart.x, -minecart.y, -minecart.z).normalize();
                double multBy = vec3d2.length() / horizontalLength(vec3d6);
                vec3d2 = vec3d6.add(0, 0, 0);
                vec3d2.x *= multBy;
                vec3d2.y *= multBy;
                vec3d2.z *= multBy;
                multBy = remainingMovement * (double)(bl ? MathHelper.sqrt(2) : 1.0F);
                Vec3d vec3d7mult = vec3d2.normalize();
                vec3d7mult.x *= multBy;
                vec3d7mult.y *= multBy;
                vec3d7mult.z *= multBy;
                Vec3d vec3d7 = vec3d.add(vec3d7mult.x, vec3d7mult.y, vec3d7mult.z);
                if (vec3d.squaredDistanceTo(vec3d5) <= vec3d.squaredDistanceTo(vec3d7)) {
                    remainingMovement = horizontalLength(vec3d5.add(-vec3d7.x, -vec3d7.y, -vec3d7.z));
                    vec3d7 = vec3d5;
                } else {
                    remainingMovement = (double)0.0F;
                }

                move(vec3d7.add(-vec3d.x, -vec3d.y, -vec3d.z));
                BlockPos blockPos1 = new BlockPos((int)vec3d7.x, (int)vec3d7.y, (int)vec3d7.z);
                BlockState blockState = minecart.world.getBlockState(blockPos1);
                if (bl) {
                    if (RailBlock.isRail(blockState.getBlock().id)) {
                        RailShape railShape2 = getRailShape(minecart.world, blockPos1);
                        if (this.restOnVShapedTrack(railShape, railShape2)) {
                            return (double)0.0F;
                        }
                    }

                    Vec3d position = Vec3d.create(minecart.x, minecart.y, minecart.z);
                    double d = getHorizontal(vec3d5).distanceTo(getHorizontal(position));
                    double e = vec3d5.y + (this.ascends(vec3d2, railShape) ? d : -d);
                    if (position.y < e) {
                        minecart.y = e;
                    }
                }

                Vec3d position = Vec3d.create(minecart.x, minecart.y, minecart.z);
                if (position.distanceTo(vec3d) < (double)1.0E-5F && vec3d7.distanceTo(vec3d) > (double)1.0E-5F) {
                    minecart.velocityX = 0;
                    minecart.velocityY = 0;
                    minecart.velocityZ = 0;
                    return (double)0.0F;
                } else {
                    minecart.velocityX = vec3d2.x;
                    minecart.velocityY = vec3d2.y;
                    minecart.velocityZ = vec3d2.z;
                    return remainingMovement;
                }
            }
        }
    }

    @Unique
    private boolean restOnVShapedTrack(RailShape currentRailShape, RailShape newRailShape)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        Vec3d velocity = Vec3d.create(minecart.x, minecart.y, minecart.z);
        if (lengthSquared(velocity) < 0.005 && newRailShape.isAscending() && this.ascends(velocity, currentRailShape) && !this.ascends(velocity, newRailShape))
        {
            minecart.velocityX = 0;
            minecart.velocityY = 0;
            minecart.velocityZ = 0;
            return true;
        }
        return false;
    }

    @Unique
    private void move(Vec3d movement)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        Vec3d vec3d = Vec3d.create(minecart.x + movement.x, minecart.y + movement.y, minecart.z + movement.z);
        minecart.move(movement.x, movement.y, movement.z);
        boolean bl = handleCollision();
        if (bl)
            minecart.move(vec3d.x - minecart.x, vec3d.y - minecart.y, vec3d.z - minecart.z);
    }

    @Unique
    private boolean handleCollision()
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        boolean bl = this.pickUpEntities(minecart.getBoundingBox().expand(0.2, (double)0.0F, 0.2));
        if (!minecart.horizontalCollision && !minecart.verticalCollision) {
            return false;
        } else {
            boolean bl2 = this.pushAwayFromEntities(minecart.getBoundingBox().expand(0.0000001, 0.0000001, 0.0000001));
            return bl && !bl2;
        }
    }

    @Unique
    private boolean pickUpEntities(Box box)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        if (minecart.passenger == null)
        {
            List list = minecart.world.getEntities(minecart, box);
            if (!list.isEmpty())
            {
                for (Object entity : list)
                {
                    if (entity instanceof Entity tEntity && !(entity instanceof PlayerEntity) && !(entity instanceof MinecartEntity) && minecart.passenger == null && !tEntity.hasVehicle())
                    {
                        tEntity.setVehicle(minecart);
                        if (tEntity.hasVehicle() && minecart.passenger != null)
                            return true;
                    }
                }
            }
        }

        return false;
    }

    @Unique
    private boolean pushAwayFromEntities(Box box)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        boolean bl = false;
        if (minecart.passenger != null)
        {
            List list = minecart.world.getEntities(minecart, box);
            if (!list.isEmpty())
            {
                for (Object entity : list)
                {
                    Entity tEntity = (Entity)entity;
                    if (entity instanceof PlayerEntity || entity instanceof MinecartEntity || minecart.passenger != null || tEntity.hasVehicle()) {
                        pushAwayFrom(tEntity, minecart);
                        bl = true;
                    }
                }
            }
        }
        else
        {
            for (Object entity2 : minecart.world.getEntities(minecart, box))
            {
                Entity tEntity = (Entity)entity2;
                if (minecart.passenger != tEntity && tEntity.isPushable() && entity2 instanceof MinecartEntity)
                {
                    pushAwayFrom(tEntity, minecart);
                    bl = true;
                }
            }
        }

        return bl;
    }

    @Unique
    private void pushAwayFrom(Entity entity, Entity minecart)
    {
        if (!minecart.noClip && !entity.noClip)
        {
            double d = minecart.x - entity.x;
            double e = minecart.z - entity.z;
            double f = MathHelper.absMax(d, e);
            if (f >= (double)0.01F)
            {
                f = Math.sqrt(f);
                d /= f;
                e /= f;
                double g = (double)1.0F / f;
                if (g > (double)1.0F) {
                    g = (double)1.0F;
                }

                d *= g;
                e *= g;
                d *= (double)0.05F;
                e *= (double)0.05F;
                if (!(entity instanceof MinecartEntity entityCart && entityCart.passenger != null) && entity.isPushable()) {
                    entity.addVelocity(-d, (double)0.0F, -e);
                }

                if (minecart.passenger == null && minecart.isPushable()) {
                    minecart.addVelocity(d, (double)0.0F, e);
                }
            }

        }
    }

    @Unique
    private double getMaxSpeed()
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        return Config.Gamerules.misc.minecartMaxSpeed * (minecart.isWet() ? (double)0.5F : (double)1.0F) / (double)20.0F;
    }

    @Unique
    @Environment(EnvType.SERVER)
    private Vec3d calcNewHorizontalVelocity(ServerWorld world, Vec3d horizontalVelocity, MinecartMoveIteration iteration, BlockPos pos, BlockState railState, RailShape railShape)
    {
        Vec3d vec3d = horizontalVelocity;
        if (!iteration.slopeVelocityApplied) {
            Vec3d vec3d2 = this.applySlopeVelocity(horizontalVelocity, railShape);
            if (horizontalLengthSquared(vec3d2) != horizontalLengthSquared(horizontalVelocity)) {
                iteration.slopeVelocityApplied = true;
                vec3d = vec3d2;
            }
        }

        if (iteration.initial) {
            Vec3d vec3d2 = this.applyInitialVelocity(vec3d);
            if (horizontalLengthSquared(vec3d2) != horizontalLengthSquared(vec3d)) {
                iteration.decelerated = true;
                vec3d = vec3d2;
            }
        }

        if (!iteration.decelerated) {
            Vec3d vec3d2 = this.decelerateFromPoweredRail(vec3d, railState);
            if (horizontalLengthSquared(vec3d2) != horizontalLengthSquared(vec3d)) {
                iteration.decelerated = true;
                vec3d = vec3d2;
            }
        }

        if (iteration.initial) {
            vec3d = applySlowdown(vec3d);
            if (lengthSquared(vec3d) > (double)0.0F) {
                double speed = Math.min(vec3d.length(), getMaxSpeed());
                vec3d = vec3d.normalize();
                vec3d.x *= speed;
                vec3d.y *= speed;
                vec3d.z *= speed;
            }
        }

        if (!iteration.accelerated) {
            Vec3d vec3d2 = this.accelerateFromPoweredRail(vec3d, pos, railState);
            if (horizontalLengthSquared(vec3d2) != horizontalLengthSquared(vec3d)) {
                iteration.accelerated = true;
                vec3d = vec3d2;
            }
        }

        return vec3d;
    }

    @Unique
    private double horizontalLength(Vec3d vec)
    {
        return Math.sqrt(vec.x * vec.x + vec.z * vec.z);
    }

    @Unique
    private double horizontalLengthSquared(Vec3d vec)
    {
        return vec.x * vec.x + vec.z * vec.z;
    }

    @Unique
    private double lengthSquared(Vec3d vec)
    {
        return vec.x * vec.x + vec.y * vec.y + vec.z * vec.z;
    }

    @Unique
    private Vec3d applySlopeVelocity(Vec3d horizontalVelocity, RailShape railShape)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        double d = Math.max((double)0.0078125F, horizontalLength(horizontalVelocity) * 0.02);
        if (minecart.isWet()) {
            d *= 0.2;
        }

        Vec3d slopeVelocity;
        switch (railShape) {
            case ASCENDING_EAST -> slopeVelocity = horizontalVelocity.add(-d, (double)0.0F, (double)0.0F);
            case ASCENDING_WEST -> slopeVelocity = horizontalVelocity.add(d, (double)0.0F, (double)0.0F);
            case ASCENDING_NORTH -> slopeVelocity = horizontalVelocity.add((double)0.0F, (double)0.0F, d);
            case ASCENDING_SOUTH -> slopeVelocity = horizontalVelocity.add((double)0.0F, (double)0.0F, -d);
            default -> slopeVelocity = horizontalVelocity;
        }

        return slopeVelocity;
    }

    // This doesn't do anything bc players cant control minecarts in beta
    @Unique
    private Vec3d applyInitialVelocity(Vec3d horizontalVelocity)
    {
        return horizontalVelocity;
        /*MinecartEntity minecart = (MinecartEntity)(Object)this;
        if (minecart.passenger instanceof ServerPlayerEntity serverPlayerEntity) {
            Vec3d vec3d = serverPlayerEntity.getInputVelocityForMinecart();
            if (lengthSquared(vec3d) > (double)0.0F) {
                Vec3d vec3d2 = vec3d.normalize();
                double d = horizontalLengthSquared(horizontalVelocity);
                if (lengthSquared(vec3d2) > (double)0.0F && d < 0.01) {
                    Vec3d normalized = horizontalVelocity.add((Vec3d.create(vec3d2.x, (double)0.0F, vec3d2.z)).normalize();
                    normalized.x *= 0.001;
                    normalized.y *= 0.001;
                    normalized.z *= 0.001;
                    return normalized;
                }
            }

            return horizontalVelocity;
        } else {
            return horizontalVelocity;
        }*/
    }

    @Unique
    private Vec3d decelerateFromPoweredRail(Vec3d velocity, BlockState railState)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        if (railState.isOf(Block.POWERED_RAIL) && (minecart.world.getBlockMeta((int)minecart.x, (int)minecart.y, (int)minecart.z) & 8) == 0)
        {
            if (velocity.length() < 0.03)
                return Vec3d.createCached(0, 0, 0);
            Vec3d multipliedVelocity = velocity.add(0, 0, 0);
            multipliedVelocity.x *= 0.5;
            multipliedVelocity.y *= 0.5;
            multipliedVelocity.z *= 0.5;
            return multipliedVelocity;
        }
        else
            return velocity;
    }

    @Unique
    private Vec3d accelerateFromPoweredRail(Vec3d velocity, BlockPos railPos, BlockState railState)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        if (railState.isOf(Block.POWERED_RAIL) && (minecart.world.getBlockMeta(railPos.x, railPos.y, railPos.z) & 8) != 0) {
            if (velocity.length() > 0.01)
            {
                Vec3d multipliedVelocity = velocity.normalize();
                double multiBy = velocity.length() + 0.06;
                multipliedVelocity.x *= multiBy;
                multipliedVelocity.y *= multiBy;
                multipliedVelocity.z *= multiBy;
                return multipliedVelocity;
            }
            else {
                Vec3d vec3d = getLaunchDirection(railPos);
                if (lengthSquared(vec3d) <= (double)0.0F)
                    return velocity;
                double multBy = velocity.length() + 0.2;
                vec3d.x *= multBy;
                vec3d.y *= multBy;
                vec3d.z *= multBy;
                return vec3d;
            }
        } else {
            return velocity;
        }
    }

    @Unique
    private Vec3d getLaunchDirection(BlockPos railPos)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        BlockState blockState = minecart.world.getBlockState(railPos);
        if (blockState.isOf(Block.POWERED_RAIL) && (minecart.world.getBlockMeta(railPos.x, railPos.y, railPos.z) & 8) != 0) {
            RailShape railShape = getRailShape(minecart.world, railPos);
            if (railShape == RailShape.EAST_WEST) {
                if (this.willHitBlockAt(railPos.west(), 5)) {
                    return Vec3d.create((double)1.0F, (double)0.0F, (double)0.0F);
                }

                if (this.willHitBlockAt(railPos.east(), 4)) {
                    return Vec3d.create((double)-1.0F, (double)0.0F, (double)0.0F);
                }
            } else if (railShape == RailShape.NORTH_SOUTH) {
                if (this.willHitBlockAt(railPos.north(), 3)) {
                    return Vec3d.create((double)0.0F, (double)0.0F, (double)1.0F);
                }

                if (this.willHitBlockAt(railPos.south(), 2)) {
                    return Vec3d.create((double)0.0F, (double)0.0F, (double)-1.0F);
                }
            }

            return Vec3d.createCached(0, 0, 0);
        } else {
            return Vec3d.createCached(0, 0, 0);
        }
    }

    @Unique
    private boolean willHitBlockAt(BlockPos pos, int face)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        return Block.BLOCKS[minecart.world.getBlockId(pos.x, pos.y, pos.z)].isSolidFace(minecart.world, pos.x, pos.y, pos.z, face);
    }

    @Unique
    protected Vec3d applySlowdown(Vec3d velocity)
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        double d = getSpeedRetention();
        Vec3d vec3d = velocity.add(0, 0, 0);
        vec3d.x *= d;
        vec3d.y *= 0;
        vec3d.z *= d;
        if (minecart.isWet())
        {
            vec3d.x *= 0.95;
            vec3d.y *= 0.95;
            vec3d.z *= 0.95;
        }

        return vec3d;
    }

    @Unique
    public double getSpeedRetention()
    {
        MinecartEntity minecart = (MinecartEntity)(Object)this;
        return minecart.passenger != null ? 0.997 : 0.975;
    }
}
