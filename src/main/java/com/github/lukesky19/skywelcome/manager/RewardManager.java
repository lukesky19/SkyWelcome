package com.github.lukesky19.skywelcome.manager;

import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.format.PlaceholderAPIUtil;
import com.github.lukesky19.skylib.player.PlayerUtil;
import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.locale.Locale;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.enums.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RewardManager {
    private final SkyWelcome skyWelcome;
    private final SettingsManager settingsManager;
    private final LocaleManager localeManager;
    private Settings settings;
    private Locale locale;

    public RewardManager(SkyWelcome skyWelcome, SettingsManager settingsManager, LocaleManager localeManager) {
        this.skyWelcome = skyWelcome;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
    }

    public void reload() {
        settings = settingsManager.getSettings();
        locale = localeManager.getLocale();
    }

    public void giveReward(Player player) {
        if(skyWelcome.isPluginDisabled()) return;
        if(!settings.welcomeRewards().enabled()) return;

        RewardType type = RewardType.getType(settings.welcomeRewards().type());
        if(type != null) {
            switch(type) {
                case ITEM -> giveItem(settings.welcomeRewards().item().material(), settings.welcomeRewards().item().amount(), player);

                case CASH -> giveCash(settings.welcomeRewards().cash(), player);

                case COMMANDS -> runCommands(settings.welcomeRewards().commands(), player);
            }
        }
    }

    private void giveItem(String material, int amount, Player player) {
        Material mat = Material.getMaterial(material);
        if(mat != null) {
            ItemStack itemStack = new ItemStack(mat);

            PlayerUtil.giveItem(player, itemStack, amount);

            for(String msg : settings.welcomeRewards().messages()) {
                player.sendMessage(FormatUtil.format(player, locale.prefix() + msg));
            }
        }
    }

    private void giveCash(double cash, Player player) {
        skyWelcome.getEconomy().depositPlayer(player, cash);

        for(String msg : settings.welcomeRewards().messages()) {
            player.sendMessage(FormatUtil.format(player, locale.prefix() + msg));
        }
    }

    private void runCommands(List<String> commands, Player player) {
        for(String command : commands) {
            skyWelcome.getServer().dispatchCommand(skyWelcome.getServer().getConsoleSender(), PlaceholderAPIUtil.parsePlaceholders(player, command));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPIUtil.parsePlaceholders(player, command));
        }

        for(String msg : settings.welcomeRewards().messages()) {
            player.sendMessage(FormatUtil.format(player, locale.prefix() + msg));
        }
    }
}
