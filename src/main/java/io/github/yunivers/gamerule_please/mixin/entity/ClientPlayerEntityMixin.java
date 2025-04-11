package io.github.yunivers.gamerule_please.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.entity.player.ClientPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin
{
    @WrapOperation(
        method = "tickMovement",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/ClientPlayerEntity;screenDistortion:F",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0
        )
    )
    public void tickMovementNetherPortalDelay(ClientPlayerEntity instance, float value, Operation<Void> original)
    {
        instance.screenDistortion += 1f / Config.Gamerules.player.playersNetherPortalDelay;
    }
}
