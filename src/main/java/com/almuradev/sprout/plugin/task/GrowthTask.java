package com.almuradev.sprout.plugin.task;

import com.almuradev.sprout.api.Sprout;
import com.almuradev.sprout.api.Stage;
import com.almuradev.sprout.api.io.Registry;
import com.almuradev.sprout.api.util.Int21TripleHashed;
import com.almuradev.sprout.api.util.TInt21TripleObjectHashMap;
import com.almuradev.sprout.plugin.SproutPlugin;

import gnu.trove.procedure.TLongObjectProcedure;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class GrowthTask implements Runnable {
	private final SproutPlugin plugin;
	private final Registry registry;
	private final String world;
	private long tick;

	public GrowthTask(SproutPlugin plugin, String world) {
		this.plugin = plugin;
		this.world = world;
		registry = plugin.getRegistry();
		tick = System.currentTimeMillis();
	}

	@Override
	public void run() {
		final World w = Bukkit.getWorld(world);
		if (w == null) {
			return;
		}
		final TInt21TripleObjectHashMap worldRegistry = registry.getRegistryFor(world);
		if (worldRegistry == null) {
			return;
		}

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
				if (!(block instanceof SpoutBlock)) {
					return true;
				}
				final SpoutBlock customBlock = (SpoutBlock) block;
				final Sprout sprout = (Sprout) o;
				final Stage current = sprout.getCurrentStage(tick);
				if (current == null) {
					return true;
				}
				final Material currentMaterial = MaterialData.getMaterial(current.getCustomName());
				tick += System.currentTimeMillis();
				final Stage next = sprout.getNextStage(tick);
				if (next == null) {
					return true;
				}
				final Material nextMaterial = MaterialData.getMaterial(next.getCustomName());
				if (currentMaterial != nextMaterial) {
					customBlock.setCustomBlock((CustomBlock) nextMaterial);
				}
				return true;
			}
		});
	}
}
