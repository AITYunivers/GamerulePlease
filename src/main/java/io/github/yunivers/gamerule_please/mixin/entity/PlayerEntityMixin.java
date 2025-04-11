package io.github.yunivers.gamerule_please.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

    @WrapOperation(
        method = "onLanding",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/Entity;I)Z",
                ordinal = 0
        )
    )
    public boolean onLandingInject(LivingEntity instance, Entity damageSource, int amount, Operation<Boolean> original) {
        if (!Config.Gamerules.player.fallDamage)
            return false;
        return original.call(instance, damageSource, amount);
    }
}
