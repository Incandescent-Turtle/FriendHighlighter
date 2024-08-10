package mod.icy_turtle.friendhighlighter.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.gui.EntitySelectPage;
import mod.icy_turtle.friendhighlighter.gui.FriendsListPage;
import mod.icy_turtle.friendhighlighter.gui.MainModScreenBuilder;
import mod.icy_turtle.friendhighlighter.gui.ModSettingsPage;
import mod.icy_turtle.friendhighlighter.util.FHUtils;
import mod.icy_turtle.friendhighlighter.util.MultiPartGUIElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.Supplier;

/**
 * Integration for the ModMenu mod.
 */
@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return MainModScreenBuilder.getModConfigScreenFactory();
    }
}