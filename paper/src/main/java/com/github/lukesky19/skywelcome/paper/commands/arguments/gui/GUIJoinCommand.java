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
package com.github.lukesky19.skywelcome.paper.commands.arguments.gui;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.paper.api.gui.impl.UUIDGUIManager;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.gui.JoinGUI;
import com.github.lukesky19.skywelcome.paper.gui.config.JoinGUIConfigManager;
import com.github.lukesky19.skywelcome.paper.integration.HookManager;
import com.github.lukesky19.skywelcome.paper.locale.Locale;
import com.github.lukesky19.skywelcome.paper.locale.LocaleManager;
import com.github.lukesky19.skywelcome.paper.messaging.MessageBroker;
import com.github.lukesky19.skywelcome.paper.player.PlayerDataManager;
import com.github.lukesky19.skywelcome.paper.settings.SettingsManager;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * This class is used to create the join command argument.
 */
public class GUIJoinCommand {
    private final @NonNull SkyWelcomePaper skyWelcome;
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull JoinGUIConfigManager joinGUIConfigManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull HookManager hookManager;
    private final @NonNull UUIDGUIManager guiManager;
    private final @NonNull MessageBroker messageBroker;

    /**
     * Default Constructor.
     * You should use {@link #GUIJoinCommand(SkyWelcomePaper, SettingsManager, LocaleManager, JoinGUIConfigManager, PlayerDataManager, HookManager, UUIDGUIManager, MessageBroker)} instead.
     * @deprecated You should use {@link #GUIJoinCommand(SkyWelcomePaper, SettingsManager, LocaleManager, JoinGUIConfigManager,PlayerDataManager, HookManager, UUIDGUIManager, MessageBroker)} instead.
     * @throws RuntimeException if used.
     */
    @Deprecated
    public GUIJoinCommand() {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param joinGUIConfigManager A {@link JoinGUIConfigManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param hookManager A {@link HookManager} instance.
     * @param guiManager A {@link UUIDGUIManager} instance.
     * @param messageBroker A {@link MessageBroker} instance.
     */
    public GUIJoinCommand(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull SettingsManager settingsManager,
            @NonNull LocaleManager localeManager,
            @NonNull JoinGUIConfigManager joinGUIConfigManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull HookManager hookManager,
            @NonNull UUIDGUIManager guiManager,
            @NonNull MessageBroker messageBroker) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.joinGUIConfigManager = joinGUIConfigManager;
        this.playerDataManager = playerDataManager;
        this.hookManager = hookManager;
        this.guiManager = guiManager;
        this.messageBroker = messageBroker;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the join command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} for the join command argument.
     */
    public @NonNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("join")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.gui.join") && ctx.getSender() instanceof Player)
                .executes(ctx -> {
                    Locale locale = localeManager.getConfiguration();
                    Player player = (Player) ctx.getSource().getSender();

                    if(settingsManager.getJoinMessages() == null) {
                        logger.error(AdventureUtility.deserialize("Unable to open the join GUI for player " + player.getName() + " due to invalid plugin settings."));
                        player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
                    if(playerData == null) {
                        logger.error(AdventureUtility.plain("Unable to open the join gui for player " + player.getName() + " due to no player data found."));
                        player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    JoinGUI joinGUI = new JoinGUI(skyWelcome, guiManager, player, settingsManager, joinGUIConfigManager, playerDataManager, messageBroker, hookManager, playerData, settingsManager.getJoinMessages());
                    boolean creationResult = joinGUI.create();
                    if(!creationResult) {
                        logger.error(AdventureUtility.deserialize("Unable to create the InventoryView for the join GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    boolean updateResult = joinGUI.update();
                    if(!updateResult) {
                        logger.error(AdventureUtility.deserialize("Unable to decorate the join GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    boolean openResult = joinGUI.open();
                    if(!openResult) {
                        logger.error(AdventureUtility.deserialize("Unable to open the join GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    return 1;
                })
                .build();
    }
}