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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.almuradev.sprout.api.io.SQLMode;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SproutConfiguration {
	private final SproutPlugin plugin;
	private FileConfiguration config;
	private Map<String, Long> growthIntervals = new HashMap<>();

	public SproutConfiguration(SproutPlugin plugin) {
		this.plugin = plugin;
	}

	public void onEnable() {
		if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
			plugin.saveDefaultConfig();
		}
		config = plugin.getConfig();
		//Parse in worlds
		final Iterator<String> worldIterator = config.getKeys(false).iterator();
		while (worldIterator.hasNext()) {
			final String world = worldIterator.next();
			final ConfigurationSection worldSection = config.getConfigurationSection(world);
			growthIntervals.put(world, worldSection.getLong("growth-interval", 350));
		}
	}

	public Long getGrowthIntervalFor(String world) {
		return growthIntervals.get(world);
	}

	/*
	 * SQL Configuration Options
	 */

	public SQLMode getMode() {
		final String raw = config.getString("sql.mode", "H2");
		try {
			return SQLMode.valueOf(raw.toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}

	public String getHost() {
		return config.getString("sql.host", "localhost");
	}

	public String getDatabase() {
		return config.getString("sql.database", "minecraft");
	}

	public int getPort() {
		return config.getInt("sql.port", 25566);
	}

	public String getUsername() {
		return config.getString("sql.username", "minecraft");
	}

	public String getPassword() {
		return config.getString("sql.password", "minecraft");
	}
}
