package io.github.yunivers.gamerule_please.mixin.entity;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin
{
    @Inject(
        method = "renderNameTag(Lnet/minecraft/entity/LivingEntity;DDD)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void renderNameTag(LivingEntity entity, double dx, double dy, double dz, CallbackInfo ci)
    {
        if (Config.Gamerules.misc.reducedDebugInfo)
            ci.cancel();
    }
}
