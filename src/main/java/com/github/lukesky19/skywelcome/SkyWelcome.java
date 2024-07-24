package com.github.lukesky19.skywelcome;

import com.github.lukesky19.skywelcome.commands.SkyWelcomeCommand;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.listeners.JoinListener;
import com.github.lukesky19.skywelcome.listeners.QuitListener;
import com.github.lukesky19.skywelcome.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SkyWelcome extends JavaPlugin {
    SettingsManager settingsManager;

    @Override
    public void onEnable() {
        // Initialize Classes
        ConfigurationUtility configurationUtility = new ConfigurationUtility(this);
        settingsManager = new SettingsManager(this, configurationUtility);
        PlayerManager playerManager = new PlayerManager(this, configurationUtility);
        MessageManager messageManager = new MessageManager(this, settingsManager, playerManager);
        SkyWelcomeCommand skyWelcomeCommand = new SkyWelcomeCommand(this, playerManager);

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new JoinListener(this, playerManager, messageManager), this);
        this.getServer().getPluginManager().registerEvents(new QuitListener(playerManager, messageManager), this);

        // Register Commands
        Objects.requireNonNull(Bukkit.getPluginCommand("skywelcome")).setExecutor(skyWelcomeCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("skywelcome")).setTabCompleter(skyWelcomeCommand);

        reload();
    }

    public void reload() {
        settingsManager.reload();
    }
}
