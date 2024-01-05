package mod.icy_turtle.friendhighlighter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static mod.icy_turtle.friendhighlighter.FriendHighlighter.LOGGER;

/**
 * Handles the saving, loading, and editing of the friend list and settings of the mod.
 */
public class FHConfig
{
    private static final Path SETTINGS_FILE = FabricLoader.getInstance().getConfigDir().resolve("FriendHighlighterSettings.json");
    private static final Path FRIENDS_LIST_FILE = FabricLoader.getInstance().getConfigDir().resolve("FriendHighlighterFriendList.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     *  Used to access the friends map.
     */
    private static FriendsListHandler friendsListHandler;
    /**
     *  Allows access to mod settings.
     */
    private static FHSettings settings;

    /**
     * Loads the config from {@link #SETTINGS_FILE} and {@link #FRIENDS_LIST_FILE} into {@link #settings} and {@link #friendsListHandler}.
     * @see #readFile(Class, Path, Object)
     */
    public static void loadConfig()
    {
        LOGGER.info("Loading config...");
        friendsListHandler = readFile(FriendsListHandler.class, FRIENDS_LIST_FILE, new FriendsListHandler());
        settings = readFile(FHSettings.class, SETTINGS_FILE, new FHSettings());
        saveConfig();
    }

    /**
     * Reads the file and deserializes it into the specified instance.
     * @param clazz the class to deserialize.
     * @param file the target
     * @param defaultValue the default value, if the object can't be deserialized.
     * @return the deserialized config.
     * @param <T> the type of object to deserialize.
     */
    private static <T> T readFile(Class<T> clazz, Path file, T defaultValue)
    {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            var deserialized = GSON.fromJson(reader, clazz);
            if(deserialized == null)
                return defaultValue;
            return deserialized;
        } catch (IOException ex) {
            LOGGER.warn("Exception thrown while trying to read " + file.getFileName() + ", creating blank config files");
            return defaultValue;
        }
    }

    /**
     * Serializes the config and puts it into {@link #SETTINGS_FILE} and {@link #FRIENDS_LIST_FILE}.
     */
    public static void saveConfig()
    {
        try(BufferedWriter settingsWriter = Files.newBufferedWriter(SETTINGS_FILE); BufferedWriter friendsListWriter = Files.newBufferedWriter(FRIENDS_LIST_FILE))
        {
            GSON.toJson(settings, settingsWriter);
            GSON.toJson(friendsListHandler, friendsListWriter);
            LOGGER.info("Saved config.");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static FriendsListHandler getFriendsListHandler()
    {
        if(friendsListHandler == null)
            loadConfig();
        return friendsListHandler;
    }

    protected static FHSettings getSettings()
    {
        if(settings == null)
            loadConfig();
        return settings;
    }
}