package com.github.lukesky19.skywelcome.config.settings;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.ConfigurationUtility;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsManagerTest {
    @Mock
    SkyWelcome skyWelcome;
    @Mock
    ConfigurationUtility configurationUtility;
    @Mock
    ComponentLogger logger;
    SettingsManager settingsManager;

    @BeforeEach
    void setUp() {
        settingsManager = new SettingsManager(skyWelcome, configurationUtility);
    }

    @AfterEach
    void tearDown() throws IOException {
        Path target = Paths.get("src/test/resources/settings.yml");
        Files.delete(target);
    }

    @Test
    public void testReloadSettings() throws IOException {
        File dataFolder = Paths.get("src/test/resources").toFile();
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        Path source = Paths.get("src/main/resources/settings.yml");
        Path target = Paths.get("src/test/resources/settings.yml");
        Files.copy(source, target);
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(target)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(target)).thenReturn(loader);

        settingsManager.reload();
        verify(skyWelcome, never()).saveResource("settings.yml", false);
        verify(logger, never()).error(MiniMessage.miniMessage().deserialize("<red>The settings configuration failed to load.</red>"));
    }

    @Test
    public void testReloadCopyDefaultSettings() throws IOException {
        File dataFolder = Paths.get("src/test/resources").toFile();
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        Path source = Paths.get("src/main/resources/settings.yml");
        Path target = Paths.get("src/test/resources/settings.yml");
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(target)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(target)).thenReturn(loader);

        settingsManager.reload();
        verify(skyWelcome).saveResource("settings.yml", false);
        Files.copy(source, target);
        assertNotEquals(settingsManager.getSettings(), null);
        verify(logger, never()).error(MiniMessage.miniMessage().deserialize("<red>The settings configuration failed to load.</red>"));
    }

    @Test
    public void testReloadError() throws IOException {
        when(skyWelcome.getComponentLogger()).thenReturn(logger);
        File dataFolder = Paths.get("src/test/resources").toFile();
        when(skyWelcome.getDataFolder()).thenReturn(dataFolder);
        Path source = Paths.get("src/main/resources/settings.yml");
        Path target = Paths.get("src/test/resources/settings.yml");
        Path badSettings = Paths.get("src/test/resources/bad-settings.yml");
        Files.copy(source, target);

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(badSettings)
                .indent(4)
                .build();
        when(configurationUtility.getYamlConfigurationLoader(any(Path.class))).thenReturn(loader);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            settingsManager.reload();
            verify(skyWelcome).saveResource("settings.yml", false);
            verify(logger).error(MiniMessage.miniMessage().deserialize("<red>The settings configuration failed to load.</red>"));
        });

        String expectedMessage = "Unknown error occurred while loading";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}