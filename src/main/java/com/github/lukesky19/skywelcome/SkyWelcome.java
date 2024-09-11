/*
    SkyWelcome allows players to toggle join, leave, MOTD messages, and to choose custom join and leave messages.
    Copyright (C) 2024  lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
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
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SkyWelcome extends JavaPlugin {
    ConfigurationUtility configurationUtility;
    SettingsManager settingsManager;
    PlayerManager playerManager;
    LocaleManager localeManager;
    GUIManager GUIManager;
    boolean pluginState;

    public void setPluginState(boolean pluginState) {
        this.pluginState = pluginState;
    }

    public boolean isPluginDisabled() {
        return !pluginState;
    }

    @Override
    public void onEnable() {
        // Set up bstats
        setupBStats();

        // Initialize Classes
        configurationUtility = new ConfigurationUtility(this);
        settingsManager = new SettingsManager(this, configurationUtility);
        playerManager = new PlayerManager(this, configurationUtility, settingsManager);
        localeManager = new LocaleManager(this, configurationUtility, settingsManager, playerManager);

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new HeadDatabaseUtil(this), this);
        this.getServer().getPluginManager().registerEvents(new JoinListener(this, playerManager, settingsManager, localeManager), this);
        this.getServer().getPluginManager().registerEvents(new QuitListener(this, playerManager, settingsManager, localeManager), this);
    }

    public void reload() {
        pluginState = true;
        settingsManager.reload();
        localeManager.reload();
        GUIManager.reload();
    }

    /**
     * Sets up bstats
     */
    private void setupBStats() {
        int pluginId = 23211;
        new Metrics(this, pluginId);
    }

    public void postHeadDatabaseAPI() {
        GUIManager = new GUIManager(this, configurationUtility);
        SkyWelcomeCommand skyWelcomeCommand = new SkyWelcomeCommand(
                this, playerManager, localeManager,
                new JoinGUI(settingsManager, playerManager, GUIManager),
                new QuitGUI(settingsManager, playerManager, GUIManager));

        // Register Commands
        Objects.requireNonNull(Bukkit.getPluginCommand("skywelcome")).setExecutor(skyWelcomeCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("skywelcome")).setTabCompleter(skyWelcomeCommand);

        reload();
    }
}
