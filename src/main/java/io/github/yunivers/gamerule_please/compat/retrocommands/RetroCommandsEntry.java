package io.github.yunivers.gamerule_please.compat.retrocommands;

import com.matthewperiut.retrocommands.api.CommandRegistry;

public class RetroCommandsEntry {
    public static void init() {
        CommandRegistry.add(new GameruleCommand());
    }
}
