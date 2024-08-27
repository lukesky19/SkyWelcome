package com.github.lukesky19.skywelcome;

import com.github.lukesky19.skywelcome.commands.SkyWelcomeCommand;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import com.github.lukesky19.skywelcome.config.gui.GUIManager;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.gui.JoinGUI;
import com.github.lukesky19.skywelcome.gui.QuitGUI;
import com.github.lukesky19.skywelcome.listeners.JoinListener;
import com.github.lukesky19.skywelcome.listeners.QuitListener;
import com.github.lukesky19.skywelcome.util.HeadDatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SkyWelcome extends JavaPlugin {
    SettingsManager settingsManager;
    GUIManager GUIManager;
    LocaleManager localeManager;

    @Override
    public void onEnable() {
        // Initialize Classes
        ConfigurationUtility configurationUtility = new ConfigurationUtility(this);
        settingsManager = new SettingsManager(this, configurationUtility);
        GUIManager = new GUIManager(this, configurationUtility);
        PlayerManager playerManager = new PlayerManager(this, configurationUtility, settingsManager);
        localeManager = new LocaleManager(this, configurationUtility, settingsManager, playerManager);
        SkyWelcomeCommand skyWelcomeCommand = new SkyWelcomeCommand(
                this, playerManager, localeManager,
                new JoinGUI(settingsManager, playerManager, GUIManager),
                new QuitGUI(settingsManager, playerManager, GUIManager));

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new JoinListener(this, playerManager, settingsManager, localeManager), this);
        this.getServer().getPluginManager().registerEvents(new QuitListener(playerManager, settingsManager, localeManager), this);
        this.getServer().getPluginManager().registerEvents(new HeadDatabaseUtil(), this);

        // Register Commands
        Objects.requireNonNull(Bukkit.getPluginCommand("skywelcome")).setExecutor(skyWelcomeCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("skywelcome")).setTabCompleter(skyWelcomeCommand);

        reload();
    }

    public void reload() {
        settingsManager.reload();
        GUIManager.reload();
        localeManager.reload();
    }
}
