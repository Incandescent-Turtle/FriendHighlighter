package mod.icy_turtle.friendhighlighter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static mod.icy_turtle.friendhighlighter.FriendHighlighter.LOGGER;

public class FHConfig
{
    private transient static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("FriendHighlighter.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private transient static FHConfig INSTANCE;

    public Map<String, HighlightedFriend> friendsMap = new HashMap<>();

    public boolean mapContainsEntity(Entity entity)
    {
        return friendsMap.containsKey(entity.getName().getString());
    }

    public HighlightedFriend getFriendFromEntity(Entity entity)
    {
        return friendsMap.get(entity.getName().getString());
    }

    public int getColorOrDefault(Entity entity)
    {
        return getColorOrDefault(entity.getName().getString());
    }

    public int getColorOrDefault(String str)
    {
        return friendsMap.getOrDefault(str, new HighlightedFriend()).color;
    }

    public static FHConfig getInstance()
    {
        if (INSTANCE == null)
            loadConfig();
        return INSTANCE;
    }

    public static void loadConfig()
    {
        LOGGER.info(INSTANCE == null ? "Loading config..." : "Reloading config...");
        INSTANCE = readFile();
        if (INSTANCE == null)
            INSTANCE = new FHConfig();
        saveConfig();
    }

    private static FHConfig readFile()
    {
        try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE)) {
            return GSON.fromJson(reader, FHConfig.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void saveConfig()
    {
        LOGGER.info("Attempting to save config...");
        if(INSTANCE != null)
        {
            try(BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE)) {
                GSON.toJson(INSTANCE, writer);
                LOGGER.info("Saved config.");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            LOGGER.error("Can't save config as config instance is null.");
        }
    }
}