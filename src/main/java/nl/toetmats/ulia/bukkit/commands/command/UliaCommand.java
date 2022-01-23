package nl.toetmats.ulia.bukkit.commands.command;

import lombok.Getter;
import lombok.Setter;
import nl.toetmats.ulia.bukkit.commands.config.CommandConfiguration;
import nl.toetmats.ulia.bukkit.commands.error.CommandError;
import nl.toetmats.ulia.bukkit.commands.interfaces.SubCommand;
import nl.toetmats.ulia.bukkit.commands.objects.Argument;
import nl.toetmats.ulia.bukkit.commands.subcommands.HelpSubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UliaCommand implements CommandExecutor, TabCompleter {

    private Map<String, SubCommand> subCommands = new HashMap<>();
    @Getter @Setter
    private CommandConfiguration configuration = new CommandConfiguration();

    @Getter
    private String commandName;

    public UliaCommand(String commandName) {
        registerSubCommand(new HelpSubCommand());
        this.commandName = commandName;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(configuration.getChatPrefix() + configuration.getNoArgumentsMessage().replaceAll("%commandName", commandName));
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());

        if (subCommand != null) {
            if (subCommand.isAllowed(player)) {
                String[] subArgs = new String[args.length - 1];
                /*
                 * Move the arguments for the sub command framework
                 */
                if (args.length != 1) System.arraycopy(args, 1, subArgs, 0, args.length - 1);
                try {
                    /*
                     * execute the sub command
                     */
                    subCommand.onExecute(player, subArgs);
                } catch (Exception e) {
                    /*
                     * It's more dead inside then i am
                     */
                    if (!(e instanceof CommandError)) {
                        e.printStackTrace();
                    }
                    sender.sendMessage(configuration.getChatPrefix() + configuration.getNoArgumentsMessage().replaceAll("%err", e.getMessage()));
                }
                return true;
            } else {
                sender.sendMessage(configuration.getChatPrefix() + configuration.getNoPermissionsMessage());
                return true;
            }
        } else {
            sender.sendMessage(configuration.getChatPrefix() + configuration.getUnknownSubCommandMessage().replaceAll("%sub", args[0]));
            subCommands.get("help").onExecute(player, args);
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();
        for (String subCommand : getSubCommands()) {
            if (args.length <= 1 && subCommand.startsWith(args[0])) completions.add(subCommand);
        }
        if (args.length == 2) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand == null) return new ArrayList<>();
            for (Argument argument : subCommand.getArguments()) {
                if (argument.getSyntax().startsWith(args[1].toLowerCase())) {
                    completions.add(argument.getSyntax());
                }
            }
        }
        return completions;
    }

    /**
     * @return All sub commands as strings
     */
    public List<String> getSubCommands() {
        return new ArrayList<>(subCommands.keySet());
    }

    /**
     * @return All sub command handlers
     */
    public Map<String, SubCommand> getSubCommandHandlers() {
        return subCommands;
    }

    /**
     * @param subCommand registers a sub command
     */
    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getCommand(), subCommand);
        subCommand.setParent(this);
    }

    public void registerSubCommands(SubCommand... commandList) {
        for (SubCommand subCommand : commandList) {
            subCommands.put(subCommand.getCommand(), subCommand);

            for (String alias : subCommand.getAliases()) {
                subCommands.put(alias, subCommand);
            }
        }
    }

    /**
     * @param argument get the sub command from a name
     * @return returns the handler, can be null
     */
    public SubCommand getSubCommand(String argument) {
        return subCommands.get(argument);
    }
}
