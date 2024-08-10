package mod.icy_turtle.friendhighlighter.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import mod.icy_turtle.friendhighlighter.FriendHighlighter;
import mod.icy_turtle.friendhighlighter.config.FHConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MainModScreenBuilder
{
	public static Screen getMainModScreen(Screen parent)
	{
		return getModConfigScreenFactory().create(parent);
	}

	public static ConfigScreenFactory<?> getModConfigScreenFactory()
	{
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create().setTitle(Text.translatable("config.friendHighlighter.title"));

			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

			ConfigCategory friendsListCategory = builder.getOrCreateCategory(Text.translatable("config.friendHighlighter.category.friendsList"));
			friendsListCategory.addEntry(FriendsListPage.createFriendsList(entryBuilder));

			ConfigCategory modSettingsCategory = builder.getOrCreateCategory(Text.literal("Mod Settings"));
			ModSettingsPage.addModSettingsToCategory(modSettingsCategory, entryBuilder);

			ConfigCategory entitySelectCategory = builder.getOrCreateCategory(Text.literal("Entity Selection Screen"));
			EntitySelectPage.addEntitySelectionElements(entitySelectCategory, entryBuilder);

			builder.setSavingRunnable(()->{
				FHConfig.saveConfig();
				FHConfig.loadConfig();
				FriendHighlighter.COMMAND_HANDLER.updateLists();
			});
			return builder.setParentScreen(parent).build();
		};
	}
}
