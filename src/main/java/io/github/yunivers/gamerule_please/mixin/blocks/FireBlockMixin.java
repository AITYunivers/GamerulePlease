package io.github.yunivers.gamerule_please.mixin.blocks;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.block.FireBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@SuppressWarnings("DiscouragedShift")
@Mixin(FireBlock.class)
public class FireBlockMixin
{
    @Inject(
        method = "onTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;isRaining()Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void validateFireTick(World world, int x, int y, int z, Random random, CallbackInfo ci)
    {
        if (!Config.Gamerules.worldUpdates.doFireTick)
            ci.cancel();
    }
}
