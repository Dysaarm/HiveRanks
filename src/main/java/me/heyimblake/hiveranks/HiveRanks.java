package me.heyimblake.hiveranks;

import me.heyimblake.hiveranks.commands.HiveRanksCommand;
import me.heyimblake.hiveranks.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HiveRanks extends JavaPlugin {
    private static HiveRanks instance;

    /**
     * Gets the instance of this class.
     *
     * @return instance of main class
     */
    public static HiveRanks getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pluginManager = Bukkit.getPluginManager();

        CachedPlayerManager.getInstance().initializeDirectory();
        ScoreboardManager.getInstance().initializeScoreboard();

        pluginManager.registerEvents(new PlayerJoinListener(), this);
        getCommand("hiveranks").setExecutor(new HiveRanksCommand());
    }

    @Override
    public void onDisable() {

    }
}
