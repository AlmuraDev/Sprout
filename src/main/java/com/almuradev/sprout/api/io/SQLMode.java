/*
 * This file is part of Sprout.
 *
 * © 2013 AlmuraDev <http://www.almuradev.com/>
 * Sprout is licensed under the GNU General Public License.
 *
 * Sprout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sprout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License. If not,
 * see <http://www.gnu.org/licenses/> for the GNU General Public License.
 */
package com.almuradev.sprout.api.io;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.h2.H2Configuration;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.alta189.simplesave.sqlite.SQLiteConfiguration;

public enum SQLMode {
	H2("H2", H2Configuration.class),
	SQLITE("SQLite", SQLiteConfiguration.class),
	MYSQL("MySQL", MySQLConfiguration.class);

	private final String identifier;
	private final Class<? extends Configuration> configuration;

	private SQLMode(String identifier, Class<? extends Configuration> configuration) {
		this.identifier = identifier;
		this.configuration = configuration;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Class<? extends Configuration> getAssociation() {
		return configuration;
	}
}
