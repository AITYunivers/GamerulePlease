package io.github.yunivers.gamerule_please.mixin.blocks;

import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.TntBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(TntBlock.class)
public class TntBlockMixin
{
    @Inject(
        method = "onMetadataChange",
        at = @At("HEAD"),
        cancellable = true
    )
    public void onMetadataChange(World world, int x, int y, int z, int meta, CallbackInfo ci) {
        if (!Config.Gamerules.misc.tntExplodes)
            ci.cancel();
    }

    @Redirect(
            method = {"onPlaced", "neighborUpdate"},
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/World;setBlock(IIII)Z"
            )
    )
    public boolean removeSetBlock(World instance, int x, int y, int z, int blockId)
    {
        if (!Config.Gamerules.misc.tntExplodes)
            return false;
        return instance.setBlock(x, y, z, blockId);
    }

    @Inject(
        method = "getDroppedItemCount",
        at = @At("RETURN"),
        cancellable = true
    )
    public void getDroppedItemCount(Random random, CallbackInfoReturnable<Integer> cir)
    {
        if (!Config.Gamerules.misc.tntExplodes)
            cir.setReturnValue(1);
    }

    @Inject(
        method = "onDestroyedByExplosion",
        at = @At("HEAD"),
        cancellable = true
    )
    public void onDestroyedByExplosion(World x, int y, int z, int par4, CallbackInfo ci)
    {
        if (!Config.Gamerules.misc.tntExplodes)
            ci.cancel();
    }
}
