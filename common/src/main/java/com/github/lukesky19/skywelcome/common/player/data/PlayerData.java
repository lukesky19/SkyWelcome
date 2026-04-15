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
package com.github.lukesky19.skywelcome.common.player.data;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.UUID;

/**
 * This class contains a player's data.
 */
public class PlayerData implements Serializable {
    /**
     * The {@link UUID} of the player this data belongs to.
     */
    private final @NonNull UUID playerId;
    /**
     * Controls whether the player's join message should be sent.
     */
    private boolean sendJoin;
    /**
     * Controls whether the player should see the motd or not.
     */
    private boolean sendMotd;
    /**
     * Controls whether the player's leave message should be sent.
     */
    private boolean sendLeave;
    /**
     * Controls whether the player's server change message should be sent.
     */
    private boolean sendServerChange;

    /**
     * The player's raw join message.
     */
    private @NonNull String joinMessage;
    /**
     * The player's raw leave message.
     */
    private @NonNull String leaveMessage;
    /**
     * The player's raw server change message.
     */
    private @NonNull String serverChangeMessage;

    /**
     * The player's join message with PlaceholderAPI placeholders resolved.
     */
    private @Nullable String parsedJoinMessage;
    /**
     * The player's leave message with PlaceholderAPI placeholders resolved.
     */
    private @Nullable String parsedLeaveMessage;
    /**
     * The player's server change message with PlaceholderAPI placeholders resolved.
     */
    private @Nullable String parsedServerChangeMessage;

    /**
     * Whether the player was last known to be vanished.
     */
    private boolean vanished = false;

    /**
     * Constructor
     * @param playerId The {@link UUID} of the player.
     * @param sendJoin Should the player's join message be sent?
     * @param sendMotd Should the server's motd be sent to the player?
     * @param sendLeave Should the player's leave message be sent?
     * @param sendServerChange Should the player's server change message be sent?
     * @param joinMessage The player's join message.
     * @param leaveMessage The player's leave message.
     * @param serverChangeMessage The player's server change message.
     */
    public PlayerData(
            @NonNull UUID playerId,
            boolean sendJoin,
            boolean sendMotd,
            boolean sendLeave,
            boolean sendServerChange,
            @NonNull String joinMessage,
            @NonNull String leaveMessage,
            @NonNull String serverChangeMessage) {
        this.playerId = playerId;
        this.sendJoin = sendJoin;
        this.sendMotd = sendMotd;
        this.sendLeave = sendLeave;
        this.sendServerChange = sendServerChange;
        this.joinMessage = joinMessage;
        this.leaveMessage = leaveMessage;
        this.serverChangeMessage = serverChangeMessage;
    }

    /**
     * Constructor
     * @param playerId The {@link UUID} of the player.
     * @param sendJoin Should the player's join message be sent?
     * @param sendMotd Should the server's motd be sent to the player?
     * @param sendLeave Should the player's leave message be sent?
     * @param sendServerChange Should the player's server change message be sent?
     * @param joinMessage The player's join message.
     * @param leaveMessage The player's leave message.
     * @param serverChangeMessage The player's server change message.
     * @param parsedJoinMessage The pre-parsed join message. (Placeholders pre-parsed)
     * @param parsedLeaveMessage The pre-parsed leave message. (Placeholders pre-parsed)
     * @param parsedServerChangeMessage The pre-parsed server change message. (Placeholders pre-parsed)
     * @param vanished The player's vanish status.
     */
    public PlayerData(
            @NonNull UUID playerId,
            boolean sendJoin,
            boolean sendMotd,
            boolean sendLeave,
            boolean sendServerChange,
            @NonNull String joinMessage,
            @NonNull String leaveMessage,
            @NonNull String serverChangeMessage,
            @Nullable String parsedJoinMessage,
            @Nullable String parsedLeaveMessage,
            @Nullable String parsedServerChangeMessage,
            boolean vanished) {
        this.playerId = playerId;
        this.sendJoin = sendJoin;
        this.sendMotd = sendMotd;
        this.sendLeave = sendLeave;
        this.sendServerChange = sendServerChange;
        this.joinMessage = joinMessage;
        this.leaveMessage = leaveMessage;
        this.serverChangeMessage = serverChangeMessage;
        this.parsedJoinMessage = parsedJoinMessage;
        this.parsedLeaveMessage = parsedLeaveMessage;
        this.parsedServerChangeMessage = parsedServerChangeMessage;
        this.vanished = vanished;
    }

    /**
     * Get the {@link UUID} of the player this data belongs to.
     * @return The {@link UUID} of the player this data belongs to.
     */
    public @NonNull UUID getPlayerId() {
        return playerId;
    }

