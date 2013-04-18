package com.almuradev.sprout.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
		//Read in default config.yml
		if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
			plugin.saveDefaultConfig();
		}
		config = plugin.getConfig();
		//Parse in worlds
		final Iterator<String> worldIterator = config.getKeys(false).iterator();
		while (worldIterator.hasNext()) {
			final String world = worldIterator.next();
			final ConfigurationSection worldSection = config.getConfigurationSection(world);
			final long growth = worldSection.getLong("growth-interval", 300);
			growthIntervals.put(world, growth);
		}
	}

	public long getGrowthIntervalFor(String world) {
		return growthIntervals.get(world) == null ? 300 : growthIntervals.get(world);
	}

	public void save() {
		try {
			config.save(new File(plugin.getDataFolder(), "config.yml"));
		} catch (Exception e) {
			throw new IllegalStateException("Could not save configuration changes to file!");
		}
	}
}
