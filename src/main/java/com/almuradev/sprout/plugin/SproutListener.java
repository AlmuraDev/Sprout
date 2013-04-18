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

import org.getspout.spout.block.SpoutCraftBlock;
import org.getspout.spoutapi.material.Block;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.block.GenericCustomBlock;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SproutListener implements Listener {
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Material material;
		Block sBlock = ((SpoutCraftBlock) event.getBlock()).getBlockType();
		if (sBlock instanceof GenericCustomBlock) {
			GenericCustomBlock customBlock = (GenericCustomBlock) sBlock;	
			
			/*
			if (customBlock.getNotchianName().equalsIgnoreCase("CEP_RPG_Florist.CEP_Florist_rosebush")) {
				material = MaterialData.getMaterial("KFood_Core.strawberry");
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new SpoutItemStack(material, 4));
				SpoutBlock spoutBlock = (SpoutBlock) event.getBlock();
				spoutBlock.setType(org.bukkit.Material.AIR);
				spoutBlock.setTypeId(material.getRawId());
				SpoutBlock myBlock = (SpoutBlock) MaterialData.getMaterial("Hi");
				spoutBlock.setCustomBlock(null);
				return;
			}
			*/
		}
	}
}
