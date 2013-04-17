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

import com.almuradev.sprout.api.io.Registry;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.io.SimpleRegistry;
import com.almuradev.sprout.plugin.task.GrowthTask;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SproutPlugin extends JavaPlugin {
	private final SimpleRegistry registry;

	public SproutPlugin() {
		registry = new SimpleRegistry();
	}

	@Override
	public void onEnable() {
		registry.add("world", 0, 0, 0, new SimpleSprout("TestSprout", null));
		registry.add("world", 0, 1, 0, new SimpleSprout("TestSprout1", null));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new GrowthTask(this, "world"), 0, 0);
	}

	public Registry getRegistry() {
		return registry;
	}
}
