/*
    SkyWelcome allows players to toggle join, leave, MOTD messages, and to choose custom join and leave messages.
    Copyright (C) 2024 lukeskywlker19

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
package com.github.lukesky19.skywelcome.common.settings;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * This interface is used to get plugin configuration shared between proxy and backend plugin settings.
 */
public interface ISettingsManager {
    /**
     * Is the plugin in proxy mode or local mode?
     * @return true for proxy mode, or false for local mode.
     */
    boolean isProxyEnabled();

    /**
     * Get the configured {@link List} of {@link MessageConfig} for join messages.
     * @return A {@link List} of {@link MessageConfig} for join messages or null.
     */
    @Nullable List<MessageConfig> getJoinMessages();

    /**
     * Get the configured {@link List} of {@link MessageConfig} for leave messages.
     * @return A {@link List} of {@link MessageConfig} for leave messages or null.
     */
    @Nullable List<MessageConfig> getLeaveMessages();

    /**
     * Get the configured {@link List} of {@link MessageConfig} for server change messages.
     * @return A {@link List} of {@link MessageConfig} for server change messages or null.
     */
    @Nullable List<MessageConfig> getServerChangeMessages();

    /**
     * Set the configured join messages.
     * @param joinMessages A {@link List} of {@link MessageConfig}.
     */
    void setJoinMessages(@NonNull List<MessageConfig> joinMessages);

    /**
     * Set the configured leave messages.
     * @param leaveMessages A {@link List} of {@link MessageConfig}.
     */
    void setLeaveMessages(@NonNull List<MessageConfig> leaveMessages);

    /**
     * Set the configured server change messages.
     * @param serverChangeMessages A {@link List} of {@link MessageConfig}.
     */
    void setServerChangeMessages(@NonNull List<MessageConfig> serverChangeMessages);

    /**
     * Get the default join message. This will be the first one in the list.
     * @return The default join message or null if the settings is null or no join messages are configured.
     */
    @Nullable String getDefaultJoinMessage();

    /**
     * Get the default leave message. This will be the first one in the list.
     * @return The default leave message or null if the settings is null or no leave messages are configured.
     */
    @Nullable String getDefaultLeaveMessage();

    /**
     * Get the default server change message. This will be the first one in the list.
     * @return The default server change message or null if the settings is null or no server change messages are configured.
     */
    @Nullable String getDefaultServerChangeMessage();
}