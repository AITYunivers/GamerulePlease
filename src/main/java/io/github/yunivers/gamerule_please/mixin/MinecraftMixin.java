package io.github.yunivers.gamerule_please.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.client.InteractionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
	@Shadow public ClientPlayerEntity player;

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
			ordinal = 0
		)
	)
	public void onDead(CallbackInfo ci)
	{
		Minecraft minecraft = (Minecraft)(Object)this;
		if (Config.Gamerules.player.doImmediateRespawn)
			minecraft.player.respawn();
	}

	@WrapOperation(
		method = "respawnPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/InteractionManager;createPlayer(Lnet/minecraft/world/World;)Lnet/minecraft/entity/player/PlayerEntity;"
		)
	)
	public PlayerEntity keepInventory(InteractionManager instance, World world, Operation<PlayerEntity> original)
	{
		if (Config.Gamerules.player.keepInventory)
		{
			Minecraft minecraft = (Minecraft)(Object)this;
			ClientPlayerEntity player = new ClientPlayerEntity(minecraft, world, minecraft.session, world.dimension.id);
			player.inventory = minecraft.player.inventory;
			player.inventory.player = player;
			player.playerScreenHandler = minecraft.player.playerScreenHandler;
			return player;
		}
		return original.call(instance, world);
	}

	@Inject(
		method = "renderProfilerChart",
		at = @At(value = "HEAD"),
		cancellable = true
	)
	private void disableLagometer(CallbackInfo ci) {
		if (Config.Gamerules.misc.reducedDebugInfo) // This shouldn't conflict with BetterF3?
			ci.cancel();
	}
}
