package io.github.yunivers.gamerule_please.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(ServerWorld.class)
public class ServerWorldMixin extends World
{
    public ServerWorldMixin(WorldStorage storage, String name, Dimension dimension, long seed)
    {
        super(storage, name, dimension, seed);
    }

    @SuppressWarnings("rawtypes")
    @WrapOperation(
        method = "createExplosion",
        at = @At(
            value = "NEW",
            target = "(DDDFLjava/util/Set;)Lnet/minecraft/network/packet/s2c/play/ExplosionS2CPacket;"
        )
    )
    public ExplosionS2CPacket gameruleplease$createExplosion_sendExplosionSource(double x, double y, double z, float radius, Set affectedBlocks, Operation<ExplosionS2CPacket> original,
                                                                                 @Local(argsOnly = true) Entity source)
    {
        ExplosionS2CPacket packet = original.call(x, y, z, radius, affectedBlocks);
        packet.gameruleplease$setEntity(source);
        return packet;
    }
}
