package io.github.yunivers.gamerule_please.mixin.network;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientNetworkHandler.class)
public abstract class ClientNetworkHandlerMixin
{
    @Shadow protected abstract Entity getEntity(int id);

    @WrapOperation(
        method = "onExplosion",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;DDDF)Lnet/minecraft/world/explosion/Explosion;"
        )
    )
    public Explosion gameruleplease$onExplosion_setEntity(World world, Entity source, double x, double y, double z, float power, Operation<Explosion> original,
                                                          @Local(argsOnly = true) ExplosionS2CPacket packet)
    {
        return original.call(world, getEntity(packet.gameruleplease$getEntityId()), x, y, z, power);
    }
}
