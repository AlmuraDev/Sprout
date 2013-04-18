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

import com.almuradev.sprout.api.io.SproutRegistry;
import com.almuradev.sprout.api.io.WorldRegistry;
import com.almuradev.sprout.plugin.io.SimpleSproutRegistry;
import com.almuradev.sprout.plugin.io.SimpleWorldRegistry;
import com.almuradev.sprout.plugin.io.Storage;

import org.bukkit.plugin.java.JavaPlugin;

public class SproutPlugin extends JavaPlugin {
	private final SimpleSproutRegistry sproutRegistry;
	private final SimpleWorldRegistry worldRegistry;
	private SproutConfiguration configuration;
	private Storage storage;

	public SproutPlugin() {
		sproutRegistry = new SimpleSproutRegistry();
		worldRegistry = new SimpleWorldRegistry();
	}

	@Override
	public void onEnable() {
		configuration = new SproutConfiguration(this);
		configuration.onEnable();
		storage = new Storage(this);
		storage.onEnable();
		getServer().getPluginManager().registerEvents(new SproutListener(this), this);
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
}
