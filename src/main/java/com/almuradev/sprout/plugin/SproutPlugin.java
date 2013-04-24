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

import java.util.HashMap;
import java.util.Map;

import com.almuradev.sprout.api.io.SQLMode;
import com.almuradev.sprout.api.io.SQLStorage;
import com.almuradev.sprout.api.io.SproutRegistry;
import com.almuradev.sprout.api.io.WorldRegistry;
import com.almuradev.sprout.plugin.io.FlatFileStorage;
import com.almuradev.sprout.plugin.io.SimpleSQLStorage;
import com.almuradev.sprout.plugin.io.SimpleSproutRegistry;
import com.almuradev.sprout.plugin.io.SimpleWorldRegistry;
import com.almuradev.sprout.plugin.task.GrowthTask;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class SproutPlugin extends JavaPlugin {
	private static final Map<String, Integer> WORLD_ID_MAP = new HashMap<>();
	private final SimpleSproutRegistry sproutRegistry;
	private final SimpleWorldRegistry worldRegistry;
	private SproutConfiguration configuration;
	private FlatFileStorage flatFileStorage;
	private SimpleSQLStorage sqlStorage;

	public SproutPlugin() {
		sproutRegistry = new SimpleSproutRegistry();
		worldRegistry = new SimpleWorldRegistry();
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		//TODO I bet this is insane...
		sqlStorage.dropAll();
	}

	@Override
	public void onEnable() {
		configuration = new SproutConfiguration(this);
		configuration.onEnable();
		flatFileStorage = new FlatFileStorage(this);
		flatFileStorage.onEnable();
		flatFileStorage.load();
		sqlStorage = new SimpleSQLStorage(this, SQLMode.H2);
		sqlStorage.onEnable(getDataFolder());
		worldRegistry.putAll(sqlStorage.getAll());
		getServer().getPluginManager().registerEvents(new SproutListener(this), this);
		startGrowthTasks();
	}

	public SproutConfiguration getConfiguration() {
		return configuration;
	}

	public SproutRegistry getSproutRegistry() {
		return sproutRegistry;
	}

	public WorldRegistry getWorldRegistry() {
		return worldRegistry;
	}

	public SQLStorage getStorage() {
		return sqlStorage;
	}

	private void startGrowthTasks() {
		for (World world : getServer().getWorlds()) {
			final String name = world.getName();
			final Long interval = configuration.getGrowthIntervalFor(name);
			if (interval == null) {
				continue;
			}
			WORLD_ID_MAP.put(name, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new GrowthTask(this, name), 0, interval));
		}
	}
}
