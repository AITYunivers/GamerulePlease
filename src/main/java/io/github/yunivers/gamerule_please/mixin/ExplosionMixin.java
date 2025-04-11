package io.github.yunivers.gamerule_please.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.yunivers.gamerule_please.config.Config;
import io.github.yunivers.gamerule_please.entity.EmptyBedEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class ExplosionMixin
{
    @Shadow public Entity source;

    @Shadow public boolean fire;

    @Inject(
        method = "explode",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"
        ),
        cancellable = true
    )
    public void explode(CallbackInfo ci)
    {
        if (fire && !Config.Gamerules.mob.mobGriefing && (source instanceof CreeperEntity || source instanceof FireballEntity))
            ci.cancel();
    }

    @WrapOperation(
        method = "playExplosionSound",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getBlockId(III)I"
        )
    )
    public int breakBlocks(World world, int x, int y, int z, Operation<Integer> original)
    {
        if (!Config.Gamerules.mob.mobGriefing && (source instanceof CreeperEntity || source instanceof FireballEntity))
            return 0;
        return original.call(world, x, y, z);
    }

    @WrapOperation(
        method = "playExplosionSound",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;dropStacks(Lnet/minecraft/world/World;IIIIF)V"
        )
    )
    public void doDropStacks(Block instance, World world, int x, int y, int z, int meta, float luck, Operation<Void> original)
    {
        if (!Config.Gamerules.drops.blockExplosionDropDecay && source instanceof EmptyBedEntity)
            luck = 1.0F;
        if (!Config.Gamerules.drops.mobExplosionDropDecay && (source instanceof CreeperEntity || source instanceof FireballEntity))
            luck = 1.0F;
        if (!Config.Gamerules.drops.tntExplosionDropDecay && source instanceof TntEntity)
            luck = 1.0F;
        original.call(instance, world, x, y, z, meta, luck);
    }
}
