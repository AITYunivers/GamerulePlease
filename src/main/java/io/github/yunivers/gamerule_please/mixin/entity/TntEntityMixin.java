package io.github.yunivers.gamerule_please.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TntEntity.class)
public class TntEntityMixin
{
    @Redirect(
        method = "explode",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDF)Lnet/minecraft/world/explosion/Explosion;"
        )
    )
    public Explosion onUse(World instance, Entity source, double x, double y, double z, float power)
    {
        return instance.createExplosion((TntEntity)(Object)this, x, y, z, power);
    }
}
