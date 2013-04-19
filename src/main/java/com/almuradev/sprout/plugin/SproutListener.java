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

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.task.GrowthTask;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;

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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		//Only allow right clicks
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}

		final ItemStack held = event.getItem();
		if (held == null) {
			return;
		}
		final Block interacted = event.getClickedBlock();
		final SpoutItemStack stack = new SpoutItemStack(held);
		final String name;

		if (stack.isCustomItem()) {
			name = stack.getMaterial().getNotchianName();
		} else {
			name = held.getType().name();
		}

		plugin.getLogger().info("Interacted with: " + name);
		//Only manage Sprouts
		final Sprout sprout = plugin.getSproutRegistry().get(name, false);
		if (sprout == null) {
			plugin.getLogger().info("No sprout found for this item!");
			return;
		}

		//Place on top of Block. TODO Keep this?
		if (event.getBlockFace() != BlockFace.UP) {
			event.setCancelled(true);
			return;
		}

		//Place on soil. TODO Expand this concept.
		if (interacted.getType() != Material.SOIL) {
			event.setCancelled(true);
			return;
		}

		final Block where = interacted.getRelative(BlockFace.UP);

		//Make sure where we are setting the block won't be already obstructed.
		if (where.getType() != Material.AIR) {
			event.setCancelled(true);
			return;
		}

		//Add Sprout to registry
		if (sprout != null) {
			((SimpleSprout) sprout).setDispersedTime(System.currentTimeMillis());
			plugin.getWorldRegistry().add(where.getWorld().getName(), where.getX(), where.getY(), where.getZ(), sprout);
		}

		if (stack.isCustomItem()) {
			final CustomBlock block = MaterialData.getCustomBlock(sprout.getStage(0).getName());
			plugin.getLogger().info("Placing stage 1 block: " + sprout.getStage(0).getName());
			((SpoutBlock) where).setCustomBlock(block);
		} else {
			where.setType(held.getType());
		}
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
