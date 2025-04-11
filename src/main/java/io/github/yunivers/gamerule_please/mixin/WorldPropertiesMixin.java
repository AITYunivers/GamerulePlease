package io.github.yunivers.gamerule_please.mixin;

import io.github.yunivers.gamerule_please.GamerulePlease;
import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.WorldProperties;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProperties.class)
public abstract class WorldPropertiesMixin
{
    @Unique
    private @Nullable Long rollbackTime = null;
    @Unique
    private boolean lastTick = false;

    @Shadow private long time;

    @Inject(
        method = "getTime",
        at = @At("HEAD"),
        cancellable = true
    )
    public void restoreGetTime(CallbackInfoReturnable<Long> cir)
    {
        if (!Config.Gamerules.worldUpdates.doDaylightCycle && rollbackTime != null) // Reverts the time when doDaylightCycle is disabled
            cir.setReturnValue(rollbackTime);
    }

    // Easier to read so suppress the warnings
    @SuppressWarnings({"DuplicateCondition", "ConstantValue"})
    @Inject(
        method = "setTime",
        at = @At("HEAD"),
        cancellable = true
    )
    public void restoreSetTime(long time, CallbackInfo ci)
    {
        if (!Config.Gamerules.worldUpdates.doDaylightCycle && rollbackTime != null && lastTick) // Reverts the time when doDaylightCycle is disabled
        {
            this.time = rollbackTime;
            rollbackTime = null;
            lastTick = false;
            ci.cancel();
        }
        else if (Config.Gamerules.worldUpdates.doDaylightCycle && rollbackTime == null)
        {
            rollbackTime = this.time;
            ci.cancel();
        }
        else if (Config.Gamerules.worldUpdates.doDaylightCycle)
            lastTick = true;
        else if (!Config.Gamerules.worldUpdates.doDaylightCycle)
            rollbackTime = time;
    }

    @Inject(
        method = "setThunderTime",
        at = @At("HEAD"),
        cancellable = true
    )
    public void trySetThunderTime(int thunderTime, CallbackInfo ci)
    {
        if (!Config.Gamerules.worldUpdates.doWeatherCycle) // Mods should be using setThundering
            ci.cancel();
    }

    @Inject(
        method = "setRainTime",
        at = @At("HEAD"),
        cancellable = true
    )
    public void trySetRainTime(int rainTime, CallbackInfo ci)
    {
        if (!Config.Gamerules.worldUpdates.doWeatherCycle) // Mods should be using setRaining
            ci.cancel();
    }

    @Inject(
        method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V",
        at = @At("TAIL")
    )
    public void readDaysNbt(NbtCompound nbt, CallbackInfo ci)
    {
        if (nbt.contains("days"))
            GamerulePlease.currentDays = nbt.getInt("days");
        else
            GamerulePlease.currentDays = 0;
    }

    @Inject(
        method = "updateProperties",
        at = @At("TAIL")
    )
    public void writeDaysNbt(NbtCompound nbt, NbtCompound playerNbt, CallbackInfo ci)
    {
        nbt.putInt("days", GamerulePlease.currentDays);
    }
}