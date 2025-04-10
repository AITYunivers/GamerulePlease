package io.github.yunivers.gamerule_please.mixin.accessors;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public class MinecraftAccessor
{
    @Accessor("INSTANCE")
    static Minecraft getInstance()
    {
        throw new AssertionError();
    }
}
