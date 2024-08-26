package com.github.lukesky19.skywelcome.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Map;

public class FormatUtil {
    static final Map<String, String> codeConversion = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("§0", "<black>"),
            new AbstractMap.SimpleEntry<>("§1", "<dark_blue>"),
            new AbstractMap.SimpleEntry<>("§2", "<dark_green>"),
            new AbstractMap.SimpleEntry<>("§3", "<dark_aqua>"),
            new AbstractMap.SimpleEntry<>("§4", "<dark_red>"),
            new AbstractMap.SimpleEntry<>("§5", "<dark_purple>"),
            new AbstractMap.SimpleEntry<>("§6", "<gold>"),
            new AbstractMap.SimpleEntry<>("§7", "<gray>"),
            new AbstractMap.SimpleEntry<>("§8", "<dark_gray>"),
            new AbstractMap.SimpleEntry<>("§9", "<blue>"),
            new AbstractMap.SimpleEntry<>("§a", "<green>"),
            new AbstractMap.SimpleEntry<>("§b", "<aqua>"),
            new AbstractMap.SimpleEntry<>("§c", "<red>"),
            new AbstractMap.SimpleEntry<>("§d", "<light_purple>"),
            new AbstractMap.SimpleEntry<>("§e", "<yellow>"),
            new AbstractMap.SimpleEntry<>("§f", "<white>"),
            new AbstractMap.SimpleEntry<>("§k", "<obfuscated>"),
            new AbstractMap.SimpleEntry<>("§l", "<bold>"),
            new AbstractMap.SimpleEntry<>("§m", "<strikethrough>"),
            new AbstractMap.SimpleEntry<>("§n", "<underlined>"),
            new AbstractMap.SimpleEntry<>("§o", "<italic>"),
            new AbstractMap.SimpleEntry<>("§r", "<reset>")
    );

    /**
     * Converts a String to a Component.
     * First, parses PlaceholderAPI placeholders.
     * Then, converts legacy §/& codes into a component format using the handleLegacyCodes(String message).
     * Lastly, converts the new String into a Component using MiniMessage.
     * @param player A player that placeholders will parse for.
     * @param message A string to format.
     * @return A formatted Component.
     */
    public static Component format(Player player, String message) {
        return MiniMessage.miniMessage().deserialize(
                handleLegacyCodes(
                        PlaceholderAPIUtil.parsePlaceholders(player, message))).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static String handleLegacyCodes(String message) {
        StringBuilder builder = new StringBuilder(message);

        for(Map.Entry<String, String> codeEntry : codeConversion.entrySet()) {
            String target = codeEntry.getKey();
            String replacement = codeEntry.getValue();

            while(builder.toString().contains(target)) {
                int startIndex = builder.toString().indexOf(target);
                int stopIndex = startIndex + target.length();

                builder.replace(startIndex, stopIndex, replacement);
            }
        }

        return builder.toString();
    }
}
