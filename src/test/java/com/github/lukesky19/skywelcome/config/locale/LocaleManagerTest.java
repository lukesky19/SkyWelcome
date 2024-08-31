package com.github.lukesky19.skywelcome.config.locale;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import com.github.lukesky19.skywelcome.config.player.PlayerSettings;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import com.github.lukesky19.skywelcome.util.FormatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocaleManagerTest {
    @Mock
    SkyWelcome skyWelcome;
    @Mock
    ComponentLogger logger;
    @Mock
    ConfigurationUtility configurationUtility;
    @Mock
    SettingsManager settingsManager;
    @Mock
    PlayerManager playerManager;
    @Mock
    Player player;
    @Mock
    Server server;
    @Mock
    Settings settings;
    UUID uuid;
    LocaleManager localeManager;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        localeManager = new LocaleManager(skyWelcome, configurationUtility, settingsManager, playerManager);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testReload() {
        File dataFolder = Paths.get("src/test/resources").toFile();
        Path target = Paths.get("src/test/resources/locale/en_US.yml");
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(settingsManager.getSettings()).thenReturn(settings);
        Settings.Options options = new Settings.Options("en_US", true, true, true);
        when(settings.options()).thenReturn(options);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(target)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(target)).thenReturn(loader);

        assertDoesNotThrow(() -> {
            localeManager.reload();
            verify(logger, never()).error(any(Component.class));
        });
    }

    // I don't know how to cause a ConfigurateException to test that it is thrown on error.
    // I don't expect the error to be thrown, it's just there just-in-case.
    @Test
    void testReloadConfigurateException() {}

    @Test
    void getLocale() throws IOException {
        File dataFolder = Paths.get("src/test/resources").toFile();
        Path source = Paths.get("src/main/resources/locale/en_US.yml");
        Path target = Paths.get("src/test/resources/locale/en_US.yml");
        Files.copy(source, target);
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(settingsManager.getSettings()).thenReturn(settings);
        Settings.Options options = new Settings.Options("en_US", true, true, true);
        when(settings.options()).thenReturn(options);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(target)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(target)).thenReturn(loader);

        localeManager.reload();

        assertNotNull(localeManager.getLocale());

        Files.delete(target);
    }

    @Test
    void testCopyDefaultLocales() throws IOException {
        File dataFolder = Paths.get("src/test/resources").toFile();
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);

        localeManager.copyDefaultLocales();
        verify(skyWelcome).saveResource("locale/en_US.yml", false);
    }

    @Test
    void sendJoinMessagePlayerSettingFalse() {
        PlayerSettings playerSettings = new PlayerSettings(false, true, true, "join", "quit");
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);

        localeManager.sendJoinMessage(player);
        verify(player, never()).sendMessage(any(Component.class));
    }

    @Test
    void sendJoinMessagePlayerOnline() {
        PlayerSettings playerSettings = new PlayerSettings(true, true, true, "join", "quit");
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(skyWelcome.getServer()).thenReturn(server);
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(player);

        when(skyWelcome.getServer().getOnlinePlayers()).thenAnswer(invocation -> playerList);
        when(player.isOnline()).thenReturn(true);

        localeManager.sendJoinMessage(player);
        verify(player).sendMessage(any(Component.class));
    }

    @Test
    void sendJoinMessagePlayerOffline() {
        PlayerSettings playerSettings = new PlayerSettings(true, true, true, "join", "quit");
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(skyWelcome.getServer()).thenReturn(server);
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(player);

        when(skyWelcome.getServer().getOnlinePlayers()).thenAnswer(invocation -> playerList);
        when(player.isOnline()).thenReturn(false);

        localeManager.sendJoinMessage(player);
        verify(player, never()).sendMessage(any(Component.class));
    }

    @Test
    void sendQuitMessagePlayerSettingFalse() {
        PlayerSettings playerSettings = new PlayerSettings(true, false, true, "join", "quit");
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);

        localeManager.sendLeaveMessage(player);
        verify(player, never()).sendMessage(any(Component.class));
    }

    @Test
    void sendLeaveMessagePlayerOnline() {
        PlayerSettings playerSettings = new PlayerSettings(true, true, true, "join", "quit");
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(skyWelcome.getServer()).thenReturn(server);
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(player);

        when(skyWelcome.getServer().getOnlinePlayers()).thenAnswer(invocation -> playerList);
        when(player.isOnline()).thenReturn(true);

        localeManager.sendLeaveMessage(player);
        verify(player).sendMessage(any(Component.class));
    }

    @Test
    void sendLeaveMessagePlayerOffline() {
        PlayerSettings playerSettings = new PlayerSettings(true, true, true, "join", "quit");
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);
        when(skyWelcome.getServer()).thenReturn(server);
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(player);

        when(skyWelcome.getServer().getOnlinePlayers()).thenAnswer(invocation -> playerList);
        when(player.isOnline()).thenReturn(false);

        localeManager.sendLeaveMessage(player);
        verify(player, never()).sendMessage(any(Component.class));
    }

    @Test
    void sendMotdPlayerSettingTrue() {
        PlayerSettings playerSettings = new PlayerSettings(true, true, true, "join", "quit");
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);

        Settings.Options options = new Settings.Options("en_US", true, true, true);
        Settings.Join join = new Settings.Join("test.join", "join");
        Settings.Quit quit = new Settings.Quit("test.quit", "quit");
        LinkedHashMap<String, Settings.Join> joinLinkedHashMap = new LinkedHashMap<>();
        LinkedHashMap<String, Settings.Quit> quitLinkedHashMap = new LinkedHashMap<>();
        joinLinkedHashMap.put("0", join);
        quitLinkedHashMap.put("0", quit);
        List<String> motdContents = new ArrayList<>();
        motdContents.add("1");
        motdContents.add("2");
        motdContents.add("3");
        Settings.Motd motd = new Settings.Motd(motdContents);
        Settings settings = new Settings(options, joinLinkedHashMap, motd, quitLinkedHashMap);

        when(settingsManager.getSettings()).thenReturn(settings);
        ArgumentCaptor<Component> argument = ArgumentCaptor.forClass(Component.class);

        localeManager.sendMotd(player);

        verify(player, times(3)).sendMessage(argument.capture());
        List<Component> list = argument.getAllValues();
        assertEquals(MiniMessage.miniMessage().deserialize("1").decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.byBoolean(false)), list.get(0));
        assertEquals(MiniMessage.miniMessage().deserialize("2").decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.byBoolean(false)), list.get(1));
        assertEquals(MiniMessage.miniMessage().deserialize("3").decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.byBoolean(false)), list.get(2));
    }

    @Test
    void sendMotdPlayerSettingFalse() {
        PlayerSettings playerSettings = new PlayerSettings(true, true, false, "join", "quit");
        when(playerManager.getPlayerSettings(player)).thenReturn(playerSettings);

        localeManager.sendMotd(player);

        verify(player, never()).sendMessage(any(Component.class));
    }
}