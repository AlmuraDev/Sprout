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
import java.util.Random;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.crop.Stage;
import com.almuradev.sprout.api.mech.Drop;
import com.almuradev.sprout.api.mech.Fertilizer;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.task.GrowthTask;
import com.almuradev.sprout.plugin.thread.SaveThread;
import com.almuradev.sprout.plugin.thread.ThreadRegistry;
import com.rits.cloning.Cloner;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;

public class SproutListener implements Listener {
	private static final Random RANDOM = new Random();
	private final SproutPlugin plugin;
	private final Cloner cloner = new Cloner();

	public SproutListener(SproutPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent event) {
		final Block fading = event.getBlock();
		//Don't do a lookup if it isn't soil.
		if (fading.getType() != Material.SOIL) {
			return;
		}
		final Block top = event.getBlock().getRelative((BlockFace.UP));
		if (plugin.getWorldRegistry().contains(top.getWorld().getName(), top.getX(), top.getY(), top.getZ())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();
		//Handle random seed drops. To preserve a possible LongGrass base block, make sure it isn't a custom block
		//TODO Configurable.
		if (event.getBlock().getType() == Material.LONG_GRASS && !(((SpoutBlock) block).getBlockType() instanceof CustomBlock) && RANDOM.nextInt(10 - 1) + 1 == 7) { //10% chance for a drop.
			final Sprout sprout = plugin.getSproutRegistry().get(RANDOM.nextInt(plugin.getSproutRegistry().size()));
			if (sprout == null) {
				return;
			}
			if (!sprout.getVariables().dropItemSourceOnGrassBreak()) {
				return;
			}
			disperseSeeds(event.getPlayer(), sprout, block);
		} else {
			//Handle breaking of Sprouts
			final Sprout sprout = plugin.getWorldRegistry().remove(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
			if (sprout == null) {
				return;
			}
			event.setCancelled(true);

			block.setType(Material.AIR);
			((SpoutBlock) block).setCustomBlock(null);
			plugin.getStorage().remove(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
			disperseDrops(event.getPlayer(), sprout, block);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockFromTo(BlockFromToEvent event) {
		final Block to = event.getToBlock();
		if (to.getType() == Material.LONG_GRASS && !(((SpoutBlock) to).getBlockType() instanceof CustomBlock) && RANDOM.nextInt(10 - 1) + 1 == 7) { //10% chance for a drop.
			final Sprout sprout = plugin.getSproutRegistry().get(RANDOM.nextInt(plugin.getSproutRegistry().size()));
			if (sprout == null) {
				return;
			}
			if (!sprout.getVariables().dropItemSourceOnGrassBreak()) {
				return;
			}
			disperseSeeds(sprout, to);
		} else {
			final Sprout sprout = plugin.getWorldRegistry().remove(to.getWorld().getName(), to.getX(), to.getY(), to.getZ());
			if (sprout == null) {
				return;
			}
			plugin.getStorage().remove(to.getWorld().getName(), to.getX(), to.getY(), to.getZ());
			event.setCancelled(true);

			to.setType(Material.AIR);
			((SpoutBlock) to).setCustomBlock(null);
			disperseDrops(sprout, to);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		final Block physics = event.getBlock();
		if (physics.getRelative(BlockFace.DOWN).getType() != Material.AIR) {
			return;
		}
		final Sprout sprout = plugin.getWorldRegistry().remove(physics.getWorld().getName(), physics.getX(), physics.getY(), physics.getZ());
		if (sprout == null) {
			return;
		}
		plugin.getStorage().remove(physics.getWorld().getName(), physics.getX(), physics.getY(), physics.getZ());
		event.setCancelled(true);
		physics.setType(Material.AIR);
		((SpoutBlock) physics).setCustomBlock(null);
		disperseDrops(sprout, physics);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityInteract(EntityInteractEvent event) {
		// Prevent trampling from other Entities
		final Material mat = event.getBlock().getType();
		final Entity entity = event.getEntity();
		if (entity instanceof LivingEntity && (mat == Material.SOIL || mat == Material.SOUL_SAND)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		// Prevent trampling
		switch (event.getAction()) {
			case PHYSICAL:
				final Block top = event.getClickedBlock().getRelative((BlockFace.UP));
				if (plugin.getWorldRegistry().contains(top.getWorld().getName(), top.getX(), top.getY(), top.getZ())) {
					event.setCancelled(true);
					break;
				}
			case RIGHT_CLICK_BLOCK:
				// Exit this method if player clicking on chest, door, button, etc.
				switch (event.getClickedBlock().getType()) {
					case CHEST:
					case WOOD_BUTTON:
					case STONE_BUTTON:
					case WOOD_DOOR:
					case IRON_DOOR:
					case IRON_DOOR_BLOCK:
					case FENCE_GATE:
					case BREWING_STAND:
					case FURNACE:
					case BURNING_FURNACE:
						return;
				}
				final Player interacter = event.getPlayer();
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

				//Fertilizer logic
				final Sprout dispersed = plugin.getWorldRegistry().get(interacted.getWorld().getName(), interacted.getX(), interacted.getY(), interacted.getZ());
				if (dispersed != null && !((SimpleSprout) dispersed).isOnLastStage() && dispersed.getVariables().allowFertilization()) {
					final Stage current = dispersed.getCurrentStage();
					final Fertilizer fertilizer;
					if (current == null) {
						fertilizer = dispersed.getFertilizerSource();
					} else {
						fertilizer = current.getFertilizer();
					}

					boolean toContinue = false;

					//Bonemeal
					if ((fertilizer.getName().equals("bonemeal") && name.equals("INK_SACK"))) {
						toContinue = true;
						//Custom Block
					} else if (fertilizer.getName().endsWith(name)) {
						toContinue = true;
						//Material
					} else if (fertilizer.getName().equals(name.toLowerCase())) {
						toContinue = true;
					}
					if (!toContinue) {
						return;
					}
					event.setCancelled(true);

					Stage stage;
					org.getspout.spoutapi.material.Material customMaterial;
					Material material;

					//Stage 0
					if (current == null) {
						stage = dispersed.getStage(1);
						customMaterial = MaterialData.getCustomBlock(stage.getName());
						material = Material.getMaterial(stage.getName().toUpperCase());

						if (customMaterial == null) {
							if (material == null) {
								return;
							}
						}

						//Grow to stage 1
						((SimpleSprout) dispersed).grow(stage);

						if (customMaterial != null) {
							((SpoutBlock) interacted).setCustomBlock((CustomBlock) customMaterial);
						} else {
							((SpoutBlock) interacted).setCustomBlock(null);
							interacted.setType(material);
						}
					} else {
						stage = ((SimpleSprout) dispersed).getNextStage();
						if (stage == null) {
							return;
						}
						customMaterial = MaterialData.getCustomBlock(stage.getName());
						material = Material.getMaterial(stage.getName().toUpperCase());

						if (customMaterial == null) {
							if (material == null) {
								return;
							}
						}

						((SimpleSprout) dispersed).incrementFertilizerCount(stage);
						if (((SimpleSprout) dispersed).getFertilizerCount(stage) >= fertilizer.getAmount()) {
							if (customMaterial != null) {
								((SpoutBlock) interacted).setCustomBlock((CustomBlock) customMaterial);
							} else {
								((SpoutBlock) interacted).setCustomBlock(null);
								interacted.setType(material);
							}
							((SimpleSprout) dispersed).grow(stage);
						}
						if (((SimpleSprout) dispersed).isOnLastStage()) {
							((SimpleSprout) dispersed).setFullyGrown(true);
							((SaveThread) ThreadRegistry.get(interacter.getWorld().getName())).QUEUE.offer(new GrowthTask.SproutInfo(interacted.getX(), interacted.getY(), interacted.getZ(), (SimpleSprout) dispersed));
						}
					}
					decrementInventory(interacter, interacter.getItemInHand());
				} else {
					//Non-fertilizer logic
					final Sprout sprout = plugin.getSproutRegistry().find(name);
					if (sprout == null) {
						return;
					}

					event.setCancelled(true);

					//Block face logic. TODO Customizable?
					if (event.getBlockFace() != BlockFace.UP) {
						return;
					}

					//Soil logic
					org.getspout.spoutapi.material.Material customMaterial = MaterialData.getCustomItem(sprout.getPlacementSource());
					Material material = Material.getMaterial(sprout.getPlacementSource().toUpperCase());
					if (customMaterial == null || !(((SpoutBlock) interacted).getBlockType() instanceof CustomBlock) || !((CustomBlock) ((SpoutBlock) interacted).getBlockType()).getFullName().equalsIgnoreCase(customMaterial.getName())) {
						if (material == null || !interacted.getType().equals(material)) {
							return;
						}
					}

					final Block where = interacted.getRelative(BlockFace.UP);

					//Make sure where we are setting the block won't be already obstructed.
					if (where.getType() != Material.AIR) {
						return;
					}

					final Sprout toInject = cloner.deepClone(sprout);
					plugin.getWorldRegistry().add(where.getWorld().getName(), where.getX(), where.getY(), where.getZ(), toInject);
					plugin.getStorage().add(where.getWorld().getName(), where.getX(), where.getY(), where.getZ(), toInject);

					//Set material
					if (stack.isCustomItem()) {
						final CustomBlock block = MaterialData.getCustomBlock(sprout.getBlockSource());
						((SpoutBlock) where).setCustomBlock(block);
						interacter.playSound(interacter.getLocation(), Sound.DIG_GRASS, 1.0F, 0.7936508F);
						decrementInventory(interacter, held);
					}
				}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onWorldInit(WorldInitEvent event) {
		GrowthTask.schedule(plugin, event.getWorld());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onWorldSave(WorldSaveEvent event) {
		GrowthTask.unschedule(event.getWorld());
	}

	private void disperseDrops(final Sprout sprout, final Block block) {
		disperseDrops(null, sprout, block);
	}

	private void disperseDrops(final Player cause, final Sprout sprout, final Block block) {
		if (cause != null && cause.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		if (!sprout.isFullyGrown()) {
			return;
		}
		final Collection<Drop> drops = sprout.getDrops();
		for (Drop drop : drops) {
			final org.getspout.spoutapi.material.Material customMaterial = MaterialData.getCustomItem(drop.getName());
			if (customMaterial == null) {
				final Material material = Material.getMaterial(drop.getName().toUpperCase());
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

	private void disperseSeeds(final Sprout sprout, final Block block) {
		disperseSeeds(null, sprout, block);
	}

	private void disperseSeeds(final Player cause, final Sprout sprout, final Block block) {
		if (cause != null && cause.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		final String seedName = sprout.getItemSource();
		final org.getspout.spoutapi.material.Material customMaterial = MaterialData.getCustomItem(seedName);
		if (customMaterial == null) {
			final Material material = Material.getMaterial(seedName);
			if (material == null) {
				return;
			}
			final ItemStack stack = new ItemStack(material); //TODO Configurable amounts
			block.getWorld().dropItemNaturally(block.getLocation(), stack);
		} else {
			final SpoutItemStack spoutStack = new SpoutItemStack(customMaterial);
			block.getWorld().dropItemNaturally(block.getLocation(), spoutStack);
		}
	}

	private void decrementInventory(final Player source, final ItemStack held) {
		//Remove item from inventory.
		if (source.getGameMode() != GameMode.CREATIVE) {
			held.setAmount(held.getAmount() - 1);
			if (held.getAmount() == 0) {
				source.setItemInHand(new ItemStack(Material.AIR));
			}
		}
	}
}
