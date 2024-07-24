package com.github.lukesky19.skywelcome.config.settings;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public record Settings(Join join, Motd motd, Quit quit) {
    @ConfigSerializable
    public record Join(String content) { }

    @ConfigSerializable
    public record Motd(List<String> contents) { }

    @ConfigSerializable
    public record Quit(String content) { }
}

