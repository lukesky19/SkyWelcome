package com.github.lukesky19.skywelcome.paper.integration;

import com.github.lukesky19.skylib.common.api.integration.Hook;
import com.github.lukesky19.skywelcome.paper.SkyWelcomePaper;
import com.github.lukesky19.skywelcome.paper.integration.hooks.*;
import com.github.lukesky19.skywelcome.paper.messaging.MessageBroker;
import com.github.lukesky19.skywelcome.paper.player.PlayerDataManager;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages hooks into different plugins.
 */
public class HookManager {
    private final @NotNull Map<Class<?>, Hook> hooks = new HashMap<>();

    /**
     * Constructor
     * @param skyWelcome A {@link SkyWelcomePaper} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param messageBroker A {@link MessageBroker} instance.
     */
    public HookManager(@NotNull SkyWelcomePaper skyWelcome, @NonNull PlayerDataManager playerDataManager, @NonNull MessageBroker messageBroker) {
        registerHook(EconomyHook.class, new EconomyHook(skyWelcome));

        registerHook(HeadDatabaseHook.class, new HeadDatabaseHook(skyWelcome));

        registerHook(EssentialsHook.class, new EssentialsHook(skyWelcome, playerDataManager, messageBroker));

        registerHook(RoseChatHook.class, new RoseChatHook(skyWelcome, playerDataManager, messageBroker));

        registerHook(SuperVanishHook.class, new SuperVanishHook(skyWelcome, playerDataManager, messageBroker));
    }

    /**
     * Register a hook.
     * @param hookClass The class.
     * @param hook The class instance.
     * @param <T> Parameter for any class that extends {@link Hook}.
     */
    public <T extends Hook> void registerHook(@NotNull Class<T> hookClass, @NotNull Hook hook) {
        hooks.put(hookClass, hook);
        hook.initialize();
    }

    /**
     * Get a hook.
     * @param hookClass The class.
     * @param <T> Parameter for any class that extends {@link Hook}.
     * @return The class instance.
     */
    public @NotNull <T extends Hook> T getHook(@NotNull Class<T> hookClass) {
        return hookClass.cast(hooks.get(hookClass));
    }
}