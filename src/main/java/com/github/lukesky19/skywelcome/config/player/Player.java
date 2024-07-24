package com.github.lukesky19.skywelcome.config.player;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record Player(
        Boolean joinMessage,
        Boolean leaveMessage,
        Boolean motd) {
}
