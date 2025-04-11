package io.github.yunivers.gamerule_please.mixin.blocks;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.yunivers.gamerule_please.config.Config;
import net.minecraft.block.FlowingLiquidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(FlowingLiquidBlock.class)
public class FlowingLiquidBlockMixin
{
    // Wow this SUCKS
    @WrapOperation(
        method = "onTick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/block/FlowingLiquidBlock;material:Lnet/minecraft/block/material/Material;",
            opcode = Opcodes.GETFIELD
        )
    )
    private Material checkSourceConversion(FlowingLiquidBlock instance, Operation<Material> original) {
        if (Config.Gamerules.worldUpdates.waterSourceConversion && instance.material == Material.WATER)
            return Material.WATER;
        else if (Config.Gamerules.worldUpdates.lavaSourceConversion && instance.material == Material.LAVA)
            return Material.WATER;
        else
            return Material.LAVA;
    }

    @ModifyExpressionValue(
        method = "onTick",
        at = @At(
            value = "CONSTANT",
            args = "intValue=1",
            ordinal = 0
        )
    )
    private int getSpreadSpeed(int original, @Local(argsOnly = true) World world)
    {
        FlowingLiquidBlock liquid = (FlowingLiquidBlock)(Object)this;
        return liquid.material == Material.LAVA && !world.dimension.evaporatesWater ? 2 : original;
    }
}
