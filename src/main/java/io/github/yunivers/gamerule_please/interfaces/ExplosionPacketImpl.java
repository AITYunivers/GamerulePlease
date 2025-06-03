package io.github.yunivers.gamerule_please.interfaces;

import net.minecraft.entity.Entity;
import net.modificationstation.stationapi.api.util.Util;

public interface ExplosionPacketImpl
{
    default void gameruleplease$setEntity(Entity entity)
    {
        Util.assertImpl();
    }

    default int gameruleplease$getEntityId()
    {
        return Util.assertImpl();
    }
}
