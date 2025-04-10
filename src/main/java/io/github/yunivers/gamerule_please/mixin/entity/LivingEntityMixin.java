package io.github.yunivers.gamerule_please.mixin.entity;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(
        method = "dropItems",
        at = @At("HEAD"),
        cancellable = true
    )
    public void dropItems(CallbackInfo ci)
    {
        if (!Config.Gamerules.drops.doMobLoot)
            ci.cancel();
    }
}
