package mod.icy_turtle.friendhighlighter.command.commands.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.icy_turtle.friendhighlighter.command.Command;
import mod.icy_turtle.friendhighlighter.command.CommandHandler;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SettingsCommand extends Command
{
	public SettingsCommand(CommandHandler cmdHandler)
	{
		super(cmdHandler);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> createCommand()
	{
		return literal("settings")
				.then(new SettingsSetCommand(cmdHandler).createCommand())
				.then(new SettingsDisplayCommand(cmdHandler).createCommand())
				.executes(context -> cmdHandler.settingsChatMsg.sendInChat());
	}
}
