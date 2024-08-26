package com.github.lukesky19.skywelcome.config.player;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record PlayerSettings(
        Boolean joinMessage,
        Boolean leaveMessage,
        Boolean motd,
        String selectedJoinMessage,
        String selectedLeaveMessage) { }
