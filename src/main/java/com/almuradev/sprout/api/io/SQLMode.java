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
