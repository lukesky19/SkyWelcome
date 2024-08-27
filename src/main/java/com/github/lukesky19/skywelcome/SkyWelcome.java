package com.github.lukesky19.skywelcome;

import com.github.lukesky19.skywelcome.commands.SkyWelcomeCommand;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import com.github.lukesky19.skywelcome.config.gui.JoinQuitManager;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.gui.JoinGUI;
import com.github.lukesky19.skywelcome.gui.QuitGUI;
import com.github.lukesky19.skywelcome.listeners.JoinListener;
import com.github.lukesky19.skywelcome.listeners.QuitListener;
import com.github.lukesky19.skywelcome.managers.*;
import com.github.lukesky19.skywelcome.util.HeadDatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SkyWelcome extends JavaPlugin {
    SettingsManager settingsManager;
    JoinQuitManager joinQuitManager;

    @Override
    public void onEnable() {
        // Initialize Classes
        ConfigurationUtility configurationUtility = new ConfigurationUtility(this);
        settingsManager = new SettingsManager(this, configurationUtility);
        joinQuitManager = new JoinQuitManager(this, configurationUtility);
        PlayerManager playerManager = new PlayerManager(this, configurationUtility, settingsManager);
        MessageManager messageManager = new MessageManager(this, settingsManager, playerManager);
        SkyWelcomeCommand skyWelcomeCommand = new SkyWelcomeCommand(
                this, playerManager,
                new JoinGUI(settingsManager, playerManager, joinQuitManager),
                new QuitGUI(settingsManager, playerManager, joinQuitManager));

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new JoinListener(this, playerManager, messageManager, settingsManager), this);
        this.getServer().getPluginManager().registerEvents(new QuitListener(playerManager, messageManager, settingsManager), this);
        this.getServer().getPluginManager().registerEvents(new HeadDatabaseUtil(), this);

        // Register Commands
        Objects.requireNonNull(Bukkit.getPluginCommand("skywelcome")).setExecutor(skyWelcomeCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("skywelcome")).setTabCompleter(skyWelcomeCommand);

        reload();
    }

    public void reload() {
        settingsManager.reload();
        joinQuitManager.reload();
    }
}
