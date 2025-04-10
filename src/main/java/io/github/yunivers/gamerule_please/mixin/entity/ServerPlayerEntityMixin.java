package io.github.yunivers.gamerule_please.mixin.entity;

import net.minecraft.entity.player.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin
{
    @Redirect(
        method = "playerTick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/ServerPlayerEntity;changeDimensionCooldown:F",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0
        )
    )
    public void playerTickNetherPortalDelay(ServerPlayerEntity instance, float value)
    {
        instance.changeDimensionCooldown = value;
    }
}
