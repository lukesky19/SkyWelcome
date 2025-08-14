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
package com.github.lukesky19.skywelcome.manager;

import com.github.lukesky19.skylib.api.gui.AbstractGUIManager;
import com.github.lukesky19.skywelcome.SkyWelcome;
import org.jetbrains.annotations.NotNull;

/**
 * This class manages open GUIs.
 */
public class GUIManager extends AbstractGUIManager {
    /**
     * Constructor
     * @param skyWelcome The plugin extending this {@link AbstractGUIManager} class.
     */
    public GUIManager(@NotNull SkyWelcome skyWelcome) {
        super(skyWelcome);
    }
}
