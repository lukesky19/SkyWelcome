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
package com.github.lukesky19.skywelcome.paper;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.libs.bstats.bukkit.Metrics;
import com.github.lukesky19.skylib.paper.api.gui.impl.UUIDGUIListener;
import com.github.lukesky19.skylib.paper.api.gui.impl.UUIDGUIManager;
import com.github.lukesky19.skylib.paper.api.plugin.SkyPlugin;
import com.github.lukesky19.skywelcome.common.database.DatabaseManager;
import com.github.lukesky19.skywelcome.paper.commands.SkyWelcomeCommand;
import com.github.lukesky19.skywelcome.paper.commands.arguments.toggle.ToggleCommand;
import com.github.lukesky19.skywelcome.paper.gui.config.ChangeGUIConfigManager;
import com.github.lukesky19.skywelcome.paper.gui.config.JoinGUIConfigManager;
import com.github.lukesky19.skywelcome.paper.gui.config.QuitGUIConfigManager;
import com.github.lukesky19.skywelcome.paper.integration.HookManager;
import com.github.lukesky19.skywelcome.paper.listener.JoinListener;
import com.github.lukesky19.skywelcome.paper.listener.QuitListener;
import com.github.lukesky19.skywelcome.paper.listener.RewardListener;
import com.github.lukesky19.skywelcome.paper.locale.LocaleManager;
import com.github.lukesky19.skywelcome.paper.messaging.MessageBroker;
import com.github.lukesky19.skywelcome.paper.player.PlayerDataManager;
import com.github.lukesky19.skywelcome.paper.reward.RewardManager;
import com.github.lukesky19.skywelcome.paper.settings.SettingsManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * The plugin's main class.
 */
public class SkyWelcomePaper extends SkyPlugin {
    // Logger
    private ComponentLogger logger;

    // Configuration
    private SettingsManager settingsManager;
    private LocaleManager localeManager;

    private JoinGUIConfigManager joinGUIConfigManager;
    private QuitGUIConfigManager quitGUIConfigManager;
    private ChangeGUIConfigManager changeGUIConfigManager;

    // Data
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private UUIDGUIManager guiManager;

    // Integration
    private MessageBroker messageBroker;
    private HookManager hookManager;

    /**
     * Default Constructor.
     */
    public SkyWelcomePaper() {}

    @Override
    public void onEnable() {
        logger = super.getComponentLogger();

        if(!checkSkyLibVersion()) return;

        // Set up bstats
        setupBStats();

        // Configuration managers
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);
        joinGUIConfigManager = new JoinGUIConfigManager(this);
        quitGUIConfigManager = new QuitGUIConfigManager(this);
        changeGUIConfigManager = new ChangeGUIConfigManager(this);

        // Data managers
        databaseManager = new DatabaseManager(this, settingsManager);
        playerDataManager = new PlayerDataManager(this, databaseManager,  settingsManager);
        guiManager = new UUIDGUIManager();

        // Setup hooks/integration
        messageBroker = new MessageBroker(this, settingsManager, playerDataManager);
        hookManager = new HookManager(this, playerDataManager, messageBroker);

        // Reward manager
        RewardManager rewardManager = new RewardManager(this, settingsManager, localeManager, hookManager);

        // Register listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new UUIDGUIListener(guiManager), this);
        pluginManager.registerEvents(new RewardListener(this, settingsManager, localeManager, rewardManager), this);
        pluginManager.registerEvents(new JoinListener(this, settingsManager, playerDataManager), this);
        pluginManager.registerEvents(new QuitListener(this, settingsManager, playerDataManager), this);

        // Register commands
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands -> {
                    SkyWelcomeCommand skyWelcomeCommand = new SkyWelcomeCommand(this, settingsManager, localeManager, joinGUIConfigManager, quitGUIConfigManager, changeGUIConfigManager, playerDataManager, hookManager, guiManager, messageBroker);
                    commands.registrar().register(skyWelcomeCommand.createCommand(), "Command to manage and use the SkyWelcome plugin.");

                    ToggleCommand toggleCommand = new ToggleCommand(this, settingsManager, localeManager, playerDataManager, messageBroker);
                    commands.registrar().register(toggleCommand.createCommand(), "Command shortcuts to toggle join, leave, and motd messages.");
                });

        // Load plugin data
        reload();
    }

    @Override
    public void onDisable() {
        if(guiManager != null) guiManager.closeOpenGUIs(true);

        if(databaseManager != null) databaseManager.shutdownDatabase();

        if(messageBroker != null) messageBroker.cleanup();
    }

    /**
     * Main reload method that reloads all plugin data.
     */
    public void reload() {
        // Close any open GUIS
        guiManager.closeOpenGUIs(false);

        // Close and clean up message broker
        messageBroker.cleanup();

        // Clear stored player data
        playerDataManager.clearPlayerData();

        // Shutdown the database
        databaseManager.shutdownDatabase().join();

        // Reload configuration
        settingsManager.loadConfiguration();
        localeManager.loadConfiguration();
        joinGUIConfigManager.loadConfiguration();
        quitGUIConfigManager.loadConfiguration();
        changeGUIConfigManager.loadConfiguration();

        // Migrate legacy player data (if any)
        playerDataManager.migrateLegacyPlayerSettings();

        // Initialize depending on whether proxy mode is enabled or not.
        if(settingsManager.isProxyEnabled()) {
            // Register plugin messaging channels
            messageBroker.init();

            // Send reload notice to proxy
            messageBroker.sendReloadNotice();
        } else {
            // Initialize the database
            databaseManager.init();

            // Load player data for any online players that joined before the plugin was fully enabled.
            // This is mostly for plugman edge cases, but 99% of the time is not necessary.
            this.getServer().getOnlinePlayers().forEach(player ->
                    playerDataManager.loadPlayerData(player.getUniqueId()));
        }
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
        if(skyLib != null && skyLib.isEnabled()) {
            String version = skyLib.getPluginMeta().getVersion();

            String[] splitVersion = version.split("\\.");
            int first = Integer.parseInt(splitVersion[0]);
            int third = Integer.parseInt(splitVersion[2]);

            if(first >= 2 && third >= 1) {
                return true;
            }
        }

        logger.error(AdventureUtility.deserialize("SkyLib Version 2.0.1.0 or newer is required to run this plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }
}