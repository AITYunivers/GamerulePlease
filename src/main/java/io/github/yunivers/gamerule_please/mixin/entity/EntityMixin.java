package io.github.yunivers.gamerule_please.mixin.entity;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(
        method = "move",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;isWet()Z"
        ),
        cancellable = true
    )
    public void moveFireTick(double dy, double dz, double par3, CallbackInfo ci)
    {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof PlayerEntity && !Config.Gamerules.player.fireDamage)
            ci.cancel();
    }

    @Inject(
        method = "setOnFire",
        at = @At("HEAD"),
        cancellable = true
    )
    public void baseFireTick(CallbackInfo ci)
    {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof PlayerEntity && !Config.Gamerules.player.fireDamage)
            ci.cancel();
    }
}
