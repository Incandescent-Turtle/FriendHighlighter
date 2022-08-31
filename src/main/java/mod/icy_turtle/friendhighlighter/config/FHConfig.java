package mod.icy_turtle.friendhighlighter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static mod.icy_turtle.friendhighlighter.FriendHighlighter.LOGGER;

/**
 * Handles the config values and loading/unloading.
 */
public class FHConfig
{
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("FriendHighlighter.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static FHConfig INSTANCE;

    /**
     * The map to be used throughout the mod to check which names are on the friends list.
     */
    public Map<String, HighlightedFriend> friendsMap = new LinkedHashMap<>();

    /**
     * Returns the instance of {@link HighlightedFriend} associated to this entity via {@link Entity#getName()}.
     * @param entity the entity to use to get the friend.
     * @return the associated {@link HighlightedFriend} instance, or null if there isn't one.
     */
    public @Nullable HighlightedFriend getFriendFromEntity(Entity entity)
    {
        return friendsMap.get(entity.getName().getString());
    }

    /**
     * Whether this entity should be affected by either outlining or name rendering.
     * @param entity the entity to test.
     * @return whether this entity should be highlighted.
     */
    public boolean shouldHighlightEntity(Entity entity)
    {
        var friend = getFriendFromEntity(entity);
        return friend != null && friend.isEnabled() && (entity instanceof PlayerEntity || !friend.onlyPlayers);
    }

    public static FHConfig getInstance()
    {
        if (INSTANCE == null)
            loadConfig();
        return INSTANCE;
    }

    /**
     * Loads the config from {@link #CONFIG_FILE} into {@link #INSTANCE}.
     * @see #readFile()
     */
    public static void loadConfig()
    {
        LOGGER.info(INSTANCE == null ? "Loading config..." : "Reloading config...");
        INSTANCE = readFile();
        if (INSTANCE == null)
            INSTANCE = new FHConfig();
        saveConfig();
    }

    /**
     * Reads {@link #CONFIG_FILE} and deserializes it into a {@link FHConfig} instance.
     * @return the deserialized config.
     */
    private static FHConfig readFile()
    {
        try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE)) {
            return GSON.fromJson(reader, FHConfig.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Serializes the config and puts it in {@link #CONFIG_FILE}.
     */
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