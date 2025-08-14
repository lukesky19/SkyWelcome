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
package com.github.lukesky19.skywelcome.data.player;

import org.jetbrains.annotations.NotNull;

/**
 * This class contains a player's data.
 */
public class PlayerData {
    private boolean sendJoin;
    private boolean sendMotd;
    private boolean sendLeave;
    private @NotNull String joinMessage;
    private @NotNull String leaveMessage;

    /**
     * Constructor
     * @param sendJoin Should the player's join message be sent?
     * @param sendMotd Should the server's motd be sent to the player?
     * @param sendLeave Should the player's leave message be sent?
     * @param joinMessage The player's join message.
     * @param leaveMessage The player's leave message.
     */
    public PlayerData(
            boolean sendJoin,
            boolean sendMotd,
            boolean sendLeave,
            @NotNull String joinMessage,
            @NotNull String leaveMessage) {
        this.sendJoin = sendJoin;
        this.sendMotd = sendMotd;
        this.sendLeave = sendLeave;
        this.joinMessage = joinMessage;
        this.leaveMessage = leaveMessage;
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
     * Get the player's join message.
     * @return The player's join message as a {@link String}.
     */
    public @NotNull String getJoinMessage() {
        return joinMessage;
    }

    /**
     * Set the player's join message.
     * @param joinMessage The new join message.
     */
    public void setJoinMessage(@NotNull String joinMessage) {
        this.joinMessage = joinMessage;
    }

    /**
     * Get the player's leave message.
     * @return The player's leave message as a {@link String}.
     */
    public @NotNull String getLeaveMessage() {
        return leaveMessage;
    }

    /**
     * Set the player's leave message.
     * @param leaveMessage The new leave message.
     */
    public void setLeaveMessage(@NotNull String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }
}
