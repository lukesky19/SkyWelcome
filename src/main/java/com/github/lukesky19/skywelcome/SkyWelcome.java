/*
    SkyWelcome allows players to toggle join, leave, MOTD messages, and to choose custom join and leave messages.
    Copyright (C) 2024 lukeskywlker19

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

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.libs.bstats.bukkit.Metrics;
import com.github.lukesky19.skywelcome.commands.SkyWelcomeCommand;
import com.github.lukesky19.skywelcome.commands.arguments.ToggleCommand;
import com.github.lukesky19.skywelcome.config.gui.GUIConfigManager;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.listener.InventoryListener;
import com.github.lukesky19.skywelcome.listener.JoinListener;
import com.github.lukesky19.skywelcome.listener.QuitListener;
import com.github.lukesky19.skywelcome.listener.RewardListener;
import com.github.lukesky19.skywelcome.manager.GUIManager;
import com.github.lukesky19.skywelcome.manager.HeadDatabaseManager;
import com.github.lukesky19.skywelcome.manager.PlayerDataManager;
import com.github.lukesky19.skywelcome.manager.RewardManager;
import com.github.lukesky19.skywelcome.manager.database.ConnectionManager;
import com.github.lukesky19.skywelcome.manager.database.DatabaseManager;
import com.github.lukesky19.skywelcome.manager.database.QueueManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The plugin's main class.
 */
public class SkyWelcome extends JavaPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private PlayerDataManager playerDataManager;
    private GUIConfigManager guiConfigManager;
    private DatabaseManager databaseManager;
    private GUIManager guiManager;

    private Economy economy;

    /**
     * Default Constructor.
     */
    public SkyWelcome() {}

    /**
     * Get the {@link Economy} for the server.
     * @return The server's economy.
     */
    public Economy getEconomy() {
        return this.economy;
    }


    @Override
    public void onEnable() {
        // Set up bstats
        setupBStats();
        if(!checkSkyLibVersion()) return;
        // Set up Economy
        if(!setupEconomy()) return;

        // Initialize Classes
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);
        guiConfigManager = new GUIConfigManager(this);

        ConnectionManager connectionManager = new ConnectionManager(this);
        QueueManager queueManager = new QueueManager(connectionManager);
        databaseManager = new DatabaseManager(connectionManager, queueManager);

        playerDataManager = new PlayerDataManager(this, settingsManager, databaseManager);

        RewardManager rewardManager = new RewardManager(this, settingsManager, localeManager);

        HeadDatabaseManager headDatabaseManager = new HeadDatabaseManager();

        guiManager = new GUIManager(this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands -> {
                    SkyWelcomeCommand skyWelcomeCommand = new SkyWelcomeCommand(this, settingsManager, localeManager, guiConfigManager, playerDataManager, headDatabaseManager, guiManager);
                    commands.registrar().register(skyWelcomeCommand.createCommand(), "Command to manage and use the SkyWelcome plugin.");

                    ToggleCommand toggleCommand = new ToggleCommand(this, localeManager, playerDataManager);
                    commands.registrar().register(toggleCommand.createCommand(), "Command shortcuts to toggle join, leave, and motd messages.");
                });

        // Register Listeners
        if(this.getServer().getPluginManager().getPlugin("HeadDatabase") != null) {
            this.getServer().getPluginManager().registerEvents(headDatabaseManager, this);
        }
        this.getServer().getPluginManager().registerEvents(new InventoryListener(guiManager), this);
        this.getServer().getPluginManager().registerEvents(new JoinListener(this, settingsManager, playerDataManager), this);
        this.getServer().getPluginManager().registerEvents(new QuitListener(this, settingsManager, playerDataManager), this);
        this.getServer().getPluginManager().registerEvents(new RewardListener(this, settingsManager, localeManager, rewardManager), this);

        // Load player data for any online players that joined before the plugin was fully enabled.
        // This is mostly for plugman edge cases, but 99% of the time is not necessary.
        this.getServer().getOnlinePlayers().forEach(player -> playerDataManager.loadPlayerData(player.getUniqueId()));

        reload();
    }

    @Override
    public void onDisable() {
        if(guiManager != null) guiManager.closeOpenGUIs(true);

        if(databaseManager != null) databaseManager.handlePluginDisable();
    }

    /**
     * Main reload method that reloads all plugin data.
     */
    public void reload() {
        if(guiManager != null) guiManager.closeOpenGUIs(false);

        settingsManager.reload();
        localeManager.reload();
        guiConfigManager.reload();
        playerDataManager.migrateLegacyPlayerSettings();
    }

    /**
     * Sets up bstats
     */
    private void setupBStats() {
        int pluginId = 23211;
        new Metrics(this, pluginId);
    }

    /**
     * Checks if the Server has the proper SkyLib version.
     * @return true if it does, false if not.
     */
    private boolean checkSkyLibVersion() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        Plugin skyLib = pluginManager.getPlugin("SkyLib");
        if (skyLib != null) {
            String version = skyLib.getPluginMeta().getVersion();
            String[] splitVersion = version.split("\\.");
            int second = Integer.parseInt(splitVersion[1]);

            if(second >= 3) {
                return true;
            }
        }

        this.getComponentLogger().error(AdventureUtil.serialize("SkyLib Version 1.3.0.0 or newer is required to run this plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }

    /**
     * Checks for Vault as a dependency and sets up the Economy instance.
     */
    private boolean setupEconomy() {
        if(getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                this.economy = rsp.getProvider();

                return true;
            }
        }

        this.getComponentLogger().error(MiniMessage.miniMessage().deserialize("<red>SkyShop has been disabled due to no Vault dependency found!</red>"));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }
}
