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
package com.github.lukesky19.skywelcome.common.database;

import com.github.lukesky19.skylib.common.api.database.connection.AbstractConnectionManager;
import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import com.github.lukesky19.skylib.libs.hikaricp.HikariConfig;
import com.github.lukesky19.skylib.libs.hikaricp.HikariDataSource;
import org.jspecify.annotations.NonNull;

import java.io.File;

/**
 * This class manages connections to the database.
 */
public class ConnectionManager extends AbstractConnectionManager {
    /**
     * Constructor
     * @param skyPlugin An {@link ISkyPlugin} instance.
     */
    public ConnectionManager(@NonNull ISkyPlugin skyPlugin) {
        super(skyPlugin);
    }

    /**
     * Creates the required {@link HikariConfig} to access the database and returns the {@link HikariDataSource}.
     * @param plugin The {@link ISkyPlugin} implementing and making use of this class.
     * @return A {@link HikariDataSource} object.
     */
    protected @NonNull HikariDataSource createHikariDataSource(@NonNull ISkyPlugin plugin) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:sqlite:" +  plugin.getDirectoryFile().getAbsolutePath() + File.separator + "database.db");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setAutoCommit(true);
        return new HikariDataSource(config);
    }
}