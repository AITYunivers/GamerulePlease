package io.github.yunivers.gamerule_please.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireballEntity.class)
public class FireballEntityMixin
{
    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZ)Lnet/minecraft/world/explosion/Explosion;"
        )
    )
    public Explosion onUse(World instance, Entity source, double x, double y, double z, float power, boolean fire)
    {
        return instance.createExplosion((FireballEntity)(Object)this, x, y, z, power, fire);
    }
}
