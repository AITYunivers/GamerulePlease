package io.github.yunivers.gamerule_please.mixin.blocks;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin
{
    @Inject(
        method = "dropStack",
        at = @At("HEAD"),
        cancellable = true
    )
    public void dropStack(World world, int x, int y, int z, ItemStack itemStack, CallbackInfo ci)
    {
        if (!Config.Gamerules.drops.doTileDrops)
            ci.cancel();
    }
}
