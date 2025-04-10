package io.github.yunivers.gamerule_please.mixin;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin
{
    @Inject(
        method = "dropInventory",
        at = @At("HEAD"),
        cancellable = true
    )
    public void keepInventory(CallbackInfo ci)
    {
        if (Config.Gamerules.player.keepInventory)
            ci.cancel();
    }
}
