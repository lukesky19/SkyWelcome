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
package com.github.lukesky19.skywelcome.commands.arguments;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.gui.GUIConfigManager;
import com.github.lukesky19.skywelcome.config.locale.Locale;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.gui.JoinGUI;
import com.github.lukesky19.skywelcome.gui.QuitGUI;
import com.github.lukesky19.skywelcome.manager.GUIManager;
import com.github.lukesky19.skywelcome.manager.HeadDatabaseManager;
import com.github.lukesky19.skywelcome.manager.PlayerDataManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used to create the gui command argument.
 */
public class GuiCommand {
    private final @NotNull SkyWelcome skyWelcome;
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull GUIConfigManager guiConfigManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull HeadDatabaseManager headDatabaseManager;
    private final @NotNull GUIManager guiManager;

    /**
     * Default Constructor.
     * You should use {@link #GuiCommand(SkyWelcome, SettingsManager, LocaleManager, GUIConfigManager, PlayerDataManager, HeadDatabaseManager, GUIManager)} instead.
     * @deprecated You should use {@link #GuiCommand(SkyWelcome, SettingsManager, LocaleManager, GUIConfigManager, PlayerDataManager, HeadDatabaseManager, GUIManager)} instead.
     * @throws RuntimeException if used.
     */
    @Deprecated
    public GuiCommand() {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

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
    public GuiCommand(@NotNull SkyWelcome skyWelcome,
                      @NotNull SettingsManager settingsManager,
                      @NotNull LocaleManager localeManager,
                      @NotNull GUIConfigManager guiConfigManager,
                      @NotNull PlayerDataManager playerDataManager,
                      @NotNull HeadDatabaseManager headDatabaseManager,
                      @NotNull GUIManager guiManager) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.guiConfigManager = guiConfigManager;
        this.playerDataManager = playerDataManager;
        this.headDatabaseManager = headDatabaseManager;
        this.guiManager = guiManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the gui command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} for the gui command argument.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("gui")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.gui") && ctx.getSender() instanceof Player);

        builder.then(Commands.literal("join")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.gui.join"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    Player player = (Player) ctx.getSource().getSender();

                    JoinGUI joinGUI = new JoinGUI(skyWelcome, guiManager, player, settingsManager, guiConfigManager, playerDataManager, headDatabaseManager);
                    boolean creationResult = joinGUI.create();
                    if(!creationResult) {
                        logger.error(AdventureUtil.serialize("Unable to create the InventoryView for the join GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    boolean updateResult = joinGUI.update();
                    if(!updateResult) {
                        logger.error(AdventureUtil.serialize("Unable to decorate the join GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    boolean openResult = joinGUI.open();
                    if(!openResult) {
                        logger.error(AdventureUtil.serialize("Unable to open the join GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    return 1;
                }));

        builder.then(Commands.literal("leave")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.gui.leave"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    Player player = (Player) ctx.getSource().getSender();

                    QuitGUI quitGUI = new QuitGUI(skyWelcome, guiManager, player, settingsManager, guiConfigManager, playerDataManager, headDatabaseManager);
                    boolean creationResult = quitGUI.create();
                    if(!creationResult) {
                        logger.error(AdventureUtil.serialize("Unable to create the InventoryView for the quit GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    boolean updateResult = quitGUI.update();
                    if(!updateResult) {
                        logger.error(AdventureUtil.serialize("Unable to decorate the quit GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    boolean openResult = quitGUI.open();
                    if(!openResult) {
                        logger.error(AdventureUtil.serialize("Unable to open the quit GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    return 1;
                }));

        builder.then(Commands.literal("quit")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.gui.quit"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    Player player = (Player) ctx.getSource().getSender();

                    QuitGUI quitGUI = new QuitGUI(skyWelcome, guiManager, player, settingsManager, guiConfigManager, playerDataManager, headDatabaseManager);
                    boolean creationResult = quitGUI.create();
                    if(!creationResult) {
                        logger.error(AdventureUtil.serialize("Unable to create the InventoryView for the quit GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    boolean updateResult = quitGUI.update();
                    if(!updateResult) {
                        logger.error(AdventureUtil.serialize("Unable to decorate the quit GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    boolean openResult = quitGUI.open();
                    if(!openResult) {
                        logger.error(AdventureUtil.serialize("Unable to open the quit GUI for player " + player.getName() + " due to a configuration error."));
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.guiOpenError()));
                        return 0;
                    }

                    return 1;
                }));

        return builder.build();
    }
}
