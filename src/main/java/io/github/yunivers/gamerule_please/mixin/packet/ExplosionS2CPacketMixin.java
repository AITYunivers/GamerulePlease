package io.github.yunivers.gamerule_please.mixin.packet;

import io.github.yunivers.gamerule_please.interfaces.ExplosionPacketImpl;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Mixin(ExplosionS2CPacket.class)
public class ExplosionS2CPacketMixin implements ExplosionPacketImpl
{
    @Unique public int entityId;

    @Inject(
        method = "read",
        at = @At("TAIL")
    )
    public void gameruleplease$read_readEntityId(DataInputStream stream, CallbackInfo ci) throws IOException
    {
        entityId = stream.readInt();
    }

    @Inject(
        method = "write",
        at = @At("TAIL")
    )
    public void gameruleplease$write_writeEntityId(DataOutputStream stream, CallbackInfo ci) throws IOException
    {
        stream.writeInt(entityId);
    }

    @Override
    public void gameruleplease$setEntity(Entity entity)
    {
        entityId = entity.id;
    }

    @Override
    public int gameruleplease$getEntityId()
    {
        return entityId;
    }
}
