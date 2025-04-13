package io.github.yunivers.gamerule_please.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.PlayerManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin
{
    @Inject(
        method = "respawnPlayer",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/ServerPlayerEntity;id:I",
            opcode = Opcodes.GETFIELD
        )
    )
    public void keepInventory(ServerPlayerEntity player, int dimensionId, CallbackInfoReturnable<ServerPlayerEntity> cir, @Local(ordinal = 1) ServerPlayerEntity newPlayer)
    {
        if (Config.Gamerules.player.keepInventory)
        {
            newPlayer.inventory = player.inventory;
            newPlayer.inventory.player = newPlayer;
            newPlayer.playerScreenHandler = player.playerScreenHandler;
        }
    }
}
