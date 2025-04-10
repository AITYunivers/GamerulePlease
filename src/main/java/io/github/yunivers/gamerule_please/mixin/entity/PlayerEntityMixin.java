package io.github.yunivers.gamerule_please.mixin.entity;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PlayerEntityMixin
{
    @Shadow public abstract boolean damage(Entity damageSource, int amount);

    @Inject(
        method = "canBreatheInWater",
        at = @At("HEAD"),
        cancellable = true
    )
    public void canBreatheInWater(CallbackInfoReturnable<Boolean> cir)
    {
        if (!Config.Gamerules.player.drowningDamage)
            cir.setReturnValue(true);
    }

    @Redirect(
        method = "onLanding",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/Entity;I)Z",
                ordinal = 0
        )
    )
    public boolean onLandingInject(LivingEntity instance, Entity damageSource, int amount) {
        if (Config.Gamerules.player.fallDamage)
            this.damage(damageSource, amount);
        return false;
    }
}
