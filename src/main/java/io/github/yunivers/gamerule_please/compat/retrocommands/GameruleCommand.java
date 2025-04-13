package io.github.yunivers.gamerule_please.compat.retrocommands;

import com.matthewperiut.retrocommands.api.Command;
import com.matthewperiut.retrocommands.util.SharedCommandSource;
import io.github.yunivers.gamerule_please.config.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Scanner;

public class GameruleCommand implements Command {
    public static final String[] ALL_CONFIG_NAMES = {
            // GamerulesPlayer
            "disablePlayerMovementCheck",
            "doImmediateRespawn",
            "drowningDamage",
            "fallDamage",
            "fireDamage",
            "keepInventory",
            "playersNetherPortalDelay",

            // GamerulesMob
            "doMobSpawning",
            "mobGriefing",
            "universalAnger",
            "forgiveDeadPlayers",

            // GamerulesDrops
            "doMobLoot",
            "doTileDrops",
            "projectilesCanBreakBlocks",
            "blockExplosionDropDecay",
            "mobExplosionDropDecay",
            "tntExplosionDropDecay",

            // GamerulesWorldUpdates
            "doDaylightCycle",
            "doWeatherCycle",
            "doFireTick",
            "randomTickSpeed",
            "lavaSourceConversion",
            "waterSourceConversion",

            // GamerulesMultiplayer
            "logAdminCommands",
            "sendCommandFeedback",
            "playersSleepingPercentage",

            // GamerulesMisc
            "reducedDebugInfo",
            "showCoordinates",
            "showDaysPlayed",
            "minecartMaxSpeed",
            "tntExplodes",
            "respawnBlocksExplode"
    };

    public static boolean updateValue(String name, Boolean value) {
        switch (name) {
            // GamerulesPlayer
            case "disablePlayerMovementCheck" -> Config.Gamerules.player.disablePlayerMovementCheck = value;
            case "doImmediateRespawn" -> Config.Gamerules.player.doImmediateRespawn = value;
            case "drowningDamage" -> Config.Gamerules.player.drowningDamage = value;
            case "fallDamage" -> Config.Gamerules.player.fallDamage = value;
            case "fireDamage" -> Config.Gamerules.player.fireDamage = value;
            case "keepInventory" -> Config.Gamerules.player.keepInventory = value;

            // GamerulesMob
            case "doMobSpawning" -> Config.Gamerules.mob.doMobSpawning = value;
            case "mobGriefing" -> Config.Gamerules.mob.mobGriefing = value;
            case "universalAnger" -> Config.Gamerules.mob.universalAnger = value;
            case "forgiveDeadPlayers" -> Config.Gamerules.mob.forgiveDeadPlayers = value;

            // GamerulesDrops
            case "doMobLoot" -> Config.Gamerules.drops.doMobLoot = value;
            case "doTileDrops" -> Config.Gamerules.drops.doTileDrops = value;
            case "projectilesCanBreakBlocks" -> Config.Gamerules.drops.projectilesCanBreakBlocks = value;
            case "blockExplosionDropDecay" -> Config.Gamerules.drops.blockExplosionDropDecay = value;
            case "mobExplosionDropDecay" -> Config.Gamerules.drops.mobExplosionDropDecay = value;
            case "tntExplosionDropDecay" -> Config.Gamerules.drops.tntExplosionDropDecay = value;

            // GamerulesWorldUpdates
            case "doDaylightCycle" -> Config.Gamerules.worldUpdates.doDaylightCycle = value;
            case "doWeatherCycle" -> Config.Gamerules.worldUpdates.doWeatherCycle = value;
            case "doFireTick" -> Config.Gamerules.worldUpdates.doFireTick = value;
            case "lavaSourceConversion" -> Config.Gamerules.worldUpdates.lavaSourceConversion = value;
            case "waterSourceConversion" -> Config.Gamerules.worldUpdates.waterSourceConversion = value;

            // GamerulesMultiplayer
            case "logAdminCommands" -> Config.Gamerules.multiplayer.logAdminCommands = value;
            case "sendCommandFeedback" -> Config.Gamerules.multiplayer.sendCommandFeedback = value;

            // GamerulesMisc
            case "reducedDebugInfo" -> Config.Gamerules.misc.reducedDebugInfo = value;
            case "showCoordinates" -> Config.Gamerules.misc.showCoordinates = value;
            case "showDaysPlayed" -> Config.Gamerules.misc.showDaysPlayed = value;
            case "tntExplodes" -> Config.Gamerules.misc.tntExplodes = value;
            case "respawnBlocksExplode" -> Config.Gamerules.misc.respawnBlocksExplode = value;

            default -> {
                return false;
            }
        }
        return true;
    }

