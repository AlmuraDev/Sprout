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
package com.almuradev.sprout.plugin;

import java.io.IOException;

import com.almuradev.sprout.api.io.SQLMode;
import com.almuradev.sprout.api.io.SQLStorage;
import com.almuradev.sprout.api.io.SproutRegistry;
import com.almuradev.sprout.api.io.WorldRegistry;
import com.almuradev.sprout.plugin.command.SproutExecutor;
import com.almuradev.sprout.plugin.io.FlatFileStorage;
import com.almuradev.sprout.plugin.io.SimpleSQLStorage;
import com.almuradev.sprout.plugin.io.SimpleSproutRegistry;
import com.almuradev.sprout.plugin.io.SimpleWorldRegistry;
import com.almuradev.sprout.plugin.task.GrowthTask;
import com.almuradev.sprout.plugin.thread.ThreadRegistry;
import org.mcstats.MetricsLite;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class SproutPlugin extends JavaPlugin {
	private final FlatFileStorage flatFileStorage;
	private final SproutConfiguration configuration;
	private final SimpleSproutRegistry sproutRegistry;
	private final SimpleSQLStorage sqlStorage;
	private final SimpleWorldRegistry worldRegistry;

	public SproutPlugin() {
		configuration = new SproutConfiguration(this);
		flatFileStorage = new FlatFileStorage(this);
		sproutRegistry = new SimpleSproutRegistry();
		sqlStorage = new SimpleSQLStorage(this);
		worldRegistry = new SimpleWorldRegistry();
	}

	@Override
	public void onDisable() {
		GrowthTask.stop(this);
		ThreadRegistry.stop();
		sqlStorage.dropAll();
	}

	@Override
	public void onEnable() {
		configuration.onEnable();
		final SQLMode mode = configuration.getMode();
		if (mode == null) {
			getLogger().severe("SQL mode within config.yml is not a valid SQL mode (h2, sqlite, mysql). Disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		flatFileStorage.onEnable(getDataFolder());
		flatFileStorage.load();
		switch (mode) {
			case H2:
			case SQLITE:
				sqlStorage.onEnable(mode, getDataFolder());
				break;
			case MYSQL:
				sqlStorage.onEnable(mode, configuration.getHost(), configuration.getDatabase(), configuration.getPort(), configuration.getUsername(), configuration.getPassword());
		}
		worldRegistry.putAll(sqlStorage.getAll());
		getServer().getPluginManager().registerEvents(new SproutListener(this), this);
		GrowthTask.schedule(this, Bukkit.getWorlds().toArray(new World[Bukkit.getWorlds().size()]));
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException ignore) {
		}
		getCommand("sprout").setExecutor(new SproutExecutor(this));
	}

	public SproutConfiguration getConfiguration() {
		return configuration;
	}

	public SproutRegistry getSproutRegistry() {
		return sproutRegistry;
	}

	public SQLStorage getStorage() {
		return sqlStorage;
	}

	public WorldRegistry getWorldRegistry() {
		return worldRegistry;
	}
}
