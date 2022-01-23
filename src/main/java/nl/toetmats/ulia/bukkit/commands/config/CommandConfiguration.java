package nl.toetmats.ulia.bukkit.commands.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;

import java.util.Random;

@Data
@NoArgsConstructor
public class CommandConfiguration {

    private String chatPrefix = ChatColor.translateAlternateColorCodes('&', "&3[&bUliaCommand&3] &7");
    private String permissionPrefix = "ulia" + new Random().nextInt(99);

    private String noArgumentsMessage = "Please provide command arguments, or use /%commandName help";
    private String errorMessage = "Something went wrong! error: %err";
    private String noPermissionsMessage = "You dont have the permissions to do this, sorry!";
    private String unknownSubCommandMessage = "Unknown sub command: %sub";

    private String helpTitleMessage = "Showing command usage for %subCommand";
    private String helpExplanation = "please click one of the following commands for their sub commands and usage";
    private String helpFooter = "Please contact staff if you need any additional help";

}
