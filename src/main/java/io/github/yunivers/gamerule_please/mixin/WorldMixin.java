package io.github.yunivers.gamerule_please.mixin;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin
{
    @ModifyConstant(
        method = "manageChunkUpdatesAndEvents",
        constant = @Constant(intValue = 80)
    )
    private int getRandomTickSpeed(int value) {
        return Config.Gamerules.worldUpdates.randomTickSpeed;
    }

    @Inject(
        method = "updateSleepingPlayers",
        at = @At("HEAD"),
        cancellable = true
    )
    public void updateSleepingPlayers(CallbackInfo ci)
    {
        World world = (World)(Object)this;
        if (Config.Gamerules.multiplayer.playersSleepingPercentage == -1)
        {
            world.allPlayersSleeping = false;
            ci.cancel();
        }
        else if (Config.Gamerules.multiplayer.playersSleepingPercentage < 100 && !world.players.isEmpty())
        {
            int minSleep = (int)Math.ceil(world.players.size() * (Config.Gamerules.multiplayer.playersSleepingPercentage / 100d));
            int sleepingCount = 0;
            for (Object playerObj : world.players)
                if (playerObj instanceof PlayerEntity player && player.isSleeping())
                    sleepingCount++;
            world.allPlayersSleeping = sleepingCount >= minSleep;
            ci.cancel();
        }
    }
}
