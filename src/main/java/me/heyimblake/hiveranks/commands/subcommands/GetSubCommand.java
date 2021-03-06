package me.heyimblake.hiveranks.commands.subcommands;

import me.heyimblake.hiveranks.CachedPlayerManager;
import me.heyimblake.hiveranks.commands.AnnotatedHiveRanksSubCommand;
import me.heyimblake.hiveranks.commands.HiveRanksSubCommandExecutor;
import me.heyimblake.hiveranks.commands.HiveRanksSubCommandHandler;
import me.heyimblake.hiveranks.util.CachedPlayer;
import me.heyimblake.hiveranks.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * https://heyimblake.me
 *
 * @author heyimblake
 * @since 3/18/2017
 */
@HiveRanksSubCommandExecutor(subCommand = "get",
        syntax = "<Target Player>",
        description = "Displays information about the rank of the target player.",
        requiresArgumentCompletion = true,
        requiresAdminPermission = false)
public class GetSubCommand extends AnnotatedHiveRanksSubCommand {

    public GetSubCommand(HiveRanksSubCommandHandler handler) {
        super(handler);
    }

    @Override
    public boolean runPlayer() {
        Player player = (Player) getHandler().getCommandSender();
        Player target = Bukkit.getPlayer(getHandler().getArguments()[0]);
        CachedPlayerManager cachedPlayerManager = CachedPlayerManager.getInstance();

        if (target == null) {
            MessageUtils.sendErrorMessage(player, "Sorry, that player could not be found! Either they are not online or their name was typed incorrectly.", true);
            return true;
        }

        if (!cachedPlayerManager.isCached(target.getUniqueId())) {
            MessageUtils.sendErrorMessage(player, "Sorry, but " + target.getName() + " does not have a cached rank.", true);
            return true;
        }

        CachedPlayer targetCache = cachedPlayerManager.getCachedPlayer(target.getUniqueId());

        MessageUtils.sendVariableSuccessfulMessage(player, true, "The HiveMC rank of %s is %s.", ChatColor.WHITE + target.getName() + ChatColor.GRAY, targetCache.getHiveRank().getColor() + targetCache.getHiveRank().getHumanName() + ChatColor.GRAY);

        if (!targetCache.isHiveRankActive())
            MessageUtils.sendVariableSuccessfulMessage(player, false, "However, they appear as %s.", targetCache.getActiveRank().getColor() + targetCache.getActiveRank().getHumanName() + ChatColor.GRAY);

        return true;
    }

    @Override
    public boolean runConsole() {
        CommandSender sender = getHandler().getCommandSender();
        Player target = Bukkit.getPlayer(getHandler().getArguments()[0]);
        CachedPlayerManager cachedPlayerManager = CachedPlayerManager.getInstance();

        if (target == null) {
            sender.sendMessage("Sorry, that player could not be found! Either they are not online or their name was typed incorrectly.");
            return true;
        }

        if (!cachedPlayerManager.isCached(target.getUniqueId())) {
            sender.sendMessage("Sorry, but " + target.getName() + " does not have a cached rank.");
            return true;
        }

        CachedPlayer targetCache = cachedPlayerManager.getCachedPlayer(target.getUniqueId());

        sender.sendMessage(String.format("The HiveMC rank of %s is %s.", target.getName(), targetCache.getHiveRank().getHumanName()));

        if (!targetCache.isHiveRankActive())
            sender.sendMessage(String.format("However, they appear as %s.", targetCache.getActiveRank().getHumanName()));

        return true;
    }
}