    public static boolean updateValue(SharedCommandSource cs, String name, Integer value) {
        switch (name) {
            // GamerulesPlayer
            case "playersNetherPortalDelay" -> {
                if (value < 0) {
                    cs.sendFeedback("Value must be above 0");
                    return false;
                } else if (value > Integer.MAX_VALUE) {
                    cs.sendFeedback("Value must be below " + Integer.MAX_VALUE);
                    return false;
                }
                Config.Gamerules.player.playersNetherPortalDelay = value;
            }
            // GamerulesWorldUpdates
            case "randomTickSpeed" -> {
                if (value < 0) {
                    cs.sendFeedback("Value must be above 0");
                    return false;
                } else if (value > Integer.MAX_VALUE) {
                    cs.sendFeedback("Value must be below " + Integer.MAX_VALUE);
                    return false;
                }
                Config.Gamerules.worldUpdates.randomTickSpeed = value;
            }
            // GamerulesMultiplayer
            case "playersSleepingPercentage" -> {
                if (value < -1) {
                    cs.sendFeedback("Value must be above -1");
                    return false;
                } else if (value > 100) {
                    cs.sendFeedback("Value must be below 100");
                    return false;
                }
                Config.Gamerules.multiplayer.playersSleepingPercentage = value;
            }
            // GamerulesMisc
            case "minecartMaxSpeed" -> {
                if (value < 1) {
                    cs.sendFeedback("Value must be above 1");
                    return false;
                } else if (value > 2000) {
                    cs.sendFeedback("Value must be below 2000");
                    return false;
                }
                Config.Gamerules.misc.minecartMaxSpeed = value;
            }

            default -> {
                return false;
            }
        }
        return true;
    }

    public static String getValue(String name) {
        switch (name) {
            // === Boolean entries ===

            // GamerulesPlayer
            case "disablePlayerMovementCheck" -> {
                return Config.Gamerules.player.disablePlayerMovementCheck.toString();
            }
            case "doImmediateRespawn" -> {
                return Config.Gamerules.player.doImmediateRespawn.toString();
            }
            case "drowningDamage" -> {
                return Config.Gamerules.player.drowningDamage.toString();
            }
            case "fallDamage" -> {
                return Config.Gamerules.player.fallDamage.toString();
            }
            case "fireDamage" -> {
                return Config.Gamerules.player.fireDamage.toString();
            }
            case "keepInventory" -> {
                return Config.Gamerules.player.keepInventory.toString();
            }

            // GamerulesMob
            case "doMobSpawning" -> {
                return Config.Gamerules.mob.doMobSpawning.toString();
            }
            case "mobGriefing" -> {
                return Config.Gamerules.mob.mobGriefing.toString();
            }
            case "universalAnger" -> {
                return Config.Gamerules.mob.universalAnger.toString();
            }
            case "forgiveDeadPlayers" -> {
                return Config.Gamerules.mob.forgiveDeadPlayers.toString();
            }

            // GamerulesDrops
            case "doMobLoot" -> {
                return Config.Gamerules.drops.doMobLoot.toString();
            }
            case "doTileDrops" -> {
                return Config.Gamerules.drops.doTileDrops.toString();
            }
            case "projectilesCanBreakBlocks" -> {
                return Config.Gamerules.drops.projectilesCanBreakBlocks.toString();
            }
            case "blockExplosionDropDecay" -> {
                return Config.Gamerules.drops.blockExplosionDropDecay.toString();
            }
            case "mobExplosionDropDecay" -> {
                return Config.Gamerules.drops.mobExplosionDropDecay.toString();
            }
            case "tntExplosionDropDecay" -> {
                return Config.Gamerules.drops.tntExplosionDropDecay.toString();
            }

            // GamerulesWorldUpdates
            case "doDaylightCycle" -> {
                return Config.Gamerules.worldUpdates.doDaylightCycle.toString();
            }
            case "doWeatherCycle" -> {
                return Config.Gamerules.worldUpdates.doWeatherCycle.toString();
            }
            case "doFireTick" -> {
                return Config.Gamerules.worldUpdates.doFireTick.toString();
            }
            case "lavaSourceConversion" -> {
                return Config.Gamerules.worldUpdates.lavaSourceConversion.toString();
            }
            case "waterSourceConversion" -> {
                return Config.Gamerules.worldUpdates.waterSourceConversion.toString();
            }

            // GamerulesMultiplayer
            case "logAdminCommands" -> {
                return Config.Gamerules.multiplayer.logAdminCommands.toString();
            }
            case "sendCommandFeedback" -> {
                return Config.Gamerules.multiplayer.sendCommandFeedback.toString();
            }

            // GamerulesMisc
            case "reducedDebugInfo" -> {
                return Config.Gamerules.misc.reducedDebugInfo.toString();
            }
            case "showCoordinates" -> {
                return Config.Gamerules.misc.showCoordinates.toString();
            }
            case "showDaysPlayed" -> {
                return Config.Gamerules.misc.showDaysPlayed.toString();
            }
            case "tntExplodes" -> {
                return Config.Gamerules.misc.tntExplodes.toString();
            }
            case "respawnBlocksExplode" -> {
                return Config.Gamerules.misc.respawnBlocksExplode.toString();
            }

            // === Integer entries ===

            // GamerulesPlayer
            case "playersNetherPortalDelay" -> {
                return Config.Gamerules.player.playersNetherPortalDelay.toString();
            }

            // GamerulesWorldUpdates
            case "randomTickSpeed" -> {
                return Config.Gamerules.worldUpdates.randomTickSpeed.toString();
            }

            // GamerulesMultiplayer
            case "playersSleepingPercentage" -> {
                return Config.Gamerules.multiplayer.playersSleepingPercentage.toString();
            }

            // GamerulesMisc
            case "minecartMaxSpeed" -> {
                return Config.Gamerules.misc.minecartMaxSpeed.toString();
            }

            default -> {
                return "Unknown gamerule: " + name;
            }
        }
    }

