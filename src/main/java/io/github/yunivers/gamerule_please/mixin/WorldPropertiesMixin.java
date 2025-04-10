package io.github.yunivers.gamerule_please.mixin;

import io.github.yunivers.gamerule_please.GamerulePlease;
import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.WorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldProperties.class)
public abstract class WorldPropertiesMixin
{
    @Shadow public abstract long getTime();

    @Inject(
        method = "setTime",
        at = @At("HEAD"),
        cancellable = true
    )
    public void trySetTime(long time, CallbackInfo ci)
    {
        long dayCheck = getTime() + 24000L;
        if (!Config.Gamerules.worldUpdates.doDaylightCycle && time == getTime() + 1) // So beds and mods like AMI can still set the time
            ci.cancel();
        else if (time < getTime() || time == dayCheck - dayCheck % 24000L)
            GamerulePlease.currentDays++;
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