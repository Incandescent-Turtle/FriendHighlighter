package mod.icy_turtle.friendhighlighter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.annotation.Config;
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

@Config(name = "friendhighlighter")
public class FHConfig
{
    private transient static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("FriendHighlighterConfig.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private transient static FHConfig INSTANCE;

    public Map<String, Integer> playerMap = new HashMap<>();

    public boolean mapContainsEntity(Entity entity)
    {
        return playerMap.containsKey(entity.getName().getString());
    }

    public int getColorOrWhite(Entity entity)
    {
        return playerMap.getOrDefault(entity.getName().getString(), 0xFFFFFF);
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
            throw new RuntimeException(ex);
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