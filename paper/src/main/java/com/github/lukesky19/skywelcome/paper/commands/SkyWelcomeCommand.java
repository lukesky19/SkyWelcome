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
package com.github.lukesky19.skywelcome.paper.commands;

import com.github.lukesky19.skylib.paper.api.gui.impl.UUIDGUIManager;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.commands.arguments.HelpCommand;
import com.github.lukesky19.skywelcome.paper.commands.arguments.ReloadCommand;
import com.github.lukesky19.skywelcome.paper.commands.arguments.gui.GuiCommand;
import com.github.lukesky19.skywelcome.paper.commands.arguments.toggle.ToggleCommand;
import com.github.lukesky19.skywelcome.paper.gui.config.ChangeGUIConfigManager;
import com.github.lukesky19.skywelcome.paper.gui.config.JoinGUIConfigManager;
import com.github.lukesky19.skywelcome.paper.gui.config.QuitGUIConfigManager;
import com.github.lukesky19.skywelcome.paper.integration.HookManager;
import com.github.lukesky19.skywelcome.paper.locale.LocaleManager;
import com.github.lukesky19.skywelcome.paper.messaging.MessageBroker;
import com.github.lukesky19.skywelcome.paper.player.PlayerDataManager;
import com.github.lukesky19.skywelcome.paper.settings.SettingsManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jspecify.annotations.NonNull;

/**
 * This class is used to create the skywelcome command.
 */
public class SkyWelcomeCommand {
    private final @NonNull SkyWelcomePaper skyWelcome;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull JoinGUIConfigManager joinGUIConfigManager;
    private final @NonNull QuitGUIConfigManager quitGUIConfigManager;
    private final @NonNull ChangeGUIConfigManager changeGUIConfigManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull HookManager hookManager;
    private final @NonNull UUIDGUIManager guiManager;
    private final @NonNull MessageBroker messageBroker;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param joinGUIConfigManager A {@link JoinGUIConfigManager} instance.
     * @param quitGUIConfigManager A {@link QuitGUIConfigManager} instance.
     * @param changeGUIConfigManager A {@link ChangeGUIConfigManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param hookManager A {@link HookManager} instance.
     * @param guiManager A {@link UUIDGUIManager} instance.
     * @param messageBroker A {@link MessageBroker} instance.
     */
    public SkyWelcomeCommand(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull SettingsManager settingsManager,
            @NonNull LocaleManager localeManager,
            @NonNull JoinGUIConfigManager joinGUIConfigManager,
            @NonNull QuitGUIConfigManager quitGUIConfigManager,
            @NonNull ChangeGUIConfigManager changeGUIConfigManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull HookManager hookManager,
            @NonNull UUIDGUIManager guiManager,
            @NonNull MessageBroker messageBroker) {
        this.skyWelcome = skyWelcome;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.joinGUIConfigManager = joinGUIConfigManager;
        this.quitGUIConfigManager = quitGUIConfigManager;
        this.changeGUIConfigManager = changeGUIConfigManager;
        this.playerDataManager = playerDataManager;
        this.hookManager = hookManager;
        this.guiManager = guiManager;
        this.messageBroker = messageBroker;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the skywelcome command.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} for the skywelcome command.
     */
    public @NonNull LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("skywelcome")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome"));

        GuiCommand guiCommand = new GuiCommand(skyWelcome, settingsManager, localeManager, joinGUIConfigManager, quitGUIConfigManager, changeGUIConfigManager, playerDataManager, hookManager, guiManager, messageBroker);
        HelpCommand helpCommand = new HelpCommand(localeManager);
        ReloadCommand reloadCommand = new ReloadCommand(skyWelcome, localeManager);
        ToggleCommand toggleCommand = new ToggleCommand(skyWelcome, settingsManager, localeManager, playerDataManager, messageBroker);

        builder.then(guiCommand.createCommand());
        builder.then(helpCommand.createCommand());
        builder.then(reloadCommand.createCommand());
        builder.then(toggleCommand.createCommand());

        return builder.build();
    }
}