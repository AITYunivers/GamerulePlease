package io.github.yunivers.gamerule_please.compat.retrocommands;

import com.matthewperiut.retrocommands.api.Command;
import com.matthewperiut.retrocommands.util.SharedCommandSource;
import io.github.yunivers.gamerule_please.config.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;
import net.glasslauncher.mods.gcapi3.api.GCAPI;
import net.glasslauncher.mods.gcapi3.impl.ConfigRootEntry;
import net.glasslauncher.mods.gcapi3.impl.EventStorage;
import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.glasslauncher.mods.gcapi3.impl.GlassYamlFile;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameruleCommand implements Command
{
    public static String[] getConfigNames()
    {
        List<String> configNames = new ArrayList<>();
        Field[] gameruleCategories = Config.Gamerules.getClass().getDeclaredFields();
        for (Field category : gameruleCategories)
        {
            Field[] gamerules = category.getType().getDeclaredFields();
            for (Field gamerule : gamerules)
                configNames.add(gamerule.getName());
        }
        return configNames.toArray(new String[0]);
    }

    public static Field getConfigField(String str)
    {
        Field[] gameruleCategories = Config.Gamerules.getClass().getDeclaredFields();
        for (Field category : gameruleCategories)
        {
            Field[] gamerules = category.getType().getDeclaredFields();
            for (Field gamerule : gamerules)
                if (gamerule.getName().equalsIgnoreCase(str))
                    return gamerule;
        }
        return null;
    }

    public static Object getConfigInstance(Field field)
    {
        try
        {
            Field[] gameruleCategories = Config.Gamerules.getClass().getDeclaredFields();
            for (Field category : gameruleCategories)
            {
                Field[] gamerules = category.getType().getDeclaredFields();
                for (Field gamerule : gamerules)
                    if (gamerule.equals(field))
                        return gamerule.get(category.get(Config.Gamerules));
            }
        }
        catch (IllegalAccessException ignored) {}
        return null;
    }

    public static Boolean setConfigInstance(Field field, Object value)
    {
        try
        {
            Field[] gameruleCategories = Config.Gamerules.getClass().getDeclaredFields();
            for (Field category : gameruleCategories)
            {
                Field[] gamerules = category.getType().getDeclaredFields();
                for (Field gamerule : gamerules)
                    if (gamerule.equals(field))
                    {
                        gamerule.set(category.get(Config.Gamerules), value);
                        return true;
                    }
            }
        }
        catch (IllegalAccessException ignored) {}
        return false;
    }

    private String setConfigValue(String configName, Object value)
    {
        Field configField = getConfigField(configName);
        if (configField == null)
            return "Unknown gamerule " + configName;

        Object configInstance = getConfigInstance(configField);
        if (configInstance == null)
            return "Unknown gamerule " + configName;

        if (configInstance.getClass().equals(value.getClass()))
        {
            if (value instanceof Boolean valueBool)
            {
                if (setConfigInstance(configField, value))
                    return "Set " + configName + " to " + valueBool;
                else
                    return "Could not set " + configName + " to " + valueBool;
            }
            else if (value instanceof Integer valueInt)
            {
                ConfigEntry configEntry = configField.getAnnotation(ConfigEntry.class);
                if (configEntry == null)
                    return "Unknown gamerule " + configName;
                else if (valueInt >= configEntry.minLength() && valueInt <= configEntry.maxLength())
                    if (setConfigInstance(configField, value))
                        return "Set " + configName + " to " + valueInt;
                    else
                        return "Could not set " + configName + " to " + valueInt;
            }
            else
                return "!! This shouldn't happen, please open an issue on the Github !!";
        }
        return "Could not convert " + value + " to " + configInstance.getClass().getName();
    }

    public static String getValue(String name)
    {
        Field field = getConfigField(name);
        if (field == null)
            return "";

        Object config = getConfigInstance(field);
        if (config != null)
            return config.toString();
        return "";
    }

    public static boolean isInteger(String str)
    {
        Scanner sc = new Scanner(str.trim());
        if (!sc.hasNextInt(10))
            return false;
        sc.nextInt(10);
        return !sc.hasNext();
    }

    public static boolean isBoolean(String str)
    {
        return str.equalsIgnoreCase("false") || str.equalsIgnoreCase("true");
    }

    @Override
    public void command(SharedCommandSource commandSource, String[] parameters)
    {
        if (parameters.length == 2)
        {
            String s = getValue(parameters[1]);
            if (s.isEmpty())
                commandSource.sendFeedback("Unknown gamerule " + parameters[1]);
            else
                commandSource.sendFeedback("Gamerule " + parameters[1] + " is currently set to: " + s);
        }
        else if (parameters.length == 3)
        {
            String configName = parameters[1];
            String setValue = parameters[2];
            if (isConfigBoolean(configName))
            {
                if (isBoolean(setValue))
                    commandSource.sendFeedback(setConfigValue(configName, Boolean.parseBoolean(setValue)));
                else
                    commandSource.sendFeedback("Expected Boolean, got " + setValue);
            }
            else if (isConfigInteger(configName))
            {
                if (isInteger(setValue))
                    commandSource.sendFeedback(setConfigValue(configName, Integer.parseInt(setValue)));
                else
                    commandSource.sendFeedback("Expected Integer, got " + setValue);
            }
            else
                commandSource.sendFeedback("!! This shouldn't happen, please open an issue on the Github !!");
            saveConfig();
        }
        else
            commandSource.sendFeedback("Improper usage, use /help gamerule");
    }

    // TODO: Make this work??? Idk why it doesnt???? I'll ask Calmilamsy tomorrow.
    @SuppressWarnings("deprecation")
    public static void saveConfig()
    {
        ConfigRootEntry category = GCCore.MOD_CONFIGS.get("gamerule_please:config");
        GCCore.saveConfig(category.modContainer(), category.configCategoryHandler(), EventStorage.EventSource.MOD_SAVE);
        GCCore.loadModConfig(category.configRoot(), category.modContainer(), category.configCategoryHandler().parentField, "gamerule_please:config", null);
    }

    @Override
    public String name()
    {
        return "gamerule";
    }

    @Override
    public void manual(SharedCommandSource commandSource)
    {
        commandSource.sendFeedback("Usage: /gamerule");
        commandSource.sendFeedback("Info: Clears chat history");
    }

    public String[] suggestion(SharedCommandSource source, int parameterNum, String currentInput, String totalInput)
    {
        if (parameterNum == 1)
        {
            ArrayList<String> outputs = new ArrayList<>();

            for (String key : getConfigNames())
                if (key.startsWith(currentInput))
                    outputs.add(key.substring(currentInput.length()));

            return outputs.toArray(new String[0]);
        }
        else if (parameterNum == 2)
        {
            if (isConfigBoolean(totalInput.split(" ")[1]))
                return new String[] { "false", "true" };
            else if (currentInput.isEmpty())
            {
                String s = getValue(totalInput.split(" ")[1]);
                if (s.isEmpty())
                    return new String[0];
                return new String[] { s };
            }
        }
        return new String[0];
    }

    private boolean isConfigBoolean(String name)
    {
        Field configField = getConfigField(name);
        if (configField != null)
            return configField.getType() == Boolean.class;
        return false;
    }

    private boolean isConfigInteger(String name)
    {
        Field configField = getConfigField(name);
        if (configField != null)
            return configField.getType() == Integer.class;
        return false;
    }
}
