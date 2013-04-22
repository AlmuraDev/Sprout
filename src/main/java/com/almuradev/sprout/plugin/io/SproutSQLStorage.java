package com.almuradev.sprout.plugin.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.io.SQLStorage;
import com.almuradev.sprout.api.io.SQLMode;
import com.almuradev.sprout.plugin.SproutPlugin;
import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.h2.H2Configuration;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.alta189.simplesave.sqlite.SQLiteConfiguration;

public class SproutSQLStorage implements SQLStorage {
	private final SproutPlugin plugin;
	private final Configuration configuration;
	private final Database database;

	public SproutSQLStorage(SproutPlugin plugin, SQLMode mode, File loc, String dbName, String hostName, String username, String password, int port) {
		this.plugin = plugin;
		try {
			configuration = mode.getAssociation().newInstance();
		} catch (Exception ignore) {
			throw new IllegalArgumentException("Cannot create the SQL configuration object!");
		}
		if (configuration instanceof H2Configuration) {
			createFile(loc);
			((H2Configuration) configuration).setDatabase(new File(loc, "sprouts_h2_db").getAbsolutePath());
		} else if (configuration instanceof SQLiteConfiguration) {
			createFile(loc);
			((SQLiteConfiguration) configuration).setPath(new File(loc, "sprouts_sqlite_db").getAbsolutePath());
		} else {
			((MySQLConfiguration) configuration)
					.setDatabase(dbName)
					.setHost(hostName)
					.setUser(username)
					.setPassword(password)
					.setPort(port);
		}
		database = DatabaseFactory.createNewDatabase(configuration);
	}

	@Override
	public SQLStorage add(String world, int x, int y, int z, Sprout sprout) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public SQLStorage remove(String world, int x, int y, int z, Sprout sprout) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public SQLStorage clear(String world) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	private void createFile(final File dir) {
		try {
			Files.createDirectories(dir.toPath());
		} catch (FileAlreadyExistsException fafe) {
			;
		} catch (IOException e) {
			plugin.getLogger().severe("Could not create " + dir.getPath() + "! Disabling...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
}
