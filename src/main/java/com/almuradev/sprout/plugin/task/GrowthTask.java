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

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.crop.Stage;
import com.almuradev.sprout.api.io.WorldRegistry;
import com.almuradev.sprout.api.util.Int21TripleHashed;
import com.almuradev.sprout.api.util.TInt21TripleObjectHashMap;
import com.almuradev.sprout.plugin.SproutPlugin;
import com.almuradev.sprout.plugin.crop.SimpleSprout;

import gnu.trove.procedure.TLongObjectProcedure;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

public class GrowthTask implements Runnable {
	private final SproutPlugin plugin;
	private final WorldRegistry worldRegistry;
	private final String world;
	private long pastTime;

	public GrowthTask(SproutPlugin plugin, String world) {
		this.plugin = plugin;
		this.world = world;
		worldRegistry = plugin.getWorldRegistry();
	}

	@Override
	public void run() {
		plugin.getLogger().info("Commencing tick of Growth task.");
		final World w = Bukkit.getWorld(world);
		if (w == null) {
			return;
		}
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
		//plugin.getLogger().info("Current time: " + localTime);
		//plugin.getLogger().info("Time since last tick: " + delta);
		worldRegistry.getInternalMap().forEachEntry(new TLongObjectProcedure() {
			@Override
			public boolean execute(long l, Object o) {
				final int x = Int21TripleHashed.key1(l);
				final int y = Int21TripleHashed.key2(l);
				final int z = Int21TripleHashed.key3(l);
				final Block block = Bukkit.getWorld(world).getBlockAt(x, y, z);
				//Only replace blocks in loaded chunks
				if (!block.getChunk().isLoaded()) {
					return true;
				}
				final Sprout sprout = (Sprout) o;
				//plugin.getLogger().info("Current Age: " + sprout.getAge());
				((SimpleSprout) sprout).grow((int) delta);
				final Stage current = sprout.getCurrentStage();
				//plugin.getLogger().info("Current Stage: " + current);
				//plugin.getLogger().info("Current Age (after growth): " + sprout.getAge());
				if (current == null) {
					return true;
				}
				final CustomBlock customBlock = MaterialData.getCustomBlock(current.getName());
				if (customBlock == null) {
					return true;
				}
				((SpoutBlock) block).setCustomBlock(customBlock);
				return true;
			}
		});
	}
}
