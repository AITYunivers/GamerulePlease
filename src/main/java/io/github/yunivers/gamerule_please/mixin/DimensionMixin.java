package io.github.yunivers.gamerule_please.mixin;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.world.dimension.Dimension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Dimension.class)
public class DimensionMixin
{
    @Unique
    private @Nullable Float previousTime = null;

    @Inject(
        method = "getTimeOfDay",
        at = @At("HEAD"),
        cancellable = true
    )
    public void getTimeOfDay(long time, float tickDelta, CallbackInfoReturnable<Float> cir)
    {
        if (!Config.Gamerules.worldUpdates.doDaylightCycle && previousTime != null)
            cir.setReturnValue(previousTime);
    }

    @Inject(
        method = "getTimeOfDay",
        at = @At("RETURN")
    )
    public void storeTimeOfDay(long time, float tickDelta, CallbackInfoReturnable<Float> cir)
    {
        previousTime = cir.getReturnValue();
    }
}
