package io.github.yunivers.gamerule_please.mixin;

import io.github.yunivers.gamerule_please.GamerulePlease;
import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.world.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Dimension.class)
public class DimensionMixin
{
    @ModifyVariable(
        method = "getTimeOfDay",
        at = @At("HEAD"),
        argsOnly = true
    )
    public long getTimeOfDay(long time)
    {
        if (!Config.Gamerules.worldUpdates.doDaylightCycle && GamerulePlease.rollbackTime != null)
            return GamerulePlease.rollbackTime;
        return time;
    }
}
