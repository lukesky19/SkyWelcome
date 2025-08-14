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
package com.github.lukesky19.skywelcome.commands;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.commands.arguments.GuiCommand;
import com.github.lukesky19.skywelcome.commands.arguments.HelpCommand;
import com.github.lukesky19.skywelcome.commands.arguments.ReloadCommand;
import com.github.lukesky19.skywelcome.commands.arguments.ToggleCommand;
import com.github.lukesky19.skywelcome.config.gui.GUIConfigManager;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.manager.GUIManager;
import com.github.lukesky19.skywelcome.manager.HeadDatabaseManager;
import com.github.lukesky19.skywelcome.manager.PlayerDataManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used to create the skywelcome command.
 */
public class SkyWelcomeCommand {
    private final @NotNull SkyWelcome skyWelcome;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull GUIConfigManager guiConfigManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull HeadDatabaseManager headDatabaseManager;
    private final @NotNull GUIManager guiManager;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param guiConfigManager A {@link GUIConfigManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param headDatabaseManager A {@link HeadDatabaseManager} instance.
     * @param guiManager A {@link GUIManager} instance.
     */
    public SkyWelcomeCommand(
            @NotNull SkyWelcome skyWelcome,
            @NotNull SettingsManager settingsManager,
            @NotNull LocaleManager localeManager,
            @NotNull GUIConfigManager guiConfigManager,
            @NotNull PlayerDataManager playerDataManager,
            @NotNull HeadDatabaseManager headDatabaseManager,
            @NotNull GUIManager guiManager) {
        this.skyWelcome = skyWelcome;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.guiConfigManager = guiConfigManager;
        this.playerDataManager = playerDataManager;
        this.headDatabaseManager = headDatabaseManager;
        this.guiManager = guiManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the skywelcome command.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} for the skywelcome command.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("skywelcome")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome"));

        GuiCommand guiCommand = new GuiCommand(skyWelcome, settingsManager, localeManager, guiConfigManager, playerDataManager, headDatabaseManager, guiManager);
        HelpCommand helpCommand = new HelpCommand(localeManager);
        ReloadCommand reloadCommand = new ReloadCommand(skyWelcome, localeManager);
        ToggleCommand toggleCommand = new ToggleCommand(skyWelcome, localeManager, playerDataManager);

        builder.then(guiCommand.createCommand());
        builder.then(helpCommand.createCommand());
        builder.then(reloadCommand.createCommand());
        builder.then(toggleCommand.createCommand());

        return builder.build();
    }
}
