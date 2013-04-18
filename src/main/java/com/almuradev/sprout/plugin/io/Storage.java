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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.crop.Stage;
import com.almuradev.sprout.api.mech.Drop;
import com.almuradev.sprout.plugin.SproutPlugin;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.crop.stage.SimpleStage;
import com.almuradev.sprout.plugin.mech.SproutDrop;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Storage {
	private final SproutPlugin plugin;
	private final File dir;

	public Storage(SproutPlugin plugin) {
		this.plugin = plugin;
		dir = plugin.getDataFolder();
	}

	public void onEnable() {
		try {
			Files.createDirectories(dir.toPath());
			Files.createFile(Paths.get(dir.getPath() + File.separator + "sprouts.yml"));
		} catch (FileAlreadyExistsException fafe) {
			;
		} catch (IOException e) {
			plugin.getLogger().severe("Could not create " + dir.getPath() + "! Disabling...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}

	protected void load() {
		try {
			Files.walkFileTree(Paths.get(dir.getPath() + File.separator + "sprouts.yml"), new FileLoadingVisitor(plugin));
		} catch (IOException ignore) {
			plugin.getLogger().severe("Encountered a major issue while attempting to find " + dir.toPath() + ". Disabling...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
}

class FileLoadingVisitor extends SimpleFileVisitor<Path> {
	private final SproutPlugin plugin;

	public FileLoadingVisitor(SproutPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public FileVisitResult visitFileFailed(Path path, IOException ioe) {
		return FileVisitResult.TERMINATE;
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attr) {
		final List<Sprout> toInject = createSprouts(path.toFile());
		if (toInject == null) {
			plugin.getLogger().severe("Could not load: " + path.getFileName() + ". Skipping...");
			return FileVisitResult.TERMINATE;
		}
		plugin.getSproutRegistry().addAll(toInject);
		return FileVisitResult.TERMINATE;
	}

	private List<Sprout> createSprouts(File yml) {
		final YamlConfiguration reader = YamlConfiguration.loadConfiguration(yml);
		final List<Sprout> createdSprouts = new ArrayList<>();
		final Iterator<String> iterator = reader.getKeys(false).iterator();

		while (iterator.hasNext()) {
			//Identifier
			final String identifier = iterator.next();
			final ConfigurationSection identifierSection = reader.getConfigurationSection(iterator.next());
			//Source
			final String initialSource = identifierSection.getString("source", "");
			//Drops
			final ConfigurationSection dropsSection = identifierSection.getConfigurationSection("drops");
			final List<Drop> drops = new LinkedList<>();
			final Iterator<String> dropsIterator = dropsSection.getKeys(false).iterator();
			while (dropsIterator.hasNext()) {
				final String dropSource = dropsIterator.next();
				final ConfigurationSection dropSection = dropsSection.getConfigurationSection(dropSource);
				final int amount = dropSection.getInt("amount", 0);
				drops.add(new SproutDrop(dropSource, amount));
			}
			//Stages
			final ConfigurationSection stagesSection = identifierSection.getConfigurationSection("stages");
			final Iterator<String> stagesIterator = stagesSection.getKeys(false).iterator();
			final Map<Integer, Stage> stages = new HashMap<>();
			while (stagesIterator.hasNext()) {
				final String index = stagesIterator.next();
				final ConfigurationSection indexSection = stagesSection.getConfigurationSection(index);
				final String stageSource = indexSection.getString("source");
				final int growthTicks = indexSection.getInt("growth-ticks");
				stages.put(Integer.parseInt(index), new SimpleStage(stageSource, growthTicks));
			}
			createdSprouts.add(new SimpleSprout(identifier, initialSource, stages, drops));
		}
		return createdSprouts;
	}
}