    public static boolean isInteger(String s, int radix) {
        Scanner sc = new Scanner(s.trim());
        if(!sc.hasNextInt(radix)) return false;
        sc.nextInt(radix);
        return !sc.hasNext();
    }

    @Override
    public void command(SharedCommandSource commandSource, String[] parameters) {
        if (parameters.length == 2) {
            String s = getValue(parameters[1]);
            if (s.startsWith("Unknown")) {
                commandSource.sendFeedback(s);
            } else {
                commandSource.sendFeedback("Gamerule " + parameters[1] + " is currently set to: " + s);
            }
        } else if (parameters.length == 3) {
            if (parameters[2].toLowerCase().equals("false") || parameters[2].toLowerCase().equals("true")) {
                boolean b = parameters[2].toLowerCase().equals("true");
                if (!updateValue(parameters[1], b)) {
                    commandSource.sendFeedback("Non-existent gamerule");
                }
            }
            else if (isInteger(parameters[2], 10)) {
                int i = Integer.parseInt(parameters[2]);
                if (!updateValue(commandSource, parameters[1], i)) {
                    commandSource.sendFeedback("Invalid value or improper gamerule");
                }
            }
            else {
                commandSource.sendFeedback("Invalid value");
            }
        }
        commandSource.sendFeedback("Improper usage, use /help gamerule");
    }

    @Override
    public String name() {
        return "gamerule";
    }

    @Override
    public void manual(SharedCommandSource commandSource) {
        commandSource.sendFeedback("Usage: /gamerule");
        commandSource.sendFeedback("Info: Clears chat history");
    }

    public String[] suggestion(SharedCommandSource source, int parameterNum, String currentInput, String totalInput) {
        if (parameterNum == 1) {
            ArrayList<String> outputs = new ArrayList<>();

            for (String key : ALL_CONFIG_NAMES) {
                if (key.startsWith(currentInput)) {
                    outputs.add(key.substring(currentInput.length()));
                }
            }

            return outputs.toArray(new String[0]);
        } else if (parameterNum == 2) {
            if (currentInput.length() == 0) {
                String s = getValue(totalInput.split(" ")[1]);
                if (s.startsWith("Unknown")) {
                    return new String[]{};
                } else {
                    return new String[]{s};
                }
            }
        }
        return new String[]{};
    }
}
