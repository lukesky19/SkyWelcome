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
package com.github.lukesky19.skywelcome.paper.reward;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.paper.api.adventure.PaperAdventureUtility;
import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackBuilder;
import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skylib.paper.api.placeholderapi.PlaceholderAPIUtil;
import com.github.lukesky19.skylib.paper.api.player.PlayerUtil;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.integration.HookManager;
import com.github.lukesky19.skywelcome.paper.integration.hooks.EconomyHook;
import com.github.lukesky19.skywelcome.paper.locale.LocaleManager;
import com.github.lukesky19.skywelcome.paper.settings.Settings;
import com.github.lukesky19.skywelcome.paper.settings.SettingsManager;
import com.github.lukesky19.skywelcome.paper.settings.WelcomeRewards;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * This class manages the distribution of rewards to players.
 */
public class RewardManager {
    private final @NonNull SkyWelcomePaper skyWelcome;
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull HookManager hookManager;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public RewardManager(
            @NonNull SkyWelcomePaper skyWelcome,
            @NonNull SettingsManager settingsManager,
            @NonNull LocaleManager localeManager,
            @NonNull HookManager hookManager) {
        this.skyWelcome = skyWelcome;
        this.logger = skyWelcome.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.hookManager = hookManager;
    }

    /**
     * Give welcome rewards to the player provided.
     * @param player The {@link Player}.
     */
    public void giveReward(@NonNull Player player) {
        Settings settings = settingsManager.getConfiguration();
        if(settings == null || settings.welcomeRewards().enabled() == null) {
            logger.warn(AdventureUtility.deserialize("Unable to give rewards to " + player.getName() + " due to invalid plugin settings."));
            return;
        }
        WelcomeRewards welcomeRewards = settings.welcomeRewards();
        if(!welcomeRewards.enabled()) return;

        if(!welcomeRewards.items().isEmpty()) giveItems(player, welcomeRewards.items());
        if(welcomeRewards.cash() != null) giveMoney(player, welcomeRewards.cash());
        if(!welcomeRewards.commands().isEmpty()) runCommands(player, welcomeRewards.commands());

        for(String msg : welcomeRewards.messages()) {
            player.sendMessage(PaperAdventureUtility.deserialize(player, localeManager.getConfiguration().prefix() + msg));
        }
    }

    /**
     * Give the reward {@link ItemStack}s created from the {@link List} of {@link ItemStackConfig}s to the player.
     * @param player The {@link Player}.
     * @param itemStackConfigList The {@link List} of {@link ItemStackConfig}s.
     */
    private void giveItems(@NonNull Player player, @NonNull List<ItemStackConfig> itemStackConfigList) {
        for(ItemStackConfig itemStackConfig : itemStackConfigList) {
            Optional<ItemStack> optionalItemStack = new ItemStackBuilder(logger)
                    .fromItemStackConfig(itemStackConfig, null, List.of())
                    .buildItemStack();
            if(optionalItemStack.isPresent()) {
                PlayerUtil.giveItem(player.getInventory(), optionalItemStack.get(), optionalItemStack.get().getAmount(), player.getLocation());
            } else {
                logger.warn(AdventureUtility.deserialize("Unable to give reward item to player due to invalid ItemStackConfig."));
            }
        }
    }

    /**
     * Give the money to the player provided.
     * @param player The {@link Player}.
     * @param money The money to give.
     */
    private void giveMoney(@NonNull Player player, double money) {
        EconomyHook economyHook = hookManager.getHook(EconomyHook.class);
        if(!economyHook.isHooked()) return;

        economyHook.addToBalance(player, money);
    }

    /**
     * Execute the commands in console. The player is used for parsing placeholders.
     * @param player The {@link Player}.
     * @param commands The {@link List} of {@link String}s for commands.
     */
    private void runCommands(@NonNull Player player, @NonNull List<String> commands) {
        ConsoleCommandSender commandSender = skyWelcome.getServer().getConsoleSender();
        for(String command : commands) {
            skyWelcome.getServer().dispatchCommand(commandSender, PlaceholderAPIUtil.parsePlaceholders(player, command));
        }
    }
}