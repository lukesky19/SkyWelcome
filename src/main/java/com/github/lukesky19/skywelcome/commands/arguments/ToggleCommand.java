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
import com.github.lukesky19.skywelcome.config.locale.Locale;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.data.player.PlayerData;
import com.github.lukesky19.skywelcome.manager.PlayerDataManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * This class is used to create the gui command argument.
 */
public class ToggleCommand {
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull PlayerDataManager playerDataManager;

    /**
     * Default Constructor.
     * You should use {@link #ToggleCommand(SkyWelcome, LocaleManager, PlayerDataManager)} instead.
     * @deprecated You should use {@link #ToggleCommand(SkyWelcome, LocaleManager, PlayerDataManager)} instead.
     * @throws RuntimeException if used.
     */
    @Deprecated
    public ToggleCommand() {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public ToggleCommand(
            @NotNull SkyWelcome skyWelcome,
            @NotNull LocaleManager localeManager,
            @NotNull PlayerDataManager playerDataManager) {
        this.logger = skyWelcome.getComponentLogger();
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the gui command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} for the gui command argument.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("toggle")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.toggle") && ctx.getSender() instanceof Player);

        builder.then(Commands.literal("join")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.toggle.join"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    Player player = (Player) ctx.getSource().getSender();
                    UUID uuid = player.getUniqueId();
                    @Nullable PlayerData playerData = playerDataManager.getPlayerData(uuid);

                    if(playerData == null) {
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + "<red>Unable to toggle the sending of your join message due to invalid player data.</red>"));
                        logger.warn(AdventureUtil.serialize("Unable to toggle the sending of player " + player.getName() + "'s join message due to invalid player data."));
                        return 0;
                    }

                    playerData.setSendJoin(!playerData.isSendJoin());

                    if(player.isOnline() && player.isConnected()) {
                        if(playerData.isSendJoin()) {
                            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.joinEnabled()));
                        } else {
                            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.joinDisabled()));
                        }
                    }

                    playerDataManager.savePlayerData(uuid, playerData);

                    return 1;
                }));

        builder.then(Commands.literal("motd")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.toggle.motd"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    Player player = (Player) ctx.getSource().getSender();
                    UUID uuid = player.getUniqueId();
                    @Nullable PlayerData playerData = playerDataManager.getPlayerData(uuid);

                    if(playerData == null) {
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + "<red>Unable to toggle the sending of the motd message due to invalid player data.</red>"));
                        logger.warn(AdventureUtil.serialize("Unable to toggle the sending the motd message for player " + player.getName() + " due to invalid player data."));
                        return 0;
                    }

                    playerData.setSendMotd(!playerData.isSendMotd());

                    if(player.isOnline() && player.isConnected()) {
                        if(playerData.isSendMotd()) {
                            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.motdEnabled()));
                        } else {
                            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.motdDisabled()));
                        }
                    }

                    playerDataManager.savePlayerData(uuid, playerData);

                    return 1;
                }));

        builder.then(Commands.literal("leave")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.toggle.leave"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    Player player = (Player) ctx.getSource().getSender();
                    UUID uuid = player.getUniqueId();
                    @Nullable PlayerData playerData = playerDataManager.getPlayerData(uuid);

                    if(playerData == null) {
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + "<red>Unable to toggle the sending of your leave message due to invalid player data.</red>"));
                        logger.warn(AdventureUtil.serialize("Unable to toggle the sending of player " + player.getName() + "'s leave message due to invalid player data."));
                        return 0;
                    }

                    playerData.setSendLeave(!playerData.isSendLeave());

                    if(player.isOnline() && player.isConnected()) {
                        if(playerData.isSendLeave()) {
                            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.quitEnabled()));
                        } else {
                            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.quitDisabled()));
                        }
                    }

                    playerDataManager.savePlayerData(uuid, playerData);

                    return 1;
                }));

        builder.then(Commands.literal("quit")
                .requires(ctx -> ctx.getSender().hasPermission("skywelcome.commands.skywelcome.toggle.quit"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    Player player = (Player) ctx.getSource().getSender();
                    UUID uuid = player.getUniqueId();
                    @Nullable PlayerData playerData = playerDataManager.getPlayerData(uuid);

                    if(playerData == null) {
                        player.sendMessage(AdventureUtil.serialize(locale.prefix() + "<red>Unable to toggle the sending of your leave message due to invalid player data.</red>"));
                        logger.warn(AdventureUtil.serialize("Unable to toggle the sending of player " + player.getName() + "'s leave message due to invalid player data."));
                        return 0;
                    }

                    playerData.setSendLeave(!playerData.isSendLeave());

                    if(player.isOnline() && player.isConnected()) {
                        if(playerData.isSendLeave()) {
                            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.quitEnabled()));
                        } else {
                            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.quitDisabled()));
                        }
                    }

                    playerDataManager.savePlayerData(uuid, playerData);

                    return 1;
                }));

        return builder.build();
    }
}
