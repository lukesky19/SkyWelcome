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
package com.github.lukesky19.skywelcome.manager;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.itemstack.ItemStackBuilder;
import com.github.lukesky19.skylib.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skylib.api.placeholderapi.PlaceholderAPIUtil;
import com.github.lukesky19.skylib.api.player.PlayerUtil;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * This class manages the distribution of rewards to players.
 */
public class RewardManager {
    private final @NotNull SkyWelcome skyWelcome;
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull LocaleManager localeManager;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcome} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     */
    public RewardManager(
            @NotNull SkyWelcome skyWelcome,
            @NotNull SettingsManager settingsManager,
            @NotNull LocaleManager localeManager) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
    }

    /**
     * Give welcome rewards to the player provided.
     * @param player The {@link Player}.
     */
    public void giveReward(@NotNull Player player) {
        Settings settings = settingsManager.getSettings();
        if(settings == null || settings.welcomeRewards().enabled() == null) {
            logger.warn(AdventureUtil.serialize("Unable to give rewards to " + player.getName() + " due to invalid plugin settings."));
            return;
        }
        if(!settings.welcomeRewards().enabled()) return;

        if(!settings.welcomeRewards().items().isEmpty()) giveItems(player, settings.welcomeRewards().items());
        if(settings.welcomeRewards().cash() != null) giveMoney(player, settings.welcomeRewards().cash());
        if(!settings.welcomeRewards().commands().isEmpty()) runCommands(player, settings.welcomeRewards().commands());

        for(String msg : settings.welcomeRewards().messages()) {
            player.sendMessage(AdventureUtil.serialize(player, localeManager.getLocale().prefix() + msg));
        }
    }

    /**
     * Give the reward {@link ItemStack}s created from the {@link List} of {@link ItemStackConfig}s to the player.
     * @param player The {@link Player}.
     * @param itemStackConfigList The {@link List} of {@link ItemStackConfig}s.
     */
    private void giveItems(@NotNull Player player, @NotNull List<ItemStackConfig> itemStackConfigList) {
        for(ItemStackConfig itemStackConfig : itemStackConfigList) {
            Optional<ItemStack> optionalItemStack = new ItemStackBuilder(logger).fromItemStackConfig(itemStackConfig, null, null, List.of()).buildItemStack();
            if(optionalItemStack.isPresent()) {
                PlayerUtil.giveItem(player.getInventory(), optionalItemStack.get(), optionalItemStack.get().getAmount(), player.getLocation());
            } else {
                logger.warn(AdventureUtil.serialize("Unable to give reward item to player due to invalid ItemStackConfig."));
            }
        }
    }

    /**
     * Give the money to the player provided.
     * @param player The {@link Player}.
     * @param money The money to give.
     */
    private void giveMoney(@NotNull Player player, double money) {
        skyWelcome.getEconomy().depositPlayer(player, money);
    }

    /**
     * Execute the commands in console. The player is used for parsing placeholders.
     * @param player The {@link Player}.
     * @param commands The {@link List} of {@link String}s for commands.
     */
    private void runCommands(@NotNull Player player, @NotNull List<String> commands) {
        ConsoleCommandSender commandSender = skyWelcome.getServer().getConsoleSender();
        for(String command : commands) {
            skyWelcome.getServer().dispatchCommand(commandSender, PlaceholderAPIUtil.parsePlaceholders(player, command));
        }
    }
}
