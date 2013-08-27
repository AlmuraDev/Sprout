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
package com.almuradev.sprout.plugin.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.crop.Stage;
import com.almuradev.sprout.api.io.WorldRegistry;
import com.almuradev.sprout.api.mech.Light;
import com.almuradev.sprout.api.util.Int21TripleHashed;
import com.almuradev.sprout.api.util.TInt21TripleObjectHashMap;
import com.almuradev.sprout.plugin.SproutPlugin;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.thread.SaveThread;
import com.almuradev.sprout.plugin.thread.ThreadRegistry;

import gnu.trove.procedure.TLongObjectProcedure;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class GrowthTask implements Runnable {
	private static final Map<String, Integer> WORLD_ID_MAP = new HashMap<>();
	private static final Random RANDOM = new Random();
	private final SproutPlugin plugin;
	private final WorldRegistry worldRegistry;
	private final String world;
	private long pastTime;

	public GrowthTask(SproutPlugin plugin, String world) {
		this.plugin = plugin;
		this.world = world;
		worldRegistry = plugin.getWorldRegistry();
	}

	public static void schedule(Plugin plugin, World... worlds) {
		final SproutPlugin sproutPlugin = (SproutPlugin) plugin;
		for (World world : worlds) {
			if (world == null) {
				continue;
			}
			final Long l = sproutPlugin.getConfiguration().getGrowthIntervalFor(world.getName());
			if (l == null) {
				continue;
			}
			plugin.getLogger().info("Growth is scheduled for [" + world.getName() + "] every ~" + l / 20 + " second(s).");
			WORLD_ID_MAP.put(world.getName(), Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new GrowthTask(sproutPlugin, world.getName()), 0, l));

			//Async saving
			ThreadRegistry.add(new SaveThread((SproutPlugin) plugin, world.getName())).start();
		}
	}

	public static void unschedule(World... worlds) {
		for (World world : worlds) {
			final Integer id = WORLD_ID_MAP.remove(world.getName());
			if (id != null) {
				Bukkit.getScheduler().cancelTask(id);
			}
			ThreadRegistry.remove(world.getName());
		}
	}

	public static void stop(Plugin plugin) {
		Bukkit.getScheduler().cancelTasks(plugin);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		final TInt21TripleObjectHashMap worldRegistry = this.worldRegistry.get(world);
		if (worldRegistry == null) {
			return;
		}
		//First tick
		if (pastTime == 0) {
			pastTime = System.currentTimeMillis() / 1000;
		}
		final long localTime = System.currentTimeMillis() / 1000;
		final long delta = localTime - pastTime;
		pastTime = localTime;

		worldRegistry.getInternalMap().forEachEntry(new TLongObjectProcedure() {
			@Override
			public boolean execute(long l, Object o) {
				final SimpleSprout sprout = (SimpleSprout) o;
				final Sprout live = plugin.getWorldRegistry().get(world, Int21TripleHashed.key1(l), Int21TripleHashed.key2(l), Int21TripleHashed.key3(l));
				if (!sprout.equals(live)) {
					return true;
				}
				if (!sprout.isFullyGrown()) {
					final Stage current = sprout.getCurrentStage();
					if (current != null) {
						if (RANDOM.nextInt(current.getGrowthChance() - 1 + 1) + 1 == current.getGrowthChance()) {
							final CustomBlock customBlock = MaterialData.getCustomBlock(current.getName());
							final Material material = Material.getMaterial(current.getName());

							if (customBlock == null) {
								if (material == null) {
									return true;
								}
							}

							final Block block = Bukkit.getWorld(world).getBlockAt(Int21TripleHashed.key1(l), Int21TripleHashed.key2(l), Int21TripleHashed.key3(l));
							if (block.getChunk().isLoaded()) {
								final Light light = current.getLight();
								// (A <= B <= C) inclusive
								if (!(light.getMinimumBlockLight() <= block.getLightFromBlocks() && block.getLightFromBlocks() <= light.getMaximumBlockLight())) {
									return true;
								}
								if (customBlock != null) {
									if (((SpoutBlock) block).getCustomBlock() != customBlock) {
										((SpoutBlock) block).setCustomBlock(customBlock);
									}
								} else {
									((SpoutBlock) block).setCustomBlock(null);
									block.setType(material);
								}
								if (sprout.isOnLastStage()) {
									sprout.setFullyGrown(true);
									((SaveThread) ThreadRegistry.get(world)).QUEUE.offer(new LocatableSprout(l, sprout));
								} else {
									sprout.grow((int) delta);
								}
							}
						}
					}
				}
				return true;
			}
		});
	}
}
