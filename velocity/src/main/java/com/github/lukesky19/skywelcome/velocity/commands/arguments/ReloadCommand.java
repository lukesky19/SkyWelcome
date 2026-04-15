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
package com.github.lukesky19.skywelcome.velocity.commands.arguments;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skywelcome.velocity.SkyWelcomeVelocity;
import com.github.lukesky19.skywelcome.velocity.locale.Locale;
import com.github.lukesky19.skywelcome.velocity.locale.LocaleManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import org.jspecify.annotations.NonNull;

/**
 * This class is used to create the reload command argument.
 */
public class ReloadCommand {
    private final @NonNull SkyWelcomeVelocity skyWelcome;
    private final @NonNull LocaleManager localeManager;

    /**
     * Default Constructor.
     * You should use {@link #ReloadCommand(SkyWelcomeVelocity, LocaleManager)} instead.
     * @deprecated You should use {@link #ReloadCommand(SkyWelcomeVelocity, LocaleManager)} instead.
     * @throws RuntimeException if used.
     */
    @Deprecated
    public ReloadCommand() {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomeVelocity} instance.
     * @param localeManager A {@link LocaleManager} instance.
     */
    public ReloadCommand(@NonNull SkyWelcomeVelocity skyWelcome, @NonNull LocaleManager localeManager) {
        this.skyWelcome = skyWelcome;
        this.localeManager = localeManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSource} for the reload command.
     * @return A {@link LiteralCommandNode} of type {@link CommandSource} for the reload command.
     */
    public @NonNull LiteralCommandNode<CommandSource> createCommand() {
        LiteralArgumentBuilder<CommandSource> builder = BrigadierCommand.literalArgumentBuilder("reload");
        builder.requires(commandSource -> commandSource.hasPermission("skywelcome.commands.skywelcome.reload"));
        builder.executes(commandContext -> {
            Locale locale = localeManager.getConfiguration();
            CommandSource commandSource = commandContext.getSource();

            skyWelcome.reload();

            if(commandSource instanceof Player) {
                commandSource.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.reload()));
            } else {
                commandSource.sendMessage(AdventureUtility.deserialize(locale.reload()));
            }

            return 1;
        });

        return builder.build();
    }
}