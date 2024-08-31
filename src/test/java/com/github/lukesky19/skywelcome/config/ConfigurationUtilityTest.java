package com.github.lukesky19.skywelcome.config;

import com.github.lukesky19.skywelcome.SkyWelcome;
import com.github.lukesky19.skywelcome.config.player.PlayerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationUtilityTest {
    @Mock
    SkyWelcome skyWelcome;
    ConfigurationUtility configurationUtility;

    @BeforeEach
    void setUp() {
        configurationUtility = new ConfigurationUtility(skyWelcome);
    }

    @Test
    public void testGetYamlConfigurationLoader() {
        Path path = Paths.get("src/test/resources/playerdata/good.yml");
        YamlConfigurationLoader loader = configurationUtility.getYamlConfigurationLoader(path);
        assertNotNull(loader);
    }
}