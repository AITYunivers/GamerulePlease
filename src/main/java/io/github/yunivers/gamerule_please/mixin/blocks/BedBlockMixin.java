package io.github.yunivers.gamerule_please.mixin.blocks;

import io.github.yunivers.gamerule_please.config.Config;
import io.github.yunivers.gamerule_please.entity.EmptyBedEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedBlockMixin
{
    @Redirect(
        method = "onUse",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZ)Lnet/minecraft/world/explosion/Explosion;"
        )
    )
    public Explosion createExplosion(World instance, Entity source, double x, double y, double z, float power, boolean fire)
    {
        return instance.createExplosion(new EmptyBedEntity(instance), x, y, z, power, fire);
    }

    @Inject(
        method = "onUse",
        at = @At("HEAD"),
        cancellable = true
    )
    public void checkIfUse(World world, int x, int y, int z, PlayerEntity player, CallbackInfoReturnable<Boolean> cir)
    {
        if (!world.isRemote && !world.dimension.hasWorldSpawn() && !Config.Gamerules.misc.respawnBlocksExplode)
            cir.setReturnValue(true);
    }
}
