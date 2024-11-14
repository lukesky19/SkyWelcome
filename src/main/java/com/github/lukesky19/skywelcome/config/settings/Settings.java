/*
    SkyWelcome allows players to toggle join, leave, MOTD messages, and to choose custom join and leave messages.
    Copyright (C) 2024  lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skywelcome.config.settings;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;

import java.util.LinkedHashMap;
import java.util.List;

@ConfigSerializable
public record Settings(
        String configVersion,
        Options options,
        LinkedHashMap<String, Join> join,
        Motd motd,
        LinkedHashMap<String, Quit> quit,
        WelcomeRewards welcomeRewards) {

    @ConfigSerializable
    public record Join(String permission, String message) { }

    @ConfigSerializable
    public record Motd(List<String> contents) { }

    @ConfigSerializable
    public record Quit(String permission, String message) { }

    @ConfigSerializable
    public record Options(String locale, Boolean joins, Boolean quits, Boolean motd) { }

    @ConfigSerializable
    public record WelcomeRewards(Boolean enabled, String type, Double cash, Item item, List<String> commands, List<String> messages) {}

    @ConfigSerializable
    public record Item(String material, Integer amount) {}
}

