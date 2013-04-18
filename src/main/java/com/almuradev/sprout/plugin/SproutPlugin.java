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

import com.almuradev.sprout.api.io.Registry;
import com.almuradev.sprout.crop.Stage;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.crop.stage.SimpleStage;
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
		final Map<Integer, Stage> stages = new HashMap<>(4);
		stages.put(0, new SimpleStage("customblock1", 0));
		stages.put(1, new SimpleStage("customblock2", 100));
		stages.put(2, new SimpleStage("customblock3", 1000));
		stages.put(3, new SimpleStage("customblock4", 10000));
		stages.put(4, new SimpleStage("customblock5", 100000));
		for (int i = 0; i < 1; i++) {
			registry.add("world", i, 0, i, new SimpleSprout("TestSprout", "datplugin.datcrop", stages, null));
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new GrowthTask(this, "world"), 250, 250);
	}

	public Registry getRegistry() {
		return registry;
	}
}
