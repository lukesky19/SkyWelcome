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

import com.github.lukesky19.skylib.libs.bstats.bukkit.Metrics;
import com.github.lukesky19.skywelcome.commands.SkyWelcomeCommand;
import com.github.lukesky19.skywelcome.config.gui.GUIManager;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.gui.JoinGUI;
import com.github.lukesky19.skywelcome.gui.QuitGUI;
import com.github.lukesky19.skywelcome.listener.JoinListener;
import com.github.lukesky19.skywelcome.listener.QuitListener;
import com.github.lukesky19.skywelcome.listener.RewardListener;
import com.github.lukesky19.skywelcome.manager.RewardManager;
import com.github.lukesky19.skywelcome.util.HeadDatabaseUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SkyWelcome extends JavaPlugin {
    SettingsManager settingsManager;
    PlayerManager playerManager;
    LocaleManager localeManager;
    RewardManager rewardManager;
    GUIManager GUIManager;
    JoinListener joinListener;
    boolean pluginState;

    Economy economy;

    /**
     * @return The server's economy.
     */
    public Economy getEconomy() {
        return this.economy;
    }

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
        // Set up Economy
        setupEconomy();

        // Initialize Classes
        settingsManager = new SettingsManager(this);
        playerManager = new PlayerManager(this, settingsManager);
        localeManager = new LocaleManager(this, settingsManager, playerManager);
        rewardManager = new RewardManager(this, settingsManager, localeManager);
        joinListener = new JoinListener(this, playerManager, settingsManager, localeManager);

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new HeadDatabaseUtil(this), this);
        this.getServer().getPluginManager().registerEvents(joinListener, this);
        this.getServer().getPluginManager().registerEvents(new QuitListener(this, playerManager, settingsManager, localeManager), this);
        this.getServer().getPluginManager().registerEvents(new RewardListener(this, settingsManager, localeManager, rewardManager), this);
    }

    public void reload() {
        pluginState = true;
        settingsManager.reload();
        localeManager.reload();
        GUIManager.reload();
        rewardManager.reload();
        joinListener.reload();
    }

    /**
     * Sets up bstats
     */
    private void setupBStats() {
        int pluginId = 23211;
        new Metrics(this, pluginId);
    }

    public void postHeadDatabaseAPI() {
        GUIManager = new GUIManager(this);
        SkyWelcomeCommand skyWelcomeCommand = new SkyWelcomeCommand(
                this, playerManager, localeManager,
                new JoinGUI(settingsManager, playerManager, GUIManager),
                new QuitGUI(settingsManager, playerManager, GUIManager));

        // Register Commands
        Objects.requireNonNull(Bukkit.getPluginCommand("skywelcome")).setExecutor(skyWelcomeCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("skywelcome")).setTabCompleter(skyWelcomeCommand);

        reload();
    }

    /**
     * Checks for Vault as a dependency and sets up the Economy instance.
     */
    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                this.economy = rsp.getProvider();
            }
        } else {
            getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>SkyWelcome has been disabled due to no Vault dependency found!</red>"));
            getServer().getPluginManager().disablePlugin(this);
        }
    }
}
