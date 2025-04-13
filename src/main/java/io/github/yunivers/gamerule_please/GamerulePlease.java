package io.github.yunivers.gamerule_please;

import io.github.yunivers.gamerule_please.compat.retrocommands.RetroCommandsEntry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

public class GamerulePlease implements ModInitializer {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    public static final Logger LOGGER = NAMESPACE.getLogger();

    public static int currentDays;
    public static @Nullable Long rollbackTime;

    public static boolean hasRetroCommands = false;

    @Override
    public void onInitialize() {
        hasRetroCommands = FabricLoader.getInstance().isModLoaded("retrocommands");
        if (hasRetroCommands) {
            RetroCommandsEntry.init();
        }
    }
}
