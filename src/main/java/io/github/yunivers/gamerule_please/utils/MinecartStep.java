package io.github.yunivers.gamerule_please.utils;

import net.minecraft.util.math.Vec3d;

public class MinecartStep
{
    final Vec3d position;
    final Vec3d movement;
    final float yRot;
    final float xRot;
    final float weight;
    //public static final PacketCodec<ByteBuf, Step> PACKET_CODEC;
    public static MinecartStep ZERO;

    public MinecartStep(Vec3d position, Vec3d movement, float yRot, float xRot, float weight) {
        this.position = position;
        this.movement = movement;
        this.yRot = yRot;
        this.xRot = xRot;
        this.weight = weight;
    }

    public Vec3d position() {
        return this.position;
    }

    public Vec3d movement() {
        return this.movement;
    }

    public float yRot() {
        return this.yRot;
    }

    public float xRot() {
        return this.xRot;
    }

    public float weight() {
        return this.weight;
    }

    static {
        //PACKET_CODEC = PacketCodec.tuple(Vec3d.PACKET_CODEC, Step::position, Vec3d.PACKET_CODEC, Step::movement, PacketCodecs.DEGREES, Step::yRot, PacketCodecs.DEGREES, Step::xRot, PacketCodecs.FLOAT, Step::weight, Step::new);
        ZERO = new MinecartStep(Vec3d.createCached(0, 0, 0), Vec3d.createCached(0, 0, 0), 0.0F, 0.0F, 0.0F);
    }
}
