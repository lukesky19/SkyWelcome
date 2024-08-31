package com.github.lukesky19.skywelcome.commands;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.locale.Locale;
import com.github.lukesky19.skywelcome.config.locale.LocaleManager;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.player.PlayerSettings;
import com.github.lukesky19.skywelcome.gui.JoinGUI;
import com.github.lukesky19.skywelcome.gui.QuitGUI;
import com.github.lukesky19.skywelcome.util.FormatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkyWelcomeCommandTest {
    @Mock
    Player player;
    @Mock
    ConsoleCommandSender consoleCommandSender;
    @Mock
    ComponentLogger logger;
    @Mock
    SkyWelcome skyWelcome;
    @Mock
    PlayerManager playerManager;
    @Mock
    LocaleManager localeManager;
    @Mock
    JoinGUI joinGUI;
    @Mock
    QuitGUI quitGUI;
    @Mock
    Command command;
    @Mock
    PlayerSettings playerSettings;

    SkyWelcomeCommand skyWelcomeCommand;
    private Locale locale;

    @BeforeEach
    void setUp() {
        locale = new Locale(
                "<gray>[</gray><aqua><bold>SkyWelcome</bold></aqua><gray>]</gray> ",
                getHelpList(),
                "<red>This command is only available in-game.</red>",
                "<aqua>Plugin configuration reloaded.</aqua>",
                "<red>You do not have permission for this command.</red>",
                "<red>Unknown command.</red>",
                "<aqua>You have enabled your join message.</aqua>",
                "<aqua>You have disabled your join message.</aqua>",
                "<aqua>You have enabled your leave message.</aqua>",
                "<aqua>You have disabled your leave message.</aqua>",
                "<aqua>You have enabled your MOTD message.</aqua>",
                "<aqua>You have disabled your MOTD message.</aqua>"
        );

        skyWelcomeCommand = new SkyWelcomeCommand(skyWelcome, playerManager, localeManager, joinGUI, quitGUI);
    }

    private static @NotNull List<String> getHelpList() {
        List<String> helpList = new ArrayList<>();
        helpList.add("<aqua>SkyWelcome is developed by <white><bold>lukeskywlker19</bold></white>.</aqua>");
        helpList.add("<aqua>Source code is released on GitHub: <click:OPEN_URL:https://github.com/lukesky19><yellow><underlined><bold>https://github.com/lukesky19</bold></underlined></yellow></click></aqua>");
        helpList.add(" ");
        helpList.add("<aqua><bold>List of Commands:</bold></aqua>");
        helpList.add("<white>/<aqua>skywelcome <yellow>help</yellow></white>");
        helpList.add("<white>/<aqua>skywelcome <yellow>reload</yellow></white>");
        helpList.add("<white>/<aqua>skywelcome <yellow>toggle</yellow> <yellow><join | leave | quit | motd></yellow></white>");
        helpList.add("<white>/<aqua>skywelcome <yellow>gui</yellow> <yellow><join | leave | quit></yellow></white>");
        return helpList;
    }

    @Test
    public void testBaseCommandAsPlayerNoArgumentsHasHelpPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{});

        ArgumentCaptor<Component> argument = ArgumentCaptor.forClass(Component.class);
        verify(player, times(9)).sendMessage(argument.capture());

        List<Component> list = argument.getAllValues();
        assertEquals(FormatUtil.format(player, locale.prefix() + locale.unknownCommand()), list.get(0));
        assertEquals(FormatUtil.format(player, locale.help().get(0)), list.get(1));
        assertEquals(FormatUtil.format(player, locale.help().get(1)), list.get(2));
        assertEquals(FormatUtil.format(player, locale.help().get(2)), list.get(3));
        assertEquals(FormatUtil.format(player, locale.help().get(3)), list.get(4));
        assertEquals(FormatUtil.format(player, locale.help().get(4)), list.get(5));
        assertEquals(FormatUtil.format(player, locale.help().get(5)), list.get(6));
        assertEquals(FormatUtil.format(player, locale.help().get(6)), list.get(7));
        assertEquals(FormatUtil.format(player, locale.help().get(7)), list.get(8));
    }

    @Test
    public void testBaseCommandAsPlayerNoArgumentsNoHelpPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{});
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.unknownCommand()));
    }

    @Test
    public void testBaseCommandAsConsoleNoArguments() {
        when(skyWelcome.getComponentLogger()).thenReturn(logger);
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(consoleCommandSender, command, "skywelcome", new String[]{});

        ArgumentCaptor<Component> argument = ArgumentCaptor.forClass(Component.class);
        verify(logger, times(9)).info(argument.capture());

        List<Component> list = argument.getAllValues();
        assertEquals(FormatUtil.format(locale.unknownCommand()), list.get(0));
        assertEquals(FormatUtil.format(locale.help().get(0)), list.get(1));
        assertEquals(FormatUtil.format(locale.help().get(1)), list.get(2));
        assertEquals(FormatUtil.format(locale.help().get(2)), list.get(3));
        assertEquals(FormatUtil.format(locale.help().get(3)), list.get(4));
        assertEquals(FormatUtil.format(locale.help().get(4)), list.get(5));
        assertEquals(FormatUtil.format(locale.help().get(5)), list.get(6));
        assertEquals(FormatUtil.format(locale.help().get(6)), list.get(7));
        assertEquals(FormatUtil.format(locale.help().get(7)), list.get(8));
    }

    @Test
    public void testBaseCommandAsPlayerUnknownArgumentsHasHelpPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"a"});

        ArgumentCaptor<Component> argument = ArgumentCaptor.forClass(Component.class);
        verify(player, times(9)).sendMessage(argument.capture());

        List<Component> list = argument.getAllValues();
        assertEquals(FormatUtil.format(player, locale.prefix() + locale.unknownCommand()), list.get(0));
        assertEquals(FormatUtil.format(player, locale.help().get(0)), list.get(1));
        assertEquals(FormatUtil.format(player, locale.help().get(1)), list.get(2));
        assertEquals(FormatUtil.format(player, locale.help().get(2)), list.get(3));
        assertEquals(FormatUtil.format(player, locale.help().get(3)), list.get(4));
        assertEquals(FormatUtil.format(player, locale.help().get(4)), list.get(5));
        assertEquals(FormatUtil.format(player, locale.help().get(5)), list.get(6));
        assertEquals(FormatUtil.format(player, locale.help().get(6)), list.get(7));
        assertEquals(FormatUtil.format(player, locale.help().get(7)), list.get(8));
    }

    @Test
    public void testBaseCommandAsPlayerUnknownArgumentsNoHelpPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"a"});

        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.unknownCommand()));
    }

    @Test
    public void testBaseCommandAsConsoleUnknownArguments() {
        when(skyWelcome.getComponentLogger()).thenReturn(logger);
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(consoleCommandSender, command, "skywelcome", new String[]{"a"});

        ArgumentCaptor<Component> argument = ArgumentCaptor.forClass(Component.class);
        verify(logger, times(9)).info(argument.capture());

        List<Component> list = argument.getAllValues();
        assertEquals(FormatUtil.format(locale.unknownCommand()), list.get(0));
        assertEquals(FormatUtil.format(locale.help().get(0)), list.get(1));
        assertEquals(FormatUtil.format(locale.help().get(1)), list.get(2));
        assertEquals(FormatUtil.format(locale.help().get(2)), list.get(3));
        assertEquals(FormatUtil.format(locale.help().get(3)), list.get(4));
        assertEquals(FormatUtil.format(locale.help().get(4)), list.get(5));
        assertEquals(FormatUtil.format(locale.help().get(5)), list.get(6));
        assertEquals(FormatUtil.format(locale.help().get(6)), list.get(7));
        assertEquals(FormatUtil.format(locale.help().get(7)), list.get(8));
    }

    @Test
    public void testReloadAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"reload"});
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.reload()));
    }

    @Test
    public void testReloadAsPlayerNoPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"reload"});
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.noPermission()));
    }

    @Test
    public void testReloadAsConsole() {
        when(skyWelcome.getComponentLogger()).thenReturn(logger);
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(consoleCommandSender, command, "skywelcome", new String[]{"reload"});
        verify(logger).info(FormatUtil.format(locale.reload()));
    }

    @Test
    public void testHelpAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"help"});

        ArgumentCaptor<Component> argument = ArgumentCaptor.forClass(Component.class);
        verify(player, times(8)).sendMessage(argument.capture());

        List<Component> list = argument.getAllValues();
        assertEquals(FormatUtil.format(player, locale.help().get(0)), list.get(0));
        assertEquals(FormatUtil.format(player, locale.help().get(1)), list.get(1));
        assertEquals(FormatUtil.format(player, locale.help().get(2)), list.get(2));
        assertEquals(FormatUtil.format(player, locale.help().get(3)), list.get(3));
        assertEquals(FormatUtil.format(player, locale.help().get(4)), list.get(4));
        assertEquals(FormatUtil.format(player, locale.help().get(5)), list.get(5));
        assertEquals(FormatUtil.format(player, locale.help().get(6)), list.get(6));
        assertEquals(FormatUtil.format(player, locale.help().get(7)), list.get(7));
    }

    @Test
    public void testHelpAsPlayerNoPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"help"});
        verify(player).sendMessage(FormatUtil.format(player,locale.prefix() + locale.noPermission()));
    }

    @Test
    public void testHelpAsConsole() {
        when(skyWelcome.getComponentLogger()).thenReturn(logger);
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(consoleCommandSender, command, "skywelcome", new String[]{"help"});

        ArgumentCaptor<Component> argument = ArgumentCaptor.forClass(Component.class);
        verify(logger, times(8)).info(argument.capture());

        List<Component> list = argument.getAllValues();
        assertEquals(FormatUtil.format(locale.help().get(0)), list.get(0));
        assertEquals(FormatUtil.format(locale.help().get(1)), list.get(1));
        assertEquals(FormatUtil.format(locale.help().get(2)), list.get(2));
        assertEquals(FormatUtil.format(locale.help().get(3)), list.get(3));
        assertEquals(FormatUtil.format(locale.help().get(4)), list.get(4));
        assertEquals(FormatUtil.format(locale.help().get(5)), list.get(5));
        assertEquals(FormatUtil.format(locale.help().get(6)), list.get(6));
        assertEquals(FormatUtil.format(locale.help().get(7)), list.get(7));
    }

    @Test
    public void testGuiAsConsole() {
        when(skyWelcome.getComponentLogger()).thenReturn(logger);
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(consoleCommandSender, command, "skywelcome", new String[]{"gui"});

        verify(logger).info(FormatUtil.format(locale.playerOnly()));
    }

    @Test
    public void testGuiAsPlayerNoArgsHasHelpPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"gui"});

        ArgumentCaptor<Component> argument = ArgumentCaptor.forClass(Component.class);
        verify(player, times(9)).sendMessage(argument.capture());

        List<Component> list = argument.getAllValues();
        assertEquals(FormatUtil.format(player, locale.prefix() + locale.unknownCommand()), list.get(0));
        assertEquals(FormatUtil.format(player, locale.help().get(0)), list.get(1));
        assertEquals(FormatUtil.format(player, locale.help().get(1)), list.get(2));
        assertEquals(FormatUtil.format(player, locale.help().get(2)), list.get(3));
        assertEquals(FormatUtil.format(player, locale.help().get(3)), list.get(4));
        assertEquals(FormatUtil.format(player, locale.help().get(4)), list.get(5));
        assertEquals(FormatUtil.format(player, locale.help().get(5)), list.get(6));
        assertEquals(FormatUtil.format(player, locale.help().get(6)), list.get(7));
        assertEquals(FormatUtil.format(player, locale.help().get(7)), list.get(8));
    }

    @Test
    public void testGuiAsPlayerNoArgsNoHelpPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"gui"});
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.unknownCommand()));
    }

    @Test
    public void testGuiJoinAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"gui", "join"});

        verify(joinGUI).createGUI(player);
        verify(joinGUI).openGUI(player);
    }

    @Test
    public void testGuiJoinAsPlayerNoPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"gui", "join"});

        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.noPermission()));
    }

    @Test
    public void testGuiQuitAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"gui", "quit"});

        verify(quitGUI).createGUI(player);
        verify(quitGUI).openGUI(player);
    }

    @Test
    public void testGuiQuitAsPlayerNoPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"gui", "quit"});

        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.noPermission()));
    }

    @Test
    public void testGuiLeaveAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"gui", "leave"});

        verify(quitGUI).createGUI(player);
        verify(quitGUI).openGUI(player);
    }

    @Test
    public void testGuiLeaveAsPlayerNoPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"gui", "leave"});

        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.noPermission()));
    }

    @Test
    public void testToggleAsConsole() {
        when(skyWelcome.getComponentLogger()).thenReturn(logger);
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(consoleCommandSender, command, "skywelcome", new String[]{"toggle"});

        verify(logger).info(FormatUtil.format(locale.playerOnly()));
    }

    @Test
    public void testToggleAsPlayerNoArgsHasHelpPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle"});

        ArgumentCaptor<Component> argument = ArgumentCaptor.forClass(Component.class);
        verify(player, times(9)).sendMessage(argument.capture());

        List<Component> list = argument.getAllValues();
        assertEquals(FormatUtil.format(player, locale.prefix() + locale.unknownCommand()), list.get(0));
        assertEquals(FormatUtil.format(player, locale.help().get(0)), list.get(1));
        assertEquals(FormatUtil.format(player, locale.help().get(1)), list.get(2));
        assertEquals(FormatUtil.format(player, locale.help().get(2)), list.get(3));
        assertEquals(FormatUtil.format(player, locale.help().get(3)), list.get(4));
        assertEquals(FormatUtil.format(player, locale.help().get(4)), list.get(5));
        assertEquals(FormatUtil.format(player, locale.help().get(5)), list.get(6));
        assertEquals(FormatUtil.format(player, locale.help().get(6)), list.get(7));
        assertEquals(FormatUtil.format(player, locale.help().get(7)), list.get(8));
    }

    @Test
    public void testToggleAsPlayerNoArgsNoHelpPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle"});
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.unknownCommand()));
    }

    @Test
    public void testToggleJoinEnabledAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(playerSettings.joinMessage()).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "join"});

        verify(playerManager).toggleJoin(player);
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.joinEnabled()));
    }

    @Test
    public void testToggleJoinDisabledAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(playerSettings.joinMessage()).thenReturn(false);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "join"});

        verify(playerManager).toggleJoin(player);
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.joinDisabled()));
    }

    @Test
    public void testToggleJoinAsPlayerNoPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "join"});

        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.noPermission()));
    }

    @Test
    public void testToggleQuitEnabledAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(playerSettings.leaveMessage()).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "quit"});

        verify(playerManager).toggleLeave(player);
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.quitEnabled()));
    }

    @Test
    public void testToggleQuitDisabledAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(playerSettings.leaveMessage()).thenReturn(false);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "quit"});

        verify(playerManager).toggleLeave(player);
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.quitDisabled()));
    }

    @Test
    public void testToggleQuitAsPlayerNoPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "quit"});

        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.noPermission()));
    }

    @Test
    public void testToggleLeaveEnabledAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(playerSettings.leaveMessage()).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "leave"});

        verify(playerManager).toggleLeave(player);
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.quitEnabled()));
    }

    @Test
    public void testToggleLeaveDisabledAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(playerSettings.leaveMessage()).thenReturn(false);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "leave"});

        verify(playerManager).toggleLeave(player);
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.quitDisabled()));
    }

    @Test
    public void testToggleLeaveAsPlayerNoPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "leave"});

        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.noPermission()));
    }

    @Test
    public void testToggleMotdEnabledAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(playerSettings.motd()).thenReturn(true);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "motd"});

        verify(playerManager).toggleMotd(player);
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.motdEnabled()));
    }

    @Test
    public void testToggleMotdDisabledAsPlayerHasPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        when(player.hasPermission(anyString())).thenReturn(true);
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(playerSettings.motd()).thenReturn(false);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "motd"});

        verify(playerManager).toggleMotd(player);
        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.motdDisabled()));
    }

    @Test
    public void testToggleMotdAsPlayerNoPermission() {
        when(localeManager.getLocale()).thenReturn(locale);
        skyWelcomeCommand.onCommand(player, command, "skywelcome", new String[]{"toggle", "motd"});

        verify(player).sendMessage(FormatUtil.format(player, locale.prefix() + locale.noPermission()));
    }
}
