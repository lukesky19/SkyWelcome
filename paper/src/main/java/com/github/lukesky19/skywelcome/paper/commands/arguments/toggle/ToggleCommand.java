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

import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.locale.LocaleManager;
import com.github.lukesky19.skywelcome.paper.messaging.MessageBroker;
import com.github.lukesky19.skywelcome.paper.player.PlayerDataManager;
import com.github.lukesky19.skywelcome.paper.settings.SettingsManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * This class is used to create the toggle command argument.
 */
public class ToggleCommand {
    private final @NonNull SkyWelcomePaper skyWelcome;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull MessageBroker messageBroker;

    /**
     * Default Constructor.
     * You should use {@link #ToggleCommand(SkyWelcomePaper, SettingsManager, LocaleManager, PlayerDataManager, MessageBroker)} instead.
     * @deprecated You should use {@link #ToggleCommand(SkyWelcomePaper, SettingsManager, LocaleManager, PlayerDataManager, MessageBroker)} instead.
     * @throws RuntimeException if used.
     */
    @Deprecated
    public ToggleCommand() {
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
    public ToggleCommand(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull SettingsManager settingsManager,
            @NonNull LocaleManager localeManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull MessageBroker messageBroker) {
        this.skyWelcome = skyWelcome;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
        this.messageBroker = messageBroker;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the toggle command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} for the toggle command argument.
     */
    public @NonNull LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("toggle")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.toggle") && ctx.getSender() instanceof Player);

        builder.then(new ToggleJoinCommand(skyWelcome, settingsManager, localeManager, playerDataManager, messageBroker)
                .createCommand());
        builder.then(new ToggleLeaveCommand(skyWelcome, settingsManager, localeManager, playerDataManager, messageBroker)
                .createCommand("leave"));
        builder.then(new ToggleLeaveCommand(skyWelcome, settingsManager, localeManager, playerDataManager, messageBroker)
                .createCommand("quit"));
        builder.then(new ToggleChangeCommand(skyWelcome, settingsManager, localeManager, playerDataManager, messageBroker)
                .createCommand());
        builder.then(new ToggleMotDCommand(skyWelcome, settingsManager, localeManager, playerDataManager, messageBroker)
                .createCommand());

        return builder.build();
    }
}