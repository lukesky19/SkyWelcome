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
package com.github.lukesky19.skywelcome.paper.integration.hooks;

import com.github.lukesky19.skylib.common.api.integration.Hook;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class manages interfacing with Vault.
 */
public class EconomyHook implements Hook {
    private final @NotNull SkyWelcomePaper skyWelcome;
    private @Nullable Economy economy;

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     */
    public EconomyHook(@NotNull SkyWelcomePaper skyWelcome) {
        this.skyWelcome = skyWelcome;
    }

    /**
     * Attempt to get the {@link Economy} from Vault.
     */
    @Override
    public void initialize() {
        if(skyWelcome.getServer().getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> rsp = skyWelcome.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                this.economy = rsp.getProvider();
            }
        }
    }

    /**
     * Is the hook initialized?
     * @return true if hooked, otherwise false.
     */
    @Override
    public boolean isHooked() {
        return economy != null;
    }

    /**
     * Add the amount provided to the player's balance.
     * @apiNote If the economy was not hooked into, this method will do nothing. Can be checked with {@link #isHooked()}.
     * @param player The {@link Player}.
     * @param amount The amount to add.
     */
    public void addToBalance(@NotNull Player player, double amount) {
        if(economy == null) return;

        economy.depositPlayer(player, amount);
    }
}