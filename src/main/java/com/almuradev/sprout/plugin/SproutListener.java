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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.mech.Drop;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.task.GrowthTask;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
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
	public void onBlockFade(BlockFadeEvent event) {
		final Block block = event.getBlock().getRelative((BlockFace.UP));
		if (plugin.getWorldRegistry().contains(block.getWorld().getName(), block.getX(), block.getY(), block.getZ())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();
		final Sprout sprout = plugin.getWorldRegistry().remove(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
		if (sprout == null) {
			return;
		}
		final Collection<Drop> drops = sprout.getDrops();
		for (Drop drop : drops) {
			final org.getspout.spoutapi.material.Material customMaterial = MaterialData.getCustomItem(drop.getName());
			if (customMaterial == null) {
				final Material material = Material.getMaterial(drop.getName());
				if (material == null) {
					continue;
				}
				final ItemStack stack = new ItemStack(material, drop.getAmount());
				block.getWorld().dropItemNaturally(block.getLocation(), stack);
			} else {
				final SpoutItemStack spoutStack = new SpoutItemStack(customMaterial, drop.getAmount());
				block.getWorld().dropItemNaturally(block.getLocation(), spoutStack);
			}
		}
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

		//Grab the material's name
		if (stack.isCustomItem()) {
			name = stack.getMaterial().getNotchianName();
		} else {
			name = held.getType().name();
		}

		//plugin.getLogger().info("Interacted with: " + name);
		//Only manage Sprouts
		final Sprout sprout = plugin.getSproutRegistry().find(name);
		if (sprout == null) {
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
		plugin.getWorldRegistry().add(where.getWorld().getName(), where.getX(), where.getY(), where.getZ(), new SimpleSprout(sprout.getName(), sprout.getBlockSource(), sprout.getItemSource(), sprout.getStages(), sprout.getDrops()));

		//Set material
		if (stack.isCustomItem()) {
			final CustomBlock block = MaterialData.getCustomBlock(sprout.getBlockSource());
			//plugin.getLogger().info("Placing source block: " + sprout.getBlockSource());
			((SpoutBlock) where).setCustomBlock(block);

        	//Remove item from inventory.
			if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
				held.setAmount(held.getAmount()-1);
				if (held.getAmount()== 0) {
					event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
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
