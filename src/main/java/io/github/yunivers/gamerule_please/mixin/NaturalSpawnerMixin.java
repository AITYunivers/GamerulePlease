package io.github.yunivers.gamerule_please.mixin;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.world.NaturalSpawner;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin
{
    @Inject(
        method = "tick",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void doMobSpawning(World world, boolean spawnAnimals, boolean spawnMonsters, CallbackInfoReturnable<Integer> cir)
    {
        if (!Config.Gamerules.mob.doMobSpawning)
            cir.setReturnValue(0);
    }
}
