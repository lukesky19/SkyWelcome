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
package com.github.lukesky19.skywelcome.velocity.commands;

import com.github.lukesky19.skywelcome.velocity.SkyWelcomeVelocity;
import com.github.lukesky19.skywelcome.velocity.commands.arguments.ReloadCommand;
import com.github.lukesky19.skywelcome.velocity.locale.LocaleManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import org.jspecify.annotations.NonNull;

/**
 * This class is used to create the skywelcome command.
 */
public class SkyWelcomeCommand {
    private final @NonNull SkyWelcomeVelocity skyWelcome;
    private final @NonNull LocaleManager localeManager;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomeVelocity} instance.
     * @param localeManager A {@link LocaleManager} instance.
     */
    public SkyWelcomeCommand(
            @NonNull SkyWelcomeVelocity skyWelcome,
            @NonNull LocaleManager localeManager) {
        this.skyWelcome = skyWelcome;
        this.localeManager = localeManager;
    }

    /**
     * Creates the {@link BrigadierCommand} for the skywelcome command.
     * @return A {@link BrigadierCommand} for the skywelcome command.
     */
    public @NonNull BrigadierCommand createCommand() {
        LiteralArgumentBuilder<CommandSource> builder = BrigadierCommand.literalArgumentBuilder("skywelcome-velocity");
        builder.requires(commandSource -> commandSource.hasPermission("skywelcome.commands.skywelcome"));

        ReloadCommand reloadCommand = new ReloadCommand(skyWelcome, localeManager);
        builder.then(reloadCommand.createCommand());

        return new BrigadierCommand(builder.build());
    }
}