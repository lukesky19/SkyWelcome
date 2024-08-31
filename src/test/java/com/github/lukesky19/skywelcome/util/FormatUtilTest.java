package com.github.lukesky19.skywelcome.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.AbstractMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormatUtilTest {
    @Mock
    PlaceholderAPI placeholderAPI;
    @Mock
    Player player;
    @Mock
    Server server;
    @Mock
    Bukkit bukkit;
    Map<String, String> codeConversion;

    @BeforeEach
    void setUp() {
        codeConversion = Map.ofEntries(
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
                new AbstractMap.SimpleEntry<>("§r", "<reset>"),
                new AbstractMap.SimpleEntry<>("&0", "<black>"),
                new AbstractMap.SimpleEntry<>("&1", "<dark_blue>"),
                new AbstractMap.SimpleEntry<>("&2", "<dark_green>"),
                new AbstractMap.SimpleEntry<>("&3", "<dark_aqua>"),
                new AbstractMap.SimpleEntry<>("&4", "<dark_red>"),
                new AbstractMap.SimpleEntry<>("&5", "<dark_purple>"),
                new AbstractMap.SimpleEntry<>("&6", "<gold>"),
                new AbstractMap.SimpleEntry<>("&7", "<gray>"),
                new AbstractMap.SimpleEntry<>("&8", "<dark_gray>"),
                new AbstractMap.SimpleEntry<>("&9", "<blue>"),
                new AbstractMap.SimpleEntry<>("&a", "<green>"),
                new AbstractMap.SimpleEntry<>("&b", "<aqua>"),
                new AbstractMap.SimpleEntry<>("&c", "<red>"),
                new AbstractMap.SimpleEntry<>("&d", "<light_purple>"),
                new AbstractMap.SimpleEntry<>("&e", "<yellow>"),
                new AbstractMap.SimpleEntry<>("&f", "<white>"),
                new AbstractMap.SimpleEntry<>("&k", "<obfuscated>"),
                new AbstractMap.SimpleEntry<>("&l", "<bold>"),
                new AbstractMap.SimpleEntry<>("&m", "<strikethrough>"),
                new AbstractMap.SimpleEntry<>("&n", "<underlined>"),
                new AbstractMap.SimpleEntry<>("&o", "<italic>"),
                new AbstractMap.SimpleEntry<>("&r", "<reset>")
        );
    }

    // Parsing PlaceholderAPI placeholders can only be done on a live server.
    // Therefore, that section is not tested here.
    @Test
    public void testFormatPlayerString() {
        Component test = FormatUtil.format(player, "Test Message. Username: §4lukesky&cwlker&319");

        for(Map.Entry<String, String> codeEntry : codeConversion.entrySet()) {
            String code = codeEntry.getKey();

            assertFalse(test.contains(Component.text(code)));
        }
    }

    @Test
    public void testFormatStringOnly() {

    }
}