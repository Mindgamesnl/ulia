package nl.toetmats.ulia.bukkit.commands.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.toetmats.ulia.bukkit.commands.command.UliaCommand;
import nl.toetmats.ulia.bukkit.commands.objects.Argument;
import nl.toetmats.ulia.service.Service;
import nl.toetmats.ulia.service.ServiceManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.*;

public abstract class SubCommand {

    @Setter @Getter protected UliaCommand parent;
    @Getter private final String command;
    @Getter protected boolean listed = true;
    @Getter private List<String> aliases = new ArrayList<>();
    @Getter private final List<Argument> arguments = new ArrayList<>();
    private final Map<String, SubCommand> moreSubCommands = new HashMap<>();
    protected boolean trimArguments = false;
    protected boolean ignorePermissions = false;

    /**
     * @param argument Your command name. For example "select"
     */
    public SubCommand(String argument) {
        this.command = argument;
        Bukkit.getPluginManager().addPermission(new Permission(parent.getConfiguration().getPermissionPrefix() + "." + command));
    }

    public SubCommand(String argument, String... aliases) {
        this(argument);
        this.aliases = Arrays.asList(aliases);
    }

    /**
     * send a openaudiomc styled message
     *
     * @param sender Command sender
     * @param message Your message
     */
    protected void message(Player sender, String message) {
        sender.sendMessage(parent.getConfiguration().getChatPrefix() + message);
    }

    /**
     * check if the sender has permissions to execute this command.
     * you do not need to run this check itself, its used by the framework.
     *
     * @param commandSender Command sender
     * @return true if the player is allowed to execute a command
     */
    public boolean isAllowed(Player commandSender) {
        if (ignorePermissions) return true;
        return commandSender.hasPermission(parent.getConfiguration().getPermissionPrefix() + ".commands." + command)
                || commandSender.hasPermission(parent.getConfiguration().getPermissionPrefix() + ".commands.*")
                || commandSender.hasPermission(parent.getConfiguration().getPermissionPrefix() +  ".*");
    }

    protected void registerSubCommands(SubCommand... commands) {
        for (SubCommand subCommand : commands) {
            moreSubCommands.put(subCommand.getCommand(), subCommand);
            subCommand.setParent(parent);
        }
    }

    /**
     * @param subCommand Another sub command to use, like a sub sub command!
     * @param user User
     * @param args Arguments
     */
    protected void delegateTo(String subCommand, Player user, String[] args) {
        SubCommand sci = moreSubCommands.get(subCommand);
        if (sci.trimArguments) {
            String[] subArgs = new String[args.length - 1];
            if (args.length != 1) System.arraycopy(args, 1, subArgs, 0, args.length - 1);
            sci.onExecute(user, subArgs);
        } else {
            sci.onExecute(user, args);
        }
    }

    /**
     * Register one or more arguments.
     * used for auto complete and the help menu
     *
     * @param args one or more arguments
     */
    protected void registerArguments(Argument... args) {
        arguments.addAll(Arrays.asList(args));
    }

    /**
     * @param sender the sender that executed the commands
     * @param args the arguments after your command, starting at index 0
     */
    public abstract void onExecute(Player sender, String[] args);

    protected boolean isInteger(String s) {
        return isInteger(s,10);
    }

    private boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    @AllArgsConstructor
    public static class CommandArguments {
        private String[] args;

        public String getSaveString(int index) {
            if (args.length >= (index + 1)) {
                return args[index];
            }
            return "";
        }
    }

    protected <T extends Service> T getService(Class<T> service) {
        return service.cast(ServiceManager.INSTANCE.loadService(service));
    }
}
