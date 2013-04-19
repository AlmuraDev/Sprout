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

import com.almuradev.sprout.plugin.task.GrowthTask;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class SproutListener implements Listener {
	private final SproutPlugin plugin;
	private static final Map<String, Integer> ID_WORLD_MAP = new HashMap<>();

	public SproutListener(SproutPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		//		Material material;
		//		Block sBlock = ((SpoutCraftBlock) event.getBlock()).getBlockType();
		//		if (sBlock instanceof GenericCustomBlock) {
		//			GenericCustomBlock customBlock = (GenericCustomBlock) sBlock;
		//
		//
		//			if (customBlock.getNotchianName().equalsIgnoreCase("CEP_RPG_Florist.CEP_Florist_rosebush")) {
		//				material = MaterialData.getMaterial("KFood_Core.strawberry");
		//				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new SpoutItemStack(material, 4));
		//				SpoutBlock spoutBlock = (SpoutBlock) event.getBlock();
		//				spoutBlock.setType(org.bukkit.Material.AIR);
		//				spoutBlock.setTypeId(material.getRawId());
		//				SpoutBlock myBlock = (SpoutBlock) MaterialData.getMaterial("Hi");
		//				spoutBlock.setCustomBlock(null);
		//				return;
		//			}
		//
		//		}
	}

	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		final String name = event.getWorld().getName();
		final Long interval = plugin.getConfiguration().getGrowthIntervalFor(name);
		if (interval == null) {
			return;
		}
		ID_WORLD_MAP.put(name, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new GrowthTask(plugin, name), 0, interval));
	}

	@EventHandler
	public void onWorldSave(WorldSaveEvent event) {
		final Integer id = ID_WORLD_MAP.remove(event.getWorld().getName());
		if (id != null) {
			Bukkit.getScheduler().cancelTask(id);
		}
	}
}
