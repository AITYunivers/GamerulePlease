package io.github.yunivers.gamerule_please.config;

import net.glasslauncher.mods.gcapi3.api.ConfigCategory;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class Config
{
    @ConfigRoot(value = "config", visibleName = "Gamerules")
    public static final Gamerules Gamerules = new Gamerules();

    public static class Gamerules
    {
        @ConfigCategory(name = "Player")
        public GamerulesPlayer player = new GamerulesPlayer();

        @ConfigCategory(name = "Mob")
        public GamerulesMob mob = new GamerulesMob();

        @ConfigCategory(name = "Drops")
        public GamerulesDrops drops = new GamerulesDrops();

        @ConfigCategory(name = "World Updates")
        public GamerulesWorldUpdates worldUpdates = new GamerulesWorldUpdates();

        @ConfigCategory(name = "Multiplayer", hidden = true)
        public GamerulesMultiplayer multiplayer = new GamerulesMultiplayer();

        @ConfigCategory(name = "Miscellaneous")
        public GamerulesMisc misc = new GamerulesMisc();
    }

    public static class GamerulesPlayer
    {
        // TODO
        @ConfigEntry(
            name = "disablePlayerMovementCheck",
            description = "Disable player movement check (NOT IMPLEMENTED)",
            multiplayerSynced = true,
            hidden = true
        )
        public Boolean disablePlayerMovementCheck = false;

        @ConfigEntry(
            name = "doImmediateRespawn",
            description = "Respawn immediately",
            multiplayerSynced = true
        )
        public Boolean doImmediateRespawn = false;

        @ConfigEntry(
            name = "drowningDamage",
            description = "Deal drowning damage",
            multiplayerSynced = true
        )
        public Boolean drowningDamage = true;

        @ConfigEntry(
            name = "fallDamage",
            description = "Deal fall damage",
            multiplayerSynced = true
        )
        public Boolean fallDamage = true;

        @ConfigEntry(
            name = "fireDamage",
            description = "Deal fire damage",
            multiplayerSynced = true
        )
        public Boolean fireDamage = true;

        @ConfigEntry(
            name = "keepInventory",
            description = "Keep inventory after death",
            multiplayerSynced = true
        )
        public Boolean keepInventory = false;

        @ConfigEntry(
            name = "playersNetherPortalDelay",
            description = "Player's Nether portal delay",
            minLength = 0,
            maxLength = Integer.MAX_VALUE,
            multiplayerSynced = true
        )
        public Integer playersNetherPortalDelay = 80;
    }

    public static class GamerulesMob
    {
        @ConfigEntry(
            name = "doMobSpawning",
            description = "Spawn mobs",
            multiplayerSynced = true
        )
        public Boolean doMobSpawning = true;

        @ConfigEntry(
            name = "mobGriefing",
            description = "Allow destructive mob actions",
            multiplayerSynced = true
        )
        public Boolean mobGriefing = true;

        // TODO
        @ConfigEntry(
            name = "universalAnger",
            description = "Universal anger (NOT IMPLEMENTED)",
            multiplayerSynced = true,
            hidden = true
        )
        public Boolean universalAnger = false;

        // TODO
        @ConfigEntry(
            name = "forgiveDeadPlayers",
            description = "Forgive dead players (NOT IMPLEMENTED)",
            multiplayerSynced = true,
            hidden = true
        )
        public Boolean forgiveDeadPlayers = true;
    }

    public static class GamerulesDrops
    {
        @ConfigEntry(
            name = "doMobLoot",
            description = "Drop mob loot",
            multiplayerSynced = true
        )
        public Boolean doMobLoot = true;

        @ConfigEntry(
            name = "doTileDrops",
            description = "Drop blocks",
            multiplayerSynced = true
        )
        public Boolean doTileDrops = true;

        // TODO
        @ConfigEntry(
            name = "projectilesCanBreakBlocks",
            description = "Projectiles can break certain blocks (NOT IMPLEMENTED)",
            multiplayerSynced = true,
            hidden = true
        )
        public Boolean projectilesCanBreakBlocks = true;

        @ConfigEntry(
            name = "blockExplosionDropDecay",
            description = "In block interaction explosions some blocks won't drop their loot",
            multiplayerSynced = true
        )
        public Boolean blockExplosionDropDecay = true;

        @ConfigEntry(
            name = "mobExplosionDropDecay",
            description = "In mob explosions, some blocks won't drop their loot",
            multiplayerSynced = true
        )
        public Boolean mobExplosionDropDecay = true;

        @ConfigEntry(
            name = "tntExplosionDropDecay",
            description = "In TNT explosions, some blocks won't drop their loot",
            multiplayerSynced = true
        )
        public Boolean tntExplosionDropDecay = true;
    }

    public static class GamerulesWorldUpdates
    {
        @ConfigEntry(
            name = "doDaylightCycle",
            description = "Advance time of day",
            multiplayerSynced = true
        )
        public Boolean doDaylightCycle = true;

        @ConfigEntry(
            name = "doWeatherCycle",
            description = "Update weather",
            multiplayerSynced = true
        )
        public Boolean doWeatherCycle = true;

        @ConfigEntry(
            name = "doFireTick",
            description = "Update fire",
            multiplayerSynced = true
        )
        public Boolean doFireTick = true;

        @ConfigEntry(
            name = "randomTickSpeed",
            description = "Random tick speed rate",
            minLength = 0,
            maxLength = Integer.MAX_VALUE,
            multiplayerSynced = true
        )
        public Integer randomTickSpeed = 80;

        @ConfigEntry(
            name = "lavaSourceConversion",
            description = "Lava converts to source",
            multiplayerSynced = true
        )
        public Boolean lavaSourceConversion = false;

        @ConfigEntry(
            name = "waterSourceConversion",
            description = "Water converts to source",
            multiplayerSynced = true
        )
        public Boolean waterSourceConversion = true;
    }

    public static class GamerulesMultiplayer
    {
        // TODO
        @ConfigEntry(
            name = "logAdminCommands",
            description = "Broadcast admin commands (NOT IMPLEMENTED)",
            multiplayerSynced = true
        )
        public Boolean logAdminCommands = true;

        // TODO
        @ConfigEntry(
            name = "sendCommandFeedback",
            description = "Send command feedback (NOT IMPLEMENTED)",
            multiplayerSynced = true
        )
        public Boolean sendCommandFeedback = true;

        @ConfigEntry(
                name = "playersSleepingPercentage",
                description = "Sleep percentage",
                minLength = -1,
                maxLength = 100,
                multiplayerSynced = true
        )
        public Integer playersSleepingPercentage = 100;
    }

    public static class GamerulesMisc
    {
        @ConfigEntry(
            name = "reducedDebugInfo",
            description = "Reduce debug info"
        )
        public Boolean reducedDebugInfo = false;

        @ConfigEntry(
            name = "showCoordinates",
            description = "Show coordinates at all times"
        )
        public Boolean showCoordinates = false;

        @ConfigEntry(
            name = "showDaysPlayed",
            description = "Show days played at all times"
        )
        public Boolean showDaysPlayed = false;

        // TODO
        @ConfigEntry(
            name = "minecartMaxSpeed",
            description = "The maximum speed a minecart may reach (NOT IMPLEMENTED)",
            minLength = 1,
            maxLength = 2000,
            multiplayerSynced = true,
            hidden = true
        )
        public Integer minecartMaxSpeed = 8;

        @ConfigEntry(
            name = "tntExplodes",
            description = "Allow TNT to be activated and to explode",
            multiplayerSynced = true
        )
        public Boolean tntExplodes = true;

        @ConfigEntry(
            name = "respawnBlocksExplode",
            description = "Allow beds to explode in other dimensions",
            multiplayerSynced = true
        )
        public Boolean respawnBlocksExplode = true;
    }
}
