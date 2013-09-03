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
package com.almuradev.sprout.plugin.thread;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;

import com.almuradev.sprout.api.util.Int21TripleHashed;
import com.almuradev.sprout.plugin.SproutPlugin;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.io.SimpleSQLStorage;
import com.almuradev.sprout.plugin.task.LocatableSprout;

import org.bukkit.Location;

public class SaveThread extends Thread {
	public final LinkedBlockingDeque<LocatableSprout> ADD = new LinkedBlockingDeque<>();
	public final LinkedBlockingDeque<LocatableSprout> REMOVE = new LinkedBlockingDeque<>();
	final SproutPlugin plugin;
	final String world;

	public SaveThread(final SproutPlugin plugin, final String world) {
		super("Save Thread - " + world);
		this.plugin = plugin;
		this.world = world;
	}

	@Override
	public void run() {
		while(!this.isInterrupted()) {
			flush();
		}
	}

	public void add(final Location location, final SimpleSprout sprout) {
		if (location == null || sprout == null) {
			return;
		}
		ADD.offer(new LocatableSprout(Int21TripleHashed.key(location.getBlockX(), location.getBlockY(), location.getBlockZ()), sprout));
	}

	public void add(final int x, final int y, final int z, final SimpleSprout sprout) {
		if (sprout == null) {
			return;
		}
		ADD.offer(new LocatableSprout(x, y, z, sprout));
	}

	public void flush() {
		ADD.removeAll(Arrays.asList(REMOVE.toArray()));
		LocatableSprout flush;

		while ((flush = ADD.poll()) != null) {
			((SimpleSQLStorage) plugin.getStorage()).add(world, flush.getLocation(), flush.getSprout());
		}

		while ((flush = REMOVE.poll()) != null) {
			((SimpleSQLStorage) plugin.getStorage()).remove(world, flush.getLocation());
		}
	}

	public void remove(final Location location, final SimpleSprout sprout) {
		if (location == null || sprout == null) {
			return;
		}
		REMOVE.offer(new LocatableSprout(Int21TripleHashed.key(location.getBlockX(), location.getBlockY(), location.getBlockZ()), sprout));
	}

	public void remove(long location, final SimpleSprout sprout) {
		if (sprout == null) {
			return;
		}
		REMOVE.offer(new LocatableSprout(location, sprout));
	}

	public void clear() {
		ADD.clear();
	}
}
