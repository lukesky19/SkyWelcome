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
package com.github.lukesky19.skywelcome.enums;

/**
 * This enum is used to identify button types.
 */
public enum ButtonType {
    /**
     * This type is used to identify the configuration for the filler buttons.
     */
    FILLER,
    /**
     * This type is used to identify the configuration for the dummy buttons.
     */
    DUMMY,
    /**
     * This type is used to identify the configuration for the return or exit button.
     */
    RETURN,
    /**
     * This type is used to identify the configuration for the next page button.
     */
    NEXT_PAGE,
    /**
     * This type is used to identify the configuration for the previous page button.
     */
    PREV_PAGE
}
