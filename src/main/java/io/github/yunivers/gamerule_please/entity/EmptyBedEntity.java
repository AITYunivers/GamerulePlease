package io.github.yunivers.gamerule_please.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

// Never used as an actual entity, just exists to indicate to CreateExplosion that it's a bed
public class EmptyBedEntity extends Entity {
    public EmptyBedEntity(World world) {
        super(world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeNbt(NbtCompound nbt) {

    }
}
