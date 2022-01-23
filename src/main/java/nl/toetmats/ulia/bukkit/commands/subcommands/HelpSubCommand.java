package nl.toetmats.ulia.bukkit.commands.subcommands;

import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import nl.toetmats.ulia.bukkit.commands.interfaces.SubCommand;
import nl.toetmats.ulia.bukkit.commands.objects.Argument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

public class HelpSubCommand extends SubCommand {

    public HelpSubCommand() {
        super("help");
        ignorePermissions = true;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            args[0] = args[0].toLowerCase();
            SubCommand subCommand = parent.getSubCommand(args[0]);
            if (subCommand != null) {
                message(sender, parent.getConfiguration().getHelpTitleMessage().replaceAll("%subCommand", args[0]));
                for (Argument argument : subCommand.getArguments()) {
                    goldMessage(sender, "/" + parent.getCommandName() + " " + subCommand.getCommand() + " " + argument.getSyntax());
                    grayMessage(sender, argument.getDescription());
                }
                return;
            }
        }

        message(sender, parent.getConfiguration().getHelpExplanation());

        for (Map.Entry<String, SubCommand> entry : parent.getSubCommandHandlers().entrySet()) {
            String command = entry.getKey();
            SubCommand handler = entry.getValue();

            // only render aliases
            if (command.equals(handler.getCommand()) && handler.isListed()) {
                goldClickableMessage(sender, "/" + parent.getCommandName() + " " + handler.getCommand(), parent.getCommandName() + " help " + handler.getCommand());
            }
        }

        message(sender, parent.getConfiguration().getHelpFooter());
    }

    private void goldMessage(CommandSender s, String message) {
        s.sendMessage(" " + ChatColor.YELLOW + "> " + ChatColor.GOLD + message);
    }

    @SneakyThrows
    private void goldClickableMessage(CommandSender s, String message, String command) {
        if (s instanceof Player) {
            sendClickableCommandMessage((Player) s, ChatColor.GOLD + " > " + message, "Click here to run " + command, command);
        } else {
            s.sendMessage(message + " - " + command);
        }
    }

    private void sendClickableCommandMessage(Player player, String msgText, String hoverMessage, String command) {
        TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(
                msgText
        )));

        TextComponent[] hover = new TextComponent[]{
                new TextComponent(ChatColor.translateAlternateColorCodes('&',
                        hoverMessage
                ))
        };
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));

        player.spigot().sendMessage(message);
    }

    private void grayMessage(CommandSender s, String message) {
        s.sendMessage("  " + ChatColor.DARK_GRAY + "> " + ChatColor.ITALIC + "" + ChatColor.GRAY + message);
    }
}
