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

import com.almuradev.sprout.crop.Sprout;
import com.almuradev.sprout.crop.Stage;
import com.almuradev.sprout.api.io.Registry;
import com.almuradev.sprout.api.util.Int21TripleHashed;
import com.almuradev.sprout.api.util.TInt21TripleObjectHashMap;
import com.almuradev.sprout.plugin.SproutPlugin;
import com.almuradev.sprout.plugin.crop.SimpleSprout;

import gnu.trove.procedure.TLongObjectProcedure;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class GrowthTask implements Runnable {
	private final SproutPlugin plugin;
	private final Registry registry;
	private final String world;

	public GrowthTask(SproutPlugin plugin, String world) {
		this.plugin = plugin;
		this.world = world;
		registry = plugin.getRegistry();
	}

	@Override
	public void run() {
		plugin.getLogger().info("Commencing tick of Growth task.");
		final World w = Bukkit.getWorld(world);
		if (w == null) {
			return;
		}
		final TInt21TripleObjectHashMap worldRegistry = registry.getRegistryFor(world);
		if (worldRegistry == null) {
			return;
		}

		final long localTime = System.currentTimeMillis();

		worldRegistry.getInternalMap().forEachEntry(new TLongObjectProcedure() {
			@Override
			public boolean execute(long l, Object o) {
				final int x = Int21TripleHashed.key1(l);
				final int y = Int21TripleHashed.key2(l);
				final int z = Int21TripleHashed.key3(l);
				//Only replace blocks in loaded chunks
				if (!w.getChunkAt(x, z).isLoaded()) {
					return true;
				}
				final Block block = w.getBlockAt(x, y, z);
				//if (((SpoutBlock)block).getCustomBlock() == null) {
				//	return true;
				//}
				plugin.getLogger().info("Found a Spout block at " + block.toString() + ". Gathering growth data from associated Sprout.");
				final Sprout sprout = (Sprout) o;
				final Stage current = sprout.getCurrentStage(localTime);
				final Stage next = sprout.getNextStage(localTime);
				if (next == null) {
					plugin.getLogger().info("Spout block " + block.toString() + " has no further growth stages.");
					return true;
				}
				if (current.getCustomName().equals(next.getCustomName())) {
					plugin.getLogger().info("Skipping replacement of same name material (" + current.getCustomName() + ")");
					return true;
				}
				final CustomBlock customBlock = MaterialData.getCustomBlock(next.getCustomName());
				if (customBlock == null) {
					plugin.getLogger().info("Could not find custom block with name: " + next.getCustomName());
					return true;
				}
				((SpoutBlock) block).setCustomBlock(customBlock);
				((SimpleSprout) sprout).setDispersedTime(sprout.getDispersedTime() + localTime);
				return true;
			}
		});
	}
}
