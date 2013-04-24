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
		//TODO I bet this is insane...
		GrowthTask.stop(this);
		sqlStorage.dropAll();
	}

	@Override
	public void onEnable() {
		configuration.onEnable();
		flatFileStorage.onEnable(getDataFolder());
		flatFileStorage.load();
		sqlStorage.onEnable(SQLMode.H2, getDataFolder());
		worldRegistry.putAll(sqlStorage.getAll());
		getServer().getPluginManager().registerEvents(new SproutListener(this), this);
		GrowthTask.schedule(this, Bukkit.getWorlds().toArray(new World[Bukkit.getWorlds().size()]));
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
