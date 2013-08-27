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
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.almuradev.sprout.plugin.mech.SproutFertilizer;
import com.almuradev.sprout.plugin.mech.SproutLight;
import com.almuradev.sprout.plugin.mech.SproutVariableHolder;

import org.getspout.spoutapi.material.MaterialData;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public final class FlatFileStorage {
	private final SproutPlugin plugin;
	private File dir;

	public FlatFileStorage(SproutPlugin plugin) {
		this.plugin = plugin;
	}

	public void onEnable(final File dir) {
		this.dir = dir;
		try {
			Files.createDirectories(dir.toPath());
			Files.createFile(Paths.get(dir.getPath() + File.separator + "sprouts.yml"));
		} catch (FileAlreadyExistsException ignore) {
		} catch (IOException e) {
			plugin.getLogger().severe("Could not create " + dir.getPath() + "! Disabling...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}

	public void load() {
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

		for (String nameRaw : reader.getKeys(false)) {
			//NAME
			final String name = replacePeriodWithBackslash(nameRaw);
			final ConfigurationSection nameSection = reader.getConfigurationSection(nameRaw);
			//BLOCK SOURCE
			final String initialRawBlockSource = nameSection.getString("block-source", "");
			final String initialBlockSource = replacePeriodWithBackslash(initialRawBlockSource);
			if (Material.getMaterial(initialBlockSource.toUpperCase()) == null && MaterialData.getCustomItem(initialBlockSource) == null) {
				plugin.getLogger().warning("The block source [" + initialBlockSource + "] for sprout [" + name + "] is not a Minecraft material or a SpoutPlugin Custom Block.");
			}
			//ITEM SOURCE
			final String initialRawItemSource = nameSection.getString("item-source", "");
			final String initialItemSource = replacePeriodWithBackslash(initialRawItemSource);
			if (Material.getMaterial(initialItemSource.toUpperCase()) == null && MaterialData.getCustomItem(initialItemSource) == null) {
				plugin.getLogger().warning("The item source [" + initialItemSource + "] for sprout [" + name + "] is not a Minecraft material or a SpoutPlugin Custom Item.");
			}
			//PLACEMENT SOURCE
			final String initialRawPlacementSource = nameSection.getString("placement-source", "soil");
			final String initialPlacementSource = replacePeriodWithBackslash(initialRawPlacementSource);
			if (Material.getMaterial(initialPlacementSource.toUpperCase()) == null && MaterialData.getCustomItem(initialPlacementSource) == null) {
				plugin.getLogger().warning("The placement source [" + initialPlacementSource + "] for sprout [" + name + "] is not a Minecraft material or a SpoutPlugin Custom Block.");
			}
			//LIGHT
			final int minLightLevel = nameSection.getInt("min-light-level", 0);
			final int maxLightLevel = nameSection.getInt("max-light-level", 15);
			//FERTILIZER
			final String fertilizerRawSource = nameSection.getString("fertilizer-source", "bonemeal");
			final String fertilizerSource = replacePeriodWithBackslash(fertilizerRawSource);
			if (!fertilizerSource.equals("bonemeal") && Material.getMaterial(fertilizerSource.toUpperCase()) == null && MaterialData.getCustomItem(fertilizerSource) == null) {
				plugin.getLogger().warning("The fertilizer source [" + fertilizerSource + "] for sprout [" + name + "] is not a Minecraft material or a SpoutPlugin Custom Item.");
			}
			final int fertilizerAmount = nameSection.getInt("fertilizer-amount", 1);
			//DROPS
			final ConfigurationSection dropsSection = nameSection.getConfigurationSection("drops");
			final List<Drop> drops = new LinkedList<>();
			for (String rawDropSource : dropsSection.getKeys(false)) {
				final String dropSource = replacePeriodWithBackslash(rawDropSource);
				if (Material.getMaterial(dropSource) == null && MaterialData.getCustomItem(dropSource) == null) {
					plugin.getLogger().warning("The drop source [" + dropSource + "] for sprout [" + name + "] is not a Minecraft material or a SpoutPlugin Custom Item.");
				}
				final ConfigurationSection dropSection = dropsSection.getConfigurationSection(rawDropSource);
				final int amount = dropSection.getInt("amount", 0);
				drops.add(new SproutDrop(dropSource, amount));
			}
			//STAGES
			final ConfigurationSection stagesSection = nameSection.getConfigurationSection("stages");
			final Iterator<String> stagesIterator = stagesSection.getKeys(false).iterator();
			final Map<Integer, Stage> stages = new LinkedHashMap<>();
			while (stagesIterator.hasNext()) {
				//INDEX
				final String index = stagesIterator.next();
				final ConfigurationSection indexSection = stagesSection.getConfigurationSection(index);
				//SOURCE
				final String rawStageSource = indexSection.getString("source");
				final String stageSource = replacePeriodWithBackslash(rawStageSource);
				if (Material.getMaterial(stageSource) == null && MaterialData.getCustomItem(stageSource) == null) {
					plugin.getLogger().warning("The stage [" + index + "] source [" + stageSource + "] for sprout [" + name + "] is not a Minecraft material or a SpoutPlugin Custom Block.");
				}
				final int minStageLightLevel = indexSection.getInt("min-light-level", minLightLevel);
				final int maxStageLightLevel = indexSection.getInt("max-light-level", maxLightLevel);
				//FERTILIZER
				final String fertilizerRawStageSource = indexSection.getString("fertilizer-source", fertilizerRawSource);
				final String fertilizerStageSource = replacePeriodWithBackslash(fertilizerRawStageSource);
				if (!fertilizerSource.equals("bonemeal") && Material.getMaterial(fertilizerStageSource.toUpperCase()) == null && MaterialData.getCustomItem(fertilizerStageSource) == null) {
					plugin.getLogger().warning("The stage fertilizer [" + index + "] source [" + fertilizerStageSource + "] for sprout [" + name + "] is not a Minecraft material or a SpoutPlugin Custom Item.");
				}
				final int fertilizerStageAmount = indexSection.getInt("fertilizer-amount", 1);
				//GROWTH
				final int growthTicks = indexSection.getInt("growth-required", 350);
				final int growthChance = indexSection.getInt("growth-chance", 10);
				stages.put(Integer.parseInt(index), new SimpleStage(stageSource, growthTicks, growthChance, new SproutFertilizer(fertilizerStageSource, fertilizerStageAmount), new SproutLight(minStageLightLevel, maxStageLightLevel)));
			}
			//VARIABLES
			final ConfigurationSection variablesSection = nameSection.getConfigurationSection("variables");
			final SimpleSprout created;
			if (variablesSection != null) {
				final boolean allowFertilization = variablesSection.getBoolean("allow-fertilization", true);
				final boolean dropItemSourceOnGrassBreak = variablesSection.getBoolean("drop-item-source-on-grass-break", true);
				created = new SimpleSprout(name, initialBlockSource, initialItemSource, initialPlacementSource, new SproutFertilizer(fertilizerSource, fertilizerAmount), new SproutLight(minLightLevel, maxLightLevel), stages, drops, new SproutVariableHolder(allowFertilization, dropItemSourceOnGrassBreak));
			} else {
				created = new SimpleSprout(name, initialBlockSource, initialItemSource, initialPlacementSource, new SproutFertilizer(fertilizerSource, fertilizerAmount), new SproutLight(minLightLevel, maxLightLevel), stages, drops);
			}
			plugin.getLogger().info("Loaded sprout [" + created.getName() + "].");
			createdSprouts.add(created);
		}
		return createdSprouts;
	}

	private String replacePeriodWithBackslash(String raw) {
		return raw.replace("\\", ".");
	}
}