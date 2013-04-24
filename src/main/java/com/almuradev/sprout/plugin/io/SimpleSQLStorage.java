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
package com.almuradev.sprout.plugin.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.io.SQLMode;
import com.almuradev.sprout.api.io.SQLStorage;
import com.almuradev.sprout.api.util.Int21TripleHashed;
import com.almuradev.sprout.api.util.TInt21TripleObjectHashMap;
import com.almuradev.sprout.plugin.SproutPlugin;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.io.table.Sprouts;
import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.h2.H2Configuration;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.alta189.simplesave.sqlite.SQLiteConfiguration;

import gnu.trove.procedure.TLongObjectProcedure;

import org.apache.commons.lang.SerializationUtils;

public class SimpleSQLStorage implements SQLStorage {
	private final SproutPlugin plugin;
	private final Configuration configuration;
	private Database database;

	public SimpleSQLStorage(SproutPlugin plugin, SQLMode mode) {
		this.plugin = plugin;
		try {
			configuration = mode.getAssociation().newInstance();
		} catch (Exception ignore) {
			throw new IllegalArgumentException("Cannot create the SQL configuration object!");
		}
	}

	public void onEnable(File loc) {
		onEnable(loc, null, null, null, null, 1337);
	}

	public void onEnable(String dbName, String hostName, String username, String password, int port) {
		onEnable(null, dbName, hostName, username, password, port);
	}

	public void onEnable(File loc, String dbName, String hostName, String username, String password, int port) {
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

		try {
			database.registerTable(Sprouts.class);
		} catch (TableRegistrationException e) {
			e.printStackTrace();
		}

		try {
			database.connect();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public SQLStorage add(String world, int x, int y, int z, String sprout, int age) {
		if (world == null || world.isEmpty() || sprout == null) {
			throw new IllegalArgumentException("World or sprout is null!");
		}

		database.save(new Sprouts(world, Int21TripleHashed.key(x, y, z), sprout, age));
		return this;
	}

	@Override
	public SQLStorage remove(String world, int x, int y, int z) {
		if (world == null || world.isEmpty()) {
			throw new IllegalArgumentException("World is null!");
		}

		final Sprouts row = database.select(Sprouts.class).where().equal("world", world).and().equal("x", x).and().equal("y", y).and().equal("z", z).execute().findOne();
		if (row != null) {
			database.remove(row);
		}
		return this;
	}

	@Override
	public Map<String, TInt21TripleObjectHashMap> getAll() {
		final HashMap<String, TInt21TripleObjectHashMap> registry = new HashMap<>();
		for (Sprouts row : database.select(Sprouts.class).execute().find()) {
			TInt21TripleObjectHashMap worldRegistry = registry.get(row.getWorld());
			if (worldRegistry == null) {
				worldRegistry = new TInt21TripleObjectHashMap();
				registry.put(row.getWorld(), worldRegistry);
			}
			final Sprout sprout = plugin.getSproutRegistry().get(row.getSprout());
			if (sprout == null) {
				plugin.getLogger().severe("Attempting to place non-existent \"" + row.getSprout() + "\" sprout into the world. Skipping...");
				continue;
			}
			final Sprout toInject = (Sprout) SerializationUtils.clone(sprout);
			((SimpleSprout) toInject).grow(row.getAge());
			worldRegistry.put(Int21TripleHashed.key1(row.getLocation()), Int21TripleHashed.key2(row.getLocation()), Int21TripleHashed.key3(row.getLocation()), toInject);
		}
		return registry;
	}

	public void dropAll() {
		for (Map.Entry<String, TInt21TripleObjectHashMap> entry : plugin.getWorldRegistry().getAll().entrySet()) {
			final String world = entry.getKey();
			entry.getValue().getInternalMap().forEachEntry(new TLongObjectProcedure() {
				@Override
				public boolean execute(long l, Object o) {
					final int x = Int21TripleHashed.key1(l);
					final int y = Int21TripleHashed.key2(l);
					final int z = Int21TripleHashed.key3(l);
					final Sprout sprout = (Sprout) o;

					final Sprouts row = database.select(Sprouts.class).where().equal("world", world).and().equal("x", x).and().equal("y", y).and().equal("z", z).execute().findOne();
					if (row == null) {
						add(world, x, y, z, sprout.getName(), sprout.getAge());
					} else {
						row.setSprout(sprout.getName());
						row.setAge(sprout.getAge());
						database.save(row);
					}
					return true;
				}
			});
		}
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