    /**
     * Should the player's join message be sent?
     * @return true or false.
     */
    public boolean isSendJoin() {
        return sendJoin;
    }

    /**
     * Set whether the player's join message should be sent or not.
     * @param sendJoin true or false.
     */
    public void setSendJoin(boolean sendJoin) {
        this.sendJoin = sendJoin;
    }

    /**
     * Should the server's motd messages be sent?
     * @return true or false.
     */
    public boolean isSendMotd() {
        return sendMotd;
    }

    /**
     * Set whether the server's motd messages should be sent or not.
     * @param sendMotd true or false.
     */
    public void setSendMotd(boolean sendMotd) {
        this.sendMotd = sendMotd;
    }

    /**
     * Should the player's leave message be sent?
     * @return true or false.
     */
    public boolean isSendLeave() {
        return sendLeave;
    }

    /**
     * Set whether the player's leave message should be sent or not.
     * @param sendLeave true or false.
     */
    public void setSendLeave(boolean sendLeave) {
        this.sendLeave = sendLeave;
    }

    /**
     * Should the player's server change message be sent?
     * @return true or false.
     */
    public boolean isSendServerChange() {
        return sendServerChange;
    }

    /**
     * Set whether the player's server change message should be sent or not.
     * @param sendServerChange true or false.
     */
    public void setSendServerChange(boolean sendServerChange) {
        this.sendServerChange = sendServerChange;
    }

    /**
     * Get the player's join message.
     * @return The player's join message as a {@link String}.
     */
    public @NonNull String getJoinMessage() {
        return joinMessage;
    }

    /**
     * Set the player's join message.
     * @param joinMessage The new join message.
     */
    public void setJoinMessage(@NonNull String joinMessage) {
        this.joinMessage = joinMessage;
    }

    /**
     * Get the player's leave message.
     * @return The player's leave message as a {@link String}.
     */
    public @NonNull String getLeaveMessage() {
        return leaveMessage;
    }

    /**
     * Set the player's leave message.
     * @param leaveMessage The new leave message.
     */
    public void setLeaveMessage(@NonNull String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    /**
     * Get the player's server change message.
     * @return The player's server change message as a {@link String}.
     */
    public @NonNull String getServerChangeMessage() {
        return serverChangeMessage;
    }

    /**
     * Set the player's server change message.
     * @param serverChangeMessage The new server change message.
     */
    public void setServerChangeMessage(@NonNull String serverChangeMessage) {
        this.serverChangeMessage = serverChangeMessage;
    }

    /**
     * Get the pre-parsed join message. Has PlaceholderAPI placeholders pre-parsed.
     * @return The pre-parsed join message. Has PlaceholderAPI placeholders pre-parsed.
     */
    public @Nullable String getParsedJoinMessage() {
        return parsedJoinMessage;
    }

    /**
     * Set the player's pre-parsed join message.
     * @param parsedJoinMessage The player's join message with PlaceholderAPI placeholders pre-parsed.
     */
    public void setParsedJoinMessage(@NonNull String parsedJoinMessage) {
        this.parsedJoinMessage = parsedJoinMessage;
    }

    /**
     * Get the pre-parsed leave message. Has PlaceholderAPI placeholders pre-parsed.
     * @return The pre-parsed leave message. Has PlaceholderAPI placeholders pre-parsed.
     */
    public @Nullable String getParsedLeaveMessage() {
        return parsedLeaveMessage;
    }

    /**
     * Set the player's pre-parsed leave message.
     * @param parsedLeaveMessage The player's leave message with PlaceholderAPI placeholders pre-parsed.
     */
    public void setParsedLeaveMessage(@NonNull String parsedLeaveMessage) {
        this.parsedLeaveMessage = parsedLeaveMessage;
    }

    /**
     * Get the pre-parsed server change message. Has PlaceholderAPI placeholders pre-parsed.
     * @return The pre-parsed server change message. Has PlaceholderAPI placeholders pre-parsed.
     */
    public @Nullable String getParsedServerChangeMessage() {
        return parsedServerChangeMessage;
    }

    /**
     * Set the player's pre-parsed server change message.
     * @param parsedServerChangeMessage The player's server change message with PlaceholderAPI placeholders pre-parsed.
     */
    public void setParsedServerChangeMessage(@NonNull String parsedServerChangeMessage) {
        this.parsedServerChangeMessage = parsedServerChangeMessage;
    }

    /**
     * Is the player vanished?
     * @return true if vanished, false if not.
     */
    public boolean isVanished() {
        return vanished;
    }

    /**
     * Set the vanished status.
     * @param vanished true if vanished, false if not.
     */
    public void setVanished(boolean vanished) {
        this.vanished = vanished;
    }
}