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
package com.github.lukesky19.skywelcome.paper.commands.arguments.toggle;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.paper.api.adventure.PaperAdventureUtility;
import com.github.lukesky19.skywelcome.common.player.data.PlayerData;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
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

import java.util.UUID;

/**
 * This class is used to create the toggle join command argument.
 */
public class ToggleJoinCommand {
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull MessageBroker messageBroker;

    /**
     * Default Constructor.
     * You should use {@link #ToggleJoinCommand(SkyWelcomePaper, SettingsManager, LocaleManager, PlayerDataManager, MessageBroker)} instead.
     * @deprecated You should use {@link #ToggleJoinCommand(SkyWelcomePaper, SettingsManager, LocaleManager, PlayerDataManager, MessageBroker)} instead.
     * @throws RuntimeException if used.
     */
    @Deprecated
    public ToggleJoinCommand() {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param messageBroker A {@link MessageBroker} instance.
     */
    public ToggleJoinCommand(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull SettingsManager settingsManager,
            @NonNull LocaleManager localeManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull MessageBroker messageBroker) {
        this.logger = skyWelcome.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
        this.messageBroker = messageBroker;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the join command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} for the join command argument.
     */
    public @NonNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("join")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.toggle.join") && ctx.getSender() instanceof Player)
                .executes(ctx -> {
                    Locale locale = localeManager.getConfiguration();
                    Player player = (Player) ctx.getSource().getSender();
                    UUID playerId = player.getUniqueId();

                    PlayerData playerData = playerDataManager.getPlayerData(playerId);
                    if(playerData == null) {
                        player.sendMessage(AdventureUtility.deserialize(locale.prefix() + "<red>Unable to toggle the sending of your join message due to invalid player data.</red>"));
                        logger.warn(AdventureUtility.deserialize("Unable to toggle the sending of player " + player.getName() + "'s join message due to invalid player data."));
                        return 0;
                    }

                    playerData.setSendJoin(!playerData.isSendJoin());

                    if(playerData.isSendJoin()) {
                        player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.joinEnabled()));
                    } else {
                        player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.joinDisabled()));
                    }

                    if(settingsManager.isProxyEnabled()) {
                        messageBroker.sendPlayerData(playerId, playerData);
                    } else {
                        playerDataManager.savePlayerData(playerId, playerData);
                    }

                    return 1;
                })
                .build();
    }
}