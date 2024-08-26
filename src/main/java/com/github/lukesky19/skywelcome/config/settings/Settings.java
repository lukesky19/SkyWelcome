package com.github.lukesky19.skywelcome.config.settings;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.LinkedHashMap;
import java.util.List;

@ConfigSerializable
public record Settings(
        String configVersion,
        Options options,
        LinkedHashMap<String, Join> join,
        Motd motd,
        LinkedHashMap<String, Quit> quit) {


    @ConfigSerializable
    public record Join(String permission, String message) { }

    @ConfigSerializable
    public record Motd(List<String> contents) { }

    @ConfigSerializable
    public record Quit(String permission, String message) { }

    @ConfigSerializable
    public record Options(Boolean joins, Boolean quits, Boolean motd) { }
}

