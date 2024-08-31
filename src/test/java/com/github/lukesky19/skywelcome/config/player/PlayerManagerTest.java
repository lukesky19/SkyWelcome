package com.github.lukesky19.skywelcome.config.player;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import com.github.lukesky19.skywelcome.config.settings.Settings;
import com.github.lukesky19.skywelcome.config.settings.SettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerManagerTest {
    @Mock
    SkyWelcome skyWelcome;
    @Mock
    ConfigurationUtility configurationUtility;
    @Mock
    SettingsManager settingsManager;
    @Mock
    ComponentLogger logger;
    @Mock
    Player player;
    UUID uuid;
    PlayerManager playerManager;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        playerManager = new PlayerManager(skyWelcome, configurationUtility, settingsManager);
    }

    @AfterEach
    void tearDown() throws IOException {
        Path directory = Paths.get("src/test/resources/playerdata");
        Path file = Paths.get(directory + File.separator + uuid + ".yml");
        Files.deleteIfExists(file);
    }

    @Test
    void getPlayerSettings() {
        File dataFolder = Paths.get("src/test/resources").toFile();
        Path result = Paths.get("src/test/resources/playerdata/good.yml");
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(player.getUniqueId()).thenReturn(uuid);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(result)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        assertDoesNotThrow(() -> {
            PlayerSettings test = playerManager.getPlayerSettings(player);
            verify(logger, never()).error(any(Component.class));
            assertNotNull(test);
            assertNotNull(test.joinMessage());
            assertNotNull(test.leaveMessage());
            assertNotNull(test.motd());
            assertNotNull(test.selectedJoinMessage());
            assertNotNull(test.selectedLeaveMessage());
        });
    }

    @Test
    void getPlayerSettingsConfigurateException() {
        File dataFolder = Paths.get("src/test/resources").toFile();
        Path result = Paths.get("src/test/resources/playerdata/bad.yml");
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(skyWelcome.getComponentLogger()).thenReturn(logger);
        when(player.getUniqueId()).thenReturn(uuid);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(result)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        assertThrows(RuntimeException.class, () -> {
           PlayerSettings test = playerManager.getPlayerSettings(player);
           verify(logger).error(any(Component.class));
            assertNull(test);
            assertNull(test.joinMessage());
            assertNull(test.leaveMessage());
            assertNull(test.motd());
            assertNull(test.selectedJoinMessage());
            assertNull(test.selectedLeaveMessage());
        });
    }

    @Test
    void createPlayerSettingsDoesNotExist() throws IOException {
        Settings.Options options = new Settings.Options("en_US", true, true, true);
        Settings.Join join = new Settings.Join("test.join", "join");
        Settings.Quit quit = new Settings.Quit("test.quit", "quit");
        LinkedHashMap<String, Settings.Join> joinLinkedHashMap = new LinkedHashMap<>();
        LinkedHashMap<String, Settings.Quit> quitLinkedHashMap = new LinkedHashMap<>();
        joinLinkedHashMap.put("0", join);
        quitLinkedHashMap.put("0", quit);
        Settings.Motd motd = new Settings.Motd(new ArrayList<>());
        Settings settings = new Settings(options, joinLinkedHashMap, motd, quitLinkedHashMap);

        File dataFolder = Paths.get("src/test/resources").toFile();
        Path result = Paths.get("src/test/resources/playerdata/" + uuid.toString() + ".yml");
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(player.getUniqueId()).thenReturn(uuid);
        when(settingsManager.getSettings()).thenReturn(settings);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(result)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        playerManager.createPlayerSettings(player);
        PlayerSettings test = playerManager.getPlayerSettings(player);
        assertNotNull(test);
        assertNotNull(test.joinMessage());
        assertNotNull(test.motd());
        assertNotNull(test.leaveMessage());
        assertNotNull(test.selectedJoinMessage());
        assertEquals(test.selectedJoinMessage(), "join");
        assertNotNull(test.selectedLeaveMessage());
        assertEquals(test.selectedLeaveMessage(), "quit");
    }

    @Test
    void createPlayerSettingsExists() throws IOException {
        Settings.Options options = new Settings.Options("en_US", true, true, true);
        Settings.Join join = new Settings.Join("test.join", "join");
        Settings.Quit quit = new Settings.Quit("test.quit", "quit");
        LinkedHashMap<String, Settings.Join> joinLinkedHashMap = new LinkedHashMap<>();
        LinkedHashMap<String, Settings.Quit> quitLinkedHashMap = new LinkedHashMap<>();
        joinLinkedHashMap.put("0", join);
        quitLinkedHashMap.put("0", quit);
        Settings.Motd motd = new Settings.Motd(new ArrayList<>());
        Settings settings = new Settings(options, joinLinkedHashMap, motd, quitLinkedHashMap);

        File dataFolder = Paths.get("src/test/resources").toFile();
        Path result = Paths.get("src/test/resources/playerdata/" + uuid.toString() + ".yml");
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(player.getUniqueId()).thenReturn(uuid);
        when(settingsManager.getSettings()).thenReturn(settings);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(result)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        Files.copy(Path.of("src/test/resources/playerdata/good.yml"), Path.of("src/test/resources/playerdata/" + uuid + ".yml"));

        playerManager.createPlayerSettings(player);
        PlayerSettings test = playerManager.getPlayerSettings(player);
        assertNotNull(test);
        assertNotNull(test.joinMessage());
        assertNotNull(test.motd());
        assertNotNull(test.leaveMessage());
        assertNotNull(test.selectedJoinMessage());
        assertNotEquals(test.selectedJoinMessage(), "join");
        assertNotNull(test.selectedLeaveMessage());
        assertNotEquals(test.selectedLeaveMessage(), "quit");
    }

    @Test
    void testSavePlayerSettings() {
        File dataFolder = Paths.get("src/test/resources").toFile();
        Path result = Paths.get("src/test/resources/playerdata/" + uuid.toString() + ".yml");
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(player.getUniqueId()).thenReturn(uuid);

        PlayerSettings settings = new PlayerSettings(true, true, true, "join", "leave");
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(result)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        assertDoesNotThrow(() -> {
            playerManager.savePlayerSettings(player, settings);

            verify(logger, never()).error(any(Component.class));
            assertTrue(Files.exists(result));
        });
    }

    // I don't know how to cause a ConfigurateException to test that it is thrown on error.
    // I don't expect the error to be thrown, it's just there just-in-case.
    @Test
    void testSavePlayerSettingsConfigurateException() {}

    @Test
    void toggleJoin() throws IOException {
        File dataFolder = Paths.get("src/test/resources").toFile();
        Path source = Paths.get("src/test/resources/playerdata/good.yml");
        Path target = Paths.get("src/test/resources/playerdata/toggle.yml");
        Files.copy(source, target);

        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(player.getUniqueId()).thenReturn(uuid);

        PlayerSettings settings = new PlayerSettings(true, true, true, "join", "leave");
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(target)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        PlayerSettings test;
        playerManager.toggleJoin(player);
        test = playerManager.getPlayerSettings(player);
        assertFalse(test.joinMessage());

        playerManager.toggleJoin(player);
        test = playerManager.getPlayerSettings(player);
        assertTrue(test.joinMessage());

        Files.deleteIfExists(target);
    }

    @Test
    void toggleLeave() throws IOException {
        File dataFolder = Paths.get("src/test/resources").toFile();
        Path source = Paths.get("src/test/resources/playerdata/good.yml");
        Path target = Paths.get("src/test/resources/playerdata/toggle.yml");
        Files.copy(source, target);

        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(player.getUniqueId()).thenReturn(uuid);

        PlayerSettings settings = new PlayerSettings(true, true, true, "join", "leave");
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(target)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        PlayerSettings test;
        playerManager.toggleLeave(player);
        test = playerManager.getPlayerSettings(player);
        assertFalse(test.leaveMessage());

        playerManager.toggleLeave(player);
        test = playerManager.getPlayerSettings(player);
        assertTrue(test.leaveMessage());

        Files.deleteIfExists(target);
    }

    @Test
    void toggleMotd() throws IOException {
        File dataFolder = Paths.get("src/test/resources").toFile();
        Path source = Paths.get("src/test/resources/playerdata/good.yml");
        Path target = Paths.get("src/test/resources/playerdata/toggle.yml");
        Files.copy(source, target);

        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(player.getUniqueId()).thenReturn(uuid);

        PlayerSettings settings = new PlayerSettings(true, true, true, "join", "leave");
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(target)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        PlayerSettings test;
        playerManager.toggleMotd(player);
        test = playerManager.getPlayerSettings(player);
        assertFalse(test.motd());

        playerManager.toggleMotd(player);
        test = playerManager.getPlayerSettings(player);
        assertTrue(test.motd());

        Files.deleteIfExists(target);
    }

    @Test
    void changeSelectedJoinMessage() throws IOException {
        File dataFolder = Paths.get("src/test/resources").toFile();
        Path source = Paths.get("src/test/resources/playerdata/good.yml");
        Path target = Paths.get("src/test/resources/playerdata/toggle.yml");
        Files.copy(source, target);

        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(player.getUniqueId()).thenReturn(uuid);

        PlayerSettings settings = new PlayerSettings(true, true, true, "join", "leave");
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(target)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        PlayerSettings test;
        playerManager.changeSelectedJoinMessage(player, "join");
        test = playerManager.getPlayerSettings(player);
        assertEquals("join", test.selectedJoinMessage());

        Files.deleteIfExists(target);
    }

    @Test
    void changeSelectedLeaveMessage() throws IOException {
        File dataFolder = Paths.get("src/test/resources").toFile();
        Path source = Paths.get("src/test/resources/playerdata/good.yml");
        Path target = Paths.get("src/test/resources/playerdata/toggle.yml");
        Files.copy(source, target);

        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        when(player.getUniqueId()).thenReturn(uuid);

        PlayerSettings settings = new PlayerSettings(true, true, true, "join", "leave");
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(target)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        PlayerSettings test;
        playerManager.changeSelectedLeaveMessage(player, "leave");
        test = playerManager.getPlayerSettings(player);
        assertEquals("leave", test.selectedLeaveMessage());

        Files.deleteIfExists(target);
    }
}