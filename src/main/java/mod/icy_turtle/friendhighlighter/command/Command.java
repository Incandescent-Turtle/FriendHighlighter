package mod.icy_turtle.friendhighlighter.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

abstract public class Command
{
	protected CommandHandler cmdHandler;

	public Command(CommandHandler cmdHandler)
	{
		this.cmdHandler = cmdHandler;
	}
	public abstract LiteralArgumentBuilder<FabricClientCommandSource> createCommand();
}