package io.github.yunivers.gamerule_please.mixin.blocks;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlock.class)
public class JukeboxBlockMixin
{
    @Inject(
            method = "onBreak",
            at = @At("HEAD"),
            cancellable = true
    )
    public void dropBlockEntityInventory(World world, int x, int y, int z, CallbackInfo ci)
    {
        if (!Config.Gamerules.drops.doTileDrops)
        {
            ci.cancel();
            world.removeBlockEntity(x, y, z);
        }
    }
}
